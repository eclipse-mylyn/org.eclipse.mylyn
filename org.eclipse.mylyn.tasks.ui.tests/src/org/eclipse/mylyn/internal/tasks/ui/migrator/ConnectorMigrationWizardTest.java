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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ConnectorMigrationWizardTest {
	public class TestConnectorMigrationWizard extends ConnectorMigrationWizard {
		private TestConnectorMigrationWizard(ConnectorMigrator migrator) {
			super(migrator);
		}

		@Override
		protected CheckboxTreeViewer createConnectorList(Composite parent, List<String> kinds) {
			return new CheckboxTreeViewer(parent) {
				{
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							fireCheckStateChanged(null);
						}
					});
				}

				@Override
				public Object[] getCheckedElements() {
					return new String[] { "foo", "bar" };
				}
			};
		}
	}

	private ConnectorMigrationWizard wizard;

	private final ConnectorMigrationUi migrationUi = spy(new ConnectorMigrationUi(
			TaskListView.getFromActivePerspective(), TasksUiPlugin.getBackupManager(), new DefaultTasksState()));

	private ConnectorMigrator migrator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		doNothing().when(migrationUi).warnOfValidationFailure(any(List.class));
		doNothing().when(migrationUi).notifyMigrationComplete();
		createMigrator(ImmutableMap.of("mock", "mock.new"));
	}

	@Test
	public void addPages() {
		createWizard(new ConnectorMigrationWizard(migrator));
		assertEquals(2, wizard.getPageCount());
	}

	@Test
	public void firstPage() {
		IWizardContainer container = createWizard(new ConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		assertEquals("End of Connector Support", firstPage.getTitle());
		assertEquals(
				"Support is ending for some connectors, but replacement connectors are installed. This wizard will help you "
						+ "migrate your configuration and data to the new connectors.",
				firstPage.getMessage());
		assertTrue(firstPage.getControl() instanceof Composite);
		Composite control = (Composite) firstPage.getControl();
		assertEquals(1, control.getChildren().length);
		assertTrue(control.getChildren()[0] instanceof Link);
	}

	@Test
	public void secondPage() {
		IWizardContainer container = createWizard(new ConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		IWizardPage secondPage = firstPage.getNextPage();
		assertEquals("Select Connectors", secondPage.getTitle());
		assertEquals(
				"Select the connectors to migrate. Your task list and repositories will be backed up before migration; you can "
						+ "undo the migration by selecting \"Restore Tasks from History\" in the Task List view "
						+ "menu and choosing the "
						+ "connector-migration-*.zip file stored in <workspace>/.metadata/.mylyn/backup.",
				secondPage.getDescription());
		assertTrue(secondPage.getControl() instanceof Composite);
		Composite control = (Composite) secondPage.getControl();
		assertEquals(1, control.getChildren().length);
		assertTrue(control.getChildren()[0] instanceof Tree);
	}

	@Test
	public void performFinishAfterConnectorsSelected()
			throws InvocationTargetException, InterruptedException, IOException {
		createMigrator(ImmutableMap.of("foo", "foo.new", "bar", "bar.new", "baz", "baz.new"));
		IWizardContainer container = createWizard(new TestConnectorMigrationWizard(migrator));
		spinEventLoop();
		wizard.performFinish();
		verify(container).run(eq(true), eq(true), any(IRunnableWithProgress.class));
		verify(migrator).setConnectorsToMigrate(eq(ImmutableList.of("foo", "bar")));
		verify(migrator).migrateConnectors(any(IProgressMonitor.class));
	}

	protected void createMigrator(Map<String, String> connectors) {
		TaskRepositoryManager manager = spy(new TaskRepositoryManager());
		createMigrator(connectors, manager);
	}

	private void createMigrator(Map<String, String> connectors, TaskRepositoryManager manager) {
		DefaultTasksState tasksState = spy(new DefaultTasksState());
		when(tasksState.getRepositoryManager()).thenReturn(manager);
		migrator = spy(new ConnectorMigrator(connectors, "", tasksState, migrationUi));
	}

	@Test
	public void performFinishNoConnectorsSelectedByDefault()
			throws InvocationTargetException, InterruptedException, IOException {
		createMigrator(ImmutableMap.of("foo", "foo.new", "bar", "bar.new", "baz", "baz.new"));
		IWizardContainer container = createWizard(new ConnectorMigrationWizard(migrator));
		wizard.performFinish();
		verify(container).run(eq(true), eq(true), any(IRunnableWithProgress.class));
		verify(migrator).setConnectorsToMigrate(eq(ImmutableList.<String> of()));
		verify(migrator).migrateConnectors(any(IProgressMonitor.class));
	}

	@Test
	public void performFinishSelectsRelevantConnectors()
			throws InvocationTargetException, InterruptedException, IOException {
		TaskRepositoryManager manager = spy(new TaskRepositoryManager());

		createAndAddConnector(manager, "mock", "Mock Connector");
		createAndAddConnector(manager, "foo", "Foo Connector");
		createAndAddConnector(manager, "bar", "Bar Connector");

		manager.addRepository(new TaskRepository("mock", "http://mock"));
		manager.addRepository(new TaskRepository("bar", "http://bar"));

		createMigrator(ImmutableMap.of("foo", "foo.new", "bar", "bar.new", "mock", "mock.new"), manager);

		IWizardContainer container = createWizard(new ConnectorMigrationWizard(migrator));
		wizard.performFinish();
		verify(container).run(eq(true), eq(true), any(IRunnableWithProgress.class));
		verify(migrator).setConnectorsToMigrate(eq(ImmutableList.of("bar", "mock")));
		verify(migrator).migrateConnectors(any(IProgressMonitor.class));
	}

	private void createAndAddConnector(TaskRepositoryManager manager, String kind, String label) {
		AbstractRepositoryConnector mockConnector = mock(AbstractRepositoryConnector.class);
		when(mockConnector.getLabel()).thenReturn(label);
		when(manager.getRepositoryConnector(kind)).thenReturn(mockConnector);
	}

	@Test
	public void performFinishSetsErrorMessage() throws InvocationTargetException, InterruptedException, IOException {
		IWizardContainer container = createWizard(new ConnectorMigrationWizard(migrator));
		doThrow(new InvocationTargetException(new IOException("Backup failed"))).when(container).run(any(Boolean.class),
				any(Boolean.class), any(IRunnableWithProgress.class));
		wizard.performFinish();
		assertEquals("Backup failed", container.getCurrentPage().getErrorMessage());
	}

	@Test
	public void isPageComplete() throws Exception {
		IWizardContainer container = createWizard(new ConnectorMigrationWizard(migrator));
		IWizardPage firstPage = container.getCurrentPage();
		IWizardPage secondPage = firstPage.getNextPage();
		assertTrue(firstPage.isPageComplete());
		assertFalse(secondPage.isPageComplete());

		container.showPage(secondPage);
		assertTrue(firstPage.isPageComplete());
		assertTrue(secondPage.isPageComplete());
	}

	@Test
	public void createConnectorList() throws Exception {
		CheckboxTreeViewer viewer = new ConnectorMigrationWizard(migrator).createConnectorList(WorkbenchUtil.getShell(),
				ImmutableList.of("mock"));
		IRepositoryManager manager = migrator.getRepositoryManager();
		assertEquals(ImmutableList.of("mock"), viewer.getInput());
		assertTrue(viewer.getLabelProvider() instanceof LabelProvider);
		assertEquals("mock", ((LabelProvider) viewer.getLabelProvider()).getText("mock"));

		AbstractRepositoryConnector connector = mock(AbstractRepositoryConnector.class);
		when(connector.getLabel()).thenReturn("My Connector");
		when(manager.getRepositoryConnector("mock")).thenReturn(connector);
		manager.addRepository(new TaskRepository("mock", "http://mock"));
		manager.addRepository(new TaskRepository("mock", "http://mock2"));
		assertEquals("My Connector (used by 2 repositories)",
				((LabelProvider) viewer.getLabelProvider()).getText("mock"));
	}

	private IWizardContainer createWizard(ConnectorMigrationWizard wiz) {
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
