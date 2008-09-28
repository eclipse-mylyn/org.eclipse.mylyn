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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.QueryImportAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Unscheduled;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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
// TODO 3.1 rename to TaskListDropTargetListener
public class TaskListDropAdapter extends ViewerDropAdapter {

	private AbstractTask newTask = null;

	private TransferData currentTransfer;

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
		if (data == null) {
			return false;
		}

		Object currentTarget = getCurrentTarget();
		List<ITask> tasksToMove = new ArrayList<ITask>();
		if (isUrl(data) && createTaskFromUrl(data)) {
			tasksToMove.add(newTask);
		} else if (TaskTransfer.getInstance().isSupportedType(currentTransfer) && data instanceof ITask[]) {
			ITask[] tasks = (ITask[]) data;
			for (ITask task : tasks) {
				if (task != null) {
					tasksToMove.add(task);
				}
			}
		} else if (data instanceof String && createTaskFromString((String) data)) {
			tasksToMove.add(newTask);
		} else if (FileTransfer.getInstance().isSupportedType(currentTransfer)) {
			// transfer the context if the target is a Task
//					if (getCurrentTarget() instanceof ITask) {
//						final AbstractTask targetTask = (AbstractTask) getCurrentTarget();
//						final String[] names = (String[]) data;
//						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//
//							public void run() {
//								boolean confirmed = MessageDialog.openConfirm(getViewer().getControl().getShell(),
//										"Task Import", "Overwrite the context of the target task with the source's?");
//								if (confirmed) {
//									String path = names[0];
//									File file = new File(path);
//									boolean succeeded = ContextCore.getContextStore().copyContext(file,
//											targetTask.getHandleIdentifier());
//									if (succeeded) {
//										new TaskActivateAction().run(targetTask);
//									}
//								}
//							}
//						});
//					} else {
			// otherwise it is queries or tasks
			final String[] names = (String[]) data;
			for (String path : names) {
				final File file = new File(path);
				final List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
				final Set<TaskRepository> repositories = new HashSet<TaskRepository>();
				final List<AbstractTask> readTasks = TasksUiPlugin.getTaskListManager().getTaskListWriter().readTasks(
						file);
				if (file.isFile()) {
					List<RepositoryQuery> readQueries;
					try {
						readQueries = TasksUiPlugin.getTaskListManager().getTaskListWriter().readQueries(file);
						if (readQueries.size() > 0) {
							queries.addAll(readQueries);
							repositories.addAll(TasksUiPlugin.getTaskListManager()
									.getTaskListWriter()
									.readRepositories(file));
						} else {
							repositories.addAll(TasksUiPlugin.getTaskListManager()
									.getTaskListWriter()
									.readRepositories(file));
						}
					} catch (IOException e) {
						StatusHandler.log(new Status(IStatus.WARNING, TasksUiPlugin.ID_PLUGIN, "The specified file \""
								+ file.getName()
								+ "\" is not an exported query. Please, check that you have provided the correct file."));
//						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//							public void run() {
//								MessageDialog.openError(null, "Query Import Error",
//										"The specified file is not an exported query. Please, check that you have provided the correct file.");
//							}
//						});
					}
				}

				// FIXME: remove async exec
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						// TODO: we should consider batching this up
						if (queries.size() > 0) {
							new QueryImportAction().importQueries(queries, repositories, getViewer().getControl()
									.getShell());
						} else {
							TasksUiInternal.importTasks(readTasks, repositories, file, getViewer().getControl()
									.getShell());
						}
					}
				});
			}
		}

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
					TasksUiInternal.getTaskList().addTask(newTask, TasksUiPlugin.getTaskList().getDefaultCategory());
				}
			}
		}

		// Make new task the current selection in the view
		if (newTask != null) {
			StructuredSelection ss = new StructuredSelection(newTask);
			getViewer().setSelection(ss);
			//getViewer().refresh();
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
	 * @return true if string is a http(s) url
	 */
	public boolean isUrl(Object data) {
		String uri = "";
		if (data instanceof String) {
			uri = (String) data;
			if ((uri.startsWith("http://") || uri.startsWith("https://"))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param data
	 *            string containing url and title separated by <quote>\n</quote>
	 * @return true if task succesfully created, false otherwise
	 */
	public boolean createTaskFromUrl(Object data) {
		if (!(data instanceof String)) {
			return false;
		}

		String[] urlTransfer = ((String) data).split("\n");

		String url = "";
		String urlTitle = "<retrieving from URL>";

		if (urlTransfer.length > 0) {
			url = urlTransfer[0];
		} else {
			return false;
		}

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getConnectorForRepositoryTaskUrl(
				url);
		if (connector != null) {
			String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(url);
			String id = connector.getTaskIdFromTaskUrl(url);
			if (repositoryUrl == null || id == null) {
				return false;
			}
			for (TaskRepository repository : TasksUi.getRepositoryManager().getRepositories(
					connector.getConnectorKind())) {
				if (repository.getRepositoryUrl().equals(repositoryUrl)) {
					try {
						newTask = (AbstractTask) TasksUiInternal.createTask(repository, id, new NullProgressMonitor());
						TasksUiInternal.refreshAndOpenTaskListElement(newTask);
						return true;
					} catch (CoreException e) {
						StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not create task", e));
						return false;
					}
				}
			}
			return false;
		} else {
			// Removed in order to default to retrieving title from url rather
			// than
			// accepting what was sent by the brower's DnD code. (see bug
			// 114401)
			// If a Title is provided, use it.
			// if (urlTransfer.length > 1) {
			// urlTitle = urlTransfer[1];
			// }
			// if (urlTransfer.length < 2) { // no title provided
			// retrieveTaskDescription(url);
			// }
			retrieveTaskDescription(url);

			newTask = TasksUiInternal.createNewLocalTask(urlTitle);
			if (newTask == null) {
				return false;
			}
			newTask.setUrl(url);
			TasksUiUtil.openTask(newTask);
			return true;
		}
	}

	public boolean createTaskFromString(String title) {
		//newTask = new Task(TasksUiPlugin.getTaskListManager().genUniqueTaskHandle(), title);
		newTask = TasksUiInternal.createNewLocalTask(title);

		if (newTask == null) {
			return false;
		} else {
			//newTask.setPriority(Task.PriorityLevel.P3.toString());
			TasksUiUtil.openTask(newTask);
			return true;
		}
	}

	@Override
	public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
		currentTransfer = transferType;

		if (FileTransfer.getInstance().isSupportedType(currentTransfer)) {
			// handle all files
			return true;
		} else if (TaskTransfer.getInstance().isSupportedType(currentTransfer)) {
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
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}

	/**
	 * Attempts to set the task pageTitle to the title from the specified url
	 */
	protected void retrieveTaskDescription(final String url) {
		try {
			AbstractRetrieveTitleFromUrlJob job = new AbstractRetrieveTitleFromUrlJob(url) {
				@Override
				protected void titleRetrieved(final String pageTitle) {
					newTask.setSummary(pageTitle);
					TasksUiInternal.getTaskList().notifyElementChanged(newTask);
				}
			};
			job.schedule();
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not open task web page", e));
		}
	}
}
