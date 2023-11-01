/*******************************************************************************
 * Copyright (c) 2004, 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Steve Elsemore - fix for bug 296963
 *     Atlassian - fix for bug 319699
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.identity.core.Account;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationScheduler;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationScheduler.Synchronizer;
import org.eclipse.mylyn.internal.tasks.core.util.TasksCoreUtil;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.OpenRepositoryTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.MultiRepositoryAwareWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizardInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.QueryWizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskHistory;
import org.eclipse.mylyn.tasks.core.data.TaskRevision;
import org.eclipse.mylyn.tasks.core.data.TaskRevision.Change;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.progress.IProgressConstants2;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class TasksUiInternal {

	private static SynchronizationScheduler synchronizationScheduler = new SynchronizationScheduler();

	private static final TaskDropHandler taskDropHandler = new TaskDropHandler();

	/**
	 * @deprecated use SWT.SHEET instead
	 */
	@Deprecated
	public static final int SWT_SHEET = 1 << 28;

	public static final String ID_MENU_ACTIVE_TASK = "org.eclipse.mylyn.tasks.ui.menus.activeTask"; //$NON-NLS-1$

	private static ObjectUndoContext undoContext;

	public static MultiRepositoryAwareWizard createNewTaskWizard(ITaskMapping taskSelection) {
		return new NewTaskWizardInternal(taskSelection);
	}

	/**
	 * get the connector discovery wizard command. Calling code should check {@link Command#isEnabled()} on return.
	 *
	 * @return the command, or null if it is not available.
	 */
	public static Command getConfiguredDiscoveryWizardCommand() {
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		final Command discoveryWizardCommand = service.getCommand("org.eclipse.mylyn.tasks.ui.discoveryWizardCommand"); //$NON-NLS-1$
		if (discoveryWizardCommand != null) {
			IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
					.getService(IHandlerService.class);
			EvaluationContext evaluationContext = createDiscoveryWizardEvaluationContext(handlerService);
			if (!discoveryWizardCommand.isEnabled()) {
				// update enabled state in case something has changed (ProxyHandler caches state)
				discoveryWizardCommand.setEnabled(evaluationContext);
			}
		}
		return discoveryWizardCommand;
	}

	public static EvaluationContext createDiscoveryWizardEvaluationContext(IHandlerService handlerService) {
		EvaluationContext evaluationContext = new EvaluationContext(handlerService.getCurrentState(), Platform.class);
		// must specify this variable otherwise the PlatformPropertyTester won't work
		evaluationContext.addVariable("platform", Platform.class); //$NON-NLS-1$
		return evaluationContext;
	}

	public static ImageDescriptor getPriorityImage(ITask task) {
		if (task.isCompleted()) {
			return CommonImages.COMPLETE;
		} else {
			return TasksUiImages.getImageDescriptorForPriority(PriorityLevel.fromString(task.getPriority()));
		}
	}

	public static List<TaskEditor> getActiveRepositoryTaskEditors() {
		List<TaskEditor> repositoryTaskEditors = new ArrayList<TaskEditor>();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IEditorReference[] editorReferences = window.getActivePage().getEditorReferences();
			for (IEditorReference editorReference : editorReferences) {
				try {
					if (editorReference.getEditorInput() instanceof TaskEditorInput) {
						TaskEditorInput input = (TaskEditorInput) editorReference.getEditorInput();
						if (input.getTask() != null) {
							IEditorPart editorPart = editorReference.getEditor(false);
							if (editorPart instanceof TaskEditor) {
								repositoryTaskEditors.add((TaskEditor) editorPart);
							}
						}
					}
				} catch (PartInitException e) {
					// ignore
				}
			}
		}
		return repositoryTaskEditors;
	}

	public static IProgressMonitor getUiMonitor(IProgressMonitor monitor) {
		return new ProgressMonitorWrapper(monitor) {
			@Override
			public void beginTask(final String name, final int totalWork) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getWrappedProgressMonitor().beginTask(name, totalWork);
					}
				});
			}

			@Override
			public void done() {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getWrappedProgressMonitor().done();
					}
				});
			}

			@Override
			public void subTask(final String name) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getWrappedProgressMonitor().subTask(name);
					}
				});
			}

			@Override
			public void worked(final int work) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						getWrappedProgressMonitor().worked(work);
					}
				});
			}
		};
	}

	public static void openEditor(TaskCategory category) {
		final String name = category.getSummary();
		InputDialog dialog = new InputDialog(WorkbenchUtil.getShell(), Messages.TasksUiInternal_Rename_Category_Title,
				Messages.TasksUiInternal_Rename_Category_Message, name, new IInputValidator() {
					public String isValid(String newName) {
						if (newName.trim().length() == 0 || newName.equals(name)) {
							return ""; //$NON-NLS-1$
						}
						Set<AbstractTaskCategory> categories = TasksUiPlugin.getTaskList().getCategories();
						for (AbstractTaskCategory category : categories) {
							if (newName.equals(category.getSummary())) {
								return Messages.TasksUiInternal_Rename_Category_Name_already_exists_Error;
							}
						}
						return null;
					}
				});
		if (dialog.open() == Window.OK) {
			TasksUiPlugin.getTaskList().renameContainer(category, dialog.getValue());
		}
	}

	public static void refreshAndOpenTaskListElement(IRepositoryElement element) {
		if (element instanceof ITask) {
			final AbstractTask task = (AbstractTask) element;

			if (task instanceof LocalTask) {
				TasksUiUtil.openTask(task);
			} else {
				String repositoryKind = task.getConnectorKind();
				final AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
						.getRepositoryConnector(repositoryKind);

				TaskRepository repository = TasksUi.getRepositoryManager()
						.getRepository(repositoryKind, task.getRepositoryUrl());
				if (repository == null) {
					displayStatus(Messages.TasksUiInternal_Failed_to_open_task, new Status(IStatus.ERROR,
							TasksUiPlugin.ID_PLUGIN, Messages.TasksUiInternal_No_repository_found));
					return;
				}

				if (connector != null) {
					boolean opened = false;
					if (TasksUiPlugin.getTaskDataManager().hasTaskData(task)) {
						opened = TasksUiUtil.openTask(task);
					}

					if (!opened) {
						if (connector.canSynchronizeTask(repository, task)) {
							// TODO consider moving this into the editor, i.e. have the editor refresh the task if task data is missing
							TasksUiInternal.synchronizeTask(connector, task, true, new JobChangeAdapter() {
								@Override
								public void done(IJobChangeEvent event) {
									PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
										public void run() {
											TasksUiUtil.openTask(task);
										}
									});
								}
							});
						} else {
							TasksUiUtil.openTask(task);
						}
					}
				}
			}
		} else if (element instanceof TaskCategory) {
			TasksUiInternal.openEditor((TaskCategory) element);
		} else if (element instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) element;
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(query.getConnectorKind());
			TasksUiInternal.openEditQueryDialog(connectorUi, query);
		}
	}

	public static TaskJob updateRepositoryConfiguration(final TaskRepository taskRepository) {
		synchronized (taskRepository) {
			taskRepository.setUpdating(true);
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(taskRepository.getConnectorKind());
		final TaskJob job = TasksUiInternal.getJobFactory()
				.createUpdateRepositoryConfigurationJob(connector, taskRepository, null);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				synchronized (taskRepository) {
					taskRepository.setUpdating(false);
				}
				if (job.getStatus() != null) {
					TasksUiInternal.asyncLogAndDisplayStatus(Messages.TasksUiInternal_Configuration_Refresh_Failed,
							job.getStatus());
				}
			}
		});
		job.schedule();
		return job;
	}

	private static void joinIfInTestMode(SynchronizationJob job) {
		// FIXME the client code should join the job
		if (CoreUtil.TEST_MODE) {
			try {
				job.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static final Job synchronizeQueries(AbstractRepositoryConnector connector, TaskRepository repository,
			Set<RepositoryQuery> queries, IJobChangeListener listener, boolean force) {
		Assert.isTrue(queries.size() > 0);

		TaskList taskList = TasksUiPlugin.getTaskList();
		for (RepositoryQuery query : queries) {
			query.setSynchronizing(true);
		}
		taskList.notifySynchronizationStateChanged(queries);

		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory()
				.createSynchronizeQueriesJob(connector, repository, queries);
		job.setUser(force);
		if (force) {
			// show the progress in the system task bar if this is a user job (i.e. forced)
			job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
		}
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force) {
			final RepositoryQuery query = queries.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (query.getStatus() != null) {
						TasksUiInternal.asyncLogAndDisplayStatus(Messages.TasksUiInternal_Query_Synchronization_Failed,
								query.getStatus());
					}
				}
			});
		}
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	/**
	 * For synchronizing a single query. Use synchronize(Set, IJobChangeListener) if synchronizing multiple queries at a
	 * time.
	 */
	public static final Job synchronizeQuery(AbstractRepositoryConnector connector, RepositoryQuery repositoryQuery,
			IJobChangeListener listener, boolean force) {
		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(repositoryQuery.getConnectorKind(), repositoryQuery.getRepositoryUrl());
		return synchronizeQueries(connector, repository, Collections.singleton(repositoryQuery), listener, force);
	}

	public static SynchronizationJob synchronizeAllRepositories(boolean force) {
		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory().createSynchronizeRepositoriesJob(null);
		job.setUser(force);
		if (force) {
			// show the progress in the system task bar if this is a user job (i.e. forced)
			job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
		}
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	public static void synchronizeRepositoryInBackground(final TaskRepository repository) {
		synchronizationScheduler.schedule(repository, new Synchronizer<SynchronizationJob>() {
			@Override
			public SynchronizationJob createJob() {
				return synchronizeRepositoryInternal(repository, false);
			}
		});
	}

	public static SynchronizationJob synchronizeRepository(TaskRepository repository, boolean force) {
		SynchronizationJob job = synchronizeRepositoryInternal(repository, force);
		synchronizationScheduler.cancel(repository);
		job.schedule();
		return job;
	}

	private static SynchronizationJob synchronizeRepositoryInternal(TaskRepository repository, boolean force) {
		SynchronizationJob job = TasksUiInternal.getJobFactory()
				.createSynchronizeRepositoriesJob(Collections.singleton(repository));
		// do not show in progress view by default
		job.setSystem(true);
		job.setUser(force);
		if (force) {
			// show the progress in the system task bar if this is a user job (i.e. forced)
			job.setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
		}
		job.setFullSynchronization(false);
		return job;
	}

	public static void synchronizeTaskInBackground(final AbstractRepositoryConnector connector, final ITask task) {
		synchronizationScheduler.schedule(task, new Synchronizer<Job>() {
			@Override
			public Job createJob() {
				SynchronizationJob job = TasksUiPlugin.getTaskJobFactory()
						.createSynchronizeTasksJob(connector, Collections.singleton(task));
				job.setUser(false);
				job.setSystem(true);
				return job;
			}
		});
	}

	/**
	 * Synchronize a single task. Note that if you have a collection of tasks to synchronize with this connector then
	 * you should call synchronize(Set<Set<AbstractTask> repositoryTasks, ...)
	 *
	 * @param listener
	 *            can be null
	 */
	public static Job synchronizeTask(AbstractRepositoryConnector connector, ITask task, boolean force,
			IJobChangeListener listener) {
		return synchronizeTasks(connector, Collections.singleton(task), force, listener);
	}

	/**
	 * @param listener
	 *            can be null
	 */
	public static Job synchronizeTasks(AbstractRepositoryConnector connector, Set<ITask> tasks, boolean force,
			IJobChangeListener listener) {
		ITaskList taskList = TasksUiInternal.getTaskList();
		for (ITask task : tasks) {
			((AbstractTask) task).setSynchronizing(true);
			synchronizationScheduler.cancel(task);
		}
		((TaskList) taskList).notifySynchronizationStateChanged(tasks);
		// TODO notify task list?

		SynchronizationJob job = TasksUiPlugin.getTaskJobFactory().createSynchronizeTasksJob(connector, tasks);
		job.setUser(force);
		job.setSystem(!force);
		job.setPriority(Job.DECORATE);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force && tasks.size() == 1) {
			final ITask task = tasks.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (task instanceof AbstractTask && ((AbstractTask) task).getStatus() != null) {
						TasksUiInternal.asyncLogAndDisplayStatus(Messages.TasksUiInternal_Task_Synchronization_Failed,
								((AbstractTask) task).getStatus());
					}
				}
			});
		}
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	public static ITaskJobFactory getJobFactory() {
		return TasksUiPlugin.getTaskJobFactory();
	}

	public static NewAttachmentWizardDialog openNewAttachmentWizard(Shell shell, TaskRepository taskRepository,
			ITask task, TaskAttribute taskAttribute, TaskAttachmentWizard.Mode mode,
			AbstractTaskAttachmentSource source) {
		TaskAttachmentWizard attachmentWizard = new TaskAttachmentWizard(taskRepository, task, taskAttribute);
		attachmentWizard.setSource(source);
		attachmentWizard.setMode(mode);
		NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(shell, attachmentWizard, false);
		dialog.setBlockOnOpen(false);
		dialog.create();
		dialog.open();
		return dialog;
	}

	private static MessageDialog createDialog(Shell shell, String title, String message, int type) {
		return new MessageDialog(shell, title, null, message, type, new String[] { IDialogConstants.OK_LABEL }, 0);
	}

	private static void displayStatus(Shell shell, final String title, final IStatus status,
			boolean showLinkToErrorLog) {
		// avoid blocking ui when in test mode
		if (CoreUtil.TEST_MODE) {
			StatusHandler.log(status);
			return;
		}

		if (status instanceof RepositoryStatus && ((RepositoryStatus) status).isHtmlMessage()) {
			WebBrowserDialog.openAcceptAgreement(shell, title, status.getMessage(),
					((RepositoryStatus) status).getHtmlMessage());
		} else {
			String message = status.getMessage();
			if (message == null || message.trim().length() == 0) {
				message = Messages.TasksUiInternal_An_unknown_error_occurred;
			}
			if (message.length() > 256) {
				message = message.substring(0, 256) + "..."; //$NON-NLS-1$
			}
			if (showLinkToErrorLog) {
				message += "\n\n" + Messages.TasksUiInternal_See_error_log_for_details; //$NON-NLS-1$
			}
			switch (status.getSeverity()) {
			case IStatus.CANCEL:
			case IStatus.INFO:
				createDialog(shell, title, message, MessageDialog.INFORMATION).open();
				break;
			case IStatus.WARNING:
				createDialog(shell, title, message, MessageDialog.WARNING).open();
				break;
			case IStatus.ERROR:
			default:
				createDialog(shell, title, message, MessageDialog.ERROR).open();
				break;
			}
		}
	}

	public static void asyncDisplayStatus(final String title, final IStatus status) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					displayStatus(title, status);
				}
			});
		} else {
			StatusHandler.log(status);
		}
	}

	public static void asyncLogAndDisplayStatus(final String title, final IStatus status) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				public void run() {
					logAndDisplayStatus(title, status);
				}
			});
		} else {
			StatusHandler.log(status);
		}
	}

	public static void logAndDisplayStatus(final String title, final IStatus status) {
		StatusHandler.log(status);
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null && !workbench.getDisplay().isDisposed()) {
			displayStatus(WorkbenchUtil.getShell(), title, status, false);
		}
	}

	public static void displayStatus(final String title, final IStatus status) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null && !workbench.getDisplay().isDisposed()) {
			displayStatus(WorkbenchUtil.getShell(), title, status, false);
		} else {
			StatusHandler.log(status);
		}
	}

	/**
	 * Creates a new local task and schedules for today
	 *
	 * @param summary
	 *            if null DEFAULT_SUMMARY (New Task) used.
	 */
	public static LocalTask createNewLocalTask(String summary) {
		if (summary == null) {
			summary = LocalRepositoryConnector.DEFAULT_SUMMARY;
		}
		TaskList taskList = TasksUiPlugin.getTaskList();
		LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), summary); //$NON-NLS-1$
		newTask.setPriority(PriorityLevel.P3.toString());
		TasksUiInternal.getTaskList().addTask(newTask);
		scheduleNewTask(newTask);

		TaskListView view = TaskListView.getFromActivePerspective();
		AbstractTaskCategory category = getSelectedCategory(view);
		if (view != null && view.getDrilledIntoCategory() != null && view.getDrilledIntoCategory() != category) {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.TasksUiInternal_Create_Task,
					MessageFormat.format(Messages.TasksUiInternal_The_new_task_will_be_added_to_the_X_container,
							UncategorizedTaskContainer.LABEL));
		}
		newTask.setAttribute(ITasksCoreConstants.PROPERTY_NEW_UNSAVED_TASK, Boolean.TRUE.toString());
		taskList.addTask(newTask, category);
		return newTask;
	}

	/**
	 * Schedules the new task according to the Tasks UI preferences
	 *
	 * @param newTask
	 *            the task to schedule
	 */
	public static void scheduleNewTask(LocalTask newTask) {
		IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		String preference = preferenceStore.getString(ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR);
		DateRange dateRange = getNewTaskScheduleDateRange(preference);
		TasksUiPlugin.getTaskActivityManager().scheduleNewTask(newTask, dateRange);
	}

	private static DateRange getNewTaskScheduleDateRange(String preference) {
		switch (preference) {
		case ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_NOT_SCHEDULED:
			return null;
		case ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_TODAY:
			return TaskActivityUtil.getCurrentWeek().getToday();
		case ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_TOMORROW:
			return TaskActivityUtil.getCurrentWeek().getToday().next();
		case ITasksUiPreferenceConstants.SCHEDULE_NEW_TASKS_FOR_THIS_WEEK:
			return TaskActivityUtil.getCurrentWeek();
		}
		return TaskActivityUtil.getCurrentWeek();
	}

	public static AbstractTaskCategory getSelectedCategory(TaskListView view) {
		Object selectedObject = null;
		if (view != null) {
			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		}
		if (selectedObject instanceof TaskCategory) {
			return (TaskCategory) selectedObject;
		} else if (selectedObject instanceof ITask) {
			ITask task = (ITask) selectedObject;
			AbstractTaskContainer container = TaskCategory.getParentTaskCategory(task);
			if (container instanceof TaskCategory) {
				return (TaskCategory) container;
			} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
				return (TaskCategory) view.getDrilledIntoCategory();
			}
		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
			return (TaskCategory) view.getDrilledIntoCategory();
		}
		return TasksUiPlugin.getTaskList().getDefaultCategory();
	}

	public static Set<AbstractTaskContainer> getContainersFromWorkingSet(Set<IWorkingSet> containers) {

		Set<AbstractTaskContainer> allTaskContainersInWorkingSets = new HashSet<AbstractTaskContainer>();
		for (IWorkingSet workingSet : containers) {
			IAdaptable[] elements = workingSet.getElements();
			for (IAdaptable adaptable : elements) {
				if (adaptable instanceof AbstractTaskContainer) {
					allTaskContainersInWorkingSets.add(((AbstractTaskContainer) adaptable));
				}
			}
		}
		return allTaskContainersInWorkingSets;
	}

	/**
	 * @param connectorUi
	 *            - repository connector ui
	 * @param query
	 *            - repository query
	 * @return - true if dialog was opened successfully and not canceled, false otherwise
	 * @since 3.0
	 */
	public static boolean openEditQueryDialog(AbstractRepositoryConnectorUi connectorUi, final IRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUi.getRepositoryManager()
					.getRepository(query.getConnectorKind(), query.getRepositoryUrl());
			if (repository == null) {
				return false;
			}

			IWizard wizard = connectorUi.getQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				QueryWizardDialog dialog = new QueryWizardDialog(shell, wizard) {
					private static final String DIALOG_SETTINGS = "EditQueryWizardWizard"; //$NON-NLS-1$

					@Override
					protected IDialogSettings getDialogBoundsSettings() {
						IDialogSettings settings = TasksUiPlugin.getDefault().getDialogSettings();
						String settingsSectionId = DIALOG_SETTINGS + '.' + query.getRepositoryUrl();
						IDialogSettings section = settings.getSection(settingsSectionId);
						if (section == null) {
							section = settings.addNewSection(settingsSectionId);
						}
						return section;
					}
				};
				dialog.create();
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					return false;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to open query dialog", e)); //$NON-NLS-1$
		}
		return false;
	}

	public static ITaskList getTaskList() {
		return TasksUiPlugin.getTaskList();
	}

	public static boolean isAnimationsEnabled() {
		IPreferenceStore store = PlatformUI.getPreferenceStore();
		return store.getBoolean(IWorkbenchPreferenceConstants.ENABLE_ANIMATIONS);
	}

	public static boolean hasValidUrl(ITask task) {
		return isValidUrl(task.getUrl());
	}

	public static boolean isValidUrl(String url) {
		if (url != null && !url.equals("") && !url.equals("http://") && !url.equals("https://")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			try {
				new URL(url);
				return true;
			} catch (MalformedURLException e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * @deprecated use {@link #closeTaskEditorInAllPages(ITask, boolean)}
	 */
	@Deprecated
	public static void closeEditorInActivePage(ITask task, boolean save) {
		Assert.isNotNull(task);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		TaskRepository taskRepository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		IEditorInput input = new TaskEditorInput(taskRepository, task);
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.closeEditor(editor, save);
		}
	}

	public static void closeTaskEditorInAllPages(ITask task, boolean save) {
		Assert.isNotNull(task);
		TaskRepository taskRepository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		IEditorInput input = new TaskEditorInput(taskRepository, task);
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (IWorkbenchWindow window : windows) {
			IWorkbenchPage[] pages = window.getPages();
			for (IWorkbenchPage page : pages) {
				IEditorPart editor = page.findEditor(input);
				if (editor != null) {
					page.closeEditor(editor, save);
				}
			}
		}
	}

	public static boolean hasLocalCompletionState(ITask task) {
		TaskRepository taskRepository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(task.getConnectorKind());
		return connector.hasLocalCompletionState(taskRepository, task);
	}

	/**
	 * Utility method to get the best parenting possible for a dialog. If there is a modal shell create it so as to
	 * avoid two modal dialogs. If not then return the shell of the active workbench window. If neither can be found
	 * return null.
	 * <p>
	 * <b>Note: Applied from patch on bug 99472.</b>
	 *
	 * @return Shell or <code>null</code>
	 * @deprecated Use {@link WorkbenchUtil#getShell()} instead
	 */
	@Deprecated
	public static Shell getShell() {
		return WorkbenchUtil.getShell();
	}

	public static TaskData createTaskData(TaskRepository taskRepository, ITaskMapping initializationData,
			ITaskMapping selectionData, IProgressMonitor monitor) throws CoreException {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(taskRepository.getConnectorKind());
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(taskRepository);
		TaskData taskData = new TaskData(mapper, taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl(),
				""); //$NON-NLS-1$
		boolean result = taskDataHandler.initializeTaskData(taskRepository, taskData, initializationData, monitor);
		if (!result) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Initialization of task failed. The provided data is insufficient.")); //$NON-NLS-1$
		}
		if (selectionData != null) {
			connector.getTaskMapping(taskData).merge(selectionData);
		}
		return taskData;
	}

	public static void createAndOpenNewTask(TaskData taskData) throws CoreException {
		ITask task = TasksUiUtil.createOutgoingNewTask(taskData.getConnectorKind(), taskData.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(taskData.getConnectorKind());
		ITaskMapping mapping = connector.getTaskMapping(taskData);
		String summary = mapping.getSummary();
		if (StringUtils.isNotEmpty(summary)) {
			task.setSummary(summary);
		}
		String taskKind = mapping.getTaskKind();
		if (StringUtils.isNotEmpty(taskKind)) {
			task.setTaskKind(taskKind);
		}
		UnsubmittedTaskContainer unsubmitted = TasksUiPlugin.getTaskList()
				.getUnsubmittedContainer(taskData.getRepositoryUrl());
		if (unsubmitted != null) {
			TasksUiPlugin.getTaskList().addTask(task, unsubmitted);
		}
		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(task, taskData);
		workingCopy.save(null, null);
		TaskRepository taskRepository = TasksUi.getRepositoryManager()
				.getRepository(taskData.getConnectorKind(), taskData.getRepositoryUrl());
		connector.updateNewTaskFromTaskData(taskRepository, task, taskData);
		TaskRepository localTaskRepository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		TaskEditorInput editorInput = new TaskEditorInput(localTaskRepository, task);
		TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, null);
	}

	public static boolean openTask(TaskRepository repository, String taskId, TaskOpenListener listener) {
		AbstractTask task = getTaskFromTaskList(repository, taskId);
		if (task != null) {
			// task is known, open in task editor
			return openKnownTaskInEditor(task, taskId, listener);
		} else {
			// search for task
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
			if (connectorUi != null) {
				try {
					return openRepositoryTask(connectorUi.getConnectorKind(), repository.getRepositoryUrl(), taskId,
							listener);
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Internal error while opening repository task", e)); //$NON-NLS-1$
				}
			}
		}
		return false;
	}

	public static boolean openTaskByIdOrKey(TaskRepository repository, String taskIdOrKey, TaskOpenListener listener) {
		AbstractTask task = getTaskFromTaskList(repository, taskIdOrKey);
		if (task != null) {
			// task is known, open in task editor
			return openKnownTaskInEditor(task, taskIdOrKey, listener);
		} else {
			// search for task
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(repository.getConnectorKind());
			if (connectorUi != null) {
				try {
					return openRepositoryTaskByIdOrKey(connectorUi.getConnectorKind(), repository.getRepositoryUrl(),
							taskIdOrKey, listener);
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"Internal error while opening repository task", e)); //$NON-NLS-1$
				}
			}
		}
		return false;
	}

	private static boolean openKnownTaskInEditor(AbstractTask task, String taskIdOrKey, TaskOpenListener listener) {
		TaskOpenEvent event = TasksUiInternal.openTask(task, taskIdOrKey);
		if (listener != null && event != null) {
			listener.taskOpened(event);
		}
		return event != null;
	}

	private static AbstractTask getTaskFromTaskList(TaskRepository repository, String taskIdOrKey) {
		Assert.isNotNull(repository);
		Assert.isNotNull(taskIdOrKey);

		AbstractTask task = (AbstractTask) TasksUiInternal.getTaskList()
				.getTask(repository.getRepositoryUrl(), taskIdOrKey);
		if (task == null) {
			task = TasksUiPlugin.getTaskList().getTaskByKey(repository.getRepositoryUrl(), taskIdOrKey);
		}
		return task;
	}

	public static TaskOpenEvent openTask(ITask task, String taskId) {
		Assert.isNotNull(task);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			TaskRepository taskRepository = TasksUi.getRepositoryManager()
					.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
			boolean openWithBrowser = !TasksUiPlugin.getDefault()
					.getPreferenceStore()
					.getBoolean(ITasksUiPreferenceConstants.EDITOR_TASKS_RICH);
			if (openWithBrowser && !(task instanceof LocalTask)) {
				TasksUiUtil.openWithBrowser(taskRepository, task);
				return new TaskOpenEvent(taskRepository, task, taskId, null, true);
			} else {
				IEditorInput editorInput = getTaskEditorInput(taskRepository, task);
				IEditorPart editor = refreshEditorContentsIfOpen(task, editorInput);
				if (editor != null) {
					synchronizeTask(taskRepository, task);
					return new TaskOpenEvent(taskRepository, task, taskId, editor, false);
				} else {
					IWorkbenchPage page = window.getActivePage();
					editor = TasksUiUtil.openEditor(editorInput, getTaskEditorId(task), page);
					if (editor != null) {
						synchronizeTask(taskRepository, task);
						return new TaskOpenEvent(taskRepository, task, taskId, editor, false);
					}
				}
			}
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for \"" //$NON-NLS-1$
					+ task.getSummary() + "\": no active workbench window")); //$NON-NLS-1$
		}
		return null;
	}

	private static IEditorInput getTaskEditorInput(TaskRepository repository, ITask task) {
		Assert.isNotNull(task);
		Assert.isNotNull(repository);
		AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(task.getConnectorKind());
		IEditorInput editorInput = connectorUi.getTaskEditorInput(repository, task);
		if (editorInput != null) {
			return editorInput;
		} else {
			return new TaskEditorInput(repository, task);
		}
	}

	private static String getTaskEditorId(final ITask task) {
		String taskEditorId = TaskEditor.ID_EDITOR;
		if (task != null) {
			ITask repositoryTask = task;
			AbstractRepositoryConnectorUi repositoryUi = TasksUiPlugin
					.getConnectorUi(repositoryTask.getConnectorKind());
			String customTaskEditorId = repositoryUi.getTaskEditorId(repositoryTask);
			if (customTaskEditorId != null) {
				taskEditorId = customTaskEditorId;
			}
		}
		return taskEditorId;
	}

	/**
	 * If task is already open and has incoming, must force refresh in place
	 */
	private static IEditorPart refreshEditorContentsIfOpen(ITask task, IEditorInput editorInput) {
		if (task != null) {
			if (task.getSynchronizationState() == SynchronizationState.INCOMING
					|| task.getSynchronizationState() == SynchronizationState.CONFLICT) {
				for (TaskEditor editor : TasksUiInternal.getActiveRepositoryTaskEditors()) {
					if (editor.getEditorInput().equals(editorInput)) {
						editor.refreshPages();
						editor.getEditorSite().getPage().activate(editor);
						return editor;
					}
				}
			}
		}
		return null;
	}

	private static void synchronizeTask(TaskRepository taskRepository, ITask task) {
		if (task instanceof LocalTask) {
			return;
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(task.getConnectorKind());
		if (connector.canSynchronizeTask(taskRepository, task)) {
			TasksUiInternal.synchronizeTaskInBackground(connector, task);
		}
	}

	public static boolean openRepositoryTask(String connectorKind, String repositoryUrl, String id) {
		return openRepositoryTask(connectorKind, repositoryUrl, id, null);
	}

	/**
	 * Only override if task should be opened by a custom editor, default behavior is to open with a rich editor,
	 * falling back to the web browser if not available.
	 *
	 * @return true if the task was successfully opened
	 */
	public static boolean openRepositoryTask(String connectorKind, String repositoryUrl, String id,
			TaskOpenListener listener) {
		return openRepositoryTask(connectorKind, repositoryUrl, id, listener, 0);
	}

	/**
	 * Only override if task should be opened by a custom editor, default behavior is to open with a rich editor,
	 * falling back to the web browser if not available.
	 *
	 * @return true if the task was successfully opened
	 */
	public static boolean openRepositoryTask(String connectorKind, String repositoryUrl, String id,
			TaskOpenListener listener, long timestamp) {
		String taskUrl = getTaskUrl(connectorKind, repositoryUrl, id);
		if (taskUrl == null) {
			return false;
		}

		IWorkbenchWindow window = getWorkbenchWindow();
		if (window == null) {
			return false;
		}
		IWorkbenchPage page = window.getActivePage();

		OpenRepositoryTaskJob job = new OpenRepositoryTaskJob(connectorKind, repositoryUrl, id, taskUrl, timestamp,
				page);
		job.setListener(listener);
		job.schedule();

		return true;
	}

	/**
	 * Opens a task with a given search string that can be a task id or task key.
	 *
	 * @return true if the task was successfully opened
	 */
	public static boolean openRepositoryTaskByIdOrKey(String connectorKind, String repositoryUrl, String idOrKey,
			TaskOpenListener listener) {
		String taskUrl = getTaskUrl(connectorKind, repositoryUrl, idOrKey);
		if (taskUrl == null) {
			return false;
		}

		IWorkbenchWindow window = getWorkbenchWindow();
		if (window == null) {
			return false;
		}
		IWorkbenchPage page = window.getActivePage();

		OpenRepositoryTaskJob job = new OpenRepositoryTaskJob(page, connectorKind, repositoryUrl, idOrKey, taskUrl);
		job.setListener(listener);
		job.schedule();

		return true;
	}

	private static IWorkbenchWindow getWorkbenchWindow() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) {
				window = windows[0];
			}
		}
		return window;
	}

	private static String getTaskUrl(String connectorKind, String repositoryUrl, String idOrKey) {
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(connectorKind);
		return connector.getTaskUrl(repositoryUrl, idOrKey);
	}

	/**
	 * @since 3.0
	 */
	public static boolean openTaskInBackground(ITask task, boolean bringToTop) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IEditorPart activeEditor = null;
			IWorkbenchPart activePart = null;
			IWorkbenchPage activePage = window.getActivePage();
			if (activePage != null) {
				activeEditor = activePage.getActiveEditor();
				activePart = activePage.getActivePart();
			}
			boolean opened = TasksUiUtil.openTask(task);
			if (opened && activePage != null) {
				if (!bringToTop && activeEditor != null) {
					activePage.bringToTop(activeEditor);
				}
				if (activePart != null) {
					activePage.activate(activePart);
				}
			}
			return opened;
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to open editor for \"" //$NON-NLS-1$
					+ task.getSummary() + "\": no active workbench window")); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Returns text masking the &amp;-character from decoration as an accelerator in SWT labels.
	 *
	 * @deprecated Use {@link CommonUiUtil#toLabel(String)} instead
	 */
	@Deprecated
	public static String escapeLabelText(String text) {
		return CommonUiUtil.toLabel(text);
	}

	public static void preservingSelection(final TreeViewer viewer, Runnable runnable) {
		final ISelection selection = viewer.getSelection();

		runnable.run();

		if (selection != null) {
			ISelection newSelection = viewer.getSelection();
			if ((newSelection == null || newSelection.isEmpty()) && !(selection == null || selection.isEmpty())) {
				// delay execution to ensure that any delayed tree updates such as expand all have been processed and the selection is revealed properly
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						viewer.setSelection(selection, true);
					}
				});
			} else if (newSelection instanceof ITreeSelection && !newSelection.isEmpty()) {
				viewer.reveal(((ITreeSelection) newSelection).getFirstElement());
			}
		}
	}

	public static String getFormattedDuration(long duration, boolean includeSeconds) {
		long seconds = duration / 1000;
		long minutes = 0;
		long hours = 0;
		// final long SECOND = 1000;
		final long MIN = 60;
		final long HOUR = MIN * 60;
		String formatted = ""; //$NON-NLS-1$

		String hour = ""; //$NON-NLS-1$
		String min = ""; //$NON-NLS-1$
		String sec = ""; //$NON-NLS-1$
		if (seconds >= HOUR) {
			hours = seconds / HOUR;
			if (hours == 1) {
				hour = hours + Messages.TasksUiInternal__hour_;
			} else if (hours > 1) {
				hour = hours + Messages.TasksUiInternal__hours_;
			}
			seconds -= hours * HOUR;

			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + Messages.TasksUiInternal__minute_;
			} else if (minutes != 1) {
				min = minutes + Messages.TasksUiInternal__minutes_;
			}
			seconds -= minutes * MIN;
			if (seconds == 1) {
				sec = seconds + Messages.TasksUiInternal__second;
			} else if (seconds > 1) {
				sec = seconds + Messages.TasksUiInternal__seconds;
			}
			formatted += hour + min;
			if (includeSeconds) {
				formatted += sec;
			}
		} else if (seconds >= MIN) {
			minutes = seconds / MIN;
			if (minutes == 1) {
				min = minutes + Messages.TasksUiInternal__minute_;
			} else if (minutes != 1) {
				min = minutes + Messages.TasksUiInternal__minutes_;
			}
			seconds -= minutes * MIN;
			if (seconds == 1) {
				sec = seconds + Messages.TasksUiInternal__second;
			} else if (seconds > 1) {
				sec = seconds + Messages.TasksUiInternal__seconds;
			}
			formatted += min;
			if (includeSeconds) {
				formatted += sec;
			}
		} else {
			if (seconds == 1) {
				sec = seconds + Messages.TasksUiInternal__second;
			} else if (seconds > 1) {
				sec = seconds + Messages.TasksUiInternal__seconds;
			}
			if (includeSeconds) {
				formatted += sec;
			}
		}
		return formatted;
	}

	public static AbstractTask getTask(String repositoryUrl, String taskId, String fullUrl) {
		AbstractTask task = null;
		if (repositoryUrl != null && taskId != null) {
			task = (AbstractTask) TasksUiInternal.getTaskList().getTask(repositoryUrl, taskId);
		}
		if (task == null && fullUrl != null) {
			task = TasksUiInternal.getTaskByUrl(fullUrl);
		}
		if (task == null && repositoryUrl != null && taskId != null) {
			task = TasksUiPlugin.getTaskList().getTaskByKey(repositoryUrl, taskId);
		}
		return task;
	}

	/**
	 * Searches for a task whose URL matches
	 *
	 * @return first task with a matching URL.
	 */
	public static AbstractTask getTaskByUrl(String taskUrl) {
		return TasksCoreUtil.getTaskByUrl(TasksUiPlugin.getTaskList(), TasksUi.getRepositoryManager(), taskUrl);
	}

	public static boolean isTaskUrl(String taskUrl) {
		Assert.isNotNull(taskUrl);
		List<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
		for (TaskRepository repository : repositories) {
			if (taskUrl.startsWith(repository.getUrl())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Cleans text for use as the text of an action to ensure that it is displayed properly.
	 *
	 * @return the cleaned text
	 * @deprecated use {@link CommonUiUtil#toMenuLabel(String)} instead
	 */
	@Deprecated
	public static String cleanTextForAction(String label) {
		return CommonUiUtil.toMenuLabel(label);
	}

	public static void executeCommand(IServiceLocator serviceLocator, String commandId, String title, Object object,
			Event event) throws NotEnabledException {
		IHandlerService service = (IHandlerService) serviceLocator.getService(IHandlerService.class);
		if (service != null) {
			ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);
			if (commandService != null) {
				Command command = commandService.getCommand(commandId);
				if (command != null) {
					try {
						if (object != null) {
							IEvaluationContext context = service.createContextSnapshot(false);
							context.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME,
									new StructuredSelection(object));
							service.executeCommandInContext(new ParameterizedCommand(command, null), event, context);
						} else {
							service.executeCommand(commandId, event);
						}
					} catch (ExecutionException e) {
						TasksUiInternal.displayStatus(title,
								new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Command execution failed", e)); //$NON-NLS-1$
					} catch (NotDefinedException e) {
						TasksUiInternal.displayStatus(title, new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								NLS.bind("The command with the id ''{0}'' is not defined.", commandId), e)); //$NON-NLS-1$
					} catch (NotHandledException e) {
						TasksUiInternal.displayStatus(title, new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								NLS.bind("The command with the id ''{0}'' is not bound.", commandId), e)); //$NON-NLS-1$
					}
				} else {
					TasksUiInternal.displayStatus(title, new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							NLS.bind("The command with the id ''{0}'' does not exist.", commandId))); //$NON-NLS-1$
				}
			} else {
				TasksUiInternal.displayStatus(title,
						new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
								NLS.bind("Command service is not available to execute command with the id ''{0}''.", //$NON-NLS-1$
										commandId),
								new Exception()));
			}
		} else {
			TasksUiInternal.displayStatus(title,
					new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							NLS.bind("Handler service is not available to execute command with the id ''{0}''.", //$NON-NLS-1$
									commandId),
							new Exception()));
		}
	}

	public static void activateTaskThroughCommand(ITask task) {
		try {
			TasksUiInternal.executeCommand(PlatformUI.getWorkbench(),
					"org.eclipse.mylyn.tasks.ui.command.activateSelectedTask", Messages.TasksUiInternal_Activate_Task, //$NON-NLS-1$
					task, null);
		} catch (NotEnabledException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					NLS.bind("Failed to activate task ''{0}''.", task.getSummary()), e)); //$NON-NLS-1$
		}
	}

	public static long getActiveTime(ITask task) {
		if (TasksUiInternal.isActivityTrackingEnabled()) {
			return TasksUiPlugin.getTaskActivityManager().getElapsedTime(task);
		}
		return 0;
	}

	public static String getTaskPrefix(String connectorKind) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getConnector(connectorKind);
		if (connector != null) {
			String prefix = connector.getTaskIdPrefix();
			// work around short prefixes which are not separated by space, e.g. "#" for Trac
			return (prefix.length() > 1) ? prefix + " " : prefix; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	public static void displayFrameworkError(String message) {
		RuntimeException exception = new RuntimeException(message);
		if (!CoreUtil.TEST_MODE) {
			StatusAdapter status = new StatusAdapter(
					new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, message, exception));
			status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, "Framework Error"); //$NON-NLS-1$
			StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG | StatusManager.BLOCK);
		}
		throw exception;
	}

	public static String getAuthenticatedUrl(TaskRepository repository, IRepositoryElement element) {
		Assert.isNotNull(repository);
		Assert.isNotNull(element);
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(repository.getConnectorKind());
		if (connector != null) {
			URL authenticatedUrl = connector.getAuthenticatedUrl(repository, element);
			if (authenticatedUrl != null) {
				return authenticatedUrl.toString();
			} else {
				String url = element.getUrl();
				if (TasksUiInternal.isValidUrl(url)) {
					return url;
				}
			}
		}
		return null;
	}

	public static TaskRepository getRepository(IRepositoryElement element) {
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		TaskRepository repository = null;
		if (element instanceof ITask) {
			repository = repositoryManager.getRepository(((ITask) element).getConnectorKind(),
					((ITask) element).getRepositoryUrl());
		} else if (element instanceof IRepositoryQuery) {
			repository = repositoryManager.getRepository(((IRepositoryQuery) element).getConnectorKind(),
					((IRepositoryQuery) element).getRepositoryUrl());
		}
		return repository;
	}

	/**
	 * Returns all the tasks in the given selection, or an empty list if the selection contains no tasks.
	 */
	public static List<ITask> getTasksFromSelection(ISelection selection) {
		Assert.isNotNull(selection);
		if (selection.isEmpty()) {
			return Collections.emptyList();
		}

		List<ITask> tasks = new ArrayList<ITask>();
		if (selection instanceof IStructuredSelection) {
			for (Object element : ((IStructuredSelection) selection).toList()) {
				ITask task = null;
				if (element instanceof ITask) {
					task = (ITask) element;
				} else if (element instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) element;
					task = (ITask) adaptable.getAdapter(ITask.class);
				}
				if (task != null) {
					tasks.add(task);
				}
			}
		}
		return tasks;
	}

	public static boolean shouldShowIncoming(ITask task) {
		SynchronizationState state = task.getSynchronizationState();
		if ((state == SynchronizationState.INCOMING
				&& !Boolean.valueOf(task.getAttribute(ITasksCoreConstants.ATTRIBUTE_TASK_SUPPRESS_INCOMING)))
				|| state == SynchronizationState.INCOMING_NEW || state == SynchronizationState.CONFLICT) {
			return true;
		}
		return false;
	}

	public static TaskData computeTaskData(TaskData taskData, TaskHistory history, String revisionId,
			IProgressMonitor monitor) throws CoreException {
		TaskData newTaskData = TaskDataState.createCopy(taskData);
		List<TaskRevision> revisions = history.getRevisions();
		Collections.reverse(revisions);
		TaskRevision lastRevision = null;
		for (TaskRevision revision : revisions) {
			for (Change change : revision.getChanges()) {
				TaskAttribute attribute = newTaskData.getRoot().getAttribute(change.getAttributeId());
				if (attribute != null) {
					attribute.setValue(change.getRemoved());
				}
			}
			// only apply changes up to this revision
			if (revisionId.equals(revision.getId())) {
				lastRevision = revision;
				break;
			}
		}

		if (lastRevision != null && lastRevision.getDate() != null) {
			// remove attachments and comments that are newer than lastRevision
			List<TaskAttribute> attributes = new ArrayList<TaskAttribute>(
					newTaskData.getRoot().getAttributes().values());
			for (TaskAttribute attribute : attributes) {
				if (TaskAttribute.TYPE_COMMENT.equals(attribute.getMetaData().getType())) {
					TaskCommentMapper mapper = TaskCommentMapper.createFrom(attribute);
					if (mapper.getCreationDate() != null && mapper.getCreationDate().after(lastRevision.getDate())) {
						newTaskData.getRoot().removeAttribute(attribute.getId());
					}
				} else if (TaskAttribute.TYPE_ATTACHMENT.equals(attribute.getMetaData().getType())) {
					TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attribute);
					if (mapper.getCreationDate() != null && mapper.getCreationDate().after(lastRevision.getDate())) {
						newTaskData.getRoot().removeAttribute(attribute.getId());
					}
				}
			}
		}

		return newTaskData;
	}

	public static boolean canGetTaskHistory(ITask task) {
		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager()
				.getRepositoryConnector(repository.getConnectorKind());
		return connector.canGetTaskHistory(repository, task);
	}

	public static Account getAccount(TaskAttribute attribute) {
		Account account;
		if (TaskAttribute.TYPE_PERSON.equals(attribute.getMetaData().getType())) {
			IRepositoryPerson person = attribute.getTaskData().getAttributeMapper().getRepositoryPerson(attribute);
			account = Account.id(person.getPersonId()).name(person.getName());
		} else {
			account = Account.id(attribute.getValue());
		}
		TaskRepository repository = attribute.getTaskData().getAttributeMapper().getTaskRepository();
		return account.kind(repository.getConnectorKind()).url(repository.getRepositoryUrl());
	}

	public static boolean isActivityTrackingEnabled() {
		return TasksUiPlugin.getTaskActivityMonitor().isEnabled()
				&& MonitorUiPlugin.getDefault().isActivityTrackingEnabled();
	}

	public static TaskDropHandler getTaskDropHandler() {
		return taskDropHandler;
	}

	public static ImageDescriptor getIconFromStatusOfQuery(RepositoryQuery query) {
		ImageDescriptor image;
		boolean showError = false;
		Throwable exception = query.getStatus().getException();
		showError = (query.getLastSynchronizedTimeStamp().equals("<never>") //$NON-NLS-1$
				&& ((RepositoryStatus.ERROR_IO == query.getStatus().getCode() && exception != null
						&& exception instanceof SocketTimeoutException) || //
				// only when we change SocketTimeout or Eclipse.org change there timeout for long running Queries
						(RepositoryStatus.ERROR_NETWORK) == query.getStatus().getCode()
								&& query.getStatus().getMessage().equals("Http error: Internal Server Error"))); //$NON-NLS-1$
		if (showError) {
			image = CommonImages.OVERLAY_SYNC_ERROR;
		} else {
			image = CommonImages.OVERLAY_SYNC_WARNING;
		}
		return image;
	}

	public static synchronized IUndoContext getUndoContext() {
		if (undoContext == null) {
			undoContext = new ObjectUndoContext(new Object(), "Tasks Context"); //$NON-NLS-1$
		}
		return undoContext;
	}

	/**
	 * Deletes the given task from the Task List, the Task Context Store, and the Task Data Manager.
	 *
	 * @param task
	 *            the task to delete
	 */
	public static void deleteTask(ITask task) {
		TasksUiInternal.getTaskList().deleteTask(task);
		TasksUiPlugin.getContextStore().deleteContext(task);
		try {
			TasksUiPlugin.getTaskDataManager().deleteTaskData(task);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to delete task data", e)); //$NON-NLS-1$
		}
	}

}
