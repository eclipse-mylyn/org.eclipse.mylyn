/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.commons.core.ICoreRunnable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AutomaticRepositoryTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.sync.DeleteTasksJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

/**
 * @author Mik Kersten
 */
public class DeleteAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.delete"; //$NON-NLS-1$

	public DeleteAction() {
		super(Messages.DeleteAction_Delete);
		setId(ID);
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
	}

	@Override
	public void run() {
		doDelete(getStructuredSelection().toList());
	}

	protected void doDelete(final List<?> toDelete) {

		boolean allLocalTasks = true;
		boolean allSupportRepositoryDeletion = true;
		boolean allElementsAreTasks = true;

		// determine what repository elements are to be deleted so that we can present the correct message to the user
		for (Object object : toDelete) {
			if (object instanceof ITask) {
				ITask task = (ITask) object;
				AbstractRepositoryConnector repositoryConnector = TasksUi
						.getRepositoryConnector(task.getConnectorKind());
				TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
						task.getRepositoryUrl());
				if (repository != null && repositoryConnector != null) {
					allSupportRepositoryDeletion &= repositoryConnector.canDeleteTask(repository, task);
				} else {
					allSupportRepositoryDeletion = false;
				}
				allLocalTasks &= task instanceof LocalTask;
			} else {
				allElementsAreTasks = false;
			}
		}

		String elements = buildElementListString(toDelete);
		String message = buildMessage(toDelete, elements, allElementsAreTasks);

		if (toDelete.isEmpty()) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.DeleteAction_Delete_failed, Messages.DeleteAction_Nothing_selected);
		} else {
			boolean deleteConfirmed = false;
			boolean deleteOnServer = false;

			final boolean allTasksDeletable = allSupportRepositoryDeletion;

			if (allLocalTasks || !allElementsAreTasks) {
				deleteConfirmed = MessageDialog.openQuestion(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.DeleteAction_Delete_Tasks, message);
			} else {
				String toggleMessage = Messages.DeleteAction_Also_delete_from_repository_X;
				if (allTasksDeletable) {
					toggleMessage = NLS.bind(toggleMessage, ""); //$NON-NLS-1$
				} else {
					toggleMessage = NLS.bind(toggleMessage, Messages.DeleteAction_Not_supported);
				}
				final MessageDialogWithToggle dialog = new MessageDialogWithToggle(WorkbenchUtil.getShell(),
						Messages.DeleteAction_Delete_Tasks, null, message, MessageDialog.QUESTION,
						new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL }, 0, toggleMessage,
						false) {
					@Override
					protected Control createContents(Composite parent) {
						Control createdControl = super.createContents(parent);
						getToggleButton().setEnabled(allTasksDeletable);
						return createdControl;
					}
				};

				deleteConfirmed = dialog.open() == IDialogConstants.YES_ID;
				deleteOnServer = dialog.getToggleState() && allTasksDeletable;
			}

			if (deleteConfirmed) {
				deleteElements(toDelete, deleteOnServer);
			}
		}
	}

	private void deleteElements(final List<?> toDelete, final boolean deleteOnServer) {
		ICoreRunnable op = new ICoreRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					monitor.beginTask(Messages.DeleteAction_Delete_in_progress, IProgressMonitor.UNKNOWN);
					prepareDeletion(toDelete);
					TasksUiPlugin.getTaskList().run(new ITaskListRunnable() {
						public void execute(IProgressMonitor monitor) throws CoreException {
							performDeletion(toDelete);
							if (deleteOnServer) {
								performDeletionFromServer(toDelete);
							}
						}

					}, monitor);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			WorkbenchUtil.runInUi(op, null);
		} catch (CoreException e) {
			Status status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					NLS.bind("Problems encountered deleting task list elements: {0}", e.getMessage()), e); //$NON-NLS-1$
			TasksUiInternal.logAndDisplayStatus(Messages.DeleteTaskRepositoryAction_Delete_Task_Repository_Failed,
					status);
		} catch (OperationCanceledException e) {
			// canceled
		}
	}

	private String buildMessage(final List<?> toDelete, String elements, boolean allElementsAreTasks) {
		String message;

		if (toDelete.size() == 1) {
			Object object = toDelete.get(0);
			if (object instanceof ITask) {
				if ((AbstractTask) object instanceof LocalTask) {
					message = Messages.DeleteAction_Permanently_delete_from_task_list;
				} else {
					message = Messages.DeleteAction_Delete_task_from_task_list_context_planning_deleted;
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
			if (allElementsAreTasks) {
				message = Messages.DeleteAction_Delete_tasks_from_task_list_context_planning_deleted;
			} else {
				message = Messages.DeleteAction_Delete_elements_from_task_list_context_planning_deleted;
			}
		}
		message += "\n\n" + elements; //$NON-NLS-1$
		return message;
	}

	private String buildElementListString(final List<?> toDelete) {
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
		return elements;
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
				TasksUiInternal.deleteTask(task);
			} else if (selectedObject instanceof IRepositoryQuery) {
				TasksUiInternal.getTaskList().deleteQuery((RepositoryQuery) selectedObject);
			} else if (selectedObject instanceof TaskCategory) {
				TasksUiInternal.getTaskList().deleteCategory((TaskCategory) selectedObject);
			} else if (selectedObject instanceof AutomaticRepositoryTaskContainer) {
				// support both the unmatched and the unsubmitted
				if (toDelete.size() == 1) {
					// loop to ensure that all subtasks are deleted as well
					for (int i = 0; i < 5; i++) {
						Collection<ITask> children = ((AutomaticRepositoryTaskContainer) selectedObject).getChildren();
						if (children.isEmpty()) {
							break;
						}
						performDeletion(children);
					}
				}
			}
		}
	}

	private void performDeletionFromServer(List<?> toDelete) {
		List<ITask> tasksToDelete = new ArrayList<ITask>();
		for (Object element : toDelete) {
			if (element instanceof ITask) {
				tasksToDelete.add((ITask) element);
			}

		}
		final DeleteTasksJob deleteTaskJob = new DeleteTasksJob(Messages.DeleteAction_Deleting_tasks_from_repositories,
				tasksToDelete, TasksUi.getRepositoryManager());
		deleteTaskJob.setPriority(Job.INTERACTIVE);
		deleteTaskJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (deleteTaskJob.getStatus() != null && !deleteTaskJob.getStatus().isOK()) {
					StatusHandler.log(deleteTaskJob.getStatus());
				}
			}
		});
		deleteTaskJob.setUser(true);
		deleteTaskJob.schedule();
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		List<?> elements = (selection).toList();
		for (Object object : elements) {
			if (object instanceof UncategorizedTaskContainer) {
				return false;
			}
		}
		return true;
	}

}
