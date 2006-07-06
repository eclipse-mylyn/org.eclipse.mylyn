/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.trac.tests;

import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.tasklist.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylar.internal.trac.MylarTracPlugin;
import org.eclipse.mylar.internal.trac.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.TracTask;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.trac.tests.support.TestFixture;
import org.eclipse.mylar.trac.tests.support.XmlRpcServer.TestData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnectorTest extends TestCase {

	private TestData data;

	private TaskRepository repository;

	private TaskRepositoryManager manager;

	private TracRepositoryConnector connector;

	protected void setUp() throws Exception {
		super.setUp();

		manager = MylarTaskListPlugin.getRepositoryManager();
		manager.clearRepositories();

		data = TestFixture.initializeRepository1();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		// TestFixture.cleanupRepository1();
	}

	protected void init(Version version) {
		String kind = MylarTracPlugin.REPOSITORY_KIND;

		repository = new TaskRepository(kind, Constants.TEST_REPOSITORY1_URL);
		repository.setAuthenticationCredentials(Constants.TEST_REPOSITORY1_USERNAME,
				Constants.TEST_REPOSITORY1_USERNAME);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository);

		AbstractRepositoryConnector abstractConnector = manager.getRepositoryConnector(kind);
		assertEquals(abstractConnector.getRepositoryType(), kind);

		connector = (TracRepositoryConnector) abstractConnector;
		connector.setForceSyncExec(true);
	}

	public void testGetRepositoryUrlFromTaskUrl() {
		TracRepositoryConnector connector = new TracRepositoryConnector();
		assertEquals("http://host/repo", connector.getRepositoryUrlFromTaskUrl("http://host/repo/ticket/1"));
		assertEquals("http://host", connector.getRepositoryUrlFromTaskUrl("http://host/ticket/2342"));
		assertEquals(null, connector.getRepositoryUrlFromTaskUrl("http://host/repo/2342"));
		assertEquals(null, connector.getRepositoryUrlFromTaskUrl("http://host/repo/ticket-2342"));
	}

	public void testCreateTaskFromExistingKeyXmlRpc() {
		init(Version.XML_RPC);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyTrac09() {
		init(Version.TRAC_0_9);
		createTaskFromExistingKey();
	}

	protected void createTaskFromExistingKey() {
		String id = data.tickets.get(0).getId() + "";
		ITask task = connector.createTaskFromExistingKey(repository, id);
		assertNotNull(task);
		assertEquals(TracTask.class, task.getClass());
		assertTrue(task.getDescription().contains("summary1"));
		assertEquals(repository.getUrl() + ITracClient.TICKET_URL + id, task.getUrl());

		task = connector.createTaskFromExistingKey(repository, "does not exist");
		assertNull(task);

		task = connector.createTaskFromExistingKey(repository, Integer.MAX_VALUE + "");
		assertNull(task);
	}

	public void testClientManagerChangeTaskRepositorySettings() throws MalformedURLException {
		init(Version.TRAC_0_9);
		ITracClient client = connector.getClientManager().getRepository(repository);
		assertEquals(Version.TRAC_0_9, client.getVersion());

		EditRepositoryWizard wizard = new EditRepositoryWizard(repository);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.create();

		((TracRepositorySettingsPage) wizard.getSettingsPage()).setTracVersion(Version.XML_RPC);
		assertTrue(wizard.performFinish());

		client = connector.getClientManager().getRepository(repository);
		assertEquals(Version.XML_RPC, client.getVersion());
	}

}
