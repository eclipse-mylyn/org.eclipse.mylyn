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

package org.eclipse.mylyn.trac.tests;

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
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.tasks.ui.TaskFactory;
import org.eclipse.mylyn.internal.tasks.ui.wizards.EditRepositoryWizard;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.InvalidTicketException;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.TracTask;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;
import org.eclipse.mylyn.internal.trac.core.model.TracTicket.Key;
import org.eclipse.mylyn.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.trac.tests.support.TestFixture;
import org.eclipse.mylyn.trac.tests.support.XmlRpcServer.TestData;
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

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		manager = TasksUiPlugin.getRepositoryManager();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());

		data = TestFixture.init010();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// TestFixture.cleanupRepository1();
	}

	protected void init(String url, Version version) {
		String kind = TracCorePlugin.REPOSITORY_KIND;
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);

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

	public void testCreateTaskFromExistingKeyXmlRpc011() throws CoreException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyXmlRpc010() throws CoreException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyTracWeb011() throws CoreException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyTracWeb010() throws CoreException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		createTaskFromExistingKey();
	}

	public void testCreateTaskFromExistingKeyTracWeb096() throws CoreException {
		init(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		createTaskFromExistingKey();
	}

	protected void createTaskFromExistingKey() throws CoreException {
		String id = data.tickets.get(0).getId() + "";
		AbstractTask task = connector.createTaskFromExistingId(repository, id, new NullProgressMonitor());
		assertNotNull(task);
		assertEquals(TracTask.class, task.getClass());
		assertTrue(task.getSummary().contains("summary1"));
		assertEquals(repository.getUrl() + ITracClient.TICKET_URL + id, task.getTaskUrl());

		try {
			task = connector.createTaskFromExistingId(repository, "does not exist", new NullProgressMonitor());
			fail("Expected CoreException");
		} catch (CoreException e) {
		}

		// No longer parsing as an integer
		// try {
		// task = connector.createTaskFromExistingId(repository,
		// Integer.MAX_VALUE + "");
		// fail("Expected CoreException");
		// } catch (CoreException e) {
		//		}
	}

	public void testClientManagerChangeTaskRepositorySettings() throws MalformedURLException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
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

	public void testPerformQueryXmlRpc011() {
		performQuery(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
	}

	public void testPerformQueryXmlRpc010() {
		performQuery(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
	}

	public void testPerformQueryWeb011() {
		performQuery(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
	}

	public void testPerformQueryWeb010() {
		performQuery(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
	}

	public void testPerformQueryWeb096() {
		performQuery(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);
	}

	protected void performQuery(String url, Version version) {
		init(url, version);

		TracSearch search = new TracSearch();
		search.addFilter("milestone", "milestone1");
		search.addFilter("milestone", "milestone2");
		search.setOrderBy("id");

		String queryUrl = url + ITracClient.QUERY_URL + search.toUrl();
		TracRepositoryQuery query = new TracRepositoryQuery(url, queryUrl, "description");

		//MultiStatus queryStatus = new MultiStatus(TracUiPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
		final List<AbstractTask> result = new ArrayList<AbstractTask>();
		QueryHitCollector hitCollector = new QueryHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(), new TaskFactory(repository)) {
			public void accept(RepositoryTaskData data) {
				fail("Unexpected call to accept()");
			}
			@Override
			public void accept(AbstractTask hit) {
				result.add(hit);
			}};
		IStatus queryStatus = connector.performQuery(query, repository, new NullProgressMonitor(), hitCollector);

		assertTrue(queryStatus.isOK());
		assertEquals(3, result.size());
		assertEquals(data.tickets.get(0).getId() + "", result.get(0).getTaskId());
		assertEquals(data.tickets.get(1).getId() + "", result.get(1).getTaskId());
		assertEquals(data.tickets.get(2).getId() + "", result.get(2).getTaskId());
	}

	public void testUpdateTaskDetails() throws InvalidTicketException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		
		TracTicket ticket = new TracTicket(123);
		ticket.putBuiltinValue(Key.DESCRIPTION, "mydescription");
		ticket.putBuiltinValue(Key.PRIORITY, "mypriority");
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");
		ticket.putBuiltinValue(Key.TYPE, "mytype");

		TracTask task = new TracTask(TracTestConstants.TEST_TRAC_010_URL, ""+123, "desc");
		assertEquals(TracTestConstants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "123", task.getTaskUrl());
		assertEquals("desc", task.getSummary());
		
		connector.updateTaskFromTicket(task, ticket, false);
		assertEquals(TracTestConstants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "123", task.getTaskUrl());
		assertEquals("123", task.getTaskKey());
		assertEquals("mysummary", task.getSummary());
		assertEquals("P3", task.getPriority());
		assertEquals("mytype", task.getTaskKind());
	}

	public void testUpdateTaskDetailsSummaryOnly() throws InvalidTicketException {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		
		TracTicket ticket = new TracTicket(456);
		ticket.putBuiltinValue(Key.SUMMARY, "mysummary");

		TracTask task = new TracTask(TracTestConstants.TEST_TRAC_010_URL, ""+456, "desc");

		connector.updateTaskFromTicket(task, ticket, false);
		assertEquals(TracTestConstants.TEST_TRAC_010_URL + ITracClient.TICKET_URL + "456", task.getTaskUrl());
		assertEquals("456", task.getTaskKey());
		assertEquals("mysummary", task.getSummary());
		assertEquals("P3", task.getPriority());
		assertEquals(AbstractTask.DEFAULT_TASK_KIND, task.getTaskKind());
	}

	public void testUpdateAttributesWeb011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.TRAC_0_9);
		updateAttributes();
	}

	public void testUpdateAttributesWeb010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.TRAC_0_9);
		updateAttributes();
	}

	public void testUpdateAttributesWeb096() throws Exception {
		init(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		updateAttributes();
	}

	public void testUpdateAttributesXmlRpc011() throws Exception {
		init(TracTestConstants.TEST_TRAC_011_URL, Version.XML_RPC);
		updateAttributes();
	}

	public void testUpdateAttributesXmlRpc010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
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

	public void testContextXmlRpc010() throws Exception {
		init(TracTestConstants.TEST_TRAC_010_URL, Version.XML_RPC);
		TracTask task = (TracTask) connector.createTaskFromExistingId(repository, data.attachmentTicketId + "", new NullProgressMonitor());
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);

		//int size = task.getTaskData().getAttachments().size();

		File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();

		assertTrue(connector.attachContext(repository, task, "", new NullProgressMonitor()));
		
		TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
		// TODO attachment may have been overridden therefore size may not have changed
		//assertEquals(size + 1, task.getTaskData().getAttachments().size());
		
		//RepositoryAttachment attachment = task.getTaskData().getAttachments().get(size);
		//assertTrue(connector.retrieveContext(repository, task, attachment, TasksUiPlugin.getDefault().getProxySettings(), TasksUiPlugin.getDefault().getDataDirectory()));
	}

	public void testContextWeb096() throws Exception {
		init(TracTestConstants.TEST_TRAC_096_URL, Version.TRAC_0_9);
		TracTask task = (TracTask) connector.createTaskFromExistingId(repository, data.attachmentTicketId + "", new NullProgressMonitor());

		File sourceContextFile = ContextCorePlugin.getContextManager().getFileForContext(task.getHandleIdentifier());
		sourceContextFile.createNewFile();
		sourceContextFile.deleteOnExit();

		try {
			connector.attachContext(repository, task, "", new NullProgressMonitor());
			fail("expected CoreException"); // operation should not be supported
		} catch (CoreException e) {
		}
	}

}