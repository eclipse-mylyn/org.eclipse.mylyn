/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.history;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.mylyn.commons.ui.AbstractColumnViewerSupport;
import org.eclipse.mylyn.internal.tasks.core.sync.GetTaskHistoryJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.compare.TaskDataDiffNode;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskHistory;
import org.eclipse.mylyn.tasks.core.data.TaskRevision;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Steffen Pingel
 */
public class TaskHistoryPage extends HistoryPage {

	private class AddedLabelProvider extends ChangeLabelProvider {

		@Override
		public String getText(TaskRevision.Change change) {
			return change.getAdded();
		}

	}

	private static class AuthorLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof TaskRevision) {
				TaskRevision entry = (TaskRevision) element;
				return entry.getAuthor().toString();
			} else if (element instanceof TaskRevision.Change) {
				return null;
			}
			return super.getText(element);
		}

	}

	private static abstract class ChangeLabelProvider extends ColumnLabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof TaskRevision) {
				TaskRevision entry = (TaskRevision) element;
				if (entry.getChanges().size() == 1) {
					return getText(entry.getChanges().get(0));
				}
				return null;
			} else if (element instanceof TaskRevision.Change) {
				return getText((TaskRevision.Change) element);
			}
			return super.getText(element);
		}

		public abstract String getText(TaskRevision.Change change);

	}

	private class FieldLabelProvider extends ChangeLabelProvider {

		@Override
		public String getText(TaskRevision.Change change) {
			return change.getField();
		}

	}

	private class RemovedLabelProvider extends ChangeLabelProvider {

		@Override
		public String getText(TaskRevision.Change change) {
			return change.getRemoved();
		}

	}

	private static class TimeLabelProvider extends ColumnLabelProvider {

		private final DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

		@Override
		public String getText(Object element) {
			if (element instanceof TaskRevision) {
				TaskRevision entry = (TaskRevision) element;
				return (entry.getDate() != null) ? formatter.format(entry.getDate()) : null;
			} else if (element instanceof TaskRevision.Change) {
				return null;
			}
			return super.getText(element);
		}

	}

	public static boolean canShowHistoryFor(Object object) {
		ITask task = getTask(object);
		if (task != null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(task.getConnectorKind());
			TaskRepository repository = TasksUiPlugin.getRepositoryManager()
					.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
			return connector.canGetTaskHistory(repository, task);
		}
		return false;
	}

	public static ITask getTask(Object object) {
		ITask task = null;
		if (object instanceof ITask) {
			task = (ITask) object;
		} else if (object instanceof IAdaptable) {
			task = (ITask) ((IAdaptable) object).getAdapter(ITask.class);
		}
		return task;
	}

	private TaskHistoryContentProvider contentProvider;

	private GetTaskHistoryJob refreshOperation;

	private TreeViewer viewer;

	public TaskHistoryPage() {
	}

	@Override
	public void createControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);

		TreeViewerColumn authorViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		authorViewerColumn.setLabelProvider(new AuthorLabelProvider());
		TreeColumn authorColumn = authorViewerColumn.getColumn();
		authorColumn.setText(Messages.TaskHistoryPage_Author_Column_Label);
		authorColumn.setWidth(120);
		authorColumn.setData(AbstractColumnViewerSupport.KEY_COLUMN_CAN_HIDE, false);

		TreeViewerColumn timeViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		timeViewerColumn.setLabelProvider(new TimeLabelProvider());
		TreeColumn timeColumn = timeViewerColumn.getColumn();
		timeColumn.setText(Messages.TaskHistoryPage_Time_Column_Label);
		timeColumn.setWidth(140);

		TreeViewerColumn fieldViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		fieldViewerColumn.setLabelProvider(new FieldLabelProvider());
		TreeColumn fieldColumn = fieldViewerColumn.getColumn();
		fieldColumn.setText(Messages.TaskHistoryPage_Field_Column_Label);
		fieldColumn.setWidth(120);

		TreeViewerColumn removedViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		removedViewerColumn.setLabelProvider(new RemovedLabelProvider());
		TreeColumn remmovedColumn = removedViewerColumn.getColumn();
		remmovedColumn.setText(Messages.TaskHistoryPage_Removed_Column_Label);
		remmovedColumn.setWidth(120);

		TreeViewerColumn addedViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		addedViewerColumn.setLabelProvider(new AddedLabelProvider());
		TreeColumn addedColumn = addedViewerColumn.getColumn();
		addedColumn.setText(Messages.TaskHistoryPage_Added_Column_Label);
		addedColumn.setWidth(120);

		contentProvider = new TaskHistoryContentProvider();
		viewer.setContentProvider(contentProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (item instanceof TaskRevision) {
					final TaskRevision revision = (TaskRevision) item;
					final TaskHistory history = (TaskHistory) viewer.getInput();

					CompareConfiguration configuration = new CompareConfiguration();
					configuration.setProperty(CompareConfiguration.IGNORE_WHITESPACE, Boolean.valueOf(true));
					configuration.setLeftEditable(false);
					configuration.setLeftLabel(Messages.TaskHistoryPage_Old_Value_Label);
					configuration.setRightEditable(false);
					configuration.setRightLabel(Messages.TaskHistoryPage_New_Value_Label);
					CompareEditorInput editorInput = new CompareEditorInput(configuration) {
						@Override
						protected Object prepareInput(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							try {
								AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
										.getRepositoryConnector(history.getTask().getConnectorKind());
								TaskData newData = connector.getTaskData(history.getRepository(),
										history.getTask().getTaskId(), monitor);
								TaskData oldData = TasksUiInternal.computeTaskData(newData, history, revision.getId(),
										monitor);
								return new TaskDataDiffNode(Differencer.CHANGE, oldData, newData);
							} catch (CoreException e) {
								throw new InvocationTargetException(e);
							}
						}
					};
					CompareUI.openCompareEditor(editorInput);
				}
			}
		});
	}

	@Override
	public void dispose() {
		cancelRefresh();
		super.dispose();
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	@Override
	public Control getControl() {
		return viewer.getControl();
	}

	public String getDescription() {
		return NLS.bind(Messages.TaskHistoryPage_Task_history_for_X_Desscription_Label, getName());
	}

	public String getName() {
		ITask task = getTask();
		return (task != null) ? task.getSummary() : null;
	}

	public ITask getTask() {
		return getTask(getInput());
	}

	@Override
	public boolean inputSet() {
		cancelRefresh();

		if (viewer == null) {
			return false;
		}

		final ITask task = getTask();
		if (task != null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(task.getConnectorKind());
			TaskRepository repository = TasksUiPlugin.getRepositoryManager()
					.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
			refreshOperation = new GetTaskHistoryJob(connector, repository, task);
			refreshOperation.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(final IJobChangeEvent event) {
					if (!event.getResult().isOK()) {
						return;
					}
					if (Display.getDefault().isDisposed()) {
						return;
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
								TaskHistory history = ((GetTaskHistoryJob) event.getJob()).getHistory();
								viewer.setInput(history);
							}
						}
					});
				}
			});
			schedule(refreshOperation);
			return true;
		}
		return false;
	}

	public boolean isValidInput(Object object) {
		return canShowHistoryFor(object);
	}

	public void refresh() {
		inputSet();
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	private void cancelRefresh() {
		if (refreshOperation != null) {
			refreshOperation.cancel();
			refreshOperation = null;
		}
	}

	private IWorkbenchPartSite getWorkbenchSite() {
		final IWorkbenchPart part = getHistoryPageSite().getPart();
		return part != null ? part.getSite() : null;
	}

	private void schedule(final Job job) {
		final IWorkbenchPartSite site = getWorkbenchSite();
		if (site != null) {
			IWorkbenchSiteProgressService progress = (IWorkbenchSiteProgressService) site
					.getAdapter(IWorkbenchSiteProgressService.class);
			if (progress != null) {
				progress.schedule(job, 0, true);
				return;
			}
		}
		// fall-back
		job.schedule();
	}

}
