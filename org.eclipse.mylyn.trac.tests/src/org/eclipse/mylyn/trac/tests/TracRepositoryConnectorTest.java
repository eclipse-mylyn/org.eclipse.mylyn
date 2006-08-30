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

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.PrivilegeLevel;
import org.eclipse.mylar.internal.tasks.ui.search.AbstractQueryHitCollector;
import org.eclipse.mylar.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.InvalidTicketException;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.core.model.TracSearch;
import org.eclipse.mylar.internal.trac.core.model.TracTicket;
import org.eclipse.mylar.internal.trac.core.model.TracVersion;
import org.eclipse.mylar.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylar.internal.trac.ui.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.ui.TracRepositoryQuery;
import org.eclipse.mylar.internal.trac.ui.TracTask;
import org.eclipse.mylar.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.core.TaskRepositoryManager;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
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

	private TaskList tasklist;

	protected void setUp() throws Exception {
		super.setUp();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		tasklist = TasksUiPlugin.getTaskListManager().getTaskList();

		data = TestFixture.init010();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		// TestFixture.cleanupRepository1();
	}

	protected void init(String url, Version version) {
		String kind = TracCorePlugin.REPOSITORY_KIND;
		Credentials credentials = MylarTestUtils.readCredentials(PrivilegeLevel.USER);

		repository = new TaskRepository(kind, url);
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
		repository.setTimeZoneId(ITracClient.TIME_ZONE);
		repository.setCharacterEncoding(ITracClient.CHARSET);
		repository.setVersion(version.name());

		manager.addRepository(repository, TasksUiPlugin.getDefault().getRepositoriesFilePath());

		AbstractRepositoryConnector abstractConnector = manager.getRepositoryConnector(kind);
		assertEquals(abstractConnector.getRepositoryType(), kind);

		connector = (TracRepositoryConnector) abstractConnector;
		TasksUiPlugin.getSynchronizationManager().setForceSyncExec(true);
	}

	public void testGetRepositoryUrlFromTaskUrl() {
		TracRepositoryConnector connector = new TracRepositoryConnector();
		assertEquals("http://host/repo", connector.getRepositoryUrlFromTaskUrl("http://host/repo/ticket/1"));
		assertEquals("http://host", connector.getRepositoryUrlFromTaskUrl("http://host/ticket/2342"));
		assertEquals(null, connector.getRepositoryUrlFromTaskUrl("http://host/repo/2342"));
		assertEquals(null, connector.getRepositoryUrlFromTaskUrl("http://host/repo/ticket-2342"));
	}

	public void testCreateTaskFromExistingKeyXmlRpc_010() {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyTrac09_010() {
		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyTrac09_096() {
		init(Constants.TEST_TRAC_096_URL, Version.TRAC_0_9);
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
		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
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

	public void testPerformQueryXmlRpc010() {
		performQuery(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
	}

	public void testPerformQueryWeb010() {
		performQuery(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
	}

	public void testPerformQueryWeb096() {
		performQuery(Constants.TEST_TRAC_096_URL, Version.TRAC_0_9);
	}

	protected void performQuery(String url, Version version) {
		init(url, version);

		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");

		String queryUrl = url + ITracClient.QUERY_URL + search.toUrl();
		TracRepositoryQuery query = new TracRepositoryQuery(url, queryUrl, "description", tasklist);

		//MultiStatus queryStatus = new MultiStatus(TracUiPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
		final List<AbstractQueryHit> result = new ArrayList<AbstractQueryHit>();
		AbstractQueryHitCollector hitCollector = new AbstractQueryHitCollector() {

			@Override
			public void addMatch(AbstractQueryHit hit) {
				result.add(hit);
			}};
		IStatus queryStatus = connector.performQuery(query, new NullProgressMonitor(), hitCollector);

		assertTrue(queryStatus.isOK());
		assertEquals(3, result.size());
		assertEquals(data.tickets.get(0).getId() + "", result.get(0).getId());
		assertEquals(data.tickets.get(1).getId() + "", result.get(1).getId());
		assertEquals(data.tickets.get(2).getId() + "", result.get(2).getId());
	}

	public void testUpdateTaskDetails() throws InvalidTicketException {
		TracTicket ticket = new TracTicket(123);
		ticket.putBuiltinValue(Key.DESCRIPTION, "mydescription");
		ticket.putBuiltinValue(Key.PRIORITY, "mypriority");
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");
		ticket.putBuiltinValue(Key.TYPE, "mytype");

		TracTask task = new TracTask(AbstractRepositoryTask.getHandle(Constants.TEST_TRAC_010_URL, 123), "desc", true);
		assertEquals(Constants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "123", task.getUrl());
		assertEquals("desc", task.getDescription());
		
		TracRepositoryConnector.updateTaskDetails(task, ticket, false);
		assertEquals(Constants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "123", task.getUrl());
		assertEquals("123: mysummary", task.getDescription());
		assertEquals("P3", task.getPriority());
		assertEquals("mytype", task.getTaskType());
	}

	public void testUpdateTaskDetailsSummaryOnly() throws InvalidTicketException {
		TracTicket ticket = new TracTicket(456);
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");

		TracTask task = new TracTask(AbstractRepositoryTask.getHandle(Constants.TEST_TRAC_010_URL, 456), "desc", true);

		TracRepositoryConnector.updateTaskDetails(task, ticket, false);
		assertEquals(Constants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "456", task.getUrl());
		assertEquals("456: mysummary", task.getDescription());
		assertEquals("P3", task.getPriority());
		assertEquals(null, task.getTaskType());
	}

	public void testUpdateAttributesWeb096() throws Exception {
		init(Constants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		updateAttributes();
	}

	public void testUpdateAttributesWeb010() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		updateAttributes();
	}

	public void testUpdateAttributesXmlRpc010() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		updateAttributes();
	}

	protected void updateAttributes() throws Exception {
		connector.updateAttributes(repository, new NullProgressMonitor());

		ITracClient server = connector.getClientManager().getRepository(repository);
		TracVersion[] versions = server.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("2.0", versions[1].getName());
	}

	public void testContext010() throws Exception {
		init(Constants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);

		int size = task.getTaskData().getAttachments().size();

		File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();

		assertTrue(connector.attachContext(repository, task, "", TasksUiPlugin.getDefault().getProxySettings()));
		
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		assertEquals(size + 1, task.getTaskData().getAttachments().size());
		
		RepositoryAttachment attachment = task.getTaskData().getAttachments().get(size);
		assertTrue(connector.retrieveContext(repository, task, attachment, TasksUiPlugin.getDefault().getProxySettings(), TasksUiPlugin.getDefault().getDataDirectory()));
	}

	public void testContext096() throws Exception {
		init(Constants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingKey(repository, data.attachmentTicketId + "");

		File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();

		try {
			connector.attachContext(repository, task, "", TasksUiPlugin.getDefault().getProxySettings());
			fail("expected CoreException"); // operation should not be supported
		} catch (CoreException e) {
		}
	}

}