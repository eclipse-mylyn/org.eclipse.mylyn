/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.IRepositorySynchronizationManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationEvent;

/**
 * Encapsulates common operations that can be performed on a task repository. Extend to connect with a Java API or WS
 * API for accessing the repository.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @since 2.0
 */
public abstract class AbstractRepositoryConnector {

	@Deprecated
	protected Set<RepositoryTemplate> templates = new LinkedHashSet<RepositoryTemplate>();

	@Deprecated
	private static final long HOUR = 1000L * 3600L;

	@Deprecated
	private static final long DAY = HOUR * 24L;

	@Deprecated
	protected ITaskList taskList;

	private boolean userManaged = true;

	private ITaskDataManager taskDataManager;

	private IRepositorySynchronizationManager synchronizationManager;

	/**
	 * @since 3.0
	 */
	@Deprecated
	public void init(ITaskList taskList) {
		this.taskList = taskList;
	}

	/**
	 * Set upon construction
	 * 
	 * @since 3.0
	 */
	public void init2(ITaskDataManager taskDataManager, IRepositorySynchronizationManager synchronizationManager) {
		this.taskDataManager = taskDataManager;
		this.synchronizationManager = synchronizationManager;
	}

	/**
	 * @return null if not supported
	 */
	public abstract AbstractAttachmentHandler getAttachmentHandler();

	/**
	 * @return null if not supported
	 */
	public abstract AbstractTaskDataHandler getTaskDataHandler();

	/**
	 * @since 3.0
	 */
	public AbstractTaskDataHandler2 getTaskDataHandler2() {
		return null;
	}

	public abstract String getRepositoryUrlFromTaskUrl(String taskFullUrl);

	public abstract String getTaskIdFromTaskUrl(String taskFullUrl);

	// API 3.0 change type of taskId to AbstractTask
	public abstract String getTaskUrl(String repositoryUrl, String taskId);

	/**
	 * Retrieves and returns a copy of task data from repository.
	 * 
	 * @since 3.0
	 */
	public abstract RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException;

	public String[] getTaskIdsFromComment(TaskRepository repository, String comment) {
		return null;
	}

	// API 3.0 rename to canCreateTaskFromId?
	public abstract boolean canCreateTaskFromKey(TaskRepository repository);

	public abstract boolean canCreateNewTask(TaskRepository repository);

	/**
	 * @since 3.0
	 */
	public boolean canQuery(TaskRepository repository) {
		return true;
	}

	/**
	 * create task and necessary subtasks (1 level nesting)
	 * 
	 * @deprecated use {@link TasksUiUtil#createTask(TaskRepository, String, IProgressMonitor)} instead
	 */
	@Deprecated
	public AbstractTask createTaskFromExistingId(TaskRepository repository, String id, IProgressMonitor monitor)
			throws CoreException {
		return createTaskFromExistingId(repository, id, true, monitor);
	}

	/**
	 * Create new repository task, adding result to tasklist
	 * 
	 * @deprecated use {@link TasksUiUtil#createTask(TaskRepository, String, IProgressMonitor)} instead
	 */
	@Deprecated
	public AbstractTask createTaskFromExistingId(TaskRepository repository, String id, boolean retrieveSubTasks,
			IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = taskList.getTask(repository.getRepositoryUrl(), id);
		if (repositoryTask == null && getTaskDataHandler() != null) {
			RepositoryTaskData taskData = null;
			taskData = getTaskDataHandler().getTaskData(repository, id, new SubProgressMonitor(monitor, 1));
			if (taskData != null) {
				repositoryTask = createTaskFromTaskData(repository, taskData, retrieveSubTasks, new SubProgressMonitor(
						monitor, 1));
				if (repositoryTask != null) {
					repositoryTask.setSynchronizationState(RepositoryTaskSyncState.INCOMING);
					taskList.addTask(repositoryTask);
				}
			}
		} // TODO: Handle case similar to web tasks (no taskDataHandler but
		// have tasks)

		return repositoryTask;
	}

	/**
	 * Creates a new task from the given task data. Does NOT add resulting task to the tasklist
	 * 
	 * @deprecated use {@link TasksUiUtil#createTask(TaskRepository, String, IProgressMonitor)} instead
	 */
	@Deprecated
	public AbstractTask createTaskFromTaskData(TaskRepository repository, RepositoryTaskData taskData,
			boolean retrieveSubTasks, IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = null;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			if (taskData != null && getTaskDataManager() != null) {
				// Use connector task factory
				repositoryTask = createTask(repository.getRepositoryUrl(), taskData.getTaskId(), taskData.getTaskId()
						+ ": " + taskData.getDescription());
				updateTaskFromTaskData(repository, repositoryTask, taskData);
				getTaskDataManager().setNewTaskData(taskData);

				if (retrieveSubTasks) {
					monitor.beginTask("Creating task", getTaskDataHandler().getSubTaskIds(taskData).size());
					for (String subId : getTaskDataHandler().getSubTaskIds(taskData)) {
						if (subId == null || subId.trim().equals("")) {
							continue;
						}
						AbstractTask subTask = createTaskFromExistingId(repository, subId, false,
								new SubProgressMonitor(monitor, 1));
						if (subTask != null) {
							taskList.addTask(subTask, repositoryTask);
						}
					}
				}
			}
		} finally {
			monitor.done();
		}
		return repositoryTask;
	}

	/**
	 * Utility method for construction of connector specific task object.
	 * 
	 * @return instance of AbstractTask
	 */
	public abstract AbstractTask createTask(String repositoryUrl, String id, String summary);

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @since 3.0
	 */
	public abstract IStatus performQuery(TaskRepository repository, AbstractRepositoryQuery query,
			AbstractTaskDataCollector resultCollector, SynchronizationEvent event, IProgressMonitor monitor);

	/**
	 * @since 2.0
	 * @deprecated use
	 *             {@link #performQuery(TaskRepository, AbstractRepositoryQuery, AbstractTaskDataCollector, SynchronizationEvent, IProgressMonitor)}
	 *             instead
	 */
	@Deprecated
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor,
			final ITaskCollector resultCollector) {
		return null;
	}

	/**
	 * The connector's summary i.e. "JIRA (supports 3.3.1 and later)"
	 */
	public abstract String getLabel();

	/**
	 * Returns a short label for the connector, e.g. Bugzilla.
	 * 
	 * @since 2.3
	 */
	public String getShortLabel() {
		String label = getLabel();
		if (label == null) {
			return null;
		}

		int i = label.indexOf("(");
		if (i != -1) {
			return label.substring(0, i).trim();
		}

		i = label.indexOf(" ");
		if (i != -1) {
			return label.substring(0, i).trim();
		}

		return label;
	}

	/**
	 * @return the unique kind of the repository, e.g. "bugzilla"
	 */
	public abstract String getConnectorKind();

	/**
	 * Updates the properties of <code>repositoryTask</code>. Invoked when on task synchronization if
	 * {@link #getTaskDataHandler()} returns <code>null</code> or
	 * {@link AbstractTaskDataHandler#getTaskData(TaskRepository, String)} returns <code>null</code>.
	 * 
	 * <p>
	 * Connectors that provide {@link RepositoryTaskData} objects for all tasks do not need to implement this method.
	 * 
	 * @param repository
	 *            the repository
	 * @param repositoryTask
	 *            the task that is synchronized
	 * @throws CoreException
	 *             thrown in case of error while synchronizing
	 * @see {@link #getTaskDataHandler()}
	 */
	@Deprecated
	public void updateTaskFromRepository(TaskRepository repository, AbstractTask repositoryTask,
			IProgressMonitor monitor) throws CoreException {
	}

	/**
	 * Updates task with latest information from <code>taskData</code>.
	 * 
	 * @return true, if properties of <code>task</code> were changed
	 * @since 3.0
	 */
	public abstract boolean updateTaskFromTaskData(TaskRepository repository, AbstractTask task,
			RepositoryTaskData taskData);

	/**
	 * Updates <code>existingTask</code> with latest information from <code>queryHit</code>.
	 * 
	 * @return true, if properties of <code>existingTask</code> were changed
	 * @since 2.0
	 * @deprecated use {@link #updateTaskFromTaskData(TaskRepository, AbstractTask, RepositoryTaskData)} instead
	 */
	@Deprecated
	public boolean updateTaskFromQueryHit(TaskRepository repository, AbstractTask existingTask, AbstractTask queryHit) {
		boolean changed = false;
		if (existingTask.isCompleted() != queryHit.isCompleted()) {
			existingTask.setCompleted(queryHit.isCompleted());
			changed = true;
		}
		if (hasTaskPropertyChanged(existingTask.getSummary(), queryHit.getSummary())) {
			existingTask.setSummary(queryHit.getSummary());
			changed = true;
		}
		if (hasTaskPropertyChanged(existingTask.getDueDate(), queryHit.getDueDate())) {
			existingTask.setDueDate(queryHit.getDueDate());
			changed = true;
		}
		if (hasTaskPropertyChanged(existingTask.getOwner(), queryHit.getOwner())) {
			existingTask.setOwner(queryHit.getOwner());
			changed = true;
		}
		if (hasTaskPropertyChanged(existingTask.getPriority(), queryHit.getPriority())) {
			existingTask.setPriority(queryHit.getPriority());
			changed = true;
		}
		if (hasTaskPropertyChanged(existingTask.getUrl(), queryHit.getUrl())) {
			existingTask.setUrl(queryHit.getUrl());
			changed = true;
		}

		return changed;
	}

	@Deprecated
	protected final boolean hasTaskPropertyChanged(Object existingProperty, Object newProperty) {
		// the query hit does not have this property
		if (newProperty == null) {
			return false;
		}
		return (existingProperty == null) ? true : !existingProperty.equals(newProperty);
	}

	@Deprecated
	public String[] getPepositoryPropertyNames() {
		return new String[] { IRepositoryConstants.PROPERTY_VERSION, IRepositoryConstants.PROPERTY_TIMEZONE,
				IRepositoryConstants.PROPERTY_ENCODING };
	}

	/**
	 * Of <code>tasks</code> provided, return all that have changed since last synchronization of
	 * <code>repository</code>.
	 * 
	 * Tasks that need to be synchronized (i.e. task data updated) should be passed to
	 * <code>collector.accept(Task)</code> method, or if repository connector can update task data, it can use
	 * <code>collector.accept(RepositoryTaskData)</code> call.
	 * 
	 * All errors should be thrown as <code>CoreException</code> for the framework to handle, since background
	 * synchronizations fail silently when disconnected.
	 * 
	 * @return false if there was no tasks changed in the repository, otherwise collection of updated tasks (within
	 *         <code>tasks</code> collection), so empty collection means that there are some other tasks changed
	 * 
	 * @throws CoreException
	 * @deprecated use {@link #preQuerySynchronization(TaskRepository, SynchronizationEvent, IProgressMonitor)} instead
	 */
	@Deprecated
	public boolean markStaleTasks(TaskRepository repository, Set<AbstractTask> tasks, IProgressMonitor monitor)
			throws CoreException {
		return false;
	}

	/**
	 * @deprecated use {@link RepositoryTemplateManager#addTemplate(String, RepositoryTemplate)} instead
	 */
	@Deprecated
	public void addTemplate(RepositoryTemplate template) {
		this.templates.add(template);
	}

	/**
	 * @deprecated use {@link RepositoryTemplateManager#getTemplates(String)} instead
	 */
	@Deprecated
	public Set<RepositoryTemplate> getTemplates() {
		return templates;
	}

	/**
	 * @deprecated use {@link RepositoryTemplateManager#removeTemplate(String, RepositoryTemplate)} instead
	 */
	@Deprecated
	public void removeTemplate(RepositoryTemplate template) {
		this.templates.remove(template);
	}

	/**
	 * @deprecated use {@link RepositoryTemplateManager#getTemplate(String, String)} instead
	 */
	@Deprecated
	public RepositoryTemplate getTemplate(String label) {
		for (RepositoryTemplate template : getTemplates()) {
			if (template.label.equals(label)) {
				return template;
			}
		}
		return null;
	}

	// API 3.0 move to RepositoryConnectorUi?
	/**
	 * Used for referring to the task in the UI.
	 * 
	 * @return
	 */
	public String getTaskIdPrefix() {
		return "task";
	}

	/**
	 * Reset and update the repository attributes from the server (e.g. products, components)
	 * 
	 * API-3.0: Rename to updateRepositoryConfiguration()
	 * 
	 * @deprecated Use {@link #updateRepositoryConfiguration(TaskRepository,IProgressMonitor)} instead
	 */
	@Deprecated
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
	}

	/**
	 * Reset and update the repository attributes from the server (e.g. products, components)
	 * 
	 * @since 3.0
	 */
	public abstract void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * Default implementation returns true every 24hrs
	 * 
	 * @param monitor
	 *            TODO
	 * 
	 * @return true to indicate that the repository configuration is stale and requires update
	 * @throws CoreException
	 * @since 3.0
	 */
	public boolean isRepositoryConfigurationStale(TaskRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean isStale = true;
		Date configDate = repository.getConfigurationDate();
		if (configDate != null) {
			isStale = (new Date().getTime() - configDate.getTime()) > DAY;
		}

		return isStale;
	}

	// API 3.0 remove and let connectors override isUserManaged? Can this property change in the life cycle of the connector?
	public void setUserManaged(boolean userManaged) {
		this.userManaged = userManaged;
	}

	/**
	 * If false, user is unable to manipulate (i.e. rename/delete), no preferences are available.
	 */
	public boolean isUserManaged() {
		return userManaged;
	}

	/**
	 * @since 2.2
	 */
	@Deprecated
	public boolean hasCredentialsManagement() {
		return false;
	}

	/**
	 * Following synchronization, the timestamp needs to be recorded. This provides a default implementation for
	 * determining the last synchronization timestamp. Override to return actual timestamp from repository.
	 * 
	 * @deprecated
	 */
	// API 3.0 move to utility class
	@Deprecated
	public String getSynchronizationTimestamp(TaskRepository repository, Set<AbstractTask> changedTasks) {
		Date mostRecent = new Date(0);
		String mostRecentTimeStamp = repository.getSynchronizationTimeStamp();
		for (AbstractTask task : changedTasks) {
			Date taskModifiedDate;
			RepositoryTaskData taskData = getTaskData(task);
			if (taskData != null && getTaskDataHandler() != null && taskData.getLastModified() != null) {
				taskModifiedDate = taskData.getAttributeFactory().getDateForAttributeType(
						RepositoryTaskAttribute.DATE_MODIFIED, taskData.getLastModified());
			} else {
				continue;
			}

			if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
				mostRecent = taskModifiedDate;
				mostRecentTimeStamp = taskData.getLastModified();
			}
		}
		return mostRecentTimeStamp;
	}

	@Deprecated
	private RepositoryTaskData getTaskData(AbstractTask task) {
		if (taskDataManager != null) {
			return taskDataManager.getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		}
		return null;
	}

	/**
	 * @since 3.0
	 */
	protected ITaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

	/**
	 * @since 3.0
	 */
	protected IRepositorySynchronizationManager getSynchronizationManager() {
		return synchronizationManager;
	}

	/**
	 * @since 3.0
	 */
	public void preSynchronization(SynchronizationEvent event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);
		} finally {
			monitor.done();
		}
	}

	/**
	 * @since 3.0
	 */
	public void postSynchronization(SynchronizationEvent event, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("", 1);

			if (event.fullSynchronization) {
				event.taskRepository.setSynchronizationTimeStamp(getSynchronizationTimestamp(event.taskRepository,
						event.changedTasks));
			}
			// TODO save repository
		} finally {
			monitor.done();
		}
	}

}
