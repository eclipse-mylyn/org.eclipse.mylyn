/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jevgeni Holodkov - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.AbstractRetrieveTitleFromUrlJob;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.StateTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Unscheduled;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TaskDropListener.Operation;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves (added URL based task creation support)
 * @author Jevgeni Holodkov
 * @author Sam Davis
 */
public class TaskListDropAdapter extends ViewerDropAdapter {

	private boolean fileTransfer;

	private boolean localTransfer;

	public TaskListDropAdapter(Viewer viewer) {
		super(viewer);
		setFeedbackEnabled(true);
	}

	@Override
	public boolean performDrop(final Object data) {
		List<ITask> tasksToMove = new ArrayList<ITask>();
		if (localTransfer) {
			ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
			List<ITask> tasks = TasksUiInternal.getTasksFromSelection(selection);
			tasksToMove.addAll(tasks);
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
			if (currentTarget instanceof ITask && getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
					&& getCurrentOperation() != DND.DROP_MOVE) {
				TasksUiInternal.getTaskDropHandler().loadTaskDropListeners();
				Operation operation = (getCurrentOperation() == DND.DROP_COPY) ? Operation.COPY : Operation.LINK;
				TasksUiInternal.getTaskDropHandler().fireTaskDropped(tasksToMove, (ITask) currentTarget, operation);
			} else {
				for (ITask task : tasksToMove) {
					if (currentTarget instanceof UncategorizedTaskContainer) {
						moveTask(task, (UncategorizedTaskContainer) currentTarget);
					} else if (currentTarget instanceof TaskCategory) {
						moveTask(task, (TaskCategory) currentTarget);
					} else if (currentTarget instanceof UnmatchedTaskContainer) {
						if (((UnmatchedTaskContainer) currentTarget).getRepositoryUrl().equals(task.getRepositoryUrl())) {
							moveTask(task, (AbstractTaskCategory) currentTarget);
						}
					} else if (currentTarget instanceof ITask) {
						ITask targetTask = (ITask) currentTarget;
						TaskListView view = TaskListView.getFromActivePerspective();
						if ((getCurrentLocation() == LOCATION_BEFORE || getCurrentLocation() == LOCATION_AFTER)
								&& view != null && view.isScheduledPresentation()) {
							if (targetTask instanceof AbstractTask) {
								DateRange targetDate = ((AbstractTask) targetTask).getScheduledForDate();
								TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task, targetDate);
							}
						} else {
							AbstractTaskCategory targetCategory = null;
							// TODO: TaskCategory only used what about AbstractTaskCategory descendants?
							ITaskContainer container = TaskCategory.getParentTaskCategory(targetTask);
							if (container instanceof TaskCategory || container instanceof UncategorizedTaskContainer) {
								targetCategory = (AbstractTaskCategory) container;
							} else if (container instanceof UnmatchedTaskContainer) {
								if (((UnmatchedTaskContainer) container).getRepositoryUrl().equals(
										task.getRepositoryUrl())) {
									targetCategory = (AbstractTaskCategory) container;
								}
							}
							if (targetCategory != null) {
								moveTask(task, targetCategory);
							}
						}
					} else if (currentTarget instanceof ScheduledTaskContainer) {
						ScheduledTaskContainer container = (ScheduledTaskContainer) currentTarget;
						if (container instanceof Unscheduled) {
							TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task, null);
						} else if (isValidTarget(container)) {
							TasksUiPlugin.getTaskActivityManager().setScheduledFor((AbstractTask) task,
									container.getDateRange());
						}
					} else if (currentTarget == null) {
						moveTask(task, TasksUiPlugin.getTaskList().getDefaultCategory());
					}
				}
			}
		}

		if (tasksToMove.size() == 1) {
			getViewer().setSelection(new StructuredSelection(tasksToMove.get(0)));
		}
		return true;
	}

	private boolean isValidTarget(ScheduledTaskContainer container) {
		// ignore incoming, outgoing, completed
		return container instanceof Unscheduled || !(container instanceof StateTaskContainer);
	}

	private void moveTask(ITask task, AbstractTaskContainer container) {
		if (!isUnsubmittedTask(task)) {
			TasksUiInternal.getTaskList().addTask(task, container);
		}
	}

	private boolean isUnsubmittedTask(ITask task) {
		if (task instanceof AbstractTask) {
			for (AbstractTaskContainer parent : ((AbstractTask) task).getParentContainers()) {
				if (parent instanceof UnsubmittedTaskContainer) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean areAllLocalTasks(List<ITask> tasksToMove) {
		for (ITask task : tasksToMove) {
			if (!(task instanceof LocalTask) || isUnsubmittedTask(task)) {
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
					TasksUiInternal.openRepositoryTask(connector.getConnectorKind(), repositoryUrl, taskId, null, 0);
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
			Object target = getCurrentTarget();
			if (target instanceof UncategorizedTaskContainer || target instanceof TaskCategory
					|| target instanceof UnmatchedTaskContainer
					|| (target instanceof ScheduledTaskContainer && isValidTarget((ScheduledTaskContainer) target))) {
				return true;
			} else if (target instanceof ITaskContainer
					&& (getCurrentLocation() == ViewerDropAdapter.LOCATION_AFTER || getCurrentLocation() == ViewerDropAdapter.LOCATION_BEFORE)) {
				return true;
			} else if (target instanceof LocalTask && getCurrentLocation() == ViewerDropAdapter.LOCATION_ON) {
				return true;
			} else if (target instanceof ITask && getCurrentLocation() == ViewerDropAdapter.LOCATION_ON
					&& getCurrentOperation() != DND.DROP_MOVE) {
				return true;
			} else {
				return false;
			}
		} else if (URLTransfer.getInstance().isSupportedType(transferType)) {
			return true;
		}

		return TextTransfer.getInstance().isSupportedType(transferType);
	}
}
