/*******************************************************************************
 * Copyright (c) 2014, 2016 Tasktop Technologies and others.
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.swt.widgets.Combo;
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

		Combo getServerUrlCombo() {
			return serverUrlCombo;
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
		TaskRepository repository = createTaskRepository();
		repository.removeProperty(ITasksCoreConstants.PROPERTY_BRAND_ID);
		AbstractRepositorySettingsPage page = createPage(null);

		assertNull(repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
		page.applyTo(repository);
		assertNull(repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));

		page.setBrand("org.mylyn");
		page.applyTo(repository);
		assertEquals("org.mylyn", repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	@Test
	public void applyToExistingRepository() {
		TaskRepository repository = createTaskRepository();
		repository.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, "existing.brand");
		AbstractRepositorySettingsPage page = createPage(repository);

		page.setBrand("org.mylyn");
		page.applyTo(repository);
		assertEquals("org.mylyn", repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	@Test
	public void applyNullBrandToExistingRepository() {
		TaskRepository repository = createTaskRepository();
		repository.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, "existing.brand");
		AbstractRepositorySettingsPage page = createPage(repository);

		page.setBrand(null);
		page.applyTo(repository);
		assertEquals("existing.brand", repository.getProperty(ITasksCoreConstants.PROPERTY_BRAND_ID));
	}

	@Test
	public void setsTitleFromBrand() {
		AbstractRepositorySettingsPage page = createPage(null);
		assertEquals("Title", page.getTitle());
		page.setBrand("org.mylyn");
		assertEquals("Label for org.mylyn", page.getTitle());
	}

	@Test
	public void setsTitleFromBrandedRepository() {
		TaskRepository repository = createTaskRepository();
		repository.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, "org.mylyn");
		AbstractRepositorySettingsPage page = createPage(repository);
		assertEquals("Label for org.mylyn", page.getTitle());
	}

	@Test
	public void setsTitleFromUnbrandedRepository() {
		TaskRepository repository = createTaskRepository();
		repository.removeProperty(ITasksCoreConstants.PROPERTY_BRAND_ID);
		AbstractRepositorySettingsPage page = createPage(repository);
		assertEquals("Title", page.getTitle());
	}

	@Test
	public void setUrlReadOnlyNotCalled() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		Combo serverUrlCombo = page.getServerUrlCombo();
		assertTrue(serverUrlCombo.isEnabled());
	}

	@Test
	public void setUrlReadOnlyTrue() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setUrlReadOnly(true);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		Combo serverUrlCombo = page.getServerUrlCombo();
		assertFalse(serverUrlCombo.isEnabled());
	}

	@Test
	public void setUrlReadOnlyFalse() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setUrlReadOnly(false);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		Combo serverUrlCombo = page.getServerUrlCombo();
		assertTrue(serverUrlCombo.isEnabled());
	}

	@Test
	public void setNeedsRepositoryCredentialsInitializeFalse() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setNeedsRepositoryCredentials(false);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		assertFalse(page.needsRepositoryCredentials());
		assertNull(page.repositoryUserNameEditor);
		assertNull(page.repositoryPasswordEditor);
	}

	@Test
	public void setNeedsRepositoryCredentialsInitializeTrue() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setNeedsRepositoryCredentials(true);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		assertTrue(page.needsRepositoryCredentials());
		assertCredentialsEnabled(page);
		assertPasswordIsSecret(page);
	}

	@Test
	public void setNeedsRepositoryCredentialsFalse() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setNeedsRepositoryCredentials(true);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		assertCredentialsEnabled(page);
		assertPasswordIsSecret(page);

		page.setNeedsRepositoryCredentials(false);

		assertCredentialsDisabled(page);
		assertPasswordIsSecret(page);
	}

	@Test
	public void setNeedsRepositoryCredentialsTrue() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setNeedsRepositoryCredentials(true);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		page.setNeedsRepositoryCredentials(false);
		assertCredentialsDisabled(page);
		assertPasswordIsSecret(page);

		page.setNeedsRepositoryCredentials(true);
		assertCredentialsEnabled(page);
		assertPasswordIsSecret(page);
	}

	@Test
	public void repositoryCredentialsEnabledOnlyIfNeededAndNotAnonymous() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		page.setNeedsRepositoryCredentials(true);
		page.setNeedsAnonymousLogin(true);
		page.createControl(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());

		assertTrue(page.needsRepositoryCredentials());
		assertTrue(page.isAnonymousAccess());
		assertCredentialsDisabled(page);
		assertPasswordIsSecret(page);

		page.setAnonymous(false);
		page.setNeedsRepositoryCredentials(false);

		assertCredentialsDisabled(page);
		assertPasswordIsSecret(page);

		page.setNeedsRepositoryCredentials(true);

		assertCredentialsEnabled(page);
		assertPasswordIsSecret(page);
	}

	@Test
	public void isUrlReadOnly() throws Exception {
		TestRepositorySettingsPage page = new TestRepositorySettingsPage(createTaskRepository());
		assertFalse(page.isUrlReadOnly());
		page.setUrlReadOnly(true);
		assertTrue(page.isUrlReadOnly());
		page.setUrlReadOnly(false);
		assertFalse(page.isUrlReadOnly());
	}

	private AbstractRepositorySettingsPage createPage(TaskRepository repository) {
		AbstractRepositorySettingsPage page = spy(new RepositorySettingsPageWithNoCredentials(repository));
		doReturn("label").when(page).getRepositoryLabel();
		when(page.needsProxy()).thenReturn(false);
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

	private void assertCredentialsEnabled(TestRepositorySettingsPage page) {
		assertTrue(page.repositoryUserNameEditor.getTextControl(page.compositeContainer).isEnabled());
		assertTrue(page.repositoryPasswordEditor.getTextControl(page.compositeContainer).isEnabled());
	}

	private void assertCredentialsDisabled(TestRepositorySettingsPage page) {
		assertFalse(page.repositoryUserNameEditor.getTextControl(page.compositeContainer).isEnabled());
		assertFalse(page.repositoryPasswordEditor.getTextControl(page.compositeContainer).isEnabled());
	}

	private void assertPasswordIsSecret(TestRepositorySettingsPage page) {
		StringFieldEditor passwordEditor = page.repositoryPasswordEditor;
		assertEquals('*', passwordEditor.getTextControl(page.compositeContainer).getEchoChar());
	}
}
