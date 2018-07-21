/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.lang.RandomStringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataState;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataStore;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.data.TaskOperation;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskDataStoreTest extends TestCase {

	private static final String MOCK_ID = "1";

	private TaskDataStore storage;

	private TaskRepository taskRepository;

	private File file;

	private TaskData data;

	private TaskDataState state;

	@Override
	protected void setUp() throws Exception {
		TaskRepositoryManager taskRepositoryManager = new TaskRepositoryManager();
		storage = new TaskDataStore(taskRepositoryManager);
		taskRepository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		file = File.createTempFile("mylyn", null);
		file.deleteOnExit();

		taskRepositoryManager.addRepositoryConnector(new MockRepositoryConnector());
		taskRepositoryManager.addRepository(taskRepository);
	}

	@Override
	protected void tearDown() throws Exception {
		file.delete();
	}

	public void testPutAndGet() throws Exception {
		TaskDataState state = new TaskDataState(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		TaskData data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		TaskMapper mapper = new TaskMapper(data, true);
		mapper.getTaskData().getRoot().createAttribute("attributeKey1").setValue("attValue!");
		mapper.setDescription("description");
		mapper.setSummary("summary");
		mapper.setTaskKind("task kind");

		TaskData oldData = new TaskData(new TaskAttributeMapper(taskRepository),
				MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		state.setRepositoryData(data);
		state.setLastReadData(oldData);
		state.setEditsData(null);

		storage.putTaskData(file, state);

		TaskDataState retrieved = storage.getTaskDataState(file);
		assertNotNull(retrieved);
		TaskData newTaskData = retrieved.getRepositoryData();
		assertNotNull(newTaskData);
		assertEquals(MockRepositoryConnector.CONNECTOR_KIND, newTaskData.getConnectorKind());
		mapper = new TaskMapper(newTaskData);
		assertEquals("description", mapper.getDescription());
		assertEquals("summary", mapper.getSummary());
		assertEquals("task kind", mapper.getTaskKind());
	}

	public void testDelete() throws Exception {
		TaskData data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		TaskDataState state = new TaskDataState(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		state.setRepositoryData(data);
		storage.putTaskData(file, state);
		storage.deleteTaskData(file);
		assertFalse(file.exists());
		assertNull(storage.getTaskDataState(file));
	}

	private void setupData() {
		data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		state = new TaskDataState(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL,
				MOCK_ID);
		state.setRepositoryData(data);
	}

	public void testAttributes() throws Exception {
		setupData();

		TaskAttribute attribute = data.getRoot().createAttribute("testId");
		attribute.getMetaData().setLabel("testName").setReadOnly(true);
		attribute.putOption("Option Name 1", "Option Value 1");
		attribute.putOption("Option Name 2", "Option Value 2");
		attribute.addValue("Value 1");
		attribute.addValue("Value 2");
		attribute.addValue("Value 3");
		attribute.getMetaData().putValue("MetaKey1", "MetaValue1");
		attribute.getMetaData().putValue("MetaKey2", "MetaValue2");
		attribute.getMetaData().putValue("MetaKey3", "MetaValue3");
		attribute.getMetaData().putValue("MetaKey4", "MetaValue4");

		assertData(1);
	}

	private void assertData(int attributeCount) throws CoreException {
		storage.putTaskData(file, state);
		state = storage.getTaskDataState(file);

		assertNotNull(state);
		assertNotNull(state.getRepositoryData());
		assertEquals(data.getRoot().toString(), state.getRepositoryData().getRoot().toString());
		assertEquals(attributeCount, state.getRepositoryData().getRoot().getAttributes().size());
	}

	public void testOperations() throws Exception {
		setupData();

		TaskOperation.applyTo(data.getRoot().createAttribute("op1"), "op1 id", "op1 label");
		TaskOperation.applyTo(data.getRoot().createAttribute("op2"), "op2 id", "op2 label");

		assertData(2);
	}

	public void testComments() throws Exception {
		setupData();

		TaskCommentMapper comment1 = new TaskCommentMapper();
		comment1.setCommentId("attachmentId1");
		TaskAttribute attr = data.getRoot().createAttribute("1");
		comment1.applyTo(attr);
		attr.createAttribute("attr1").setValue("attr1Name");

		TaskCommentMapper comment2 = new TaskCommentMapper();
		comment2.setCommentId("attachmentId2");
		comment2.setCreationDate(new Date());
		comment2.setNumber(100);
		comment2.applyTo(data.getRoot().createAttribute("2"));

		assertData(2);
	}

	public void testAttachments() throws Exception {
		setupData();

		TaskAttachmentMapper attachment1 = new TaskAttachmentMapper();
		attachment1.setAuthor(taskRepository.createPerson("thecreator"));
		attachment1.setDeprecated(false);
		attachment1.setPatch(true);
		TaskAttribute attr = data.getRoot().createAttribute("1");
		attachment1.applyTo(attr);
		attr.createAttribute("attr1").setValue("attr1Name");
		attr.createAttribute("attr2").setValue("attr1Name");

		TaskAttachmentMapper attachment2 = new TaskAttachmentMapper();
		attachment2.setAuthor(taskRepository.createPerson("thecreator2"));
		attachment2.setDeprecated(true);
		attachment2.setPatch(false);
		attachment2.applyTo(data.getRoot().createAttribute("2"));

		assertData(2);
	}

	public void testReadWriteInvalidCharacters() throws Exception {
		setupData();
		data.getRoot().createAttribute("attribute").setValue("\u0000");

		storage.putTaskData(file, state);

		try {
			TaskDataState state2 = storage.getTaskDataState(file);
			fail("Expected CoreException, got " + state2);
		} catch (CoreException expected) {
		}
	}

	public void testReadWriteC0Characters() throws Exception {
		setupData();
		data.getRoot().createAttribute("attribute").setValue("\u0001\u001F");

		storage.putTaskData(file, state);

		TaskDataState state2 = storage.getTaskDataState(file);
		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());
	}

	public void testReadWriteC1Characters() throws Exception {
		setupData();
		data.getRoot().createAttribute("attribute").setValue("\u007F\u0080");

		storage.putTaskData(file, state);

		TaskDataState state2 = storage.getTaskDataState(file);
		assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData().getRoot().toString());
	}

	public void testReadWriteC0C1Characters() throws Exception {
		setupData();
		data.getRoot().createAttribute("attribute").setValue("\u0001\u001F\u007F\u0080");

		storage.putTaskData(file, state);

		if (System.getProperty("java.version").compareTo("1.6") < 0) {
			// Java 1.5 fails to parse C1 characters with XML 1.1
			try {
				TaskDataState state2 = storage.getTaskDataState(file);
				fail("Expected CoreException, got '" + state2.getRepositoryData().getRoot().toString() + "'");
			} catch (CoreException expected) {
			}
		} else {
			// Java 1.6 is apparently able to parse C1 characters with XML 1.1
			TaskDataState state2 = storage.getTaskDataState(file);
			assertEquals(state.getRepositoryData().getRoot().toString(), state2.getRepositoryData()
					.getRoot()
					.toString());
		}
	}

	public void testReadWriteSetValue() throws Exception {
		setupData();
		TaskAttribute attribute = data.getRoot().createAttribute("attribute");
		assertFalse(attribute.hasValue());
		attribute.setValue("foo");
		assertTrue(attribute.hasValue());

		File file = File.createTempFile("mylyn", null);
		file.deleteOnExit();
		storage.putTaskData(file, state);

		TaskDataState state2 = storage.getTaskDataState(file);
		assertTrue(state2.getRepositoryData().getRoot().getAttribute("attribute").hasValue());
	}

	public void testReadWriteUnsetValue() throws Exception {
		setupData();
		TaskAttribute attribute = data.getRoot().createAttribute("attribute");
		assertFalse(attribute.hasValue());
		attribute.setValue("foo");
		assertTrue(attribute.hasValue());
		attribute.clearValues();
		assertFalse(attribute.hasValue());

		File file = File.createTempFile("mylyn", null);
		file.deleteOnExit();
		storage.putTaskData(file, state);

		TaskDataState state2 = storage.getTaskDataState(file);
		assertFalse(state2.getRepositoryData().getRoot().getAttribute("attribute").hasValue());
	}

	public void testCorruptedData() throws Exception {
		if (!hasXerces()) {
			System.err.println("Skipping testCorruptedData() due to Xerces missing");
			return;
		}
		state = storage.getTaskDataState(CommonTestUtil.getFile(this, "testdata/taskdata-bug406647.zip"));
		assertFalse(state.getRepositoryData().getRoot().toString().contains("<ke"));
		assertFalse(state.getRepositoryData().getRoot().toString().contains("<va"));
		assertFalse(state.getRepositoryData().getRoot().toString().contains("ey>"));
		assertFalse(state.getRepositoryData().getRoot().toString().contains("al>"));
	}

	private boolean hasXerces() {
		try {
			Class.forName("org.apache.xerces.parsers.SAXParser");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public void testRandomDataXml_1_0() throws Exception {
		randomData(32, Integer.MAX_VALUE);
	}

	public void testRandomDataXml_1_1() throws Exception {
		if (!hasXerces()) {
			System.err.println("Skipping testRandomDataXml_1_1 due to Xerces missing");
			return;
		}
		randomData(0, Integer.MAX_VALUE);
	}

	private void randomData(int start, int end) throws Exception {
		setupData();

		for (int i = 0; i < 1000; i++) {
			TaskAttribute attribute = data.getRoot().createAttribute("testId");
			attribute.getMetaData().setLabel(generateString(start, end));
			attribute.putOption(generateString(start, end), generateString(start, end));
			attribute.putOption(generateString(start, end), generateString(start, end));
			attribute.addValue(generateString(start, end));
			attribute.addValue(generateString(start, end));
			if (start == 0) {
				// ensure that XML is read as version 1.1
				attribute.addValue(generateString(start, end) + "\u0001");
			}
		}

		String expectedValue = data.getRoot().toString();
		storage.putTaskData(file, state);

		state = storage.getTaskDataState(file);
		String actualValue = state.getRepositoryData().getRoot().toString();
		assertEquals(expectedValue, actualValue);
	}

	/**
	 * Returns a random string that doesn't contain "key" or "val".
	 */
	private String generateString(int start, int end) {
		return RandomStringUtils.random(1000, start, end, true, true);
	}

}
