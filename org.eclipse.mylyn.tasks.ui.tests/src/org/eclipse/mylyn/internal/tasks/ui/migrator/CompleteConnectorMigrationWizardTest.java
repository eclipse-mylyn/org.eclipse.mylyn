/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.migrator.CompleteConnectorMigrationWizard.MapContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class CompleteConnectorMigrationWizardTest {

	private CompleteConnectorMigrationWizard wizard;

	private ConnectorMigrator migrator;

	private ConnectorMigrationUi migrationUi;

	private DefaultTasksState tasksState;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		tasksState = new DefaultTasksState();
		migrationUi = spy(new ConnectorMigrationUi(TaskListView.getFromActivePerspective(),
				TasksUiPlugin.getBackupManager(), tasksState));
		doNothing().when(migrationUi).warnOfValidationFailure(any(List.class));
		doNothing().when(migrationUi).notifyMigrationComplete();
		migrator = createMigrator(ImmutableMap.of("mock", "mock.new"));
	}

	private ConnectorMigrator createMigrator(ImmutableMap<String, String> kinds) {
		ConnectorMigrator migrator = spy(new ConnectorMigrator(kinds, "", tasksState, migrationUi));
		when(migrator.allQueriesMigrated()).thenReturn(false);
		return migrator;
	}

	@After
	public void tearDown() throws Exception {
		TestFixture.resetTaskList();
	}

	@Test
	public void addPages() {
		createWizard(new CompleteConnectorMigrationWizard(migrator));
		assertEquals(2, wizard.getPageCount());
	}

	@Test
	public void firstPage() {
		IWizardContainer container = createWizard(new CompleteConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		assertEquals("Have You Recreated Your Queries?", firstPage.getTitle());
		assertEquals(
				"Migration will remove your old queries. Please ensure you have created the new queries you want. "
						+ "Your old and new queries are shown below and you can edit them by double-clicking.",
				firstPage.getMessage());
		assertTrue(firstPage.getControl() instanceof Composite);
		Composite control = (Composite) firstPage.getControl();
		assertEquals(4, control.getChildren().length);
		assertTrue(control.getChildren()[0] instanceof Label);
		assertTrue(control.getChildren()[1] instanceof Label);
		assertTrue(control.getChildren()[2] instanceof Tree);
		assertTrue(control.getChildren()[3] instanceof Tree);
	}

	@Test
	public void firstPageSomeQueriesMigrated() {
		when(migrator.anyQueriesMigrated()).thenReturn(true);
		IWizardContainer container = createWizard(new CompleteConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		assertEquals("Have You Recreated Your Queries?", firstPage.getTitle());
		assertEquals(
				"Migration will remove your old queries. Some queries could not be automatically migrated. "
						+ "Please review your old and new queries and edit or create new ones as needed. "
						+ "Your old and new queries are shown below and you can edit them by double-clicking.",
				firstPage.getMessage());
		assertTrue(firstPage.getControl() instanceof Composite);
		Composite control = (Composite) firstPage.getControl();
		assertEquals(4, control.getChildren().length);
		assertTrue(control.getChildren()[0] instanceof Label);
		assertTrue(control.getChildren()[1] instanceof Label);
		assertTrue(control.getChildren()[2] instanceof Tree);
		assertTrue(control.getChildren()[3] instanceof Tree);
	}

	@Test
	public void firstPageAllQueriesMigrated() {
		when(migrator.allQueriesMigrated()).thenReturn(true);
		IWizardContainer container = createWizard(new CompleteConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		assertEquals("Complete Migration", firstPage.getTitle());
		assertNull(firstPage.getNextPage());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void queryTreeShowsOnlySelectedConnectors() {
		migrator = createMigrator(ImmutableMap.of("mock", "mock.new", "kind", "kind.new"));
		migrator.setConnectorsToMigrate(ImmutableList.of("kind"));
		createWizard(new CompleteConnectorMigrationWizard(migrator));
		ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
		verify(wizard, times(2)).createRepositoryQueryMap(captor.capture());
		assertEquals(ImmutableSet.of("kind"), ImmutableSet.copyOf(captor.getAllValues().get(0)));
		assertEquals(ImmutableSet.of("kind.new"), ImmutableSet.copyOf(captor.getAllValues().get(1)));

		migrator = createMigrator(ImmutableMap.of("mock", "mock.new", "kind", "kind.new"));
		migrator.setConnectorsToMigrate(ImmutableList.of("mock", "kind"));
		createWizard(new CompleteConnectorMigrationWizard(migrator));
		captor = ArgumentCaptor.forClass(Collection.class);
		verify(wizard, times(2)).createRepositoryQueryMap(captor.capture());
		assertEquals(ImmutableSet.of("mock", "kind"), ImmutableSet.copyOf(captor.getAllValues().get(0)));
		assertEquals(ImmutableSet.of("mock.new", "kind.new"), ImmutableSet.copyOf(captor.getAllValues().get(1)));
	}

	@Test
	public void secondPage() {
		IWizardContainer container = createWizard(new CompleteConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		IWizardPage secondPage = firstPage.getNextPage();
		assertEquals("Complete Migration", secondPage.getTitle());
		assertEquals("Clicking finish will migrate your tasks and private data. This may take a while.",
				secondPage.getMessage());
		assertTrue(secondPage.getControl() instanceof Composite);
		Composite control = (Composite) secondPage.getControl();
		assertEquals(1, control.getChildren().length);
		assertTrue(control.getChildren()[0] instanceof Text);
		String text = ((Text) control.getChildren()[0]).getText();
		assertTrue(text.contains("When you click finish, your context, scheduled dates, private notes and other data "
				+ "will be migrated to the new connectors. Any tasks in your task list that are not included in the new "
				+ "queries will be downloaded using the new connectors. The old tasks, "
				+ "queries, and repositories will be deleted."));
		assertTrue(text.contains(
				"This may take a while. You should not use the task list or task editor while this is happening. "
						+ "You will be prompted when migration is complete."));
		assertTrue(text.contains("You will be able to "
				+ "undo the migration by selecting \"Restore Tasks from History\" in the Task List view menu and choosing the "
				+ "connector-migration-*.zip file stored in <workspace>/.metadata/.mylyn/backup. This will restore your task "
				+ "list and repositories to the state they were in before the migration, but any data stored by 3rd party "
				+ "plugins for Mylyn may be lost"));
	}

	@Test
	public void performFinish() throws InvocationTargetException, InterruptedException, IOException {
		createWizard(new CompleteConnectorMigrationWizard(migrator));
		assertTrue(wizard.performFinish());
	}

	@Test
	public void isPageComplete() throws Exception {
		IWizardContainer container = createWizard(new CompleteConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		IWizardPage secondPage = firstPage.getNextPage();
		assertTrue(firstPage.isPageComplete());
		assertFalse(secondPage.isPageComplete());

		container.showPage(secondPage);
		assertTrue(firstPage.isPageComplete());
		assertTrue(secondPage.isPageComplete());
	}

	@Test
	public void createQueryTree() throws Exception {
		TaskRepository repository = createRepository("mock", "http://mock");
		Map<TaskRepository, ? extends Set<RepositoryQuery>> queries = ImmutableMap.of(repository,
				ImmutableSet.of(new RepositoryQuery("mock", "mock")));
		TreeViewer viewer = new CompleteConnectorMigrationWizard(migrator).createQueryTree(WorkbenchUtil.getShell(),
				queries);
		assertEquals(queries, viewer.getInput());
		assertTrue(viewer.getContentProvider() instanceof MapContentProvider);
		assertTrue(viewer.getLabelProvider() instanceof TaskElementLabelProvider);
		assertNotNull(((TaskElementLabelProvider) viewer.getLabelProvider()).getImage(repository));
		assertEquals("http://mock", ((TaskElementLabelProvider) viewer.getLabelProvider()).getText(repository));

		assertEquals(0, viewer.getExpandedElements().length);
		spinEventLoop();
		assertEquals(1, viewer.getExpandedElements().length);
	}

	@Test
	public void createRepositoryQueryMap() throws Exception {
		TaskRepository repository1 = createRepository("mock", "http://mock");
		TaskRepository repository2 = createRepository("mock", "http://mock2");
		ImmutableSet<TaskRepository> repositories = ImmutableSet.of(repository1, repository2);
		RepositoryQuery query1 = createQuery(repository1);
		RepositoryQuery query2 = createQuery(repository1);
		RepositoryQuery query3 = createQuery(repository2);
		RepositoryQuery query4 = createQuery(repository2);

		Map<TaskRepository, Set<RepositoryQuery>> map = //
				new CompleteConnectorMigrationWizard(migrator).createRepositoryQueryMap(ImmutableList.of("mock"));
		assertEquals(repositories, map.keySet());
		assertEquals(ImmutableSet.of(query1, query2), map.get(repository1));
		assertEquals(ImmutableSet.of(query3, query4), map.get(repository2));
	}

	@Test
	public void createRepositoryQueryMapExcludesRepositoryWithNoQueries() throws Exception {
		TaskRepository repository = createRepository("mock", "http://mock");
		createRepository("mock", "http://mock2");
		RepositoryQuery query = createQuery(repository);

		Map<TaskRepository, Set<RepositoryQuery>> map = //
				new CompleteConnectorMigrationWizard(migrator).createRepositoryQueryMap(ImmutableList.of("mock"));
		assertEquals(ImmutableSet.of(repository), map.keySet());
		assertEquals(ImmutableSet.of(query), map.get(repository));
	}

	@Test
	public void createRepositoryQueryMapMigratedQuery() throws Exception {
		TaskRepository repository = createRepository("mock", "http://mock");
		TaskRepository migratedRepository = createRepository("mock-new", "http://mock");
		RepositoryQuery query = createQuery(repository);
		RepositoryQuery migratedQuery = createQuery(migratedRepository);

		Map<TaskRepository, Set<RepositoryQuery>> map = //
				new CompleteConnectorMigrationWizard(migrator).createRepositoryQueryMap(ImmutableList.of("mock"));
		assertEquals(ImmutableSet.of(repository), map.keySet());
		assertEquals(ImmutableSet.of(query), map.get(repository));

		map = new CompleteConnectorMigrationWizard(migrator).createRepositoryQueryMap(ImmutableList.of("mock-new"));
		assertEquals(ImmutableSet.of(migratedRepository), map.keySet());
		assertEquals(ImmutableSet.of(migratedQuery), map.get(migratedRepository));
	}

	protected TaskRepository createRepository(String kind, String url) {
		TaskRepository repository = new TaskRepository(kind, url);
		migrator.getRepositoryManager().addRepository(repository);
		return repository;
	}

	protected RepositoryQuery createQuery(TaskRepository repository) {
		RepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(),
				repository.getConnectorKind() + repository.getRepositoryUrl() + Math.random());
		query.setRepositoryUrl(repository.getRepositoryUrl());
		TasksUiPlugin.getTaskList().addQuery(query);
		return query;
	}

	private IWizardContainer createWizard(CompleteConnectorMigrationWizard wiz) {
		wizard = spy(wiz);
		WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
		dialog.create();
		IWizardContainer container = spy(wizard.getContainer());
		when(wizard.getContainer()).thenReturn(container);
		return container;
	}

	private void spinEventLoop() {
		while (Display.getCurrent().readAndDispatch()) {
		}
	}
}
