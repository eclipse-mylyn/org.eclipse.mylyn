/*******************************************************************************
 * Copyright (c) 2014, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

@SuppressWarnings("restriction")
public class RepositorySettingsPageTest {

	public static class TestRepositorySettingsPage extends AbstractRepositorySettingsPage {

		public TestRepositorySettingsPage(TaskRepository taskRepository) {
			super("Title", "Description", taskRepository, MockRepositoryConnector.getDefault());
			setNeedsProxy(true);
		}

		@Override
		protected Validator getValidator(TaskRepository repository) {
			Validator validator = new Validator() {

				@Override
				public void run(IProgressMonitor monitor) throws CoreException {
				}
			};
			validator.setStatus(Status.OK_STATUS);
			return validator;
		}

		@Override
		public String getConnectorKind() {
			return MockRepositoryConnector.CONNECTOR_KIND;
		}

		@Override
		protected void createAdditionalControls(Composite parent) {
			// ignore
		}
	}

	public static class RepositorySettingsPageWithNoCredentials extends TestRepositorySettingsPage {

		public RepositorySettingsPageWithNoCredentials(TaskRepository taskRepository) {
			super(taskRepository);
			setNeedsRepositoryCredentials(false);
		}
	}

	@Test
	public void proxyPortTriggersValidation() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		IWizardContainer container = applyWizardContainer(page);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		verify(container, times(2)).updateButtons();

		page.proxyPortEditor.setStringValue("123");
		verify(container, times(3)).updateButtons();
	}

	@Test
	public void validatesWithNoCredentials() throws Exception {
		TestRepositorySettingsPage page = new RepositorySettingsPageWithNoCredentials(createTaskRepository());
		applyWizardContainer(page);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		page.validateSettings();
	}

	@Test
	public void labelAndUrlUpdatedWhenNoCredentials() throws Exception {
		TaskRepository repository = createTaskRepository();
		TestRepositorySettingsPage page = new RepositorySettingsPageWithNoCredentials(repository);
		applyWizardContainer(page);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		assertEquals(repository.getRepositoryUrl(), page.getRepositoryUrl());
		assertEquals(repository.getRepositoryLabel(), page.getRepositoryLabel());
	}

	@Test
	public void labelAndUrlNotUpdatedWhenNoTaskRepository() throws Exception {
		TestRepositorySettingsPage page = new RepositorySettingsPageWithNoCredentials(null);
		applyWizardContainer(page);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		assertEquals("", page.getRepositoryUrl());
		assertEquals("", page.getRepositoryLabel());
	}

	@Test
	public void applyToNewRepository() {
		NewRepositoryWizard wizard = mock(NewRepositoryWizard.class);
		when(wizard.getBrand()).thenReturn("org.mylyn");
		AbstractRepositorySettingsPage page = createPage(wizard);
		TaskRepository repository = createTaskRepository();

		page.applyTo(repository);
		assertEquals("org.mylyn", repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	@Test
	public void applyToExistingRepository() {
		EditRepositoryWizard wizard = mock(EditRepositoryWizard.class);
		AbstractRepositorySettingsPage page = createPage(wizard);
		TaskRepository repository = createTaskRepository();

		page.applyTo(repository);
		assertNull(repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));

		repository.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, "org.mylyn");
		page.applyTo(repository);
		assertEquals("org.mylyn", repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	protected AbstractRepositorySettingsPage createPage(Wizard wizard) {
		AbstractRepositorySettingsPage page = spy(new RepositorySettingsPageWithNoCredentials(null));
		doReturn("label").when(page).getRepositoryLabel();
		when(page.needsProxy()).thenReturn(false);
		when(page.getWizard()).thenReturn(wizard);
		return page;
	}

	private IWizardContainer applyWizardContainer(TestRepositorySettingsPage page) {
		IWizard wizard = mock(IWizard.class);
		IWizardContainer container = mock(IWizardContainer.class);
		when(wizard.getContainer()).thenReturn(container);
		page.setWizard(wizard);
		return container;
	}

	private TaskRepository createTaskRepository() {
		TaskRepository repository = new TaskRepository("mock", "url");
		repository.setRepositoryLabel("label");
		return repository;
	}
}
