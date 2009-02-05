/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonsUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.ICoreRunnable;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AutomaticRepositoryTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * @author Mik Kersten
 */
public class DeleteAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.delete"; //$NON-NLS-1$

	public DeleteAction() {
		setText(Messages.DeleteAction_Delete);
		setId(ID);
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
	}

	@Override
	public void run() {
		ISelection selection = TaskListView.getFromActivePerspective().getViewer().getSelection();
		doDelete(((IStructuredSelection) selection).toList());
	}

	protected void doDelete(final List<?> toDelete) {
		String elements = ""; //$NON-NLS-1$
		int i = 0;
		for (Object object : toDelete) {
			if (object instanceof UnmatchedTaskContainer) {
				continue;
			}

			i++;
			if (i < 20) {
				// TODO this action should be based on the action enablement and check if the container is user managed or not
				if (object instanceof IRepositoryElement) {
					elements += "    " + ((IRepositoryElement) object).getSummary() + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else {
				elements += "..."; //$NON-NLS-1$
				break;
			}
		}

		String message;

		if (toDelete.size() == 1) {
			Object object = toDelete.get(0);
			if (object instanceof ITask) {
				if (((AbstractTask) object).isLocal()) {
					message = Messages.DeleteAction_Permanently_delete_the_task_listed_below;
				} else {
					message = Messages.DeleteAction_Delete_the_planning_information_and_context_for_the_repository_task;
				}
			} else if (object instanceof TaskCategory) {
				message = Messages.DeleteAction_Permanently_delete_the_category;
			} else if (object instanceof IRepositoryQuery) {
				message = Messages.DeleteAction_Permanently_delete_the_query;
			} else if (object instanceof UnmatchedTaskContainer) {
				message = Messages.DeleteAction_Delete_the_planning_information_and_context_of_all_unmatched_tasks;
			} else if (object instanceof UnsubmittedTaskContainer) {
				message = Messages.DeleteAction_Delete_all_of_the_unsubmitted_tasks;
			} else {
				message = Messages.DeleteAction_Permanently_delete_the_element_listed_below;
			}
		} else {
			message = Messages.DeleteAction_Delete_the_elements_listed_below;
		}

		message += "\n\n" + elements; //$NON-NLS-1$

		if (toDelete.isEmpty()) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.DeleteAction_Delete_failed, Messages.DeleteAction_Nothing_selected);
		} else {
			boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getShell(), Messages.DeleteAction_Confirm_Delete, message);
			if (deleteConfirmed) {
				ICoreRunnable op = new ICoreRunnable() {
					public void run(IProgressMonitor monitor) throws CoreException {
						try {
							monitor.beginTask(Messages.DeleteAction_Delete_in_progress, IProgressMonitor.UNKNOWN);
							prepareDeletion(toDelete);
							TasksUiPlugin.getTaskList().run(new ITaskListRunnable() {
								public void execute(IProgressMonitor monitor) throws CoreException {
									performDeletion(toDelete);
								}
							}, monitor);
						} finally {
							monitor.done();
						}
					}
				};
				try {
					CommonsUiUtil.runInUi(op, null);
				} catch (CoreException e) {
					Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
							"Problems encountered deleting task list elements: {0}", e.getMessage()), e); //$NON-NLS-1$
					TasksUiInternal.logAndDisplayStatus(
							Messages.DeleteTaskRepositoryAction_Delete_Task_Repository_Failed, status);
				} catch (OperationCanceledException e) {
					// canceled
				}
			}
		}
	}

	public static void prepareDeletion(Collection<?> toDelete) {
		for (Object selectedObject : toDelete) {
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) selectedObject;
				TasksUi.getTaskActivityManager().deactivateTask(task);
				TasksUiInternal.closeTaskEditorInAllPages(task, false);
			} else if (selectedObject instanceof AutomaticRepositoryTaskContainer) {
				// support both the unmatched and the unsubmitted
				if (toDelete.size() == 1) {
					prepareDeletion(((AutomaticRepositoryTaskContainer) selectedObject).getChildren());
				}
			}
		}
	}

	public static void performDeletion(Collection<?> toDelete) {
		for (Object selectedObject : toDelete) {
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) selectedObject;
				TasksUiInternal.getTaskList().deleteTask(task);
				try {
					TasksUiPlugin.getTaskDataManager().deleteTaskData(task);
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to delete task data", //$NON-NLS-1$
							e));
				}
				ContextCore.getContextManager().deleteContext(task.getHandleIdentifier());
			} else if (selectedObject instanceof IRepositoryQuery) {
				TasksUiInternal.getTaskList().deleteQuery((RepositoryQuery) selectedObject);
			} else if (selectedObject instanceof TaskCategory) {
				TasksUiInternal.getTaskList().deleteCategory((TaskCategory) selectedObject);
			} else if (selectedObject instanceof AutomaticRepositoryTaskContainer) {
				// support both the unmatched and the unsubmitted
				if (toDelete.size() == 1) {
					// loop to ensure that all subtasks are deleted as well
					performDeletion(((AutomaticRepositoryTaskContainer) selectedObject).getChildren());
				}
			}
		}
	}

}
