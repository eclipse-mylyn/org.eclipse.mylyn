/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.bugzilla.tests.ui;

import java.net.MalformedURLException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.ui.tasklist.BugzillaConnectorUi;
import org.eclipse.mylyn.internal.bugzilla.ui.tasklist.BugzillaRepositorySettingsPage;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tests.util.TasksUiTestUtil;
import org.eclipse.ui.PlatformUI;

import junit.framework.TestCase;

/**
 * @author Rob Elves
 */
public class BugzillaRepositorySettingsPageTest extends TestCase {

	private TaskRepositoryManager manager;

	private TaskRepository repository;

	private EditRepositoryWizard wizard;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				BugzillaFixture.current().getRepositoryUrl());
		UserCredentials credentials = CommonTestUtil.getCredentials(PrivilegeLevel.USER);
		repository.setCredentials(AuthenticationType.REPOSITORY,
				new AuthenticationCredentials(credentials.getUserName(), credentials.getPassword()), false);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		TasksUiTestUtil.ensureTasksUiInitialization();
		BugzillaConnectorUi connectorUi = new BugzillaConnectorUi();
		wizard = new EditRepositoryWizard(repository, connectorUi);
	}

	private BugzillaClient createClient(String hostUrl, String username, String password, String htAuthUser,
			String htAuthPass, String encoding) throws MalformedURLException {
		TaskRepository taskRepository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, hostUrl);

		AuthenticationCredentials credentials = new AuthenticationCredentials(username, password);
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);

		AuthenticationCredentials webCredentials = new AuthenticationCredentials(htAuthUser, htAuthPass);
		taskRepository.setCredentials(AuthenticationType.HTTP, webCredentials, false);
		taskRepository.setCharacterEncoding(encoding);

		BugzillaRepositoryConnector connector = (BugzillaRepositoryConnector) TasksUi
				.getRepositoryConnector(repository.getConnectorKind());
		return BugzillaClientFactory.createClient(taskRepository, connector);
	}

	public void testValidationInvalidPassword() throws Exception {

		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		// BugzillaClient client =
		// BugzillaClientFactory.createClient(page.getServerUrl(),
		// page.getUserName(), page.getPassword(), page.getHttpAuthUserId(),
		// page.getHttpAuthPassword(), page.getCharacterEncoding());
		page.setPassword("bogus");
		try {
			BugzillaClient client = createClient(page.getRepositoryUrl(), page.getUserName(), page.getPassword(),
					page.getHttpAuthUserId(), page.getHttpAuthPassword(), page.getCharacterEncoding());
			client.validate(null);
		} catch (CoreException e) {
			return;
		}
		fail("LoginException didn't occur!");
	}

	public void testValidationInvalidUserid() throws Exception {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		page.setUserId("bogus");
		try {
			BugzillaClient client = createClient(page.getRepositoryUrl(), page.getUserName(), page.getPassword(),
					page.getHttpAuthUserId(), page.getHttpAuthPassword(), page.getCharacterEncoding());
			client.validate(null);
		} catch (CoreException e) {
			return;
		}
		fail("LoginException didn't occur!");
	}

	public void testValidationInvalidUrl() throws Exception {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		page.setUrl("http://mylar.eclipse.org");
		try {
			BugzillaClient client = createClient(page.getRepositoryUrl(), page.getUserName(), page.getPassword(),
					page.getHttpAuthUserId(), page.getHttpAuthPassword(), page.getCharacterEncoding());
			client.validate(null);
			fail("UnknownHostException didn't occur!");
		} catch (CoreException e) {
			// skip assertion, some environments will still resolve invalid addresses
			//assertTrue(e.getStatus().getException() instanceof UnknownHostException);
		}
	}

	// TODO: Test locking up?
	// public void testAutoVersion() throws Exception {
	// repository.setVersion(BugzillaRepositorySettingsPage.LABEL_AUTOMATIC_VERSION);
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
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), wizard);
		dialog.create();
		BugzillaRepositorySettingsPage page = (BugzillaRepositorySettingsPage) wizard.getSettingsPage();
		BugzillaClient client = createClient(page.getRepositoryUrl(), page.getUserName(), page.getPassword(),
				page.getHttpAuthUserId(), page.getHttpAuthPassword(), page.getCharacterEncoding());
		client.validate(null);
		page.setUrl(BugzillaFixture.current().getRepositoryUrl());
		wizard.performFinish();
		assertEquals(1, manager.getAllRepositories().size());
		TaskRepository repositoryTest = manager.getRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				BugzillaFixture.current().getRepositoryUrl());
		assertNotNull(repositoryTest);
		assertEquals(tempUid, repositoryTest.getUserName());
		assertEquals(tempPass, repositoryTest.getPassword());
	}
}
