/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskDelegate;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.CategoryEditor;
import org.eclipse.mylyn.internal.tasks.ui.editors.CategoryEditorInput;
import org.eclipse.mylyn.internal.tasks.ui.wizards.MultiRepositoryAwareWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewTaskWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class TasksUiInternal {

	public static MultiRepositoryAwareWizard createNewTaskWizard(TaskSelection taskSelection) {
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

	// API 3.0 move to internal class?
	public static void refreshAndOpenTaskListElement(AbstractTaskContainer element) {
		if (element instanceof AbstractTask || element instanceof ScheduledTaskDelegate) {
			final AbstractTask task;
			if (element instanceof ScheduledTaskDelegate) {
				task = ((ScheduledTaskDelegate) element).getCorrespondingTask();
			} else {
				task = (AbstractTask) element;
			}

			if (task == null) {
				// FIXME display error?
				return;
			}

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
					RepositoryTaskData taskData = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
							task.getRepositoryUrl(), task.getTaskId());
					if (taskData != null || connector.getTaskDataHandler() == null
							|| connector.getTaskDataHandler2() != null) {
						TasksUiUtil.openTaskAndRefresh(task);
					} else {
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
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(query.getConnectorKind());
			connectorUi.openEditQueryDialog(query);
		}
	}

	public static TaskJob updateRepositoryConfiguration(final TaskRepository taskRepository) {
		synchronized (taskRepository) {
			taskRepository.setUpdating(true);
		}
	
		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				taskRepository.getConnectorKind());
		final TaskJob job = TasksUiInternal.getJobFactory().createUpdateRepositoryConfigurationJob(connector, taskRepository);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				synchronized (taskRepository) {
					taskRepository.setUpdating(false);
				}
				if (job.getError() != null) {
					Display display = PlatformUI.getWorkbench().getDisplay();
					if (!display.isDisposed()) {
						StatusHandler.displayStatus("Configuration Refresh Failed", job.getError());
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
			Set<AbstractRepositoryQuery> queries, IJobChangeListener listener, boolean force) {
		Assert.isTrue(queries.size() > 0);
	
		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		for (AbstractRepositoryQuery query : queries) {
			query.setSynchronizing(true);
		}
		taskList.notifyContainersUpdated(queries);
	
		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeQueriesJob(connector, repository,
				queries);
		job.setUser(force);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force) {
			final AbstractRepositoryQuery query = queries.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (query.getSynchronizationStatus() != null) {
						Display display = PlatformUI.getWorkbench().getDisplay();
						if (!display.isDisposed()) {
							StatusHandler.displayStatus("Query Synchronization Failed",
									query.getSynchronizationStatus());
						}
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
	public static final Job synchronizeQuery(AbstractRepositoryConnector connector,
			AbstractRepositoryQuery repositoryQuery, IJobChangeListener listener, boolean force) {
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
	 * 		can be null
	 */
	public static Job synchronizeTask(AbstractRepositoryConnector connector, AbstractTask task, boolean force,
			IJobChangeListener listener) {
		return synchronizeTasks(connector, Collections.singleton(task), force, listener);
	}

	/**
	 * @param listener
	 * 		can be null
	 */
	public static Job synchronizeTasks(AbstractRepositoryConnector connector, Set<AbstractTask> tasks, boolean force,
			IJobChangeListener listener) {
		ITaskList taskList = TasksUi.getTaskListManager().getTaskList();
		for (AbstractTask task : tasks) {
			task.setSynchronizing(true);
			taskList.notifyTaskChanged(task, false);
		}
		// TODO notify task list?
	
		SynchronizationJob job = TasksUiPlugin.getTasksJobFactory().createSynchronizeTasksJob(connector, tasks);
		job.setUser(force);
		job.setPriority(Job.DECORATE);
		if (listener != null) {
			job.addJobChangeListener(listener);
		}
		if (force && tasks.size() == 1) {
			final AbstractTask task = tasks.iterator().next();
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					if (task.getSynchronizationStatus() != null) {
						Display display = PlatformUI.getWorkbench().getDisplay();
						if (!display.isDisposed()) {
							StatusHandler.displayStatus("Task Synchronization Failed", task.getSynchronizationStatus());
						}
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
}
