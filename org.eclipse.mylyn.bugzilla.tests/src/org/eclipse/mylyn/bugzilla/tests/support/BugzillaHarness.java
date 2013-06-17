/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.data.FileTaskAttachmentSource;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

public class BugzillaHarness {

	private final BugzillaFixture fixture;

	private TaskRepository repository;

	private BugzillaClient priviledgedClient;

	public BugzillaRepositoryConnector connector() {
		return fixture.connector();
	}

	public TaskRepository repository() {
		if (repository == null) {
			repository = fixture.singleRepository();
		}
		return repository;
	}

	public ITask getTask(String taskId) throws Exception {
		TaskRepository repository = repository();
		TaskData taskData = fixture.connector().getTaskData(repository, taskId, null);
		ITask task = TasksUi.getRepositoryModel().createTask(repository, taskData.getTaskId());
		TasksUiPlugin.getTaskDataManager().putUpdatedTaskData(task, taskData, true);
		return task;
	}

	private BugzillaClient priviledgedClient() throws Exception {
		if (priviledgedClient == null) {
			priviledgedClient = fixture.client();
		}
		return priviledgedClient;
	}

	TaskData createTaskData(ITaskMapping initializationData, ITaskMapping selectionData, IProgressMonitor monitor)
			throws CoreException {
		AbstractTaskDataHandler taskDataHandler = connector().getTaskDataHandler();
		TaskAttributeMapper mapper = taskDataHandler.getAttributeMapper(repository());
		TaskData taskData = new TaskData(mapper, repository().getConnectorKind(), repository().getRepositoryUrl(), ""); //$NON-NLS-1$
		boolean result = taskDataHandler.initializeTaskData(repository(), taskData, initializationData, monitor);
		if (!result) {
			throw new CoreException(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Initialization of task failed. The provided data is insufficient.")); //$NON-NLS-1$
		}
		if (selectionData != null) {
			connector().getTaskMapping(taskData).merge(selectionData);
		}
		return taskData;
	}

	private RepositoryResponse createNewTask(TaskData taskData) throws IOException, CoreException, Exception {
		return fixture.submitTask(taskData, priviledgedClient());
	}

	public BugzillaHarness(BugzillaFixture fixture) {
		this.fixture = fixture;
	}

	public String taskXmlRpcExists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20XMLRPC%20getBugData&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=TestComponent&product=TestProduct";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createXmlRpcTask() throws Exception {
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

		// create Task
		TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		RepositoryResponse response = createNewTask(taskDataNew);

		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		ITask task = getTask(taskId);
		assertNotNull(task);
		TaskData taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
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
			priviledgedClient().postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment,
					attrAttachment, new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		taskData = fixture.getTask(taskData.getTaskId(), priviledgedClient());
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
		priviledgedClient().postUpdateAttachment(attachmentAttribute, "update", null);

		task = getTask(taskId);
		assertNotNull(task);
		taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
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
		String cf_bugidValue = taskCfBugIdExists();
		if (cf_bugidValue == null) {
			cf_bugidValue = createCfBugIdTask();
		}
		cf_bugid.setValue(cf_bugidValue);
		response = fixture.submitTask(taskData, priviledgedClient());

		return taskId;
	}

	public String taskAttachmentAttributesExists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20Bug%20with%20Attachment&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=ManualC2&product=ManualTest";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createAttachmentAttributesTask() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {

			@Override
			public String getProduct() {
				return "ManualTest";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getComponent() {
				return "ManualC2";
			}

			@Override
			public String getSummary() {
				return "test Bug with Attachment";
			}

			@Override
			public String getDescription() {
				return "The Description of the test with Attachment Bug";
			}
		};

		// create Task
		TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		RepositoryResponse response = createNewTask(taskDataNew);

		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		ITask task = getTask(taskId);
		assertNotNull(task);
		TaskData taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
		assertNotNull(taskData);
		TaskAttribute attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		TaskAttachmentMapper attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		String fileName = "test-attach-1.txt";
		File attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		BufferedWriter write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file from " + System.currentTimeMillis());
		write.close();

		FileTaskAttachmentSource attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("My Attachment 1");
		attachment.setName("My Attachment 1");
		try {
			priviledgedClient().postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment,
					attrAttachment, new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		taskData = fixture.getTask(taskData.getTaskId(), priviledgedClient());
		assertNotNull(taskData);

		task = getTask(taskId);
		assertNotNull(task);
		taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
		assertNotNull(taskData);
		attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		fileName = "test-attach-2.txt";
		attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file 2 from " + System.currentTimeMillis());
		write.close();

		attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("My Attachment 2");
		attachment.setName("My Attachment 2");
		try {
			priviledgedClient().postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment,
					attrAttachment, new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		taskData = fixture.getTask(taskData.getTaskId(), priviledgedClient());
		assertNotNull(taskData);

		task = getTask(taskId);
		assertNotNull(task);
		taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
		assertNotNull(taskData);
		attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		fileName = "test-attach-3.txt";
		attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file 3 from " + System.currentTimeMillis());
		write.close();

		attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("My Attachment 3");
		attachment.setName("My Attachment 3");
		TaskAttribute child = attrAttachment.createMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
		if (child != null) {
			child.setValue("1");
		}
		try {
			priviledgedClient().postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment,
					attrAttachment, new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		taskData = fixture.getTask(taskData.getTaskId(), priviledgedClient());
		assertNotNull(taskData);

		task = getTask(taskId);
		assertNotNull(task);
		taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
		assertNotNull(taskData);
		attrAttachment = taskData.getAttributeMapper().createTaskAttachment(taskData);
		attachmentMapper = TaskAttachmentMapper.createFrom(attrAttachment);

		fileName = "test-attach-4.txt";
		attachFile = new File(fileName);
		attachFile.createNewFile();
		attachFile.deleteOnExit();
		write = new BufferedWriter(new FileWriter(attachFile));
		write.write("test file 4 from " + System.currentTimeMillis());
		write.close();

		attachment = new FileTaskAttachmentSource(attachFile);
		attachment.setContentType("text/plain");
		attachment.setDescription("My Attachment 4");
		attachment.setName("My Attachment 4");
		child = attrAttachment.createMappedAttribute(TaskAttribute.ATTACHMENT_IS_PATCH);
		if (child != null) {
			child.setValue("1");
		}
		try {
			priviledgedClient().postAttachment(taskData.getTaskId(), attachmentMapper.getComment(), attachment,
					attrAttachment, new NullProgressMonitor());
		} catch (Exception e) {
			fail("never reach this!");
		}
		taskData = fixture.getTask(taskData.getTaskId(), priviledgedClient());
		assertNotNull(taskData);

		TaskAttribute attachment1 = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(1);
		assertNotNull(attachment1);
		TaskAttribute obsolete = attachment1.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		obsolete.setValue("1"); //$NON-NLS-1$
		((BugzillaTaskDataHandler) connector().getTaskDataHandler()).postUpdateAttachment(repository(), attachment1,
				"update", new NullProgressMonitor()); //$NON-NLS-1$

		task = getTask(taskId);
		assertNotNull(task);
		taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
		assertNotNull(taskData);

		TaskAttribute attachment2 = taskData.getAttributeMapper()
				.getAttributesByType(taskData, TaskAttribute.TYPE_ATTACHMENT)
				.get(3);
		assertNotNull(attachment);
		obsolete = attachment2.getMappedAttribute(TaskAttribute.ATTACHMENT_IS_DEPRECATED);
		assertNotNull(obsolete);
		obsolete.setValue("1"); //$NON-NLS-1$
		((BugzillaTaskDataHandler) connector().getTaskDataHandler()).postUpdateAttachment(repository(), attachment2,
				"update", new NullProgressMonitor()); //$NON-NLS-1$

		return taskId;
	}

	public String taskCustomFieldExists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20Bug%20with%20Custom%20Fields&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=ManualC2&product=ManualTest";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createCustomFieldTask() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {

			@Override
			public String getProduct() {
				return "ManualTest";
			}
		};
		final TaskMapping taskMappingSelect = new TaskMapping() {
			@Override
			public String getComponent() {
				return "ManualC2";
			}

			@Override
			public String getSummary() {
				return "test Bug with Custom Fields";
			}

			@Override
			public String getDescription() {
				return "The Description of the test with Custom Fields Bug";
			}
		};

		// create Task
		TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		RepositoryResponse response = createNewTask(taskDataNew);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		String taskId = response.getTaskId();

		ITask task = getTask(taskId);
		assertNotNull(task);
		TaskData taskData = fixture.connector().getTaskData(repository(), task.getTaskId(), null);
		assertNotNull(taskData);

		TaskMapper mapper = new TaskMapper(taskData);
		TaskAttribute cf_multiselect = mapper.getTaskData().getRoot().getAttribute("cf_multiselect");
		cf_multiselect.setValue("Green");
		response = fixture.submitTask(taskData, priviledgedClient());

		return taskId;
	}

	public ArrayList<String> taskMissingHitsExists() {
		ArrayList<String> taskIDs = new ArrayList<String>();

		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20Missing%20Hits&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=ManualC2&product=ManualTest";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			for (Integer integer : sks) {
				taskIDs.add("" + integer);
			}
		}
		return taskIDs;
	}

	public void createMissingHitsTask() throws Exception {
		final TaskMapping taskMappingInit = new TaskMapping() {

			@Override
			public String getProduct() {
				return "ManualTest";
			}
		};
		final TaskMapping taskMappingSelect1 = new TaskMapping() {
			@Override
			public String getComponent() {
				return "ManualC2";
			}

			@Override
			public String getSummary() {
				return "test Missing Hits 1";
			}

			@Override
			public String getDescription() {
				return "The Description of the test Missing Hits 1";
			}
		};
		final TaskMapping taskMappingSelect2 = new TaskMapping() {
			@Override
			public String getComponent() {
				return "ManualC2";
			}

			@Override
			public String getSummary() {
				return "test Missing Hits 2";
			}

			@Override
			public String getDescription() {
				return "The Description of the test Missing Hits 2";
			}
		};
		final TaskMapping taskMappingSelect3 = new TaskMapping() {
			@Override
			public String getComponent() {
				return "ManualC2";
			}

			@Override
			public String getSummary() {
				return "test Missing Hits 3";
			}

			@Override
			public String getDescription() {
				return "The Description of the test Missing Hits 3";
			}
		};
		createTask(taskMappingInit, taskMappingSelect1);
		createTask(taskMappingInit, taskMappingSelect2);
		createTask(taskMappingInit, taskMappingSelect3);
	}

	private String createTask(TaskMapping taskMappingInit, TaskMapping taskMappingSelect) throws Exception {

		// create Task
		TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		RepositoryResponse response = createNewTask(taskDataNew);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		return response.getTaskId();
	}

	public String taskAliasExists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20Alias%20Bug&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=TestComponent&product=TestProduct";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createAliasTask() throws Exception {
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
				return "test Alias Bug";
			}

			@Override
			public String getDescription() {
				return "The Description of the Alias Bug";
			}
		};
		final TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		TaskAttribute alias = taskDataNew.getRoot().getAttribute("alias");
		if (alias == null) {
			alias = taskDataNew.getRoot().createAttribute("alias");
		}
		alias.setValue("Fritz");
		RepositoryResponse response = createNewTask(taskDataNew);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		return response.getTaskId();
	}

	public String taskAlias2Exists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20Alias%20Bug2&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=TestComponent&product=TestProduct";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createAliasTask2() throws Exception {
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
				return "test Alias Bug2";
			}

			@Override
			public String getDescription() {
				return "The Description of the Alias Bug";
			}
		};

		// create Task
		TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		RepositoryResponse response = createNewTask(taskDataNew);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		return response.getTaskId();
	}

	public String taskCfBugIdExists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20CF%20Bug%20ID%20Bug&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=TestComponent&product=TestProduct";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createCfBugIdTask() throws Exception {
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
				return "test CF Bug ID Bug";
			}

			@Override
			public String getDescription() {
				return "The Description of the CF Bug ID Bug";
			}
		};
		final TaskData taskDataNew = createTaskData(taskMappingInit, taskMappingSelect, null);
		RepositoryResponse response = createNewTask(taskDataNew);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		return response.getTaskId();
	}

	public String enhanceSearchTaskExists() {
		String taskID = null;
		String queryUrlString = repository().getRepositoryUrl() + "/buglist.cgi?"
				+ "short_desc=test%20EnhanceSearch&resolution=---&query_format=advanced"
				+ "&short_desc_type=casesubstring&component=TestComponent&product=TestProduct";
		RepositoryQuery query = new RepositoryQuery(repository().getConnectorKind(), "handle-testQueryViaConnector");
		query.setUrl(queryUrlString);
		final Map<Integer, TaskData> changedTaskData = new HashMap<Integer, TaskData>();
		TaskDataCollector collector = new TaskDataCollector() {
			@Override
			public void accept(TaskData taskData) {
				changedTaskData.put(Integer.valueOf(taskData.getTaskId()), taskData);
			}
		};
		connector().performQuery(repository(), query, collector, null, new NullProgressMonitor());
		if (changedTaskData.size() > 0) {
			Set<Integer> ks = changedTaskData.keySet();
			SortedSet<Integer> sks = new TreeSet<Integer>(ks);
			taskID = sks.last().toString();
		}
		return taskID;
	}

	public String createEnhanceSearchTask() throws Exception {
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
				return "test EnhanceSearch";
			}

			@Override
			public String getDescription() {
				return "The Description of the Bug 335278";
			}
		};
		final TaskData[] taskDataNew = new TaskData[1];

		// create Task
		taskDataNew[0] = TasksUiInternal.createTaskData(repository(), taskMappingInit, taskMappingSelect, null);
		ITask taskNew = TasksUiUtil.createOutgoingNewTask(taskDataNew[0].getConnectorKind(),
				taskDataNew[0].getRepositoryUrl());

		ITaskDataWorkingCopy workingCopy = TasksUi.getTaskDataManager().createWorkingCopy(taskNew, taskDataNew[0]);
		Set<TaskAttribute> changed = new HashSet<TaskAttribute>();
		workingCopy.save(changed, null);
		RepositoryResponse response = BugzillaFixture.current().submitTask(taskDataNew[0], priviledgedClient());
		((AbstractTask) taskNew).setSubmitting(true);
		assertNotNull(response);
		assertEquals(ResponseKind.TASK_CREATED.toString(), response.getReposonseKind().toString());
		return response.getTaskId();
	}

}
