/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import static org.eclipse.mylyn.internal.tasks.ui.migrator.TaskPredicates.isQueryForRepository;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.DeleteAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskDataSnapshotOperation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class ConnectorMigrationUi {

	private static final String MIGRATE = "migrate"; //$NON-NLS-1$

	private static final String COMLETE_MIGRATION = "complete-migration"; //$NON-NLS-1$

	private final TaskListView taskListView;

	private final TaskListBackupManager backupManager;

	private final TasksState tasksState;

	public ConnectorMigrationUi(TaskListView taskListView, TaskListBackupManager backupManager, TasksState tasksState) {
		this.taskListView = taskListView;
		this.backupManager = backupManager;
		this.tasksState = tasksState;
	}

	/**
	 * @noextend This class is not intended to be subclassed by clients.
	 * @noinstantiate This class is not intended to be instantiated by clients.
	 */
	protected class CompleteMigrationJob extends Job {
		private boolean finishedCompleteMigrationWizard;

		private final ConnectorMigrator migrator;

		private CompleteMigrationJob(String name, ConnectorMigrator migrator) {
			super(name);
			this.migrator = migrator;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (!finishedCompleteMigrationWizard) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (taskListView != null && taskListView.getServiceMessageControl() != null) {
							ServiceMessage message = new ServiceMessage("") { //$NON-NLS-1$

								@Override
								public boolean openLink(String link) {
									if (link.equals(COMLETE_MIGRATION)) {
										if (createCompleteMigrationWizard(migrator).open() == Window.OK) {
											finishedCompleteMigrationWizard = true;
											return true;
										}
									}
									return false;
								}
							};
							message.setTitle(Messages.ConnectorMigrationUi_Connector_Migration);
							message.setDescription(NLS.bind(Messages.ConnectorMigrator_complete_migration_prompt_title,
									COMLETE_MIGRATION));
							message.setImage(Dialog.DLG_IMG_MESSAGE_WARNING);
							taskListView.getServiceMessageControl().setMessage(message);
						}
					}
				});
				schedule(TimeUnit.MILLISECONDS.convert(getCompletionPromptFrequency(), TimeUnit.SECONDS));
			}
			return Status.OK_STATUS;
		}

		public void dispose() {
			finishedCompleteMigrationWizard = true;
		}
	}

	public void promptToMigrate(final ConnectorMigrator migrator) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (taskListView != null && taskListView.getServiceMessageControl() != null) {
					ServiceMessage message = new ServiceMessage("") { //$NON-NLS-1$
						@Override
						public boolean openLink(String link) {
							if (link.equals(MIGRATE)) {
								if (createMigrationWizard(migrator).open() == Window.OK) {
									createPromptToCompleteMigrationJob(migrator).schedule();
									return true;
								}
							}
							return false;
						}

					};
					message.setTitle(Messages.ConnectorMigrationUi_End_of_Connector_Support);
					message.setDescription(
							NLS.bind(Messages.ConnectorMigrator_complete_migration_prompt_message, MIGRATE));
					message.setImage(Dialog.DLG_IMG_MESSAGE_INFO);
					taskListView.getServiceMessageControl().setMessage(message);
				}
			}
		});
	}

	protected Job createPromptToCompleteMigrationJob(ConnectorMigrator migrator) {
		Job job = new CompleteMigrationJob(Messages.ConnectorMigrationUi_Complete_Connector_Migration_Prompt, migrator);
		job.setUser(false);
		job.setSystem(true);
		return job;
	}

	protected WizardDialog createMigrationWizard(ConnectorMigrator migrator) {
		return createWizardDialog(new ConnectorMigrationWizard(migrator));
	}

	protected WizardDialog createCompleteMigrationWizard(ConnectorMigrator migrator) {
		return createWizardDialog(new CompleteConnectorMigrationWizard(migrator));
	}

	protected WizardDialog createWizardDialog(Wizard wizard) {
		WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
		dialog.create();
		dialog.setBlockOnOpen(true);
		return dialog;
	}

	/**
	 * @return the frequency in seconds with which the completion prompt will be shown
	 */
	protected int getCompletionPromptFrequency() {
		return 5;
	}

	public void warnOfValidationFailure(final List<TaskRepository> failedValidation) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				String repositoryList = Joiner.on("\n") //$NON-NLS-1$
						.join(Iterables.transform(failedValidation, repositoryToLabel()));
				MessageDialog.openWarning(WorkbenchUtil.getShell(), Messages.ConnectorMigrationUi_Validation_Failed,
						NLS.bind(Messages.ConnectorMigrationWizard_validation_failed, repositoryList));
			}
		});
	}

	private static Function<TaskRepository, String> repositoryToLabel() {
		return new Function<TaskRepository, String>() {
			@Override
			public String apply(TaskRepository repository) {
				return repository.getRepositoryLabel();
			}
		};
	}

	protected void backupTaskList(final IProgressMonitor monitor) throws IOException {
		try {
			monitor.subTask(Messages.ConnectorMigrationUi_Backing_up_task_list);
			String backupFolder = TasksUiPlugin.getDefault().getBackupFolderPath();
			new File(backupFolder).mkdirs();
			String fileName = getBackupFileName(new Date());
			new TaskDataSnapshotOperation(backupFolder, fileName).run(new SubProgressMonitor(monitor, 1));
			// also take a snapshot because user might try to restore from snapshot
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					backupManager.backupNow(true);
				}
			});
			monitor.worked(1);
		} catch (InvocationTargetException e) {
			throw (IOException) e.getCause();
		}
	}

	protected String getBackupFileName(Date date) {
		// we use an underscore in the date format to prevent TaskListBackupManager from thinking this is one of its backups
		// and deleting it (TaskListBackupManager.MYLYN_BACKUP_REGEXP matches any string).
		return "connector-migration-" + new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(date) + ".zip"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Deletes the given tasks and repository, and all queries associated with the repository, while preserving the
	 * credentials of <code>newRepository</code>.
	 *
	 * @param newRepository
	 */
	protected void delete(final Set<ITask> tasks, final TaskRepository repository, final TaskRepository newRepository,
			IProgressMonitor monitor) {
		final Set<RepositoryQuery> queries = Sets.filter(tasksState.getTaskList().getQueries(),
				isQueryForRepository(repository));
		final UnsubmittedTaskContainer unsubmitted = tasksState.getTaskList()
				.getUnsubmittedContainer(repository.getRepositoryUrl());
		try {
			monitor.subTask(Messages.ConnectorMigrationUi_Deleting_old_repository_tasks_and_queries);
			try {
				DeleteAction.prepareDeletion(tasks);
				if (unsubmitted != null) {
					DeleteAction.prepareDeletion(unsubmitted.getChildren());
				}
				DeleteAction.prepareDeletion(queries);
			} catch (Exception e) {// in case an error happens closing editors, we still want to delete
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
			tasksState.getTaskList().run(new ITaskListRunnable() {
				@Override
				public void execute(IProgressMonitor monitor) throws CoreException {
					for (ITask task : tasks) {
						delete(task);
					}
					if (unsubmitted != null) {
						for (ITask task : unsubmitted.getChildren()) {
							delete(task);
						}
					}
					DeleteAction.performDeletion(queries);
					Map<AuthenticationType, AuthenticationCredentials> credentialsMap = new HashMap<>();
					for (AuthenticationType type : AuthenticationType.values()) {
						AuthenticationCredentials credentials = repository.getCredentials(type);
						if (credentials != null) {
							credentialsMap.put(type, credentials);
						}
					}
					tasksState.getRepositoryManager().removeRepository(repository);
					for (AuthenticationType type : credentialsMap.keySet()) {
						newRepository.setCredentials(type, credentialsMap.get(type),
								newRepository.getSavePassword(type));
					}
				}
			}, monitor);
			tasksState.getRepositoryModel().clear();
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, Messages.ConnectorMigrationUi_Error_deleting_task, e));
		}
	}

	/**
	 * Delete a task without deleting the context.
	 */
	protected void delete(ITask task) {
		tasksState.getTaskList().deleteTask(task);
		try {
			tasksState.getTaskDataManager().deleteTaskData(task);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to delete task data", e));//$NON-NLS-1$
		}
	}

	public void notifyMigrationComplete() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(WorkbenchUtil.getShell(), Messages.ConnectorMigrationUi_Connector_Migration_Complete,
						Messages.ConnectorMigrationUi_Connector_migration_completed_successfully_You_may_resume_using_the_task_list);
			}
		});
	}

}
