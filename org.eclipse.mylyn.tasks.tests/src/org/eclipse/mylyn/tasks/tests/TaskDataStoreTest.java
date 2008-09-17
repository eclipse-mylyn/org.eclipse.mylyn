/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
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
		taskRepository = new TaskRepository(MockRepositoryConnector.REPOSITORY_KIND,
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
		TaskDataState state = new TaskDataState(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		TaskData data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		TaskMapper mapper = new TaskMapper(data, true);
		mapper.getTaskData().getRoot().createAttribute("attributeKey1").setValue("attValue!");
		mapper.setDescription("description");
		mapper.setSummary("summary");
		mapper.setTaskKind("task kind");

		TaskData oldData = new TaskData(new TaskAttributeMapper(taskRepository),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);

		state.setRepositoryData(data);
		state.setLastReadData(oldData);
		state.setEditsData(null);

		storage.putTaskData(file, state);

		TaskDataState retrieved = storage.getTaskDataState(file);
		assertNotNull(retrieved);
		TaskData newTaskData = retrieved.getRepositoryData();
		assertNotNull(newTaskData);
		assertEquals(MockRepositoryConnector.REPOSITORY_KIND, newTaskData.getConnectorKind());
		mapper = new TaskMapper(newTaskData);
		assertEquals("description", mapper.getDescription());
		assertEquals("summary", mapper.getSummary());
		assertEquals("task kind", mapper.getTaskKind());
	}

	public void testDelete() throws Exception {
		TaskData data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		TaskDataState state = new TaskDataState(MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		state.setRepositoryData(data);
		storage.putTaskData(file, state);
		storage.deleteTaskData(file);
		assertFalse(file.exists());
		assertNull(storage.getTaskDataState(file));
	}

	private void setupData() {
		data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.REPOSITORY_KIND,
				MockRepositoryConnector.REPOSITORY_URL, MOCK_ID);
		state = new TaskDataState(MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL,
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
}
