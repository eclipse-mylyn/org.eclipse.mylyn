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

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskListBackupManager;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class ConnectorMigratorTest {
	public class SpyTasksState extends DefaultTasksState {

		TaskActivityManager taskActivityManager = spy(super.getTaskActivityManager());

		TaskDataManager taskDataManager = spy(super.getTaskDataManager());

		RepositoryModel repositoryModel = spy(super.getRepositoryModel());

		TaskList taskList = spy(super.getTaskList());

		AbstractTaskContextStore contextStore = spy(super.getContextStore());

		TaskJobFactory taskJobFactory = spy(super.getTaskJobFactory());

		TaskRepositoryManager repositoryManager = spy(super.getRepositoryManager());

		@Override
		public TaskActivityManager getTaskActivityManager() {
			return taskActivityManager;
		}

		@Override
		public TaskDataManager getTaskDataManager() {
			return taskDataManager;
		}

		@Override
		public RepositoryModel getRepositoryModel() {
			return repositoryModel;
		}

		@Override
		public TaskList getTaskList() {
			return taskList;
		}

		@Override
		public AbstractTaskContextStore getContextStore() {
			return contextStore;
		}

		@Override
		public TaskJobFactory getTaskJobFactory() {
			return taskJobFactory;
		}

		@Override
		public TaskRepositoryManager getRepositoryManager() {
			return repositoryManager;
		}

	}

	private final ImmutableMap<String, String> kinds = ImmutableMap.of("mock", "mock.new");

	private final TaskRepository repository = new TaskRepository("mock", "http://mock");

	private final ImmutableSet<TaskRepository> singleRepository = ImmutableSet.of(repository);

	private TaskRepository migratedRepository = new TaskRepository("mock.new", "http://mock");

	private final AuthenticationCredentials repoCreds = new AuthenticationCredentials("u1", "p1");

	private final AuthenticationCredentials proxyCreds = new AuthenticationCredentials("u2", "p2");

	private final AuthenticationCredentials httpCreds = new AuthenticationCredentials("u3", "p3");

	private final AuthenticationCredentials certCreds = new AuthenticationCredentials("u4", "p4");

	private final AbstractRepositoryConnector connector = mock(AbstractRepositoryConnector.class);

	private final AbstractRepositoryConnector newConnector = mock(AbstractRepositoryConnector.class);

	private final TaskListView taskList = mock(TaskListView.class);

	private final TaskListBackupManager backupManager = mock(TaskListBackupManager.class);

	DefaultTasksState tasksState = spy(new SpyTasksState());

	private TaskRepositoryManager manager = tasksState.getRepositoryManager();

	private final ConnectorMigrationUi migrationUi = spy(new ConnectorMigrationUi(taskList, backupManager, tasksState));

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		doNothing().when(migrationUi).warnOfValidationFailure(any(List.class));
		doNothing().when(migrationUi).notifyMigrationComplete();
	}

	@After
	public void tearDown() throws Exception {
		TestFixture.resetTaskList();
		new File("test-context.zip").delete();
	}

	@Test
	public void setConnectorsToMigrate() throws Exception {
		ConnectorMigrator migrator = createMigrator(true, true, ImmutableMap.of("mock", "mock.new", "kind", "kind.new"),
				ImmutableSet.of(repository, new TaskRepository("kind", "http://mock")), true);
		try {
			migrator.setConnectorsToMigrate(ImmutableList.of("foo"));
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {// NOSONAR
		}
		migrator.setConnectorsToMigrate(ImmutableList.of("kind"));
		assertEquals(ImmutableMap.of("kind", "kind.new"), migrator.getSelectedConnectors());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void migrateConnectors() throws Exception {
		assertMigrateConnectors();
		verify(migrationUi, never()).warnOfValidationFailure((List<TaskRepository>) any());
	}

	@Test
	public void migrateConnectorsValidationFailure() throws Exception {
		when(newConnector.validateRepository(any(TaskRepository.class), any(IProgressMonitor.class))).thenThrow(
				new CoreException(new Status(0, "org.eclipse.mylyn.tasks.ui.tests", 0, "Error", new Exception())));
		assertMigrateConnectors();
		verify(migrationUi).warnOfValidationFailure(ImmutableList.of(migratedRepository));
	}

	@Test
	public void cancelMigration() throws Exception {
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, singleRepository, true));
		IProgressMonitor monitor = new NullProgressMonitor();
		monitor.setCanceled(true);
		IProgressMonitor spyMonitor = spy(monitor);
		try {
			migrator.setConnectorsToMigrate(ImmutableList.of("mock"));
			migrator.migrateConnectors(spyMonitor);
			fail("Expected OperationCanceledException");
		} catch (OperationCanceledException e) {// NOSONAR
		}
		verify(spyMonitor).beginTask("Migrating repositories", 2);
		verify(spyMonitor).isCanceled();
		verify(manager, never()).getRepositoryConnector(any(String.class));
		verify(manager, never()).addRepository(any(TaskRepository.class));
		verify(migrator, never()).getMigratedRepository(any(String.class), any(TaskRepository.class));
	}

	private ConnectorMigrator assertMigrateConnectors() throws CoreException, IOException {
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, singleRepository, true));
		IProgressMonitor monitor = migrateConnectors(migrator);

		assertTrue(repository.isOffline());
		assertEquals("http://mock (Unsupported, do not delete)", repository.getRepositoryLabel());
		InOrder inOrder = inOrder(manager, newConnector, migrationUi, migrator, monitor);
		inOrder.verify(monitor).beginTask("Migrating repositories", 2);

		inOrder.verify(migrationUi).backupTaskList(monitor);
		inOrder.verify(monitor).subTask("Backing up task list");
		inOrder.verify(monitor).worked(1);

		inOrder.verify(monitor).subTask("Migrating http://mock");
		inOrder.verify(migrator).migrateRepository("mock.new", "http://mock", repository);
		inOrder.verify(manager).addRepository(migratedRepository);
		inOrder.verify(migrator).disconnect(repository);
		inOrder.verify(monitor).worked(1);
		inOrder.verify(monitor).beginTask("Validating repository connections", 1);
		inOrder.verify(monitor).subTask("Validating connection to http://mock");
		inOrder.verify(newConnector).validateRepository(migratedRepository, monitor);
		inOrder.verify(monitor).done();
		verifyNoMoreInteractions(newConnector);
		return migrator;
	}

	private IProgressMonitor migrateConnectors(ConnectorMigrator migrator) throws IOException {
		IProgressMonitor monitor = mock(IProgressMonitor.class);
		migrator.setConnectorsToMigrate(ImmutableList.of("mock"));
		migrator.migrateConnectors(monitor);
		return monitor;
	}

	@Test
	public void migrateRepository() throws Exception {
		ConnectorMigrator migrator = createMigrator(true, true, kinds, singleRepository, true);
		populateRepository();

		migratedRepository = migrator.getMigratedRepository("mock.new", repository);
		assertEquals("foovalue", migratedRepository.getProperty("foo"));
		assertEquals("barvalue", migratedRepository.getProperty("bar"));
		assertNull(migratedRepository.getProperty(IRepositoryConstants.PROPERTY_SYNCTIMESTAMP));
		assertEquals("My Label", migratedRepository.getRepositoryLabel());
		assertEquals("mock.new", migratedRepository.getConnectorKind());

		assertEquals(repoCreds, migratedRepository.getCredentials(AuthenticationType.REPOSITORY));
		assertEquals(proxyCreds, migratedRepository.getCredentials(AuthenticationType.PROXY));
		assertEquals(httpCreds, migratedRepository.getCredentials(AuthenticationType.HTTP));
		assertEquals(certCreds, migratedRepository.getCredentials(AuthenticationType.CERTIFICATE));

		assertTrue(migratedRepository.getSavePassword(AuthenticationType.REPOSITORY));
		assertTrue(migratedRepository.getSavePassword(AuthenticationType.PROXY));
		assertFalse(migratedRepository.getSavePassword(AuthenticationType.HTTP));
		assertFalse(migratedRepository.getSavePassword(AuthenticationType.CERTIFICATE));
	}

	@Test
	public void migrateExistingRepositoryDoesNothing() throws Exception {
		migratedRepository.setRepositoryLabel("My Old Label");

		ConnectorMigrator migrator = createMigrator(true, true, kinds, singleRepository, true);
		when(manager.getRepository("mock.new", "http://mock")).thenReturn(migratedRepository);
		populateRepository();

		assertSame(migratedRepository, migrator.getMigratedRepository("mock.new", repository));
		assertNull(migratedRepository.getProperty("foo"));
		assertNull(migratedRepository.getProperty("bar"));
		assertNull(migratedRepository.getProperty(IRepositoryConstants.PROPERTY_SYNCTIMESTAMP));
		assertEquals("My Old Label", migratedRepository.getRepositoryLabel());
		assertEquals("mock.new", migratedRepository.getConnectorKind());

		assertNull(migratedRepository.getCredentials(AuthenticationType.REPOSITORY));
		assertNull(migratedRepository.getCredentials(AuthenticationType.PROXY));
		assertNull(migratedRepository.getCredentials(AuthenticationType.HTTP));
		assertNull(migratedRepository.getCredentials(AuthenticationType.CERTIFICATE));

	}

	private void populateRepository() {
		repository.setProperty("foo", "foovalue");
		repository.setProperty("bar", "barvalue");
		repository.setProperty(IRepositoryConstants.PROPERTY_SYNCTIMESTAMP, "123");
		repository.setRepositoryLabel("My Label");
		repository.setCredentials(AuthenticationType.REPOSITORY, repoCreds, true);
		repository.setCredentials(AuthenticationType.PROXY, proxyCreds, true);
		repository.setCredentials(AuthenticationType.HTTP, httpCreds, false);
		repository.setCredentials(AuthenticationType.CERTIFICATE, certCreds, false);
	}

	@Test
	public void needsMigrationEmptyKinds() {
		try {
			createMigrator(true, true, ImmutableMap.<String, String> of(), singleRepository, true);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {// NOSONAR
		}
	}

	@Test
	public void needsMigrationNoRepositories() {
		ConnectorMigrator migrator = createMigrator(true, true, kinds, ImmutableSet.<TaskRepository> of(), true);
		assertFalse(migrator.needsMigration());
	}

	@Test
	public void needsMigrationNoConnectors() {
		ConnectorMigrator migrator = createMigrator(false, false, kinds, singleRepository, true);
		assertFalse(migrator.needsMigration());
	}

	@Test
	public void needsMigrationNoNewConnector() {
		ConnectorMigrator migrator = createMigrator(true, false, kinds, singleRepository, true);
		assertFalse(migrator.needsMigration());
	}

	@Test
	public void needsMigrationNoOldConnector() {
		ConnectorMigrator migrator = createMigrator(false, true, kinds, singleRepository, true);
		assertFalse(migrator.needsMigration());
	}

	@Test
	public void needsMigrationOneRepository() {
		ConnectorMigrator migrator = createMigrator(true, true, kinds, singleRepository, true);
		assertTrue(migrator.needsMigration());
	}

	@Test
	public void needsMigrationMultipleRepositories() {
		ConnectorMigrator migrator = createMigrator(true, true, kinds,
				ImmutableSet.of(repository, new TaskRepository("mock", "http://mock2")), true);
		assertTrue(migrator.needsMigration());
	}

	@Test
	public void needsMigrationMultiKindsOneRepository() {
		ImmutableMap<String, String> multiKinds = ImmutableMap.of("mock", "mock.new", "kind", "kind.new");
		ConnectorMigrator migrator = createMigrator(true, true, multiKinds, singleRepository, true);
		assertTrue(migrator.needsMigration());

		migrator = createMigrator(true, true, multiKinds, ImmutableSet.of(new TaskRepository("kind", "http://mock")),
				true);
		assertTrue(migrator.needsMigration());
	}

	@Test
	public void needsMigrationMultiKindsMultiRepositories() {
		ConnectorMigrator migrator = createMigrator(true, true, ImmutableMap.of("mock", "mock.new", "kind", "kind.new"),
				ImmutableSet.of(repository, new TaskRepository("kind", "http://mock")), true);
		assertTrue(migrator.needsMigration());
	}

	@Test
	public void migrateTasksWaitsForSyncJobs() throws Exception {
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		JobListener listener = mock(JobListener.class);
		when(listener.isComplete()).thenReturn(false, false, true);
		when(migrator.getSyncTaskJobListener()).thenReturn(listener);
		migrator.migrateTasks(new NullProgressMonitor());
		verify(listener, times(3)).isComplete();
	}

	@Test
	public void migrateTasks() throws Exception {
		when(newConnector.getConnectorKind()).thenReturn("mock.new");
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		TaskData taskData2 = new TaskData(mock(TaskAttributeMapper.class), "mock.new", repository.getRepositoryUrl(),
				"2.migrated");
		doReturn(taskData2).when(migrator).getTaskData(eq("key2"), eq(newConnector), any(TaskRepository.class),
				any(IProgressMonitor.class));
		ITask task1 = new TaskTask("mock", "http://mock", "1");
		task1.setTaskKey("key1");
		((AbstractTask) task1).setSynchronizationState(SynchronizationState.INCOMING_NEW);
		ITask task2 = new TaskTask("mock", "http://mock", "2");
		task2.setTaskKey("key2");
		((AbstractTask) task2).setSynchronizationState(SynchronizationState.INCOMING);
		ITask task1Migrated = new TaskTask("mock.new", "http://mock", "1.migrated");
		task1Migrated.setTaskKey("key1");
		TaskTask taskOtherRepo = new TaskTask("mock", "http://other-mock", "1");
		tasksState.getTaskList().addTask(task1);
		tasksState.getTaskList().addTask(task1Migrated);
		tasksState.getTaskList().addTask(taskOtherRepo);
		tasksState.getTaskList().addTask(task2);
		RepositoryQuery query = new RepositoryQuery("mock", "mock");
		query.setRepositoryUrl("http://mock");
		tasksState.getTaskList().addQuery(query);
		migrateConnectors(migrator);
		NullProgressMonitor monitor = new NullProgressMonitor();

		migrator.migrateTasks(monitor);
		verify(tasksState.getTaskActivityManager()).deactivateActiveTask();
		TaskRepository newRepository = manager.getRepository("mock.new", "http://mock");
		verify(migrator).migrateTasks(ImmutableSet.of(task1, task2), repository, newRepository,
				manager.getRepositoryConnector("mock.new"), monitor);

		verify(migrator, never()).getTaskData("key1", newConnector, newRepository, monitor);
		verify(migrator, never()).createTask(argThat(not(taskData2)), any(TaskRepository.class));
		verify(migrator).getTaskData("key2", newConnector, newRepository, monitor);
		verify(migrator).createTask(taskData2, newRepository);
		verify(migrator).migratePrivateData((AbstractTask) task1, (AbstractTask) task1Migrated, monitor);
		ITask task2Migrated = new TaskTask("mock.new", "http://mock", "2.migrated");
		task2Migrated.setTaskKey("key2");
		verify(migrator).migratePrivateData((AbstractTask) task2, (AbstractTask) task2Migrated, monitor);

		verify(migrationUi).delete(ImmutableSet.of(task1, task2), repository, newRepository, monitor);
		assertEquals(SynchronizationState.INCOMING_NEW, tasksState.getTaskList()
				.getTask(repository.getRepositoryUrl(), "1.migrated")
				.getSynchronizationState());
		assertEquals(SynchronizationState.INCOMING, tasksState.getTaskList()
				.getTask(repository.getRepositoryUrl(), "2.migrated")
				.getSynchronizationState());

		assertEquals(ImmutableSet.of(taskOtherRepo, task1Migrated, task2Migrated),
				ImmutableSet.copyOf(tasksState.getTaskList().getAllTasks()));
		verify(tasksState.getRepositoryManager()).removeRepository(repository);
		assertTrue(tasksState.getTaskList().getQueries().isEmpty());
		assertEquals(ImmutableSet.of(newRepository), tasksState.getRepositoryManager().getRepositories("mock.new"));
	}

	@Test
	public void migrateTasksSameId() throws Exception {
		when(newConnector.getConnectorKind()).thenReturn("mock.new");
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		TaskData taskData1 = new TaskData(mock(TaskAttributeMapper.class), "mock.new", repository.getRepositoryUrl(),
				"1");
		doReturn(taskData1).when(migrator).getTaskData(eq("key1"), eq(newConnector), any(TaskRepository.class),
				any(IProgressMonitor.class));
		TaskData taskData2 = new TaskData(mock(TaskAttributeMapper.class), "mock.new", repository.getRepositoryUrl(),
				"2");
		doReturn(taskData2).when(migrator).getTaskData(eq("key2"), eq(newConnector), any(TaskRepository.class),
				any(IProgressMonitor.class));
		ITask task1 = new TaskTask("mock", "http://mock", "1");
		task1.setTaskKey("key1");
		((AbstractTask) task1).setSynchronizationState(SynchronizationState.INCOMING_NEW);
		ITask task2 = new TaskTask("mock", "http://mock", "2");
		task2.setTaskKey("key2");
		((AbstractTask) task2).setSynchronizationState(SynchronizationState.INCOMING);
		ITask task1Migrated = new TaskTask("mock.new", "http://mock", "1");
		task1Migrated.setTaskKey("key1");
		TaskTask taskOtherRepo = new TaskTask("mock", "http://other-mock", "1");
		tasksState.getTaskList().addTask(task1);
		tasksState.getTaskList().addTask(task1Migrated);
		tasksState.getTaskList().addTask(taskOtherRepo);
		tasksState.getTaskList().addTask(task2);
		RepositoryQuery query = new RepositoryQuery("mock", "mock");
		query.setRepositoryUrl("http://mock");
		tasksState.getTaskList().addQuery(query);
		migrateConnectors(migrator);
		NullProgressMonitor monitor = new NullProgressMonitor();

		migrator.migrateTasks(monitor);
		verify(tasksState.getTaskActivityManager()).deactivateActiveTask();
		TaskRepository newRepository = manager.getRepository("mock.new", "http://mock");
		verify(migrator).migrateTasks(ImmutableSet.of(task1, task2), repository, newRepository,
				manager.getRepositoryConnector("mock.new"), monitor);

		verify(migrator).getTaskData("key1", newConnector, newRepository, monitor);
		verify(migrator).getTaskData("key2", newConnector, newRepository, monitor);
		verify(migrator).createTask(taskData2, newRepository);
		ITask task2Migrated = new TaskTask("mock.new", "http://mock", "2");
		task2Migrated.setTaskKey("key2");
		verify(migrator).migratePrivateData((AbstractTask) task2, (AbstractTask) task2Migrated, monitor);
		verify(migrator).migratePrivateData((AbstractTask) task1, (AbstractTask) task1Migrated, monitor);

		verify(migrationUi).delete(ImmutableSet.of(task1, task2), repository, newRepository, monitor);
		assertEquals(SynchronizationState.INCOMING_NEW,
				tasksState.getTaskList().getTask(repository.getRepositoryUrl(), "1").getSynchronizationState());
		assertEquals(SynchronizationState.INCOMING,
				tasksState.getTaskList().getTask(repository.getRepositoryUrl(), "2").getSynchronizationState());

		assertEquals(ImmutableSet.of(taskOtherRepo, task1Migrated, task2Migrated),
				ImmutableSet.copyOf(tasksState.getTaskList().getAllTasks()));
		verify(tasksState.getRepositoryManager()).removeRepository(repository);
		assertTrue(tasksState.getTaskList().getQueries().isEmpty());
		assertEquals(ImmutableSet.of(newRepository), tasksState.getRepositoryManager().getRepositories("mock.new"));
	}

	@Test
	public void migratePrivateData() throws Exception {
		ConnectorMigrator migrator = createMigrator(true, true, kinds, ImmutableSet.of(repository), false);
		AbstractTask oldTask = new TaskTask("mock", "http://mock", "1");
		AbstractTask newTask = new TaskTask("mock.new", "http://mock", "1.migrated");

		oldTask.setNotes("some notes");
		DateRange scheduledDate = new DateRange(createCalendar(3));
		Calendar dueDate = createCalendar(5);
		tasksState.getTaskActivityManager().setScheduledFor(oldTask, scheduledDate);
		tasksState.getTaskActivityManager().setDueDate(oldTask, dueDate.getTime());
		oldTask.setEstimatedTimeHours(7);

		migrator.migratePrivateData(oldTask, newTask, new NullProgressMonitor());
		assertEquals("some notes", newTask.getNotes());
		assertEquals(scheduledDate, newTask.getScheduledForDate());
		assertEquals(dueDate.getTime(), newTask.getDueDate());
		assertEquals(7, newTask.getEstimatedTimeHours());
	}

	@Test
	public void migrateCategories() throws Exception {
		ConnectorMigrator migrator = createMigrator(true, true, kinds, ImmutableSet.of(repository), false);
		AbstractTask oldTask1 = new TaskTask("mock", "http://mock", "1");
		AbstractTask oldTask2 = new TaskTask("mock", "http://mock", "2");
		AbstractTask newTask1 = new TaskTask("mock.new", "http://mock", "1.migrated");
		AbstractTask newTask2 = new TaskTask("mock.new", "http://mock", "2.migrated");

		TaskCategory category1 = new TaskCategory("category1");
		TaskCategory category2 = new TaskCategory("category2");
		tasksState.getTaskList().addCategory(category1);
		tasksState.getTaskList().addCategory(category2);
		tasksState.getTaskList().addTask(oldTask1, category1);
		tasksState.getTaskList().addTask(oldTask2, category2);

		migrator.migratePrivateData(oldTask1, newTask1, new NullProgressMonitor());
		migrator.migratePrivateData(oldTask2, newTask2, new NullProgressMonitor());
		assertEquals(category1, getCategory(newTask1));
		assertEquals(category2, getCategory(newTask2));
	}

	@Test
	public void getCategories() throws Exception {
		ConnectorMigrator migrator = createMigrator(true, true, kinds, ImmutableSet.of(repository), false);
		AbstractTask oldTask1 = new TaskTask("mock", "http://mock", "1");
		AbstractTask oldTask2 = new TaskTask("mock", "http://mock", "2");
		AbstractTask newTask1 = new TaskTask("mock.new", "http://mock", "1.migrated");
		AbstractTask newTask2 = new TaskTask("mock.new", "http://mock", "2.migrated");

		TaskCategory category1 = new TaskCategory("category1");
		TaskCategory category2 = new TaskCategory("category2");
		tasksState.getTaskList().addCategory(category1);
		tasksState.getTaskList().addCategory(category2);
		tasksState.getTaskList().addTask(oldTask1, category1);
		tasksState.getTaskList().addTask(newTask1, category1);
		tasksState.getTaskList().addTask(oldTask2, category2);
		tasksState.getTaskList().addTask(newTask2, category2);
		tasksState.getTaskList().addTask(new TaskTask("mock.new", "http://mock", "not categorized"));

		ImmutableMap<AbstractTask, TaskCategory> expected = ImmutableMap.of(oldTask1, category1, oldTask2, category2,
				newTask1, category1, newTask2, category2);
		assertEquals(expected, migrator.getCategories());

		tasksState.getTaskList().addTask(new TaskTask("mock.new", "http://mock", "3"), category1);
		assertEquals(expected, migrator.getCategories());
	}

	@Test
	public void migrateNoQueries() {
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		RepositoryQuery q1 = createQuery("q1", repository, migrator, false);
		RepositoryQuery q2 = createQuery("q2", repository, migrator, false);

		migrator.migrateQueries(repository, migratedRepository, new NullProgressMonitor());
		assertFalse(migrator.anyQueriesMigrated());
		assertFalse(migrator.allQueriesMigrated());
	}

	@Test
	public void migrateSomeQueries() {
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		RepositoryQuery q1 = createQuery("q1", repository, migrator, true);
		RepositoryQuery q2 = createQuery("q2", repository, migrator, false);

		assertFalse(migrator.anyQueriesMigrated());
		assertTrue(migrator.allQueriesMigrated());

		migrator.migrateQueries(repository, migratedRepository, new NullProgressMonitor());
		assertTrue(migrator.anyQueriesMigrated());
		assertFalse(migrator.allQueriesMigrated());
	}

	@Test
	public void migrateAllQueries() {
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		RepositoryQuery q1 = createQuery("q1", repository, migrator, true);
		RepositoryQuery q2 = createQuery("q2", repository, migrator, true);

		migrator.migrateQueries(repository, migratedRepository, new NullProgressMonitor());
		assertTrue(migrator.anyQueriesMigrated());
		assertTrue(migrator.allQueriesMigrated());
	}

	@Test
	public void migrateSomeQueriesMultipleRepositories() {
		TaskRepository otherRepository = new TaskRepository("mock", "http://other-mock");
		ConnectorMigrator migrator = spy(
				createMigrator(true, true, kinds, ImmutableSet.of(repository, otherRepository), false));
		RepositoryQuery q1 = createQuery("q1", repository, migrator, true);
		RepositoryQuery q2 = createQuery("q2", repository, migrator, true);
		RepositoryQuery q3 = createQuery("q3", otherRepository, migrator, false);
		RepositoryQuery q4 = createQuery("q4", otherRepository, migrator, false);

		migrator.migrateQueries(repository, migratedRepository, new NullProgressMonitor());
		migrator.migrateQueries(otherRepository, migratedRepository, new NullProgressMonitor());
		assertTrue(migrator.anyQueriesMigrated());
		assertFalse(migrator.allQueriesMigrated());
	}

	@Test
	public void migrateAllQueriesMultipleRepositories() {
		TaskRepository otherRepository = new TaskRepository("mock", "http://other-mock");
		ConnectorMigrator migrator = spy(createMigrator(true, true, kinds, ImmutableSet.of(repository), false));
		RepositoryQuery q1 = createQuery("q1", repository, migrator, true);
		RepositoryQuery q2 = createQuery("q2", repository, migrator, true);
		RepositoryQuery q3 = createQuery("q3", otherRepository, migrator, true);
		RepositoryQuery q4 = createQuery("q4", otherRepository, migrator, true);

		migrator.migrateQueries(repository, migratedRepository, new NullProgressMonitor());
		migrator.migrateQueries(otherRepository, migratedRepository, new NullProgressMonitor());
		assertTrue(migrator.anyQueriesMigrated());
		assertTrue(migrator.allQueriesMigrated());
	}

	private RepositoryQuery createQuery(String handle, TaskRepository repository, ConnectorMigrator migrator,
			boolean shouldMigrate) {
		RepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), handle);
		query.setRepositoryUrl(repository.getRepositoryUrl());
		tasksState.getTaskList().addQuery(query);
		if (shouldMigrate) {
			RepositoryQuery migratedQuery = new RepositoryQuery(repository.getConnectorKind(), handle + ".migrated");
			migratedQuery.setRepositoryUrl(repository.getRepositoryUrl() + "/migrated");
			when(migrator.migrateQuery(eq(query), any(TaskRepository.class), any(TaskRepository.class),
					any(IProgressMonitor.class))).thenReturn(migratedQuery);
		}
		return query;
	}

	private AbstractTaskCategory getCategory(AbstractTask newTask) {
		for (AbstractTaskCategory category : tasksState.getTaskList().getCategories()) {
			Optional<ITask> task = Iterables.tryFind(category.getChildren(), Predicates.<ITask> equalTo(newTask));
			if (task.isPresent()) {
				return category;
			}
		}
		return null;
	}

	private Calendar createCalendar(int daysInFuture) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, daysInFuture);
		return cal;
	}

	private ConnectorMigrator createMigrator(boolean hasOldConnector, boolean hasNewConnector,
			Map<String, String> kinds, Set<TaskRepository> repositories, boolean mockManager) {
		if (mockManager) {
			manager = mock(TaskRepositoryManager.class);
		}
		when(tasksState.getRepositoryManager()).thenReturn(manager);
		ConnectorMigrator migrator = new ConnectorMigrator(kinds, "", tasksState, migrationUi);
		when(manager.getRepositories("mock")).thenReturn(repositories);
		if (hasOldConnector) {
			when(manager.getRepositoryConnector("mock")).thenReturn(connector);
			when(manager.getRepositoryConnector("kind")).thenReturn(connector);
		}
		if (hasNewConnector) {
			when(manager.getRepositoryConnector("mock.new")).thenReturn(newConnector);
			when(manager.getRepositoryConnector("kind.new")).thenReturn(newConnector);
		}
		return migrator;
	}

}
