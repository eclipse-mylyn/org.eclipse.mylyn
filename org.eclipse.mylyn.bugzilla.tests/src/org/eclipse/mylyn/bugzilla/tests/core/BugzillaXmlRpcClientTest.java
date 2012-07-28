/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.AbstractBugzillaOperation;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;
import org.eclipse.mylyn.internal.bugzilla.core.CustomTransitionManager;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.core.service.BugzillaXmlRpcClient;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tests.util.TestFixture;

/**
 * Tests should be run against Bugzilla 3.6 or greater
 * 
 * @author Frank Becker
 */
public class BugzillaXmlRpcClientTest extends TestCase {

	private static final String BUGZILLA_LE_4_0 = "<4.0";

	private static final String BUGZILLA_GE_4_0 = ">=4.0";

	private BugzillaXmlRpcClient bugzillaClient;

	private TaskRepository repository;

	private BugzillaRepositoryConnector connector;

	protected TaskRepositoryManager manager;

	private BugzillaClient client;

	@SuppressWarnings("serial")
	private final Map<String, Map<String, ArrayList<String>>> fixtureTransitionsMap = new HashMap<String, Map<String, ArrayList<String>>>() {
		{
			put(BUGZILLA_LE_4_0, new HashMap<String, ArrayList<String>>() {
				{
					put("NEW", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
						}
					});
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
						}
					});
					put("ASSIGNED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("markNew");
						}
					});
					put("REOPENED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("close");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("close");
						}
					});
					put("CLOSED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
						}
					});
				}
			});
			put(BUGZILLA_GE_4_0, new HashMap<String, ArrayList<String>>() {
				{
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("CONFIRMED");
							add("duplicate");
							add("IN_PROGRESS");
						}
					});
					put("IN_PROGRESS", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("CONFIRMED");
						}
					});
					put("CONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("IN_PROGRESS");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("CONFIRMED");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("CONFIRMED");
						}
					});
				}
			});
			put(BugzillaFixture.CUSTOM_WF, new HashMap<String, ArrayList<String>>() {
				{
					put("NEW", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("close");
						}
					});
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
						}
					});
					put("ASSIGNED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("markNew");
						}
					});
					put("REOPENED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
							add("reopen");
							add("close");
							add("verify");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("close");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("close");
						}
					});
					put("CLOSED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
						}
					});
				}
			});
			put(BugzillaFixture.CUSTOM_WF_AND_STATUS, new HashMap<String, ArrayList<String>>() {
				{
					put("NEW", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("close");
							add("reopen");
						}
					});
					put("UNCONFIRMED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
							add("close");
						}
					});
					put("ASSIGNED", new ArrayList<String>() {
						{
							add("resolve");
							add("duplicate");
							add("markNew");
							add("close");
						}
					});
					put("REOPENED", new ArrayList<String>() {
						{
							add("resolve");
							add("accept");
							add("duplicate");
							add("markNew");
							add("reopen");
							add("close");
						}
					});
					put("RESOLVED", new ArrayList<String>() {
						{
							add("verify");
							add("reopen");
							add("close");
						}
					});
					put("VERIFIED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("close");
							add("MODIFIED");
						}
					});
					put("CLOSED", new ArrayList<String>() {
						{
							add("reopen");
							add("resolve");
							add("duplicate");
							add("verify");
						}
					});
					put("ON_DEV", new ArrayList<String>() {
						{
							add("close");
							add("POST");
						}
					});
					put("POST", new ArrayList<String>() {
						{
							add("close");
						}
					});
					put("MODIFIED", new ArrayList<String>() {
						{
							add("close");
							add("ON_DEV");
						}
					});
				}
			});
		}
	};

	@Override
	public void setUp() throws Exception {
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);
		manager = TasksUiPlugin.getRepositoryManager();
		TestFixture.resetTaskListAndRepositories();
		WebLocation webLocation = new WebLocation(BugzillaFixture.current().getRepositoryUrl() + "/xmlrpc.cgi");
		webLocation.setCredentials(AuthenticationType.REPOSITORY, "tests@mylyn.eclipse.org", "mylyntest");
		client = BugzillaFixture.current().client(PrivilegeLevel.USER);
		repository = BugzillaFixture.current().repository();
		connector = BugzillaFixture.current().connector();
		bugzillaClient = new BugzillaXmlRpcClient(webLocation, client);
		bugzillaClient.setContentTypeCheckingEnabled(true);
		TasksUi.getRepositoryManager().addRepository(repository);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestFixture.resetTaskList();
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	protected TaskDataModel createModel(ITask task) throws CoreException {
		ITaskDataWorkingCopy taskDataState = getWorkingCopy(task);
		return new TaskDataModel(repository, task, taskDataState);
	}

	protected ITaskDataWorkingCopy getWorkingCopy(ITask task) throws CoreException {
		return TasksUiPlugin.getTaskDataManager().getWorkingCopy(task);
	}

	protected void submit(TaskDataModel model) {
		SubmitJob submitJob = TasksUiInternal.getJobFactory().createSubmitTaskJob(connector, model.getTaskRepository(),
				model.getTask(), model.getTaskData(), model.getChangedOldAttributes());
		submitJob.schedule();
		try {
			submitJob.join();
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

//	protected void synchAndAssertState(Set<ITask> tasks, SynchronizationState state) {
//		for (ITask task : tasks) {
//			TasksUiInternal.synchronizeTask(connector, task, true, null);
//			TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
//			assertEquals(task.getSynchronizationState(), state);
//		}
//	}

	protected ITask generateLocalTaskAndDownload(String id) throws CoreException {
		ITask task = TasksUi.getRepositoryModel().createTask(repository, id);
		TasksUiPlugin.getTaskList().addTask(task);
		TasksUiInternal.synchronizeTask(connector, task, true, null);
		TasksUiPlugin.getTaskDataManager().setTaskRead(task, true);
		return task;
	}

	protected String taskExists() {
		String taskID = null;
		String queryUrlString = repository.getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20XMLRPC%20getBugData&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=TestComponent&product=TestProduct";
		RepositoryQuery query = new RepositoryQuery(repository.getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector.performQuery(repository, query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	protected String createTask() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {

			@Override
			public String getProduct() {
				return "TestProduct";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getComponent() {
				return "TestComponent";
			}

			@Override
			public String getSummary() {
				return "test XMLRPC getBugData";
			}

			@Override
			public String getDescription() {
				return "The Description of the XMLRPC getBugData Bug";
			}
		};
		TaskAttribute flagA = null;
		TaskAttribute flagB = null;
		TaskAttribute flagC = null;
		TaskAttribute flagD = null;
		TaskAttribute stateA = null;
		TaskAttribute stateB = null;
		TaskAttribute stateC = null;
		TaskAttribute stateD = null;
		final TaskData[] taskDataNew = new TaskData[1];

		// create Task
		taskDataNew[0] = TasksUiInternal.createTaskData(repository, taskMappingInit, taskMappingSelect, null);
		ITask taskNew = TasksUiUtil.createOutgoingNewTask(taskDataNew[0].getConnectorKind(),
				taskDataNew[0].getRepositoryUrl());

		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(taskNew, taskDataNew[0]);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		workingCopy.save(changed, null);

		RepositoryResponse response = BugzillaFixture.current().submitTask(taskDataNew[0], client);//connector.getTaskDataHandler().postTaskData(repository, taskDataNew[0], changed,
		//new NullProgressMonitor());
		((AbstractTask) taskNew).setSubmitting(true);

		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		ITask task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		TaskDataModel model = createModel(task);
		TaskData taskData = model.getTaskData();
		assertNotNull(taskData);
		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		/* Test uploading a proper file */
		String fileName = "test-attach-1.txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file from " + System.currentTimeMillis());
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("Description");
		attachment.setName("My Attachment 1");
		try {
			client.postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment, attrAttachment,
					new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		taskData = BugzillaFixture.current().getTask(taskData.getTaskId(), client);
		assertNotNull(taskData);

		TaskAttribute attachmentAttribute = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(0);
		int flagCount = 0;
		int flagCountUnused = 0;
		TaskAttribute attachmentFlag1 = null;
		TaskAttribute attachmentFlag2 = null;
		for (TaskAttribute attribute : attachmentAttribute.getAttributes().values()) {
			if (!attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
				continue;
			}
			flagCount++;
			if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG_TYPE)) {
				flagCountUnused++;
				TaskAttribute stateAttribute = taskData.getAttributeMapper().getAssoctiatedAttribute(attribute);
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag1")) {
					attachmentFlag1 = attribute;
				}
				if (stateAttribute.getMetaData().getLabel().equals("AttachmentFlag2")) {
					attachmentFlag2 = attribute;
				}
			}
		}
		assertEquals(2, flagCount);
		assertEquals(2, flagCountUnused);
		assertNotNull(attachmentFlag1);
		assertNotNull(attachmentFlag2);
		TaskAttribute stateAttribute1 = taskData.getAttributeMapper().getAssoctiatedAttribute(attachmentFlag1);
		stateAttribute1.setValue("?");
		TaskAttribute requestee = attachmentFlag1.getAttribute("requestee"); //$NON-NLS-1$
		requestee.setValue("guest@mylyn.eclipse.org");
		client.postUpdateAttachment(attachmentAttribute, "update", null);

		task = generateLocalTaskAndDownload(taskId);
		assertNotNull(task);
		model = createModel(task);
		taskData = model.getTaskData();
		assertNotNull(taskData);

		for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
			if (taskAttribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
				TaskAttribute state = taskAttribute.getAttribute("state");
				if (state.getMetaData().getLabel().equals("BugFlag1")) {
					flagA = taskAttribute;
					stateA = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag2")) {
					flagB = taskAttribute;
					stateB = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag3")) {
					flagC = taskAttribute;
					stateC = state;
				} else if (state.getMetaData().getLabel().equals("BugFlag4")) {
					flagD = taskAttribute;
					stateD = state;
				}
			}
		}
		assertNotNull(flagA);
		assertNotNull(flagB);
		assertNotNull(flagC);
		assertNotNull(flagD);
		assertNotNull(stateA);
		assertNotNull(stateB);
		assertNotNull(stateC);
		assertNotNull(stateD);
		if (flagD != null) {
			TaskAttribute requesteeD = flagD.getAttribute("requestee");
			requesteeD.setValue("guest@mylyn.eclipse.org");
		}
		if (stateA != null) {
			stateA.setValue("+");
		}
		if (stateB != null) {
			stateB.setValue("?");
		}
		if (stateC != null) {
			stateC.setValue("?");
		}
		if (stateD != null) {
			stateD.setValue("?");
		}

		TaskAttribute cf_freetext = taskData.getRoot().getAttribute("cf_freetext");
		TaskAttribute cf_dropdown = taskData.getRoot().getAttribute("cf_dropdown");
		TaskAttribute cf_largetextbox = taskData.getRoot().getAttribute("cf_largetextbox");
		TaskAttribute cf_multiselect = taskData.getRoot().getAttribute("cf_multiselect");
		TaskAttribute cf_datetime = taskData.getRoot().getAttribute("cf_datetime");
		TaskAttribute cf_bugid = taskData.getRoot().getAttribute("cf_bugid");
		cf_freetext.setValue("Freetext");
		cf_dropdown.setValue("one");
		cf_largetextbox.setValue("large text box");
		cf_multiselect.setValue("Blue");
		cf_datetime.setValue("2012-01-01 00:00:00");
		cf_bugid.setValue("3");
//		<cf_freetext>aaaa</cf_freetext>
//		<cf_dropdown>one</cf_dropdown>
//		<cf_largetextbox>aaaaaaaaaaa</cf_largetextbox>
//		<cf_multiselect>Blue</cf_multiselect>
//		<cf_datetime>2012-02-01 00:00:00</cf_datetime>
//		<cf_bugid>3</cf_bugid>
		model.attributeChanged(cf_freetext);
		model.attributeChanged(cf_dropdown);
		model.attributeChanged(cf_largetextbox);
		model.attributeChanged(cf_multiselect);
		model.attributeChanged(cf_datetime);
		model.attributeChanged(cf_bugid);
		model.attributeChanged(flagA);
		model.attributeChanged(flagB);
		model.attributeChanged(flagC);
		model.attributeChanged(flagD);
		changed.clear();
		changed.add(flagA);
		changed.add(flagB);
		changed.add(flagC);
		changed.add(flagD);
		changed.add(cf_freetext);
		changed.add(cf_dropdown);
		changed.add(cf_largetextbox);
		changed.add(cf_multiselect);
		changed.add(cf_datetime);
		changed.add(cf_bugid);

		workingCopy.save(changed, null);
		response = BugzillaFixture.current().submitTask(taskData, client);

		return taskId;
	}

//	@SuppressWarnings("unused")
// only for local development work
//	public void testXmlRpc() throws Exception {
//		if (!BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
//			IProgressMonitor monitor = new NullProgressMonitor();
//			HashMap<?, ?> x1 = bugzillaClient.getTime(monitor);
//			Date x2 = bugzillaClient.getDBTime(monitor);
//			Date x3 = bugzillaClient.getWebTime(monitor);
//			if (BugzillaFixture.current() != BugzillaFixture.BUGS_3_4) {
//				Object[] xx3 = bugzillaClient.getAllFields(monitor);
//				Object[] xx4 = bugzillaClient.getFieldsWithNames(monitor, new String[] { "qa_contact" });
//				Object[] xx5 = bugzillaClient.getFieldsWithIDs(monitor, new Integer[] { 12, 18 });
//			}
//		}
//	}
	public void testGetVersion() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			IProgressMonitor monitor = new NullProgressMonitor();
			String version = bugzillaClient.getVersion(monitor);
			assertEquals(BugzillaFixture.current().getVersion().toUpperCase(), version.toUpperCase());
		}
	}

	@SuppressWarnings("unchecked")
	public void testUserInfo() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			IProgressMonitor monitor = new NullProgressMonitor();
			int uID = bugzillaClient.login(monitor);
			assertEquals(2, uID);
			Object[] userList0 = bugzillaClient.getUserInfoFromIDs(monitor, new Integer[] { 1, 2 });
			assertNotNull(userList0);
			assertEquals(2, userList0.length);
			assertEquals(((Integer) 1), ((HashMap<String, Integer>) userList0[0]).get("id"));
			assertEquals("admin@mylyn.eclipse.org", ((HashMap<String, String>) userList0[0]).get("email"));
			assertEquals("admin@mylyn.eclipse.org", ((HashMap<String, String>) userList0[0]).get("name"));
			assertEquals("Mylyn Admin", ((HashMap<String, String>) userList0[0]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList0[0]).get("can_login"));

			assertEquals(((Integer) 2), ((HashMap<String, Integer>) userList0[1]).get("id"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList0[1]).get("email"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList0[1]).get("name"));
			assertEquals("Mylyn Test", ((HashMap<String, String>) userList0[1]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList0[1]).get("can_login"));

			Object[] userList1 = bugzillaClient.getUserInfoFromNames(monitor,
					new String[] { "tests@mylyn.eclipse.org" });
			assertNotNull(userList1);
			assertEquals(1, userList1.length);
			assertEquals(((Integer) 2), ((HashMap<String, Integer>) userList1[0]).get("id"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList1[0]).get("email"));
			assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList1[0]).get("name"));
			assertEquals("Mylyn Test", ((HashMap<String, String>) userList1[0]).get("real_name"));
			assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList1[0]).get("can_login"));

			if (BugzillaFixture.current() != BugzillaFixture.BUGS_3_4) {
				Object[] userList2 = bugzillaClient.getUserInfoWithMatch(monitor, new String[] { "est" });
				assertEquals(2, userList2.length);
				assertEquals(((Integer) 3), ((HashMap<String, Integer>) userList2[0]).get("id"));
				assertEquals("guest@mylyn.eclipse.org", ((HashMap<String, String>) userList2[0]).get("email"));
				assertEquals("guest@mylyn.eclipse.org", ((HashMap<String, String>) userList2[0]).get("name"));
				assertEquals("Mylyn guest", ((HashMap<String, String>) userList2[0]).get("real_name"));
				assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList2[0]).get("can_login"));

				assertEquals(((Integer) 2), ((HashMap<String, Integer>) userList2[1]).get("id"));
				assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList2[1]).get("email"));
				assertEquals("tests@mylyn.eclipse.org", ((HashMap<String, String>) userList2[1]).get("name"));
				assertEquals("Mylyn Test", ((HashMap<String, String>) userList2[1]).get("real_name"));
				assertEquals(((Boolean) true), ((HashMap<String, Boolean>) userList2[1]).get("can_login"));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void testProductInfo() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			IProgressMonitor monitor = new NullProgressMonitor();
			int uID = bugzillaClient.login(monitor);
			assertEquals(2, uID);
			Object[] selProductIDs = bugzillaClient.getSelectableProducts(monitor);
			assertNotNull(selProductIDs);
			assertEquals(3, selProductIDs.length);
			Object[] enterProductIDs = bugzillaClient.getEnterableProducts(monitor);
			assertNotNull(enterProductIDs);
			assertEquals(3, enterProductIDs.length);
			Object[] accessibleProductIDs = bugzillaClient.getAccessibleProducts(monitor);
			assertNotNull(accessibleProductIDs);
			assertEquals(3, accessibleProductIDs.length);
			Object[] productDetails = bugzillaClient.getProducts(monitor, new Integer[] { 1, 3 });
			assertNotNull(productDetails);
			assertEquals(2, productDetails.length);
			assertTrue(((HashMap<String, Integer>) productDetails[0]).get("id") == 1
					|| ((HashMap<String, Integer>) productDetails[1]).get("id") == 1);
			assertTrue(((HashMap<String, Integer>) productDetails[0]).get("id") == 3
					|| ((HashMap<String, Integer>) productDetails[1]).get("id") == 3);
			int idx = ((HashMap<String, Integer>) productDetails[0]).get("id") == 1 ? 0 : 1;

			assertEquals(((Integer) 1), ((HashMap<String, Integer>) productDetails[idx]).get("id"));
			assertEquals(
					"This is a test product. This ought to be blown away and replaced with real stuff in a finished installation of bugzilla.",
					((HashMap<String, String>) productDetails[idx]).get("description"));
			idx = (idx + 1) % 2;
			assertEquals(((Integer) 3), ((HashMap<String, Integer>) productDetails[idx]).get("id"));
			assertEquals("Product for manual testing",
					((HashMap<String, String>) productDetails[idx]).get("description"));

		}
	}

	@SuppressWarnings("unused")
	public void testXmlRpcInstalled() throws Exception {
		int uID = -1;
		IProgressMonitor monitor = new NullProgressMonitor();
		BugzillaFixture a = BugzillaFixture.current();
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			try {
				uID = bugzillaClient.login(monitor);
				fail("Never reach this! We should get an XmlRpcException");
			} catch (XmlRpcException e) {
				assertEquals("The server returned an unexpected content type: 'text/html; charset=UTF-8'",
						e.getMessage());
			}
		} else {
			uID = bugzillaClient.login(monitor);
			assertEquals(2, uID);
		}
	}

	public void testTransitionManagerWithXml() throws Exception {

		if (BugzillaFixture.current().getBugzillaVersion().isSmaller(BugzillaVersion.BUGZILLA_3_6)) {
			return;
		} else if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)) {
			return;
		} else {
			CustomTransitionManager ctm = new CustomTransitionManager();
			ctm.parse(new NullProgressMonitor(), bugzillaClient);

			ArrayList<String> transitions = new ArrayList<String>();
			Map<String, ArrayList<String>> expectTransitions;

			/*
			 * Copy and paste this block to test valid transitions for different start statuses
			 * 
			 * We check that only valid operations are returned. There is no
			 * way to determine (using the operation 'reopen') whether "REOPEN" or "UNCONFIRMED"
			 * is valid.
			 */
			if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.CUSTOM_WF)) {
				expectTransitions = fixtureTransitionsMap.get(BugzillaFixture.CUSTOM_WF);
			} else if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.CUSTOM_WF_AND_STATUS)) {
				expectTransitions = fixtureTransitionsMap.get(BugzillaFixture.CUSTOM_WF_AND_STATUS);
			} else if (BugzillaFixture.current().getBugzillaVersion().isSmaller(BugzillaVersion.BUGZILLA_4_0)) {
				expectTransitions = fixtureTransitionsMap.get(BUGZILLA_LE_4_0);
			} else {
				expectTransitions = fixtureTransitionsMap.get(BUGZILLA_GE_4_0);
			}
			for (String start : expectTransitions.keySet()) {
				transitions.clear();
				ArrayList<String> expectedStateTransition = expectTransitions.get(start);
				for (AbstractBugzillaOperation s : ctm.getValidTransitions(start)) {
					String end = s.toString();
					if (expectedStateTransition.contains(end)) {
						transitions.add(end);
					} else {
						fail("The status " + start + " is not expected to transition to " + end.toString());
					}
				}
				assertEquals("Missing transitions for " + start + ", only found " + transitions, transitions.size(),
						ctm.getValidTransitions(start).size());
			}
		}
	}

	public void testXmlRpcBugGet() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)
				|| BugzillaFixture.current() == BugzillaFixture.BUGS_3_4) {
			return;
		} else {
			Set<String> taskIds = new HashSet<String>();
			String taskId = taskExists();
			if (taskId == null) {
				taskId = createTask();
			}

			taskIds.add(taskId);
			final Map<String, TaskData> results = new HashMap<String, TaskData>();
			final Map<String, TaskData> resultsXMLRPC = new HashMap<String, TaskData>();
			TaskDataCollector collector = new TaskDataCollector() {
				@Override
				public void accept(TaskData taskData) {
					results.put(taskData.getTaskId(), taskData);
				}
			};
			TaskDataCollector collectorXMLRPC = new TaskDataCollector() {
				@Override
				public void accept(TaskData taskData) {
					resultsXMLRPC.put(taskData.getTaskId(), taskData);
				}
			};

			final CoreException[] collectionException = new CoreException[1];
			final Boolean[] updateConfig = new Boolean[1];

			class CollectorWrapper extends TaskDataCollector {

				private final IProgressMonitor monitor2;

				private final TaskDataCollector collector;

				@Override
				public void failed(String taskId, IStatus status) {
					collector.failed(taskId, status);
				}

				public CollectorWrapper(TaskDataCollector collector, IProgressMonitor monitor2) {
					this.collector = collector;
					this.monitor2 = monitor2;
				}

				@Override
				public void accept(TaskData taskData) {
					try {
						AbstractTaskDataHandler taskDataHandler = connector.getTaskDataHandler();
						taskDataHandler.initializeTaskData(repository, taskData, null, new SubProgressMonitor(monitor2,
								1));
					} catch (CoreException e) {
						// this info CoreException is only used internal
						if (e.getStatus().getCode() == IStatus.INFO && e.getMessage().contains("Update Config")) { //$NON-NLS-1$
							if (updateConfig[0] == null) {
								updateConfig[0] = new Boolean(true);
							}
						} else if (collectionException[0] == null) {
							collectionException[0] = e;
						}
					}
					collector.accept(taskData);
					monitor2.worked(1);
				}
			}

			TaskDataCollector collector2 = new CollectorWrapper(collector, new NullProgressMonitor());
			TaskDataCollector collector3 = new CollectorWrapper(collectorXMLRPC, new NullProgressMonitor());
			client.getTaskData(taskIds, collector2, new BugzillaAttributeMapper(repository, connector),
					new NullProgressMonitor());
			if (collectionException[0] != null) {
				throw collectionException[0];
			}
			bugzillaClient.getTaskData(taskIds, collector3, new BugzillaAttributeMapper(repository, connector),
					new NullProgressMonitor());

			if (collectionException[0] != null) {
				throw collectionException[0];
			}
			assertEquals(results.size(), resultsXMLRPC.size());
			@SuppressWarnings("unused")
			String div = "";
			for (String taskID : results.keySet()) {
				TaskData taskDataHTML = results.get(taskID);
				TaskData taskDataXMLRPC = resultsXMLRPC.get(taskID);
				assertNotNull(taskDataHTML);
				assertNotNull(taskDataXMLRPC);
				Map<String, TaskAttribute> attributesHTML = taskDataHTML.getRoot().getAttributes();
				Map<String, TaskAttribute> attributesXMLRPC = taskDataXMLRPC.getRoot().getAttributes();
				div += compareAttributes(attributesHTML, attributesXMLRPC, "", "Root-" + taskID + ": "); //$NON-NLS-1$
//				TaskAttribute aa0 = taskDataHTML.getRoot().getAttribute("estimated_time");
//				TaskAttribute aa1 = taskDataXMLRPC.getRoot().getAttribute("estimated_time");
//				TaskAttribute ab0 = taskDataHTML.getRoot().getAttribute("remaining_time");
//				TaskAttribute ab1 = taskDataXMLRPC.getRoot().getAttribute("remaining_time");
//				TaskAttribute ac0 = taskDataHTML.getRoot().getAttribute("actual_time");
//				TaskAttribute ac1 = taskDataXMLRPC.getRoot().getAttribute("actual_time");
//				TaskAttribute ad0 = taskDataHTML.getRoot().getAttribute("deadline");
//				TaskAttribute ad1 = taskDataXMLRPC.getRoot().getAttribute("deadline");
//				@SuppressWarnings("unused")
//				int i = 9;
//				i++;
			}
			@SuppressWarnings("unused")
			// set breakpoint to see what is the div between HTML and XMLRPC
			int i = 9;
			i++;
		}
	}

	private String compareAttributes(Map<String, TaskAttribute> attributesHTML,
			Map<String, TaskAttribute> attributesXMLRPC, String div, String prefix) {
		for (String attributeNameHTML : attributesHTML.keySet()) {
			TaskAttribute attributeHTML = attributesHTML.get(attributeNameHTML);
			TaskAttribute attributeXMLRPC = attributesXMLRPC.get(attributeNameHTML);
			if (attributeXMLRPC == null) {
				div += (prefix + attributeNameHTML + " not in XMLRPC\n");
				continue;
			}
			if (attributeHTML.getValues().size() > 1) {
				List<String> i1 = attributeHTML.getValues();
				List<String> i2 = attributeXMLRPC.getValues();
				if (i1.size() != i2.size()) {
					div += (prefix + attributeNameHTML + " has size " + i1.size() + " but got " + i2.size() + "\n");
				}
				for (String string : i1) {
					if (!i2.contains(string)) {
						div += (prefix + attributeNameHTML + " did not have " + string + "\n");
					}
				}
			}
			if (attributeHTML.getValue().compareTo(attributeXMLRPC.getValue()) != 0) {
				div += (prefix + attributeNameHTML + " value not equal HTML = \'" + attributeHTML.getValue()
						+ "\' XMLRPC = \'" + attributeXMLRPC.getValue() + "\'\n");
			}
			TaskAttributeMetaData metaHTML = attributeHTML.getMetaData();
			TaskAttributeMetaData metaXMLRPC = attributeXMLRPC.getMetaData();
			if (metaHTML != null && metaXMLRPC == null) {
				div += (prefix + attributeNameHTML + " MetaData not in XMLRPC\n");
			}

			if (metaHTML != null && metaXMLRPC != null) {
				String a0 = metaHTML.getKind();
				String a1 = metaXMLRPC.getKind();

				if (metaHTML.getKind() != null && metaXMLRPC.getKind() == null) {
					div += (prefix + attributeNameHTML + " MetaData Kind not in XMLRPC\n");
				} else if (metaHTML.getKind() != null && metaHTML.getKind().compareTo(metaXMLRPC.getKind()) != 0) {
					div += (prefix + attributeNameHTML + " Meta Kind not equal HTML = \'" + metaHTML.getKind()
							+ "\' XMLRPC = \'" + metaXMLRPC.getKind() + "\'\n");
				}
				if (metaHTML.getType() != null && metaXMLRPC.getType() == null) {
					div += (prefix + attributeNameHTML + " MetaData Type not in XMLRPC\n");
				} else if (metaHTML.getType() != null && metaHTML.getType().compareTo(metaXMLRPC.getType()) != 0) {
					div += (prefix + attributeNameHTML + " Meta Type not equal HTML = \'" + metaHTML.getType()
							+ "\' XMLRPC = \'" + metaXMLRPC.getType() + "\'\n");
				}
				if (metaHTML.getLabel() != null && metaXMLRPC.getLabel() == null) {
					div += (prefix + attributeNameHTML + " MetaData Label not in XMLRPC\n");
				} else if (metaHTML.getLabel() != null && metaHTML.getLabel().compareTo(metaXMLRPC.getLabel()) != 0) {
					div += (prefix + attributeNameHTML + " Meta Label not equal HTML = \'" + metaHTML.getLabel()
							+ "\' XMLRPC = \'" + metaXMLRPC.getLabel() + "\'\n");
				}
			}
			Map<String, TaskAttribute> subAttribHTML = attributeHTML.getAttributes();
			if (!subAttribHTML.isEmpty()) {
				Map<String, TaskAttribute> subAttribXMLRPC = attributeXMLRPC.getAttributes();
				div = compareAttributes(subAttribHTML, subAttribXMLRPC, div, prefix + attributeNameHTML + ": ");
			}
		}
		return div;
	}

	public void testUpdateProductInfo() throws Exception {
		if (BugzillaFixture.current().getDescription().equals(BugzillaFixture.XML_RPC_DISABLED)
				|| BugzillaFixture.current() == BugzillaFixture.BUGS_3_4) {
			return;
		}
		RepositoryConfiguration repositoryConfiguration = connector.getRepositoryConfiguration(repository.getRepositoryUrl());

		for (String product : repositoryConfiguration.getProducts()) {
			repositoryConfiguration.setDefaultMilestone(product, null);
		}

		bugzillaClient.updateProductInfo(new NullProgressMonitor(), repositoryConfiguration);
		for (String product : repositoryConfiguration.getProducts()) {
			if (product.equals("ManualTest") || product.equals("Product with Spaces") || product.equals("TestProduct")) {
				assertEquals("---", repositoryConfiguration.getDefaultMilestones(product));
			} else {
				fail("never reach this");
			}
		}
	}
}
