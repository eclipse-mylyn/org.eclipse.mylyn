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
 *     Atlassian - improvements for bug 319397
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.net.URL;
import java.util.Collection;
import java.util.Date;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskHistory;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskRelation;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * Encapsulates common operations that can be performed on a task repository. Extend to connect with a Java API or WS
 * API for accessing the repository.
 * <p>
 * General notes:
 * <ul>
 * <li>Only methods that take a progress monitor can do network I/O.</li>
 * <li>{@link TaskRepository}, {@link ITask} and {@link TaskData} instances passes as parameters are guaranteed to match
 * this connector.</li>
 * <li>Methods are not expected to throw runtime exceptions. If repository operations results in an error</li>
 * {@link CoreException} should be thrown with a {@link RepositoryStatus} specifying error details.</li>
 * </ul>
 * <h3>Synchronization</h3>
 * <p>
 * The tasks framework has a notion of synchronization for keeping a local cache of tasks synchronized with repository
 * state. Synchronization are anchored around queries which brings in tasks from the repository based on search
 * criteria. A synchronization has several stages:
 * <ol>
 * <li>Identify stale tasks (optional): {@link #preSynchronization(ISynchronizationSession, IProgressMonitor)}
 * <li>Perform each query:
 * {@link #performQuery(TaskRepository, IRepositoryQuery, TaskDataCollector, ISynchronizationSession, IProgressMonitor)}
 * <li>Check each retrieved task if it is stale: {@link #hasTaskChanged(TaskRepository, ITask, TaskData)}
 * <li>Update cached state: {@link #updateTaskFromTaskData(TaskRepository, ITask, TaskData)}
 * <li>For each stale task: {@link #getTaskData(TaskRepository, String, IProgressMonitor)}
 * <li>Update cached state: {@link #updateTaskFromTaskData(TaskRepository, ITask, TaskData)}
 * <li>Persist synchronization state (optional): {@link #postSynchronization(ISynchronizationSession, IProgressMonitor)}
 * </ol>
 * <p>
 * Connectors can implement these methods in several ways depending on APIs provided by the repository. In order to
 * ensure correct synchronization it is important that the method interaction follows one of the contracts specified
 * below.
 * <p>
 * Methods denoted as optional above are required to synchronize tasks not contained in queries. See
 * {@link #preSynchronization(ISynchronizationSession, IProgressMonitor)} and
 * {@link ISynchronizationSession#markStale(ITask)} for more details.
 * </p>
 * <h4>Full data synchronization</h4>
 * <ul>
 * <li>
 * {@link #performQuery(TaskRepository, IRepositoryQuery, TaskDataCollector, ISynchronizationSession, IProgressMonitor)}
 * returns full task data.
 * </ul>
 * <h4>Stale tasks are managed on a per repository basis</h4>
 * <ul>
 * <li>
 * {@link #performQuery(TaskRepository, IRepositoryQuery, TaskDataCollector, ISynchronizationSession, IProgressMonitor)}
 * returns partial task data
 * <li>{@link #preSynchronization(ISynchronizationSession, IProgressMonitor)} invokes
 * {@link ISynchronizationSession#markStale(ITask)} for all changed tasks in the session
 * <li>{@link #postSynchronization(ISynchronizationSession, IProgressMonitor)} stores the synchronization stamp for full
 * synchronizations only
 * </ul>
 * <h4>Stale tasks are managed on a per task basis</h4>
 * <ul>
 * <li>
 * {@link #performQuery(TaskRepository, IRepositoryQuery, TaskDataCollector, ISynchronizationSession, IProgressMonitor)}
 * returns partial task data
 * <li>{@link #hasTaskChanged(TaskRepository, ITask, TaskData)} returns true if the partial task data has changes
 * <li>{@link #updateTaskFromTaskData(TaskRepository, ITask, TaskData)} updates {@link ITask} partially for partial task
 * data
 * <li>{@link #hasTaskChanged(TaskRepository, ITask, TaskData)} returns true for the full task data even if the task was
 * already updated for partial task data
 * <li>{@link #updateTaskFromTaskData(TaskRepository, ITask, TaskData)} updates {@link ITask} fully for full task data
 * </ul>
 * <h4>Partial data synchronization only</h4>
 * <ul>
 * <li>
 * {@link #performQuery(TaskRepository, IRepositoryQuery, TaskDataCollector, ISynchronizationSession, IProgressMonitor)}
 * returns partial task data
 * <li>{@link #hasTaskChanged(TaskRepository, ITask, TaskData)} returns true if the partial task data has changes
 * <li>{@link #canSynchronizeTask(TaskRepository, ITask)} returns <code>false</code> so full task data is never
 * retrieved
 * </ul>
 * 
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 2.0
 */
public abstract class AbstractRepositoryConnector {

	private static final long REPOSITORY_CONFIGURATION_UPDATE_INTERVAL = 24 * 60 * 60 * 1000;

	/**
	 * Returns true, if the connector provides a wizard for creating new tasks.
	 * 
	 * @since 2.0
	 */
	// TODO move this to ConnectorUi.hasNewTaskWizard()
	public abstract boolean canCreateNewTask(@NonNull TaskRepository repository);

	/**
	 * Returns true, if the connector supports retrieval of tasks based on String keys.
	 * 
	 * @since 2.0
	 */
	public abstract boolean canCreateTaskFromKey(@NonNull TaskRepository repository);

	/**
	 * Returns true, if the connector supports retrieval of task history for <code>task</code>.
	 * 
	 * @see #getHistory(TaskRepository, ITask, IProgressMonitor)
	 * @since 3.6
	 */
	public boolean canGetTaskHistory(@NonNull TaskRepository repository, @NonNull ITask task) {
		return false;
	}

	/**
	 * Returns true, if the connector supports querying the repository.
	 * 
	 * @since 3.0
	 * @see #performQuery(TaskRepository, IRepositoryQuery, TaskDataCollector, ISynchronizationSession,
	 *      IProgressMonitor)
	 */
	public boolean canQuery(@NonNull TaskRepository repository) {
		return true;
	}

	/**
	 * Returns true, if the connectors support retrieving full task data for <code>task</code>.
	 * 
	 * @since 3.0
	 * @see #getTaskData(TaskRepository, String, IProgressMonitor)
	 */
	public boolean canSynchronizeTask(@NonNull TaskRepository taskRepository, @NonNull ITask task) {
		return true;
	}

	/**
	 * Returns true, if the connector supports deletion of <code>task</code> which is part of <code>repository</code>.
	 * 
	 * @since 3.3
	 */
	public boolean canDeleteTask(@NonNull TaskRepository repository, @NonNull ITask task) {
		return false;
	}

	/**
	 * Return true, if the connector supports creation of task repositories. The default default implementation returns
	 * true.
	 * 
	 * @since 3.4
	 */
	public boolean canCreateRepository() {
		return true;
	}

	/**
	 * Returns the unique kind of the repository, e.g. "bugzilla".
	 * 
	 * @since 2.0
	 */
	@NonNull
	public abstract String getConnectorKind();

	/**
	 * The connector's summary i.e. "JIRA (supports 3.3.1 and later)"
	 * 
	 * @since 2.0
	 */
	@NonNull
	public abstract String getLabel();

	/**
	 * Returns the repository URL for <code>taskUrl</code> if it is a valid task URL for this connector.
	 * <p>
	 * Implementations typically match the task identifier based on repository specific patterns. For a Bugzilla task
	 * URL for example the implementation would match on <code>bugs.cgi</code> and return the repository specific
	 * portion of the URL: &quot;<i>http://bugs/</i><b>bugs.cgi?bugid=</b>123&quot;.
	 * 
	 * @return a task identifier or <code>null</code>, if <code>taskUrl</code> is not recognized
	 * @see #getTaskData(TaskRepository, String, IProgressMonitor)
	 */
	@Nullable
	public abstract String getRepositoryUrlFromTaskUrl(@NonNull String taskUrl);

	/**
	 * Returns a short label for the connector, e.g. Bugzilla.
	 * <p>
	 * The default implementations returns the substring of the text returned by {@link #getLabel()} up to the first
	 * occurrence of <em>(</em> or a space.
	 * 
	 * @since 2.3
	 */
	@Nullable
	public String getShortLabel() {
		String label = getLabel();
		if (label == null) {
			return null;
		}

		int i = label.indexOf("("); //$NON-NLS-1$
		if (i != -1) {
			return label.substring(0, i).trim();
		}

		i = label.indexOf(" "); //$NON-NLS-1$
		if (i != -1) {
			return label.substring(0, i).trim();
		}

		return label;
	}

	/**
	 * Returns the attachment handler. The method is expected to always return the same instance.
	 * <p>
	 * The default implementation returns <code>null</code>.
	 * 
	 * @return the attachment handler, or null, if attachments are not supported
	 * @since 3.0
	 */
	@Nullable
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return null;
	}

	/**
	 * Returns the full task data. The method is expected to always return the same instance.
	 * 
	 * @param repository
	 *            the task repository matching this connector
	 * @param taskId
	 *            a task identifier
	 * @param monitor
	 *            the progress monitor
	 * @see #canSynchronizeTask(TaskRepository, ITask)
	 * @see #canCreateTaskFromKey(TaskRepository)
	 * @see TaskData#isPartial()
	 * @since 3.0
	 */
	@NonNull
	public abstract TaskData getTaskData(@NonNull TaskRepository repository, @NonNull String taskId,
			@NonNull IProgressMonitor monitor) throws CoreException;

	/**
	 * Specifies whether or not this connector supports
	 * {@link #searchByTaskKey(TaskRepository, String, IProgressMonitor) searching} the given repository by task key.
	 * 
	 * @param repository
	 * @throws CoreException
	 *             if the repository is invalid
	 * @since 3.19
	 */
	public boolean supportsSearchByTaskKey(@NonNull TaskRepository repository) throws CoreException {
		return false;
	}

	/**
	 * Searches the given repository for a task with the given task key. The returned <code>TaskData</code> may be
	 * partial.
	 * <p>
	 * This is an optional operation that is useful for connectors that cannot map from the
	 * {@link #getBrowserUrl(TaskRepository, IRepositoryElement) browser URL} to a task ID.
	 * 
	 * @return the matching <code>TaskData</code> or <code>null</code> if no matching task was found.
	 * @throws CoreException
	 *             if the search fails or the repository is invalid
	 * @throws UnsupportedOperationException
	 *             if searching by task key is not {@link #supportsSearchByTaskKey(TaskRepository) supported}
	 * @since 3.19
	 */
	@Nullable
	public TaskData searchByTaskKey(@NonNull TaskRepository repository, @NonNull String taskKey,
			@NonNull IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the task data handler. The method is expected to always return the same instance.
	 * <p>
	 * The default implementation returns <code>null</code>.
	 * 
	 * @return the task data handler, or null, task data submission is not supported
	 * @since 3.0
	 */
	@Nullable
	public AbstractTaskDataHandler getTaskDataHandler() {
		return null;
	}

	/**
	 * Returns the task identifier for <code>taskUrl</code> if it is a valid task URL for this connector. The task
	 * identifier needs to be a task ID or key that is recognized by
	 * {@link #getTaskData(TaskRepository, String, IProgressMonitor)}.
	 * <p>
	 * Implementations typically match the task identifier based on repository specific patterns such as
	 * &quot;http://bugs/<b>bugs.cgibugid=123</b>&quot;.
	 * 
	 * @return a task identifier or <code>null</code>, if <code>taskUrl</code> is not recognized
	 * @see #getTaskData(TaskRepository, String, IProgressMonitor)
	 * @since 2.0
	 */
	@Nullable
	public abstract String getTaskIdFromTaskUrl(@NonNull String taskUrl);

	/**
	 * Used for referring to the task in the UI.
	 */
	@NonNull
	public String getTaskIdPrefix() {
		return "task"; //$NON-NLS-1$
	}

	/**
	 * Extracts task identifiers from <code>comment</code>. This is used to linking text such as commit messages to
	 * tasks.
	 * <p>
	 * Implementations typically scan <code>comment</code> for repository specific patterns such as KEY-123 for JIRA.
	 * 
	 * @return null, if the method is not supported; an array of task identifiers otherwise
	 * @since 2.0
	 */
	@Nullable
	public String[] getTaskIdsFromComment(@NonNull TaskRepository repository, @NonNull String comment) {
		return null;
	}

	/**
	 * Returns a mapping for {@link TaskData}. The mapping maps the connector specific representation to the standard
	 * schema defined in {@link ITaskMapping}.
	 * 
	 * @since 3.0
	 */
	@NonNull
	public ITaskMapping getTaskMapping(@NonNull TaskData taskData) {
		return new TaskMapper(taskData);
	}

	/**
	 * Return other tasks associated with this task.
	 * <p>
	 * For subtasks implementations are expected to return relations creates by {@link TaskRelation#subtask(String)}.
	 * <p>
	 * The default implementation returns <code>null</code>.
	 * 
	 * @return a list of relations or null if <code>taskData</code> does not have relations or if task relations are not
	 *         supported
	 * @since 3.0
	 */
	@Nullable
	public Collection<TaskRelation> getTaskRelations(@NonNull TaskData taskData) {
		return null;
	}

	/**
	 * Returns a task URL for the task referenced by <code>taskIdOrKey</code> in the repository referenced by
	 * <code>repositoryUrl</code>.
	 * 
	 * @return a task URL or null if the connector does not support task URLs
	 * @see #getTaskIdFromTaskUrl(String)
	 * @see #getRepositoryUrlFromTaskUrl(String)
	 * @since 2.0
	 */
	@Nullable
	public abstract String getTaskUrl(@NonNull String repositoryUrl, @NonNull String taskIdOrKey);

	/**
	 * Returns a URL for <code>element</code> that contains authentication information such as a session ID.
	 * <p>
	 * Returns <code>null</code> by default. Clients may override.
	 * 
	 * @param repository
	 *            the repository for <code>element</code>
	 * @param element
	 *            the element to return the authenticated url for
	 * @return null, if no corresponding authenticated URL is available for <code>element</code>; the URL, otherwise
	 * @see IRepositoryElement#getUrl()
	 * @since 3.4
	 */
	@Nullable
	public URL getAuthenticatedUrl(@NonNull TaskRepository repository, @NonNull IRepositoryElement element) {
		return null;
	}

	/**
	 * Returns a browsable URL for <code>element</code>.
	 * <p>
	 * Returns <code>null</code> by default. Clients may override.
	 * 
	 * @param repository
	 *            the repository for <code>element</code>
	 * @param element
	 *            the element to return the url for
	 * @return null, if no corresponding URL is available for <code>element</code>; the URL otherwise
	 * @since 3.12
	 */
	@Nullable
	public URL getBrowserUrl(@NonNull TaskRepository repository, @NonNull IRepositoryElement element) {
		return null;
	}

	/**
	 * Returns <code>true</code>, if the state in <code>taskData</code> is different than the state stored in
	 * <code>task</code>.
	 * <p>
	 * See {@link AbstractRepositoryConnector} for more details how this method interacts with other methods.
	 * 
	 * @since 3.0
	 * @see #updateTaskFromTaskData(TaskRepository, ITask, TaskData)
	 */
	public abstract boolean hasTaskChanged(@NonNull TaskRepository taskRepository, @NonNull ITask task,
			@NonNull TaskData taskData);

	/**
	 * Returns <code>true</code> if the completion state for <code>task</code> is managed locally and not on the
	 * repository which is the common case and default.
	 * <p>
	 * The default implementation returns <code>false</code>.
	 * 
	 * @since 3.0
	 */
	public boolean hasLocalCompletionState(@NonNull TaskRepository repository, @NonNull ITask task) {
		return false;
	}

	/**
	 * Returns <code>true</code>, if <code>task</code> has a due date that is managed on the repository.
	 * <p>
	 * The default implementation returns <code>false</code>.
	 * 
	 * @since 3.0
	 */
	public boolean hasRepositoryDueDate(@NonNull TaskRepository repository, @NonNull ITask task,
			@NonNull TaskData taskData) {
		return false;
	}

	/**
	 * Returns <code>true</code> to indication that the repository configuration is stale and requires update
	 * <p>
	 * The default implementation returns <code>true</code> every 24 hours.
	 * 
	 * @return true to indicate that the repository configuration is stale and requires update
	 * @since 3.0
	 */
	public boolean isRepositoryConfigurationStale(@NonNull TaskRepository repository, @NonNull IProgressMonitor monitor)
			throws CoreException {
		Date configDate = repository.getConfigurationDate();
		if (configDate != null) {
			return (new Date().getTime() - configDate.getTime()) > REPOSITORY_CONFIGURATION_UPDATE_INTERVAL;
		}
		return true;
	}

	/**
	 * Returns true, if users can manage create repositories for this connector.
	 * <p>
	 * The default implementation returns true.
	 * 
	 * @since 2.0
	 */
	public boolean isUserManaged() {
		return true;
	}

	/**
	 * Runs <code>query</code> on <code>repository</code>, results are passed to <code>collector</code>. If a repository
	 * does not return the full task data for a result, {@link TaskData#isPartial()} will return true.
	 * <p>
	 * Implementors must complete executing <code>query</code> before returning from this method.
	 * <p>
	 * See {@link AbstractRepositoryConnector} for more details how this method interacts with other methods.
	 * 
	 * @param repository
	 *            task repository to run query against
	 * @param query
	 *            query to run
	 * @param collector
	 *            callback for returning results
	 * @param session
	 *            provides additional information for running the query, may be <code>null</code>
	 * @param monitor
	 *            for reporting progress
	 * @return {@link Status#OK_STATUS} in case of success, an error status otherwise
	 * @throws OperationCanceledException
	 *             if the query was canceled
	 * @since 3.0
	 */
	@NonNull
	public abstract IStatus performQuery(@NonNull TaskRepository repository, @NonNull IRepositoryQuery query,
			@NonNull TaskDataCollector collector, @Nullable ISynchronizationSession session,
			@NonNull IProgressMonitor monitor);

	/**
	 * Delete the task from the server
	 * 
	 * @throws UnsupportedOperationException
	 *             if this is not implemented by the connector
	 * @since 3.3
	 */
	@NonNull
	public IStatus deleteTask(@NonNull TaskRepository repository, @NonNull ITask task, @NonNull IProgressMonitor monitor)
			throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Hook into the synchronization process.
	 * 
	 * @since 3.0
	 */
	public void postSynchronization(@NonNull ISynchronizationSession event, @NonNull IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask("", 1); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	/**
	 * Hook into the synchronization process.
	 * <p>
	 * See {@link AbstractRepositoryConnector} for more details how this method interacts with other methods.
	 * 
	 * @since 3.0
	 */
	public void preSynchronization(@NonNull ISynchronizationSession event, @NonNull IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask("", 1); //$NON-NLS-1$
		} finally {
			monitor.done();
		}
	}

	/**
	 * Updates the local repository configuration cache (e.g. products and components). Connectors are encouraged to
	 * implement {@link #updateRepositoryConfiguration(TaskRepository, ITask, IProgressMonitor)} in addition this
	 * method.
	 * 
	 * @param repository
	 *            the repository to update configuration for
	 * @since 3.0
	 * @see #isRepositoryConfigurationStale(TaskRepository, IProgressMonitor)
	 */
	public abstract void updateRepositoryConfiguration(@NonNull TaskRepository taskRepository,
			@NonNull IProgressMonitor monitor) throws CoreException;

	/**
	 * Updates the local repository configuration cache (e.g. products and components). The default implementation
	 * invokes {@link #updateRepositoryConfiguration(TaskRepository, IProgressMonitor)}.
	 * 
	 * @param repository
	 *            the repository to update configuration for
	 * @param task
	 *            if not null, limit the update to the details relevant to task
	 * @see #updateRepositoryConfiguration(TaskRepository, IProgressMonitor)
	 * @since 3.3
	 */
	public void updateRepositoryConfiguration(@NonNull TaskRepository taskRepository, @Nullable ITask task,
			@NonNull IProgressMonitor monitor) throws CoreException {
		updateRepositoryConfiguration(taskRepository, monitor);
	}

	/**
	 * Updates <code>task</code> based on the state in <code>taskData</code>. {@link TaskMapper#applyTo(ITask)} can be
	 * used to map common attributes.
	 * <p>
	 * See {@link AbstractRepositoryConnector} for more details how this method interacts with other methods.
	 * 
	 * @see #hasTaskChanged(TaskRepository, ITask, TaskData)
	 * @see TaskMapper#applyTo(ITask)
	 * @since 3.0
	 */
	public abstract void updateTaskFromTaskData(@NonNull TaskRepository taskRepository, @NonNull ITask task,
			@NonNull TaskData taskData);

	/**
	 * Called when a new task is created, before it is opened in a task editor. Connectors should override this method
	 * if they need information from the {@link TaskData} to determine kind labels or other information that should be
	 * displayed in a new task editor.
	 * 
	 * @since 3.5
	 */
	public void updateNewTaskFromTaskData(@NonNull TaskRepository taskRepository, @NonNull ITask task,
			@NonNull TaskData taskData) {
	}

	/**
	 * Invoked when a task associated with this connector is migrated. This typically happens when an unsubmitted task
	 * is submitted to the repository. Implementers may override to implement custom migration rules.
	 * <p>
	 * Does nothing by default.
	 * 
	 * @param event
	 *            provides additional details
	 * @since 3.4
	 */
	public void migrateTask(@NonNull TaskMigrationEvent event) {
	}

	/**
	 * Returns if the user using the repository is the owner of the task. Subclasses may override.
	 * 
	 * @param repository
	 *            repository task is associated with
	 * @param task
	 *            task to determined ownership of
	 * @return true if user using the repository is owner of the task
	 * @since 3.5
	 */
	public boolean isOwnedByUser(@NonNull TaskRepository repository, @NonNull ITask task) {
		return (task.getOwner() != null && task.getOwner().equals(repository.getUserName()))
				|| (task.getOwnerId() != null && task.getOwnerId().equals(repository.getUserName()));
	}

	/**
	 * Retrieves the history for <code>task</code>. Throws {@link UnsupportedOperationException} by default.
	 * 
	 * @param repository
	 *            the repository
	 * @param task
	 *            the task to retrieve history for
	 * @param monitor
	 *            a progress monitor
	 * @return the history for <code>task</code>
	 * @throws CoreException
	 *             thrown in case retrieval fails
	 * @see #canGetHistory(TaskRepository, ITask)
	 * @since 3.6
	 */
	@NonNull
	public TaskHistory getTaskHistory(@NonNull TaskRepository repository, @NonNull ITask task,
			@NonNull IProgressMonitor monitor) throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Validates the connection to {@code repository} and returns information about the repository. This typically
	 * requires connecting to the repository.
	 * <p>
	 * Throws {@link UnsupportedOperationException} if not implemented by clients.
	 * 
	 * @param repository
	 *            the repository
	 * @param monitor
	 *            a progress monitor
	 * @throws CoreException
	 *             thrown in case the operation fails
	 * @since 3.11
	 */
	@NonNull
	public RepositoryInfo validateRepository(@NonNull TaskRepository repository, @Nullable IProgressMonitor monitor)
			throws CoreException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Set the category of the {@code repository} to the default TaskRepository.CATEGORY_BUGS.
	 * <p>
	 * Subclasses may override.
	 * 
	 * @param repository
	 *            the repository
	 * @since 3.11
	 */
	public void applyDefaultCategory(@NonNull TaskRepository repository) {
		repository.setCategory(TaskRepository.CATEGORY_BUGS);
	}

}
