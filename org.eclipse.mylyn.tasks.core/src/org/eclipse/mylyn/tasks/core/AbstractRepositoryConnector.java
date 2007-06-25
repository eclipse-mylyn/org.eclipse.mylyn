/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
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
import org.eclipse.mylyn.internal.tasks.core.TaskDataManager;

/**
 * Encapsulates common operations that can be performed on a task repository.  Extend to
 * connect with a Java API or WS API for accessing the repository.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @since	2.0
 */
public abstract class AbstractRepositoryConnector {

	protected Set<RepositoryTemplate> templates = new LinkedHashSet<RepositoryTemplate>();

	protected TaskList taskList;

	private boolean userManaged = true;

	private TaskDataManager taskDataManager;

	public void init(TaskList taskList) {
		this.taskList = taskList;
	}
	
	/**
	 * Set upon construction
	 */
	public void setTaskDataManager(TaskDataManager taskDataManager) {
		this.taskDataManager = taskDataManager;
		AbstractAttachmentHandler handler = getAttachmentHandler();
		if (handler != null) {
			handler.setTaskDataManager(taskDataManager);
		}
	}

	/**
	 * @return null if not supported
	 */
	public abstract AbstractAttachmentHandler getAttachmentHandler();

	/**
	 * @return null if not supported
	 */
	public abstract AbstractTaskDataHandler getTaskDataHandler();

	public abstract String getRepositoryUrlFromTaskUrl(String taskFullUrl);

	public abstract String getTaskIdFromTaskUrl(String taskFullUrl);

	public abstract String getTaskUrl(String repositoryUrl, String taskId);

	public String[] getTaskIdsFromComment(TaskRepository repository, String comment) {
		return null;
	}

	public abstract boolean canCreateTaskFromKey(TaskRepository repository);

	public abstract boolean canCreateNewTask(TaskRepository repository);

	/**
	 * create task and necessary subtasks (1 level nesting)
	 */
	public AbstractTask createTaskFromExistingId(TaskRepository repository, String id, IProgressMonitor monitor)
			throws CoreException {
		return createTaskFromExistingId(repository, id, true, monitor);
	}

	/**
	 * Create new repository task, adding result to tasklist
	 */
	public AbstractTask createTaskFromExistingId(TaskRepository repository, String id, boolean retrieveSubTasks,
			IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = taskList.getTask(repository.getUrl(), id);
		if (repositoryTask == null && getTaskDataHandler() != null) {
			RepositoryTaskData taskData = null;
			taskData = getTaskDataHandler().getTaskData(repository, id, new SubProgressMonitor(monitor, 1));
			if (taskData != null) {
				repositoryTask = createTaskFromTaskData(repository, taskData, retrieveSubTasks, new SubProgressMonitor(
						monitor, 1));
				if (repositoryTask != null) {
					taskList.addTask(repositoryTask);
				}
			}
		} // TODO: Handle case similar to web tasks (no taskDataHandler but
		// have tasks)

		return repositoryTask;
	}

	/**
	 * Creates a new task from the given task data. Does NOT add resulting task to the tasklist
	 */
	public AbstractTask createTaskFromTaskData(TaskRepository repository, RepositoryTaskData taskData,
			boolean retrieveSubTasks, IProgressMonitor monitor) throws CoreException {
		AbstractTask repositoryTask = null;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			if (taskData != null && getTaskDataManager() != null) {
				// Use connector task factory
				repositoryTask = createTask(repository.getUrl(), taskData.getId(), taskData.getId() + ": "
						+ taskData.getDescription());
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
	 * Utility method for construction of connector specific task object TODO: Move to 'task' factory
	 * 
	 * @return instance of AbstractTask
	 */
	public abstract AbstractTask createTask(String repositoryUrl, String id, String summary);

	/**
	 * Implementors must execute query synchronously.
	 * 
	 * @param query
	 * @param repository
	 *            TODO
	 * @param monitor
	 * @param resultCollector
	 *            IQueryHitCollector that collects the hits found
	 */
	public abstract IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository,
			IProgressMonitor monitor, ITaskCollector resultCollector);

	/**
	 * The connector's summary i.e. "JIRA (supports 3.3.1 and later)"
	 */
	public abstract String getLabel();

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
	public abstract void updateTaskFromRepository(TaskRepository repository, AbstractTask repositoryTask,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Updates task with latest information from {@code taskData}
	 * 
	 * @since 2.0
	 */
	public abstract void updateTaskFromTaskData(TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData taskData);

	/**
	 * Updates <code>existingTask</code> with latest information from <code>queryHit</code>.
	 * 
	 * @return true, if properties of <code>existingTask</code> were changed
	 * @since 2.0
	 */
	public boolean updateTaskFromQueryHit(TaskRepository repository, AbstractTask existingTask,
			AbstractTask queryHit) {
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
	
	protected final boolean hasTaskPropertyChanged(Object existingProperty, Object newProperty) {
		// the query hit does not have this property
		if (newProperty == null) {
			return false;
		}
		return (existingProperty == null) ? true : !existingProperty.equals(newProperty);
	}
	
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
	 * @param tasks
	 *            TODO
	 * 
	 * @return null if there was no tasks changed in the repository, otherwise collection of updated tasks (within
	 *         <code>tasks</code> collection), so empty collection means that there are some other tasks changed
	 * 
	 * @throws CoreException
	 */
	public abstract boolean markStaleTasks(TaskRepository repository, Set<AbstractTask> tasks, IProgressMonitor monitor)
			throws CoreException;

	public void addTemplate(RepositoryTemplate template) {
		this.templates.add(template);
	}

	public Set<RepositoryTemplate> getTemplates() {
		return templates;
	}

	public void removeTemplate(RepositoryTemplate template) {
		this.templates.remove(template);
	}

	/** returns null if template not found */
	public RepositoryTemplate getTemplate(String label) {
		for (RepositoryTemplate template : getTemplates()) {
			if (template.label.equals(label)) {
				return template;
			}
		}
		return null;
	}

	/**
	 * Used for referring to the task in the UI.
	 * @return
	 */
	public String getTaskIdPrefix() {
		return "task";
	}

	/**
	 * Reset and update the repository attributes from the server (e.g. products, components)
	 * 
	 * TODO: remove?
	 */
	public abstract void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException;

	public void setUserManaged(boolean userManaged) {
		this.userManaged = userManaged;
	}

	public boolean isUserManaged() {
		return userManaged;
	}

	/**
	 * Following synchronization, the timestamp needs to be recorded. This provides a default implementation for
	 * determining the last synchronization timestamp. Override to return actual timestamp from repository.
	 */
	public String getSynchronizationTimestamp(TaskRepository repository, Set<AbstractTask> changedTasks) {
		Date mostRecent = new Date(0);
		String mostRecentTimeStamp = repository.getSynchronizationTimeStamp();
		for (AbstractTask task : changedTasks) {
			Date taskModifiedDate;

			if (getTaskData(task) != null && getTaskDataHandler() != null
					&& getTaskData(task).getLastModified() != null) {
				taskModifiedDate = getTaskData(task).getAttributeFactory().getDateForAttributeType(
						RepositoryTaskAttribute.DATE_MODIFIED, getTaskData(task).getLastModified());
			} else {
				continue;
			}

			if (taskModifiedDate != null && taskModifiedDate.after(mostRecent)) {
				mostRecent = taskModifiedDate;
				mostRecentTimeStamp = getTaskData(task).getLastModified();
			}
		}
		return mostRecentTimeStamp;
	}

	private RepositoryTaskData getTaskData(AbstractTask task) {
		if (taskDataManager != null) {
			return taskDataManager.getNewTaskData(task.getRepositoryUrl(), task.getTaskId());
		}
		return null;
	}

	private TaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

}
