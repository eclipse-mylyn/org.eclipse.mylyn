/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants.ObjectSchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskAttachmentJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SubmitTaskJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeQueriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeRepositoriesJob;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizeTasksJob;
import org.eclipse.mylyn.internal.tasks.core.sync.UpdateRepositoryConfigurationJob;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskJobListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.core.sync.TaskJob;

/**
 * @author Steffen Pingel
 */
public class TaskJobFactory implements ITaskJobFactory {
	private static final String ALL_CONNECTORS = "org.eclipse.mylyn.tasks.core.all.connectors"; //$NON-NLS-1$

	/**
	 * listeners provided by extension point
	 */
	private static Map<String, List<TaskJobListener>> taskJobListeners;

	private final TaskList taskList;

	private final TaskDataManager taskDataManager;

	private final IRepositoryManager repositoryManager;

	private final IRepositoryModel tasksModel;

	private boolean fetchSubtasks = true;

	protected static synchronized List<TaskJobListener> getTaskJobListeners(AbstractRepositoryConnector connector) {
		if (taskJobListeners == null) {
			taskJobListeners = new HashMap<String, List<TaskJobListener>>();
			List<TaskJobListener> listeners = loadTaskJobListeners(""); //$NON-NLS-1$
			taskJobListeners.put(ALL_CONNECTORS, listeners);
		}
		if (taskJobListeners.get(connector.getConnectorKind()) == null) {
			List<TaskJobListener> listeners = loadTaskJobListeners(connector.getConnectorKind());
			taskJobListeners.put(connector.getConnectorKind(), listeners);
		}
		List<TaskJobListener> listeners = new ArrayList<TaskJobListener>();
		listeners.addAll(taskJobListeners.get(ALL_CONNECTORS));
		listeners.addAll(taskJobListeners.get(connector.getConnectorKind()));
		return listeners;
	}

	protected static List<TaskJobListener> loadTaskJobListeners(String connectorKind) {
		ExtensionPointReader<TaskJobListener> reader = new ExtensionPointReader<TaskJobListener>(
				ITasksCoreConstants.ID_PLUGIN,
				"taskJobListeners", "listener", TaskJobListener.class, "connectorKind", connectorKind); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		reader.read();
		return reader.getItems();
	}

	public TaskJobFactory(TaskList taskList, TaskDataManager taskDataManager, IRepositoryManager repositoryManager,
			IRepositoryModel tasksModel) {
		this.taskList = taskList;
		this.taskDataManager = taskDataManager;
		this.repositoryManager = repositoryManager;
		this.tasksModel = tasksModel;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector, Set<ITask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector,
				repositoryManager, tasks);
		job.setFetchSubtasks(fetchSubtasks);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeTasksJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, Set<ITask> tasks) {
		SynchronizeTasksJob job = new SynchronizeTasksJob(taskList, taskDataManager, tasksModel, connector,
				taskRepository, tasks);
		job.setFetchSubtasks(fetchSubtasks);
		job.setPriority(Job.LONG);
		return job;
	}

	public SynchronizationJob createSynchronizeQueriesJob(AbstractRepositoryConnector connector,
			TaskRepository repository, Set<RepositoryQuery> queries) {
		SynchronizationJob job = new SynchronizeQueriesJob(taskList, taskDataManager, tasksModel, connector,
				repository, queries);
		job.setFetchSubtasks(fetchSubtasks);
		job.setPriority(Job.DECORATE);
		return job;
	}

	public SynchronizationJob createSynchronizeRepositoriesJob(Set<TaskRepository> repositories) {
		SynchronizeRepositoriesJob job = new SynchronizeRepositoriesJob(taskList, taskDataManager, tasksModel,
				repositoryManager);
		job.setFetchSubtasks(fetchSubtasks);
		job.setRepositories(repositories);
		job.setPriority(Job.DECORATE);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				// XXX: since the Task List does not properly refresh parent
				// containers, force the refresh of it's root
				taskList.notifyElementsChanged(null);
			}
		});
		return job;
	}

	public SubmitJob createSubmitTaskJob(AbstractRepositoryConnector connector, TaskRepository taskRepository,
			final ITask task, TaskData taskData, Set<TaskAttribute> oldAttributes) {
		SubmitJob job = new SubmitTaskJob(taskDataManager, connector, taskRepository, task, taskData, oldAttributes,
				getTaskJobListeners(connector));
		job.setPriority(Job.INTERACTIVE);
		job.setUser(true);
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					((AbstractTask) task).setSynchronizing(true);
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Unexpected error", e)); //$NON-NLS-1$
		}
		taskList.notifySynchronizationStateChanged(task);
		return job;
	}

	public TaskJob createUpdateRepositoryConfigurationJob(final AbstractRepositoryConnector connector,
			final TaskRepository taskRepository, final ITask task) {
		UpdateRepositoryConfigurationJob updateJob = new UpdateRepositoryConfigurationJob(
				Messages.TaskJobFactory_Refreshing_repository_configuration, taskRepository, task, connector);
		updateJob.setPriority(Job.INTERACTIVE);
		updateJob.setRule(new ObjectSchedulingRule(taskRepository));
		updateJob.setUser(true);
		return updateJob;
	}

	@Deprecated
	public TaskJob createUpdateRepositoryConfigurationJob(final AbstractRepositoryConnector connector,
			final TaskRepository taskRepository) {
		return createUpdateRepositoryConfigurationJob(connector, taskRepository, null);
	}

	public SubmitJob createSubmitTaskAttachmentJob(AbstractRepositoryConnector connector,
			TaskRepository taskRepository, final ITask task, AbstractTaskAttachmentSource source, String comment,
			TaskAttribute attachmentAttribute) {
		SubmitJob job = new SubmitTaskAttachmentJob(taskDataManager, connector, taskRepository, task, source, comment,
				attachmentAttribute);
		job.setPriority(Job.INTERACTIVE);
		try {
			taskList.run(new ITaskListRunnable() {
				public void execute(IProgressMonitor monitor) throws CoreException {
					((AbstractTask) task).setSynchronizing(true);
				}
			});
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Unexpected error", e)); //$NON-NLS-1$
		}
		taskList.notifySynchronizationStateChanged(task);
		job.setUser(true);
		return job;
	}

	public void setFetchSubtasks(boolean fetchSubtasks) {
		this.fetchSubtasks = fetchSubtasks;
	}

	public boolean getFetchSubtasks() {
		return fetchSubtasks;
	}
}
