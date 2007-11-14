/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.tasklist.BugzillaRepositorySettingsPage;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.web.core.WebClientUtil;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class RepositoryEditorWizardTest extends TestCase {

	private TaskRepositoryManager manager;

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		Credentials credentials = TestUtil.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setAnonymous(false);
		TasksUiPlugin.getRepositoryManager().addRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	public void testValidationInvalidPassword() throws Exception {

		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		// BugzillaClient client =
		// BugzillaClientFactory.createClient(page.getServerUrl(),
		// page.getUserName(), page.getPassword(), page.getHttpAuthUserId(),
		// page.getHttpAuthPassword(), page.getCharacterEncoding());
		page.setPassword("bogus");
		try {
			BugzillaClient client = BugzillaClientFactory.createClient(page.getServerUrl(), page.getUserName(),
					page.getPassword(), page.getHttpAuthUserId(), page.getHttpAuthPassword(),
					WebClientUtil.getPlatformProxy(), page.getCharacterEncoding());
			client.validate();
		} catch (CoreException e) {
			return;
		}
		fail("LoginException didn't occur!");
	}

	public void testValidationInvalidUserid() throws Exception {
		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		page.setUserId("bogus");
		try {
			BugzillaClient client = BugzillaClientFactory.createClient(page.getServerUrl(), page.getUserName(),
					page.getPassword(), page.getHttpAuthUserId(), page.getHttpAuthPassword(),
					WebClientUtil.getPlatformProxy(), page.getCharacterEncoding());
			client.validate();
		} catch (CoreException e) {
			return;
		}
		fail("LoginException didn't occur!");
	}

	public void testValidationInvalidUrl() throws Exception {
		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		page.setUrl("http://mylar.eclipse.org");
		try {
			BugzillaClient client = BugzillaClientFactory.createClient(page.getServerUrl(), page.getUserName(),
					page.getPassword(), page.getHttpAuthUserId(), page.getHttpAuthPassword(),
					WebClientUtil.getPlatformProxy(), page.getCharacterEncoding());
			client.validate();
		} catch (CoreException e) {
			assertTrue(e.getStatus().getException() instanceof UnknownHostException);
			return;
		}
		fail("UnknownHostException didn't occur!");
	}

	// TODO: Test locking up?
	// public void testAutoVersion() throws Exception {
	// repository.setVersion(BugzillaRepositorySettingsPage.LABEL_AUTOMATIC_VERSION);
	// EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
	// WizardDialog dialog = new
	// WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
	// wizard);
	// dialog.create();
	// BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage)
	// wizard.getSettingsPage();
	// page.setTesting(true);
	// assertEquals(BugzillaRepositorySettingsPage.LABEL_AUTOMATIC_VERSION,
	// page.getVersion());
	// page.validateSettings();
	// assertEquals("2.22", page.getVersion());
	// }

	public void testPersistChangeOfUrl() throws Exception {
		assertEquals(1, manager.getAllRepositories().size());
		String tempUid = repository.getUserName();
		String tempPass = repository.getPassword();
		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		BugzillaClient client = BugzillaClientFactory.createClient(page.getServerUrl(), page.getUserName(),
				page.getPassword(), page.getHttpAuthUserId(), page.getHttpAuthPassword(),
				WebClientUtil.getPlatformProxy(), page.getCharacterEncoding());
		client.validate();
		page.setUrl(IBugzillaConstants.TEST_BUGZILLA_218_URL);
		wizard.performFinish();
		assertEquals(1, manager.getAllRepositories().size());
		TaskRepository repositoryTest = manager.getRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_218_URL);
		assertNotNull(repositoryTest);
		assertEquals(tempUid, repositoryTest.getUserName());
		assertEquals(tempPass, repositoryTest.getPassword());
	}

	public void testPersistChangeUserId() throws Exception {
		assertEquals(1, manager.getAllRepositories().size());
		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		BugzillaClient client = BugzillaClientFactory.createClient(page.getServerUrl(), page.getUserName(),
				page.getPassword(), page.getHttpAuthUserId(), page.getHttpAuthPassword(),
				WebClientUtil.getPlatformProxy(), page.getCharacterEncoding());
		client.validate();
		page.setUserId("bogus");
		wizard.performFinish();
		assertEquals(1, manager.getAllRepositories().size());
		TaskRepository repositoryTest = manager.getRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		assertNotNull(repositoryTest);
		wizard = new EditRepositoryWizard(repositoryTest);
		dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		try {
			client = BugzillaClientFactory.createClient(page.getServerUrl(), page.getUserName(), page.getPassword(),
					page.getHttpAuthUserId(), page.getHttpAuthPassword(), WebClientUtil.getPlatformProxy(),
					page.getCharacterEncoding());
			client.validate();
		} catch (CoreException e) {
			return;
		}
		fail("LoginException didn't occur!");
	}

}
