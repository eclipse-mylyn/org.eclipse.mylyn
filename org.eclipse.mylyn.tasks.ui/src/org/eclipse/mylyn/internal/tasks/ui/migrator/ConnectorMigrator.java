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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.any;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.mylyn.internal.tasks.ui.migrator.TaskPredicates.isQueryForConnector;
import static org.eclipse.mylyn.internal.tasks.ui.migrator.TaskPredicates.isTaskForConnector;
import static org.eclipse.mylyn.internal.tasks.ui.migrator.TaskPredicates.isTaskSynchronizing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.osgi.util.NLS;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * Allows users to migrate their data from an old connector to a new one for the same repository. Performs the following
 * steps:
 *
 * <pre>
 * * uses task list message service to prompt users to migrate
 * * backs up the task list
 * * automatically migrates repositories
 * * directs user to manually migrate queries and click a button in the task list message to complete migration
 * * once new queries have finished syncing, any tasks that are missing from the new queries are fetched by searching by task key
 * * private data (context, notes, categories, scheduled dates, and private due dates) is automatically migrated for all tasks
 * * all tasks are marked read except those which were incoming before migration
 * * old repositories are deleted
 * </pre>
 */
public class ConnectorMigrator {

	private static final ImmutableSet<String> EXCLUDED_REPOSITORY_PROPERTIES = ImmutableSet.of(
			IRepositoryConstants.PROPERTY_CONNECTOR_KIND, IRepositoryConstants.PROPERTY_SYNCTIMESTAMP,
			IRepositoryConstants.PROPERTY_URL);

	protected static class OldTaskState {

		private final SynchronizationState syncState;

		private final ITask oldTask;

		public OldTaskState(ITask oldTask) {
			this.oldTask = oldTask;
			this.syncState = oldTask.getSynchronizationState();
		}

		public ITask getOldTask() {
			return oldTask;
		}

		public SynchronizationState getSyncState() {
			return syncState;
		}
	}

	private final Map<String, String> connectorKinds;

	private final String explanatoryText;

	private final TasksState tasksState;

	private List<String> connectorsToMigrate = ImmutableList.of();

	private final ConnectorMigrationUi migrationUi;

	private final Map<TaskRepository, TaskRepository> repositories = new HashMap<TaskRepository, TaskRepository>();

	private final Table<TaskRepository, String, OldTaskState> oldTasksStates = HashBasedTable.create();

	private Map<ITask, AbstractTaskCategory> categories;

	private final JobListener syncTaskJobListener = new JobListener(new Runnable() {

		@Override
		public void run() {
			completeMigration();
		}
	});

	private boolean anyQueriesMigrated;

	private boolean allQueriesMigrated = true;

	public ConnectorMigrator(Map<String, String> connectorKinds, String explanatoryText, TasksState tasksState,
			ConnectorMigrationUi migrationUi) {
		checkArgument(!connectorKinds.isEmpty());
		this.connectorKinds = connectorKinds;
		this.explanatoryText = explanatoryText;
		this.migrationUi = migrationUi;
		this.tasksState = tasksState;
	}

	public Map<String, String> getConnectorKinds() {
		return ImmutableMap.copyOf(connectorKinds);
	}

	public String getExplanatoryText() {
		return explanatoryText;
	}

	public boolean needsMigration() {
		for (Entry<String, String> entry : connectorKinds.entrySet()) {
			String oldKind = entry.getKey();
			String newKind = entry.getValue();
			if (getRepositoryManager().getRepositoryConnector(oldKind) != null
					&& getRepositoryManager().getRepositoryConnector(newKind) != null
					&& !getRepositoryManager().getRepositories(oldKind).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void setConnectorsToMigrate(List<String> connectors) {
		checkArgument(connectorKinds.keySet().containsAll(connectors));
		this.connectorsToMigrate = ImmutableList.copyOf(connectors);
	}

	protected void migrateConnectors(IProgressMonitor monitor) throws IOException {
		final List<TaskRepository> failedValidation = new ArrayList<>();
		List<TaskRepository> oldRepositories = gatherRepositoriesToMigrate(connectorsToMigrate);
		monitor.beginTask(Messages.ConnectorMigrator_Migrating_repositories, oldRepositories.size() + 1);
		getMigrationUi().backupTaskList(monitor);

		for (TaskRepository repository : oldRepositories) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			monitor.subTask(NLS.bind(Messages.ConnectorMigrator_Migrating_X, repository.getRepositoryLabel()));
			String kind = repository.getConnectorKind();
			String newKind = getConnectorKinds().get(kind);
			TaskRepository newRepository = getMigratedRepository(newKind, repository);
			getRepositoryManager().addRepository(newRepository);
			repositories.put(repository, newRepository);
			Set<ITask> tasksToMigrate = Sets.filter(getTaskList().getTasks(repository.getRepositoryUrl()),
					isTaskForConnector(repository.getConnectorKind()));
			for (ITask task : tasksToMigrate) {
				oldTasksStates.put(newRepository, task.getTaskKey(), new OldTaskState(task));
			}
			migrateQueries(repository, newRepository, monitor);
			disconnect(repository);
			monitor.worked(1);
		}

		Set<TaskRepository> newRepositories = ImmutableSet.copyOf(repositories.values());
		monitor.beginTask(Messages.ConnectorMigrator_Validating_repository_connections, newRepositories.size());
		for (TaskRepository newRepository : newRepositories) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			monitor.subTask(NLS.bind(Messages.ConnectorMigrator_Validating_connection_to_X,
					newRepository.getRepositoryLabel()));
			AbstractRepositoryConnector newConnector = getRepositoryManager()
					.getRepositoryConnector(newRepository.getConnectorKind());
			try {
				newConnector.validateRepository(newRepository, monitor);
			} catch (UnsupportedOperationException | CoreException e) {
				failedValidation.add(newRepository);
			}
			monitor.worked(1);
		}

		monitor.done();

		if (!failedValidation.isEmpty()) {
			getMigrationUi().warnOfValidationFailure(failedValidation);
		}
	}

	protected void migrateQueries(TaskRepository repository, TaskRepository newRepository, IProgressMonitor monitor) {
		Set<RepositoryQuery> queriesForUrl = getTaskList().getRepositoryQueries(repository.getRepositoryUrl());
		Set<RepositoryQuery> queries = Sets.filter(queriesForUrl, isQueryForConnector(repository.getConnectorKind()));
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.ConnectorMigrator_Migrating_Queries,
				queries.size());
		for (RepositoryQuery query : queries) {
			RepositoryQuery migratedQuery = migrateQuery(query, repository, newRepository, subMonitor);
			if (migratedQuery != null) {
				getTaskList().addQuery(migratedQuery);
				anyQueriesMigrated = true;
			} else {
				allQueriesMigrated = false;
			}
			subMonitor.worked(1);
		}
	}

	/**
	 * Connectors can override to attempt to automatically migrate queries if possible.
	 */
	protected RepositoryQuery migrateQuery(RepositoryQuery query, TaskRepository repository,
			TaskRepository newRepository, IProgressMonitor monitor) {
		return null;
	}

	/**
	 * @return whether any queries have been migrated for any repository
	 */
	protected boolean anyQueriesMigrated() {
		return anyQueriesMigrated;
	}

	/**
	 * @return whether all queries have been migrated for all migrated repositories; returns <code>true</code> if no
	 *         repositories have yet been migrated
	 */
	protected boolean allQueriesMigrated() {
		return allQueriesMigrated;
	}

	protected void disconnect(TaskRepository repository) {
		repository.setOffline(true);
		// we need to change the label so that the new repo doesn't have the same label, so that it can be edited
		repository.setRepositoryLabel(
				NLS.bind(Messages.ConnectorMigrator_X_Unsupported_do_not_delete, repository.getRepositoryLabel()));
		Set<RepositoryQuery> queriesForUrl = getTaskList().getRepositoryQueries(repository.getRepositoryUrl());
		for (RepositoryQuery query : Sets.filter(queriesForUrl, isQueryForConnector(repository.getConnectorKind()))) {
			query.setAutoUpdate(false);// prevent error logged when Mylyn asks new connector to sync query for old connector
		}
	}

	protected List<TaskRepository> gatherRepositoriesToMigrate(List<String> connectors) {
		List<TaskRepository> oldRepositories = new ArrayList<TaskRepository>();
		for (String kind : connectors) {
			oldRepositories.addAll(getRepositoryManager().getRepositories(kind));
		}
		return oldRepositories;
	}

	protected TaskRepository getMigratedRepository(String newKind, TaskRepository oldRepository) {
		String migratedRepositoryUrl = getMigratedRepositoryUrl(oldRepository);
		TaskRepository newRepository = getRepositoryManager().getRepository(newKind, migratedRepositoryUrl);
		if (newRepository == null) {
			newRepository = migrateRepository(newKind, migratedRepositoryUrl, oldRepository);
		}
		return newRepository;
	}

	protected String getMigratedRepositoryUrl(TaskRepository oldRepository) {
		return oldRepository.getRepositoryUrl();
	}

	protected TaskRepository migrateRepository(String newKind, String migratedRepositoryUrl,
			TaskRepository oldRepository) {
		TaskRepository newRepository = new TaskRepository(newKind, migratedRepositoryUrl);
		for (Entry<String, String> entry : oldRepository.getProperties().entrySet()) {
			if (!EXCLUDED_REPOSITORY_PROPERTIES.contains(entry.getKey())) {
				newRepository.setProperty(entry.getKey(), entry.getValue());
			}
		}
		for (AuthenticationType type : AuthenticationType.values()) {
			AuthenticationCredentials credentials = oldRepository.getCredentials(type);
			newRepository.setCredentials(type, credentials, oldRepository.getSavePassword(type));
		}
		return newRepository;
	}

	protected void migrateTasks(IProgressMonitor monitor) {
		tasksState.getTaskActivityManager().deactivateActiveTask();
		// Note: we're assuming the new connector uses different task IDs (and therefore different handle identifiers)
		// from the old one. This may not be the case for Bugzilla.
		for (Entry<TaskRepository, TaskRepository> entry : repositories.entrySet()) {
			TaskRepository oldRepository = entry.getKey();
			TaskRepository newRepository = entry.getValue();
			monitor.subTask(NLS.bind(Messages.ConnectorMigrator_Migrating_tasks_for_X, newRepository));
			AbstractRepositoryConnector newConnector = getRepositoryManager()
					.getRepositoryConnector(newRepository.getConnectorKind());
			Set<ITask> tasksToMigrate = Sets.filter(getTaskList().getTasks(oldRepository.getRepositoryUrl()),
					isTaskForConnector(oldRepository.getConnectorKind()));
			migrateTasks(tasksToMigrate, oldRepository, newRepository, newConnector, monitor);
		}
		monitor.subTask(Messages.ConnectorMigrator_Waiting_for_tasks_to_synchronize);
		getSyncTaskJobListener().start();
		while (!getSyncTaskJobListener().isComplete()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			}
		}
	}

	protected void migrateTasks(final Set<ITask> tasksToMigrate, final TaskRepository oldRepository,
			final TaskRepository newRepository, final AbstractRepositoryConnector newConnector,
			final IProgressMonitor monitor) {
		ImmutableMap<String, ITask> tasksByKey = FluentIterable
				.from(getTaskList().getTasks(newRepository.getRepositoryUrl()))
				.filter(isTaskForConnector(newConnector.getConnectorKind()))
				.uniqueIndex(new Function<ITask, String>() {
					@Override
					public String apply(ITask task) {
						return task.getTaskKey();
					}
				});
		final Map<AbstractTask, OldTaskState> migratedTasks = new HashMap<>();
		Set<ITask> tasksToSynchronize = new HashSet<ITask>();
		for (ITask oldTask : tasksToMigrate) {
			String taskKey = oldTask.getTaskKey();
			ITask newTask = tasksByKey.get(taskKey);
			if (newTask == null) {
				TaskData taskData = getTaskData(taskKey, newConnector, newRepository, monitor);
				if (taskData != null) {
					newTask = createTask(taskData, newRepository);
					tasksToSynchronize.add(newTask);
				}
			}
			if (newTask instanceof AbstractTask) {
				OldTaskState oldTaskState = oldTasksStates.get(newRepository, oldTask.getTaskKey());
				if (oldTaskState == null) {
					oldTaskState = new OldTaskState(oldTask);
				}
				migratedTasks.put((AbstractTask) newTask, oldTaskState);
			}
			if (newTask instanceof AbstractTask && oldTask instanceof AbstractTask) {
				migratePrivateData((AbstractTask) oldTask, (AbstractTask) newTask, monitor);
			}
		}
		oldTasksStates.row(newRepository).clear();
		migrateTaskContext(migratedTasks);
		getMigrationUi().delete(tasksToMigrate, oldRepository, newRepository, monitor);
		for (ITask task : tasksToSynchronize) {
			getTaskList().addTask(task);
		}
		SynchronizationJob job = tasksState.getTaskJobFactory().createSynchronizeTasksJob(newConnector, newRepository,
				tasksToSynchronize);
		getSyncTaskJobListener().add(job, new Runnable() {
			@Override
			public void run() {
				long start = System.currentTimeMillis();
				while (any(migratedTasks.keySet(), isTaskSynchronizing())
						&& System.currentTimeMillis() - start < MILLISECONDS.convert(4, HOURS)) {
					try {
						Thread.sleep(MILLISECONDS.convert(3, SECONDS));
					} catch (InterruptedException e) {// NOSONAR
					}
				}
				for (Entry<AbstractTask, OldTaskState> entry : migratedTasks.entrySet()) {
					AbstractTask newTask = entry.getKey();
					OldTaskState oldTask = entry.getValue();
					newTask.setSynchronizationState(oldTask.getSyncState());
				}
				Set<RepositoryQuery> queries = getTaskList().getRepositoryQueries(newRepository.getRepositoryUrl());
				if (!queries.isEmpty()) {
					SynchronizationJob synchronizeQueriesJob = tasksState.getTaskJobFactory()
							.createSynchronizeQueriesJob(newConnector, newRepository, queries);
					synchronizeQueriesJob.schedule();
				}
			}
		});
		job.schedule();
	}

	private void migrateTaskContext(Map<AbstractTask, OldTaskState> taskStates) {
		Map<ITask, ITask> tasks = taskStates.entrySet()
				.stream()
				.collect(Collectors.toMap(e -> e.getValue().getOldTask(), e -> e.getKey()));
		TasksUiPlugin.getContextStore().moveContext(tasks);
	}

	protected void completeMigration() {
		categories = null;
		getMigrationUi().notifyMigrationComplete();
	}

	protected void migratePrivateData(AbstractTask oldTask, AbstractTask newTask, IProgressMonitor monitor) {
		AbstractTaskCategory category = getCategories().get(oldTask);
		if (category != null) {
			getTaskList().addTask(newTask, category);
		}
		newTask.setNotes(oldTask.getNotes());
		tasksState.getTaskActivityManager().setScheduledFor(newTask, oldTask.getScheduledForDate());
		tasksState.getTaskActivityManager().setDueDate(newTask, oldTask.getDueDate());
		newTask.setEstimatedTimeHours(oldTask.getEstimatedTimeHours());
	}

	protected ITask createTask(TaskData taskData, TaskRepository repository) {
		return new TaskTask(repository.getConnectorKind(), repository.getRepositoryUrl(), taskData.getTaskId());
	}

	/**
	 * This method is used to support migrating tasks that are not contained in any migrated query.
	 */
	protected TaskData getTaskData(String taskKey, AbstractRepositoryConnector newConnector,
			TaskRepository newRepository, IProgressMonitor monitor) {
		try {
			if (newConnector.supportsSearchByTaskKey(newRepository)) {
				return newConnector.searchByTaskKey(newRepository, taskKey, monitor);
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Failed to migrate task " //$NON-NLS-1$
					+ taskKey + " for repository " + newRepository.getRepositoryLabel(), e)); //$NON-NLS-1$
		}
		return null;
	}

	public Map<String, String> getSelectedConnectors() {
		return Maps.filterKeys(getConnectorKinds(), in(connectorsToMigrate));
	}

	protected TaskList getTaskList() {
		return tasksState.getTaskList();
	}

	protected IRepositoryManager getRepositoryManager() {
		return tasksState.getRepositoryManager();
	}

	/**
	 * @return The task categorization that existed the first time this method was called
	 */
	protected Map<ITask, AbstractTaskCategory> getCategories() {
		if (categories == null) {
			categories = new HashMap<ITask, AbstractTaskCategory>();
			for (AbstractTaskCategory category : getTaskList().getCategories()) {
				for (ITask task : category.getChildren()) {
					categories.put(task, category);
				}
			}
		}
		return categories;
	}

	public ConnectorMigrationUi getMigrationUi() {
		return migrationUi;
	}

	protected JobListener getSyncTaskJobListener() {
		return syncTaskJobListener;
	}
}
