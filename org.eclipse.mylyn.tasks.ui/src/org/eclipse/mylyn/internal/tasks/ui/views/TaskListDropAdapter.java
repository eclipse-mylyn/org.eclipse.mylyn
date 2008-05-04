/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.RetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.ui.TaskListModifyOperation;
import org.eclipse.mylyn.internal.tasks.ui.TaskTransfer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.actions.QueryImportAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskActivateAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.TaskImportAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Mik Kersten
 * @author Rob Elves (added URL based task creation support)
 * @author Jevgeni Holodkov
 */
// API 3.0 rename to TaskListDropTargetListener
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

		TaskListModifyOperation modOperation = new TaskListModifyOperation() {

			@Override
			protected void operations(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException {
				Object currentTarget = getCurrentTarget();
				List<AbstractTask> tasksToMove = new ArrayList<AbstractTask>();
				if (isUrl(data) && createTaskFromUrl(data)) {
					tasksToMove.add(newTask);
				} else if (TaskTransfer.getInstance().isSupportedType(currentTransfer)
						&& data instanceof AbstractTask[]) {
					AbstractTask[] tasks = (AbstractTask[]) data;
					for (AbstractTask task : tasks) {
						if (task != null) {
							tasksToMove.add(task);
						}
					}
				} else if (data instanceof String && createTaskFromString((String) data)) {
					tasksToMove.add(newTask);
				} else if (FileTransfer.getInstance().isSupportedType(currentTransfer)) {
					// transfer the context if the target is a Task
					if (getCurrentTarget() instanceof AbstractTask) {
						AbstractTask targetTask = (AbstractTask) getCurrentTarget();
						final String[] names = (String[]) data;
						boolean confirmed = MessageDialog.openConfirm(getViewer().getControl().getShell(),
								ITasksUiConstants.TITLE_DIALOG,
								"Overwrite the context of the target task with the source's?");
						if (confirmed) {
							String path = names[0];
							File file = new File(path);
							if (ContextCore.getContextManager().isValidContextFile(file)) {
								ContextCore.getContextManager().copyContext(targetTask.getHandleIdentifier(), file);
								new TaskActivateAction().run(targetTask);
							}
						}
					} else {
						// otherwise it is queries or tasks
						final String[] names = (String[]) data;
						List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
						Map<AbstractTask, InteractionContext> taskContexts = new HashMap<AbstractTask, InteractionContext>();
						Set<TaskRepository> repositories = new HashSet<TaskRepository>();

						for (String path : names) {
							File file = new File(path);
							if (file.isFile()) {
								List<AbstractRepositoryQuery> readQueries;
								try {
									readQueries = TasksUiPlugin.getTaskListManager().getTaskListWriter().readQueries(
											file);
									if (readQueries.size() > 0) {
										queries.addAll(readQueries);
										repositories.addAll(TasksUiPlugin.getTaskListManager()
												.getTaskListWriter()
												.readRepositories(file));
									} else {
										List<AbstractTask> readTasks = TasksUiPlugin.getTaskListManager()
												.getTaskListWriter()
												.readTasks(file);
										for (AbstractTask task : readTasks) {
											taskContexts.put(task, ContextCorePlugin.getContextManager().loadContext(
													task.getHandleIdentifier(), file));
										}
										repositories.addAll(TasksUiPlugin.getTaskListManager()
												.getTaskListWriter()
												.readRepositories(file));
									}
								} catch (IOException e) {
									PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
										public void run() {
											MessageDialog.openError(null, "Query Import Error",
													"The specified file is not an exported query. Please, check that you have provided the correct file.");
										}
									});
								}
							}

						}

						if (queries.size() > 0) {
							new QueryImportAction().importQueries(queries, repositories, getViewer().getControl()
									.getShell());
						} else {
							TaskImportAction action = new TaskImportAction();
							action.importTasks(taskContexts, repositories, getViewer().getControl().getShell());
							action.refreshTaskListView();
						}
					}
				}

				if (currentTarget instanceof LocalTask
						&& areAllLocalTasks(tasksToMove)
						&& getCurrentLocation() == LOCATION_ON
						&& TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
								TasksUiPreferenceConstants.LOCAL_SUB_TASKS_ENABLED)) {
					for (AbstractTask task : tasksToMove) {
						if (!task.contains(((LocalTask) currentTarget).getHandleIdentifier())) {
							TasksUi.getTaskListManager().getTaskList().addTask(task, (LocalTask) currentTarget);
						}
					}
				} else {
					for (AbstractTask task : tasksToMove) {
						if (currentTarget instanceof UncategorizedTaskContainer) {
							TasksUi.getTaskListManager().getTaskList().addTask(task,
									(UncategorizedTaskContainer) currentTarget);
						} else if (currentTarget instanceof TaskCategory) {
							TasksUi.getTaskListManager().getTaskList().addTask(task, (TaskCategory) currentTarget);
						} else if (currentTarget instanceof UnmatchedTaskContainer) {
							if (((UnmatchedTaskContainer) currentTarget).getRepositoryUrl().equals(
									task.getRepositoryUrl())) {
								TasksUi.getTaskListManager().getTaskList().addTask(task,
										(AbstractTaskCategory) currentTarget);
							}
						} else if (currentTarget instanceof AbstractTask) {
							AbstractTask targetTask = (AbstractTask) currentTarget;
							AbstractTaskCategory targetCategory = null;
							// TODO: TaskCategory only used what about AbstractTaskCategory descendants?
							AbstractTaskContainer container = TaskCategory.getParentTaskCategory(targetTask);
							if (container instanceof TaskCategory || container instanceof UncategorizedTaskContainer) {
								targetCategory = (AbstractTaskCategory) container;
							} else if (container instanceof UnmatchedTaskContainer) {
								if (((UnmatchedTaskContainer) container).getRepositoryUrl().equals(
										task.getRepositoryUrl())) {
									targetCategory = (AbstractTaskCategory) container;
								}
							}
							if (targetCategory != null) {
								TasksUi.getTaskListManager().getTaskList().addTask(task, targetCategory);
							}
						} else if (currentTarget instanceof ScheduledTaskContainer) {
							ScheduledTaskContainer container = (ScheduledTaskContainer) currentTarget;
							Calendar newSchedule = TaskActivityUtil.getCalendar();
							newSchedule.setTimeInMillis(container.getStart().getTimeInMillis());
							TaskActivityUtil.snapEndOfWorkDay(newSchedule);
							TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, newSchedule.getTime(),
									container.isCaptureFloating());
						} else if (currentTarget == null) {
							TasksUi.getTaskListManager().getTaskList().addTask(newTask,
									TasksUiPlugin.getTaskListManager().getTaskList().getDefaultCategory());
						}
					}
				}

				// Make new task the current selection in the view
				if (newTask != null) {
					StructuredSelection ss = new StructuredSelection(newTask);
					getViewer().setSelection(ss);
					getViewer().refresh();
				}

			}
		};

		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		try {
			service.run(true, true, modOperation);
		} catch (InterruptedException e) {
			// ignore
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Drop failed", e.getCause()));
		}

		return true;

	}

	private boolean areAllLocalTasks(List<AbstractTask> tasksToMove) {
		for (AbstractTask task : tasksToMove) {
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
	 * 		string containing url and title separated by <quote>\n</quote>
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
						newTask = TasksUiUtil.createTask(repository, id, new NullProgressMonitor());
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

			// NOTE: setting boolean param as false so that we go directly to
			// the
			// browser tab as with a previously-created task
			TasksUiUtil.openEditor(newTask, false);
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
			TasksUiUtil.openEditor(newTask, false);
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
			} else if (getCurrentTarget() instanceof AbstractTaskContainer
					&& (getCurrentLocation() == ViewerDropAdapter.LOCATION_AFTER || getCurrentLocation() == ViewerDropAdapter.LOCATION_BEFORE)) {
				return true;
			} else if (getCurrentTarget() instanceof LocalTask
					&& getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
					&& TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
							TasksUiPreferenceConstants.LOCAL_SUB_TASKS_ENABLED)) {
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
			RetrieveTitleFromUrlJob job = new RetrieveTitleFromUrlJob(url) {
				@Override
				protected void titleRetrieved(final String pageTitle) {
					newTask.setSummary(pageTitle);
					TasksUi.getTaskListManager().getTaskList().notifyTaskChanged(newTask, false);
				}
			};
			job.schedule();
		} catch (RuntimeException e) {
			// FIXME what exception is caught here?
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not open task web page", e));
		}
	}
}
