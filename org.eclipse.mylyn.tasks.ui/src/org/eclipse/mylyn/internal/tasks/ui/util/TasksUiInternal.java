/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskDataStorageManager;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.OpenRepositoryTaskJob;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.CategoryEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.CategoryEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.wizards.MultiRepositoryAwareWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.TaskAttachmentWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector.Capability;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TasksUiInternal {

	public static MultiRepositoryAwareWizard createNewTaskWizard(ITaskMapping taskSelection) {
		return new NewTaskWizard(taskSelection);
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
		final IEditorInput input = new CategoryEditorInput(category);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					TasksUiUtil.openEditor(input, CategoryEditor.ID_EDITOR, page);
				}
			}
		});
	}

	public static void refreshAndOpenTaskListElement(IRepositoryElement element) {
		if (element instanceof ITask) {
			final AbstractTask task = (AbstractTask) element;

			if (task instanceof LocalTask) {
				TasksUiUtil.openTask(task);
			} else {
				String repositoryKind = task.getConnectorKind();
				final AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						repositoryKind);

				TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryKind,
						task.getRepositoryUrl());
				if (repository == null) {
					StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
							"No repository found for task. Please create repository in "
									+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + "."));
					return;
				}

				if (connector != null) {
					boolean opened = false;
					if (connector instanceof AbstractLegacyRepositoryConnector) {
						RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
								task.getRepositoryUrl(), task.getTaskId());
						if (taskData != null || connector.getTaskDataHandler() == null) {
							TasksUiUtil.openTask(task);
							opened = true;
						}
					} else if (TasksUiPlugin.getTaskDataManager().hasTaskData(task)) {
						opened = TasksUiUtil.openTask(task);
					}

					if (!opened) {
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

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		final TaskJob job = TasksUiInternal.getJobFactory().createUpdateRepositoryConfigurationJob(connector,
				taskRepository);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				synchronized (taskRepository) {
					taskRepository.setUpdating(false);
				}
				if (job.getStatus() != null) {
					Display display = PlatformUI.getWorkbench().getDisplay();
					if (!display.isDisposed()) {
						TasksUiInternal.displayStatus("Configuration Refresh Failed", job.getStatus());
					}
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

		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeQueriesJob(connector, repository,
				queries);
		job.setUser(force);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force) {
			final RepositoryQuery query = queries.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (query.getStatus() != null) {
						TasksUiInternal.asyncDisplayStatus("Query Synchronization Failed", query.getStatus());
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
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(repositoryQuery.getConnectorKind(),
				repositoryQuery.getRepositoryUrl());
		return synchronizeQueries(connector, repository, Collections.singleton(repositoryQuery), listener, force);
	}

	public static SynchronizationJob synchronizeAllRepositories(boolean force) {
		Set<TaskRepository> repositories = new HashSet<TaskRepository>(TasksUi.getRepositoryManager()
				.getAllRepositories());
		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeRepositoriesJob(repositories);
		job.setUser(force);
		job.schedule();
		joinIfInTestMode(job);
		return job;
	}

	public static SynchronizationJob synchronizeRepository(TaskRepository repository, boolean force) {
		return TasksUiPlugin.getSynchronizationScheduler().synchronize(repository);
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
		}
		((TaskList) taskList).notifySynchronizationStateChanged(tasks);
		// TODO notify task list?

		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeTasksJob(connector, tasks);
		job.setUser(force);
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
						TasksUiInternal.asyncDisplayStatus("Task Synchronization Failed",
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
		return TasksUiPlugin.getTasksJobFactory();
	}

	public static NewAttachmentWizardDialog openNewAttachmentWizard(Shell shell, TaskRepository taskRepository,
			ITask task, TaskAttribute taskAttribute, TaskAttachmentWizard.Mode mode, AbstractTaskAttachmentSource source) {
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

	public static void displayStatus(Shell shell, final String title, final IStatus status) {
		if (status.getCode() == RepositoryStatus.ERROR_INTERNAL) {
			StatusHandler.fail(status);
		} else {
			if (status instanceof RepositoryStatus && ((RepositoryStatus) status).isHtmlMessage()) {
				WebBrowserDialog.openAcceptAgreement(shell, title, status.getMessage(),
						((RepositoryStatus) status).getHtmlMessage());
			} else {
				switch (status.getSeverity()) {
				case IStatus.CANCEL:
				case IStatus.INFO:
					createDialog(shell, title, status.getMessage(), MessageDialog.INFORMATION).open();
					break;
				case IStatus.WARNING:
					createDialog(shell, title, status.getMessage(), MessageDialog.WARNING).open();
					break;
				case IStatus.ERROR:
				default:
					createDialog(shell, title, status.getMessage(), MessageDialog.ERROR).open();
					break;
				}
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

	public static void displayStatus(final String title, final IStatus status) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null && !workbench.getDisplay().isDisposed()
				&& workbench.getDisplay().getActiveShell() != null) {
			Shell shell = workbench.getDisplay().getActiveShell();
			displayStatus(shell, title, status);
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
		LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), summary);
		newTask.setPriority(PriorityLevel.P3.toString());
		TasksUiInternal.getTaskList().addTask(newTask);
		TasksUiPlugin.getTaskActivityManager().scheduleNewTask(newTask);

		Object selectedObject = null;
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
		}
		if (selectedObject instanceof TaskCategory) {
			taskList.addTask(newTask, (TaskCategory) selectedObject);
		} else if (selectedObject instanceof ITask) {
			ITask task = (ITask) selectedObject;

			AbstractTaskContainer container = TaskCategory.getParentTaskCategory(task);

			if (container instanceof TaskCategory) {
				taskList.addTask(newTask, container);
			} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
				taskList.addTask(newTask, view.getDrilledIntoCategory());
			} else {
				taskList.addTask(newTask, TasksUiPlugin.getTaskList().getDefaultCategory());
			}
		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
			taskList.addTask(newTask, view.getDrilledIntoCategory());
		} else {
			if (view != null && view.getDrilledIntoCategory() != null) {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
						"The new task has been added to the root of the list, since tasks can not be added to a query.");
			}
			taskList.addTask(newTask, TasksUiPlugin.getTaskList().getDefaultCategory());
		}
		return newTask;
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
	 * @since 3.0
	 */
	public static void openEditQueryDialog(AbstractRepositoryConnectorUi connectorUi, IRepositoryQuery query) {
		try {
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			if (repository == null) {
				return;
			}

			IWizard wizard = connectorUi.getQueryWizard(repository, query);

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (wizard != null && shell != null && !shell.isDisposed()) {
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.create();
				dialog.setTitle("Edit Repository Query");
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
					return;
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to open query dialog", e));
		}
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
		if (url != null && !url.equals("") && !url.equals("http://") && !url.equals("https://")) {
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
	 * @since 3.0
	 */
	public static void closeEditorInActivePage(ITask task, boolean save) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return;
		}
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		IEditorInput input = new TaskEditorInput(taskRepository, task);
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.closeEditor(editor, save);
		}
	}

	/**
	 * @since 3.0
	 */
	// API 3.0 consider moving this somewhere else and renaming to addToTaskList
	public static ITask createTask(TaskRepository repository, String id, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		ITaskList taskList = getTaskList();
		AbstractTask task = (AbstractTask) taskList.getTask(repository.getRepositoryUrl(), id);
		if (task == null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getConnector(repository.getConnectorKind());
			if (connector instanceof AbstractLegacyRepositoryConnector) {
				RepositoryTaskData taskData = ((AbstractLegacyRepositoryConnector) connector).getLegacyTaskData(
						repository, id, new SubProgressMonitor(monitor, 1));
				if (taskData != null) {
					task = createTaskFromTaskData((AbstractLegacyRepositoryConnector) connector, taskList, repository,
							taskData, true, new SubProgressMonitor(monitor, 1));
					if (task != null) {
						task.setSynchronizationState(SynchronizationState.INCOMING);
						taskList.addTask(task);
					}
				}
			}
			// FIXME 3.0 support new task data
		}
		return task;
	}

	/**
	 * Create new repository task, adding result to tasklist
	 */
	public static AbstractTask createTaskFromExistingId(AbstractLegacyRepositoryConnector connector,
			ITaskList taskList, TaskRepository repository, String id, boolean retrieveSubTasks, IProgressMonitor monitor)
			throws CoreException {
		AbstractTask task = (AbstractTask) taskList.getTask(repository.getRepositoryUrl(), id);
		if (task == null) {
			RepositoryTaskData taskData = connector.getLegacyTaskData(repository, id,
					new SubProgressMonitor(monitor, 1));
			if (taskData != null) {
				task = createTaskFromTaskData(connector, taskList, repository, taskData, retrieveSubTasks,
						new SubProgressMonitor(monitor, 1));
				if (task != null) {
					task.setSynchronizationState(SynchronizationState.INCOMING);
					taskList.addTask(task);
				}
			}
		}
		return task;
	}

	/**
	 * Creates a new task from the given task data. Does NOT add resulting task to the task list.
	 */
	private static AbstractTask createTaskFromTaskData(AbstractLegacyRepositoryConnector connector, ITaskList taskList,
			TaskRepository repository, RepositoryTaskData taskData, boolean retrieveSubTasks, IProgressMonitor monitor)
			throws CoreException {
		AbstractTask task = null;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			TaskDataStorageManager taskDataManager = TasksUiPlugin.getTaskDataStorageManager();
			if (taskData != null) {
				// Use connector task factory
				task = connector.createTask(repository.getRepositoryUrl(), taskData.getTaskId(), taskData.getTaskId()
						+ ": " + taskData.getDescription());
				connector.updateTaskFromTaskData(repository, task, taskData);
				taskDataManager.setNewTaskData(taskData);

				if (retrieveSubTasks) {
					monitor.beginTask("Creating task", connector.getLegacyTaskDataHandler()
							.getSubTaskIds(taskData)
							.size());
					for (String subId : connector.getLegacyTaskDataHandler().getSubTaskIds(taskData)) {
						if (subId == null || subId.trim().equals("")) {
							continue;
						}
						AbstractTask subTask = createTaskFromExistingId(connector, taskList, repository, subId, false,
								new SubProgressMonitor(monitor, 1));
						if (subTask != null) {
							taskList.addTask(subTask, task);
						}
					}
				}
			}
		} finally {
			monitor.done();
		}
		return task;
	}

	public static void importTasks(Collection<AbstractTask> tasks, Set<TaskRepository> repositories, File zipFile,
			Shell shell) {
		TasksUiPlugin.getRepositoryManager().insertRepositories(repositories,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());

		for (AbstractTask loadedTask : tasks) {
			// need to deactivate since activation is managed centrally
			loadedTask.setActive(false);

			TaskList taskList = TasksUiPlugin.getTaskList();
			if (taskList.getTask(loadedTask.getHandleIdentifier()) != null) {
				boolean confirmed = MessageDialog.openConfirm(shell, "Import Task", "Task '" + loadedTask.getSummary()
						+ "' already exists. Do you want to override it's context with the source?");
				if (confirmed) {
					//					ContextCore.getContextStore().importContext(taskContexts.get(loadedTask));
					ContextCore.getContextStore().importContext(loadedTask.getHandleIdentifier(), zipFile);
				}
			} else {
				//				ContextCore.getContextStore().importContext(taskContexts.get(loadedTask));
				ContextCore.getContextStore().importContext(loadedTask.getHandleIdentifier(), zipFile);
				getTaskList().addTask(loadedTask);
			}
		}

	}

	public static boolean hasCapability(ITask task, Capability capability) {
		TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				task.getConnectorKind());
		return connector.hasCapability(capability, taskRepository, task, new CapabilityContext(
				TasksUi.getTaskDataManager()));
	}

	public static Shell getShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return Display.getDefault().getActiveShell();
	}

	public static TaskData createTaskData(TaskRepository taskRepository, ITaskMapping initializationData,
			ITaskMapping selectionData, IProgressMonitor monitor) throws CoreException {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(taskRepository);
		TaskData taskData = new TaskData(mapper, taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl(),
				"");
		taskDataHandler.initializeTaskData(taskRepository, taskData, initializationData, monitor);
		if (selectionData != null) {
			connector.getTaskMapping(taskData).copyFrom(selectionData);
		}
		return taskData;
	}

	public static void createAndOpenNewTask(TaskData taskData) throws CoreException {
		ITask task = TasksUiUtil.createOutgoingNewTask(taskData.getConnectorKind());
		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(task, taskData);
		workingCopy.save(null, null);

		TaskRepository localTaskRepository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		TaskEditorInput editorInput = new TaskEditorInput(localTaskRepository, task);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		TasksUiUtil.openEditor(editorInput, TaskEditor.ID_EDITOR, page);
	}

	/**
	 * Only override if task should be opened by a custom editor, default behavior is to open with a rich editor,
	 * falling back to the web browser if not available.
	 * 
	 * @return true if the task was successfully opened
	 */
	// API 3.0 review, move to tasks ui
	public static boolean openRepositoryTask(String connectorKind, String repositoryUrl, String id) {
		IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
		AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(connectorKind);
		String taskUrl = connector.getTaskUrl(repositoryUrl, id);
		if (taskUrl == null) {
			return false;
		}

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) {
				window = windows[0];
			}
		}
		if (window == null) {
			return false;
		}
		IWorkbenchPage page = window.getActivePage();

		OpenRepositoryTaskJob job = new OpenRepositoryTaskJob(connectorKind, repositoryUrl, id, taskUrl, page);
		job.schedule();

		return true;
	}

}
