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

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.core.sync.SynchronizationSession;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @deprecated Do not use. This class is pending for removal: see bug 237552.
 */
@Deprecated
public abstract class AbstractLegacyRepositoryConnector extends AbstractRepositoryConnector {

	@Deprecated
	protected Set<RepositoryTemplate> templates = new LinkedHashSet<RepositoryTemplate>();

	@Deprecated
	private static final long HOUR = 1000L * 3600L;

	@Deprecated
	private static final long DAY = HOUR * 24L;

	@Deprecated
	protected ITaskList taskList;

	private ITaskDataManager taskDataManager;

	private boolean userManaged = true;

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
	public void init(ITaskDataManager taskDataManager) {
		this.taskDataManager = taskDataManager;
	}

	/**
	 * @return null if not supported
	 */
	@Deprecated
	public abstract AbstractTaskDataHandler getLegacyTaskDataHandler();

	/**
	 * create task and necessary subtasks (1 level nesting)
	 * 
	 * @deprecated use {@link TasksUiUtil#createTask(TaskRepository, String, IProgressMonitor)} instead
	 */
	@Deprecated
	public ITask createTaskFromExistingId(TaskRepository repository, String id, IProgressMonitor monitor)
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
		AbstractTask repositoryTask = (AbstractTask) taskList.getTask(repository.getRepositoryUrl(), id);
		if (repositoryTask == null && getLegacyTaskDataHandler() != null) {
			RepositoryTaskData taskData = null;
			taskData = getLegacyTaskDataHandler().getTaskData(repository, id, new SubProgressMonitor(monitor, 1));
			if (taskData != null) {
				repositoryTask = createTaskFromTaskData(repository, taskData, retrieveSubTasks, new SubProgressMonitor(
						monitor, 1));
				if (repositoryTask != null) {
					repositoryTask.setSynchronizationState(SynchronizationState.INCOMING);
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
//				((TaskDataManager) getTaskDataManager()).setNewTaskData(taskData);

				if (retrieveSubTasks) {
					monitor.beginTask("Creating task", getLegacyTaskDataHandler().getSubTaskIds(taskData).size());
					for (String subId : getLegacyTaskDataHandler().getSubTaskIds(taskData)) {
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
	@Deprecated
	public abstract AbstractTask createTask(String repositoryUrl, String id, String summary);

	/**
	 * Updates the properties of <code>repositoryTask</code>. Invoked when on task synchronization if
	 * {@link #getLegacyTaskDataHandler()} returns <code>null</code> or
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
	 * @see {@link #getLegacyTaskDataHandler()}
	 */
	@Deprecated
	public void updateTaskFromRepository(TaskRepository repository, ITask repositoryTask, IProgressMonitor monitor)
			throws CoreException {
	}

	/**
	 * Updates task with latest information from <code>taskData</code>.
	 * 
	 * @return true, if properties of <code>task</code> were changed
	 * @since 3.0
	 */
	@Deprecated
	public abstract boolean updateTaskFromTaskData(TaskRepository repository, ITask task, RepositoryTaskData taskData);

	/**
	 * Updates <code>existingTask</code> with latest information from <code>queryHit</code>.
	 * 
	 * @return true, if properties of <code>existingTask</code> were changed
	 * @since 2.0
	 * @deprecated use {@link #updateTaskFromTaskData(TaskRepository, AbstractTask, RepositoryTaskData)} instead
	 */
	@Deprecated
	public boolean updateTaskFromQueryHit(TaskRepository repository, ITask existingTask, AbstractTask queryHit) {
		boolean changed = false;
		if (existingTask.isCompleted() != queryHit.isCompleted()) {
			((AbstractTask) existingTask).setCompleted(queryHit.isCompleted());
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
	 * @deprecated use {@link #preQuerySynchronization(TaskRepository, SynchronizationSession, IProgressMonitor)}
	 *             instead
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

	/**
	 * Reset and update the repository attributes from the server (e.g. products, components) *
	 * 
	 * @deprecated Use {@link #updateRepositoryConfiguration(TaskRepository,IProgressMonitor)} instead
	 */
	@Deprecated
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
	}

	/**
	 * Following synchronization, the timestamp needs to be recorded. This provides a default implementation for
	 * determining the last synchronization timestamp. Override to return actual timestamp from repository.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public String getSynchronizationTimestamp(TaskRepository repository, Set<ITask> changedTasks) {
		Date mostRecent = new Date(0);
		String mostRecentTimeStamp = repository.getSynchronizationTimeStamp();
		for (ITask task : changedTasks) {
			Date taskModifiedDate;
			RepositoryTaskData taskData = getTaskData(task);
			if (taskData != null && getLegacyTaskDataHandler() != null && taskData.getLastModified() != null) {
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
	private RepositoryTaskData getTaskData(ITask task) {
		return null;
	}

	@Deprecated
	public RepositoryTaskData getLegacyTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		if (getLegacyTaskDataHandler() != null) {
			return getLegacyTaskDataHandler().getTaskData(repository, taskId, monitor);
		}
		throw new UnsupportedOperationException();
	}

	public void setUserManaged(boolean userManaged) {
		this.userManaged = userManaged;
	}

	/**
	 * If false, user is unable to manipulate (i.e. rename/delete), no preferences are available.
	 */
	@Override
	public boolean isUserManaged() {
		return userManaged;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 3.0
	 */
	@Override
	public void updateTaskFromTaskData(TaskRepository repository, ITask task, TaskData taskData) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 3.0
	 */
	@Override
	public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @since 3.0
	 */
	protected ITaskDataManager getTaskDataManager() {
		return taskDataManager;
	}

}
