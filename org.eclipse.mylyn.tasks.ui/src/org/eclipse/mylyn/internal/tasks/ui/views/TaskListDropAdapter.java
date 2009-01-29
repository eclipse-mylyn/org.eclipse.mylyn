/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jevgeni Holodkov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.ui.util.PlatformUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Unscheduled;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves (added URL based task creation support)
 * @author Jevgeni Holodkov
 */
public class TaskListDropAdapter extends ViewerDropAdapter {

	private boolean fileTransfer;

	private boolean localTransfer;

	public TaskListDropAdapter(Viewer viewer) {
		super(viewer);
		setFeedbackEnabled(true);
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		// support dragging from sources only supporting DROP_LINK
		if (event.detail == DND.DROP_NONE && (event.operations & DND.DROP_LINK) == DND.DROP_LINK) {
			event.detail = DND.DROP_LINK;
		}
		super.dragOver(event);
	}

	@Override
	public boolean performDrop(final Object data) {
		List<ITask> tasksToMove = new ArrayList<ITask>();

		if (localTransfer) {
			ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
			if (selection instanceof IStructuredSelection) {
				for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
					Object item = it.next();
					if (item instanceof ITask) {
						tasksToMove.add((ITask) item);
					}
				}
			}
		} else if (fileTransfer) {
			// TODO implement dropping of files
		} else if (data instanceof String) {
			String text = (String) data;
			AbstractTask task = createTaskFromUrl(text);
			if (task == null) {
				task = TasksUiInternal.createNewLocalTask(text);
			}
			if (task != null) {
				tasksToMove.add(task);
				final ITask newTask = task;
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						TasksUiUtil.openTask(newTask);
					}
				});
			}
		}

		Object currentTarget = getCurrentTarget();
		if (currentTarget instanceof LocalTask && areAllLocalTasks(tasksToMove) && getCurrentLocation() == LOCATION_ON) {
			for (ITask task : tasksToMove) {
				if (!((AbstractTask) task).contains(((LocalTask) currentTarget).getHandleIdentifier())) {
					TasksUiInternal.getTaskList().addTask(task, (LocalTask) currentTarget);
				}
			}
		} else {
			for (ITask task : tasksToMove) {
				if (currentTarget instanceof UncategorizedTaskContainer) {
					TasksUiInternal.getTaskList().addTask(task, (UncategorizedTaskContainer) currentTarget);
				} else if (currentTarget instanceof TaskCategory) {
					TasksUiInternal.getTaskList().addTask(task, (TaskCategory) currentTarget);
				} else if (currentTarget instanceof UnmatchedTaskContainer) {
					if (((UnmatchedTaskContainer) currentTarget).getRepositoryUrl().equals(task.getRepositoryUrl())) {
						TasksUiInternal.getTaskList().addTask(task, (AbstractTaskCategory) currentTarget);
					}
				} else if (currentTarget instanceof ITask) {
					ITask targetTask = (ITask) currentTarget;
					AbstractTaskCategory targetCategory = null;
					// TODO: TaskCategory only used what about AbstractTaskCategory descendants?
					ITaskContainer container = TaskCategory.getParentTaskCategory(targetTask);
					if (container instanceof TaskCategory || container instanceof UncategorizedTaskContainer) {
						targetCategory = (AbstractTaskCategory) container;
					} else if (container instanceof UnmatchedTaskContainer) {
						if (((UnmatchedTaskContainer) container).getRepositoryUrl().equals(task.getRepositoryUrl())) {
							targetCategory = (AbstractTaskCategory) container;
						}
					}
					if (targetCategory != null) {
						TasksUiInternal.getTaskList().addTask(task, targetCategory);
					}
				} else if (currentTarget instanceof ScheduledTaskContainer) {
					ScheduledTaskContainer container = (ScheduledTaskContainer) currentTarget;
					if (container instanceof Unscheduled) {
						TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task, null);
					} else {
						TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task,
								container.getDateRange());
					}
				} else if (currentTarget == null) {
					TasksUiInternal.getTaskList().addTask(task, TasksUiPlugin.getTaskList().getDefaultCategory());
				}
			}
		}

		if (tasksToMove.size() == 1) {
			getViewer().setSelection(new StructuredSelection(tasksToMove.get(0)));
		}
		return true;
	}

	private boolean areAllLocalTasks(List<ITask> tasksToMove) {
		for (ITask task : tasksToMove) {
			if (!(task instanceof LocalTask)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param data
	 *            string containing url and title separated by <quote>\n</quote>
	 */
	private AbstractTask createTaskFromUrl(String data) {
		if (!data.startsWith("http://") && !data.startsWith("https://")) { //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}

		String[] urlTransfer = data.split("\n"); //$NON-NLS-1$
		if (urlTransfer.length > 0) {
			String url = urlTransfer[0];
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getConnectorForRepositoryTaskUrl(url);
			if (connector != null) {
				// attempt to find task in task list
				String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(url);
				String taskId = connector.getTaskIdFromTaskUrl(url);
				AbstractTask task = TasksUiInternal.getTask(repositoryUrl, taskId, url);
				if (task != null) {
					return task;
				}
				if (repositoryUrl != null && taskId != null) {
					// attempt to open task in background
					// TODO: consider attaching a listener to OpenRepsitoryTaskJob to move task to drop target
					TasksUiInternal.openRepositoryTask(connector.getConnectorKind(), repositoryUrl, taskId);
				}
			} else {
				// create local task, using title of web page as a summary
				final String summary = Messages.TaskListDropAdapter__retrieving_from_URL_;
				final LocalTask newTask = TasksUiInternal.createNewLocalTask(summary);
				newTask.setUrl(url);
				AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(url) {
					@Override
					protected void titleRetrieved(final String pageTitle) {
						// make sure summary was not changed in the mean time 
						if (newTask.getSummary().equals(summary)) {
							newTask.setSummary(pageTitle);
							TasksUiInternal.getTaskList().notifyElementChanged(newTask);
						}
					}
				};
				job.schedule();
				return newTask;
			}
		}
		return null;
	}

	@Override
	public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
		fileTransfer = false;
		localTransfer = false;
		if (FileTransfer.getInstance().isSupportedType(transferType)) {
			fileTransfer = true;
			// TODO handle all files
			return false;
		} else if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			localTransfer = true;
			if (getCurrentTarget() instanceof UncategorizedTaskContainer || getCurrentTarget() instanceof TaskCategory
					|| getCurrentTarget() instanceof UnmatchedTaskContainer
					|| getCurrentTarget() instanceof ScheduledTaskContainer) {
				return true;
			} else if (getCurrentTarget() instanceof ITaskContainer
					&& (getCurrentLocation() == ViewerDropAdapter.LOCATION_AFTER || getCurrentLocation() == ViewerDropAdapter.LOCATION_BEFORE)) {
				return true;
			} else if (getCurrentTarget() instanceof LocalTask && getCurrentLocation() == ViewerDropAdapter.LOCATION_ON) {
				return true;
			} else {
				return false;
			}
		} else if (PlatformUtil.getUrlTransfer().isSupportedType(transferType)) {
			return true;
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}

}
