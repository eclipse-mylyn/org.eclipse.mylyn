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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskDataState;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryOperation;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.OfflineFileStorage;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.tests.connector.MockAttributeFactory;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.ui.XMLMemento;

/**
 * @author Rob Elves
 */
public class OfflineStorageTest extends TestCase {

	File dataDir;

	OfflineFileStorage storage;

	@Override
	protected void setUp() throws Exception {
		//dataDir = new File("c:/offline");
		dataDir = new File(TasksUiPlugin.getDefault().getDataDirectory() + "/offline");
		storage = new OfflineFileStorage(dataDir);
		storage.start();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		storage.stop();
		removeTestFiles(dataDir);
	}

	private void removeTestFiles(File folder) {
		if (folder.isDirectory()) {
			for (File file : folder.listFiles()) {
				removeTestFiles(file);
			}
		}
		folder.delete();
	}

	public void testPutAndGet() throws Exception {

		TaskDataState state = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, "1");

		RepositoryTaskData newData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1", "kind");

		newData.setAttributeValue("attributeKey1", "attValue!");
		newData.setDescription("description");
		newData.setNew(true);
		newData.setNewComment("new comment");
		newData.setSummary("summary");
		newData.setTaskKey("task key");

		RepositoryTaskData oldData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1", "kind");

		Set<RepositoryTaskAttribute> edits = new HashSet<RepositoryTaskAttribute>();

		state.setNewTaskData(newData);
		state.setOldTaskData(oldData);
		state.setEdits(edits);

		storage.put(state);

		TaskDataState retrieved = storage.get(MockRepositoryConnector.REPOSITORY_URL, "1");
		assertNotNull(retrieved);
		RepositoryTaskData newTaskData = retrieved.getNewTaskData();
		assertNotNull(newTaskData);
		assertEquals(MockRepositoryConnector.REPOSITORY_KIND, newTaskData.getConnectorKind());
		assertEquals("description", newTaskData.getDescription());
		assertEquals("new comment", newTaskData.getNewComment());
		assertEquals("task key", newTaskData.getTaskKey());
		assertEquals("kind", newTaskData.getTaskKind());
	}

	public void testRemove() throws Exception {
		assertNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, "1"));

		TaskDataState state = new TaskDataState(MockRepositoryConnector.REPOSITORY_URL, "1");

		RepositoryTaskData newData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");

		newData.setAttributeValue("attributeKey1", "attValue!");
		newData.setDescription("description");
		newData.setNew(true);
		newData.setNewComment("new comment");
		newData.setSummary("summary");
		newData.setTaskKey("task key");

		RepositoryTaskData oldData = new RepositoryTaskData(new MockAttributeFactory(),
				MockRepositoryConnector.REPOSITORY_KIND, MockRepositoryConnector.REPOSITORY_URL, "1");

		Set<RepositoryTaskAttribute> edits = new HashSet<RepositoryTaskAttribute>();

		state.setNewTaskData(newData);
		state.setOldTaskData(oldData);
		state.setEdits(edits);

		storage.put(state);
		assertNotNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, "1"));
		storage.remove(MockRepositoryConnector.REPOSITORY_URL, "1");
		assertNull(storage.get(MockRepositoryConnector.REPOSITORY_URL, "1"));

	}

	public void testAttributes() throws Exception {
		List<RepositoryTaskAttribute> attributes = new ArrayList<RepositoryTaskAttribute>();
		RepositoryTaskAttribute attribute = new RepositoryTaskAttribute("testId", "testName", true);
		attribute.setReadOnly(true);
		attribute.addOption("Option Name 1", "Option Value 1");
		attribute.addOption("Option Name 2", "Option Value 2");
		attribute.addValue("Value 1");
		attribute.addValue("Value 2");
		attribute.addValue("Value 3");
		attribute.putMetaDataValue("MetaKey1", "MetaValue1");
		attribute.putMetaDataValue("MetaKey2", "MetaValue2");
		attribute.putMetaDataValue("MetaKey3", "MetaValue3");
		attribute.putMetaDataValue("MetaKey4", "MetaValue4");

		attributes.add(attribute);

		XMLMemento memento = XMLMemento.createWriteRoot("Attributes");
		storage.addAttributes(memento, attributes);
		List<RepositoryTaskAttribute> readAttributes = storage.readAttributes(memento);
		assertNotNull(readAttributes);
		assertEquals(1, readAttributes.size());
		RepositoryTaskAttribute attr = readAttributes.get(0);

		assertEquals("testId", attr.getId());
		assertEquals("testName", attr.getName());
		assertEquals(true, attr.isHidden());
		assertEquals(true, attr.isReadOnly());

		assertNotNull(attr.getOptions());
		assertEquals(2, attr.getOptions().size());

		assertEquals("Option Name 1", attr.getOptions().get(0));
		assertEquals("Option Name 2", attr.getOptions().get(1));
		assertEquals("Option Value 1", attr.getOptionParameter(attr.getOptions().get(0)));
		assertEquals("Option Value 2", attr.getOptionParameter(attr.getOptions().get(1)));

		assertEquals("Value 1", attr.getValues().get(0));
		assertEquals("Value 2", attr.getValues().get(1));
		assertEquals("Value 3", attr.getValues().get(2));

		assertEquals("MetaValue1", attribute.getMetaDataValue("MetaKey1"));
		assertEquals("MetaValue2", attribute.getMetaDataValue("MetaKey2"));
		assertEquals("MetaValue3", attribute.getMetaDataValue("MetaKey3"));
		assertEquals("MetaValue4", attribute.getMetaDataValue("MetaKey4"));
	}

	public void testOperations() throws Exception {
		RepositoryOperation op = new RepositoryOperation("knob1", "operationName1");
		op.setChecked(true);
		op.setInputName("InputName");
		op.setInputValue("InputValue");
		op.setUpOptions("TheOptionName");
		op.addOption("optionName1", "optionValue1");
		op.addOption("optionName2", "optionValue2");
		op.setOptionSelection("optionSelection1");

		RepositoryOperation op2 = new RepositoryOperation("knob2", "operationName2");
		op2.setChecked(true);
		op2.setInputName("InputName2");
		op2.setInputValue("InputValue2");
		op2.setUpOptions("TheOptionName2");
		op2.addOption("optionName1", "optionValue1");
		op2.addOption("optionName2", "optionValue2");
		op2.setOptionSelection("optionSelection2");
		XMLMemento memento = XMLMemento.createWriteRoot("Operations");

		List<RepositoryOperation> operations = new ArrayList<RepositoryOperation>();
		operations.add(op);
		operations.add(op2);
		storage.addOperations(memento, operations);
		List<RepositoryOperation> readOperations = storage.readOperations(memento);
		RepositoryOperation op1 = readOperations.get(0);
		assertEquals("knob1", op1.getKnobName());
		assertEquals("operationName1", op1.getOperationName());
		assertEquals("InputName", op1.getInputName());
		assertEquals("InputValue", op1.getInputValue());

		assertEquals("TheOptionName", op1.getOptionName());
		assertEquals(2, op1.getOptionNames().size());
		assertEquals("optionValue1", op1.getOptionValue(op1.getOptionNames().get(0)));
		assertEquals("optionValue2", op1.getOptionValue(op1.getOptionNames().get(1)));
		assertEquals("optionSelection1", op1.getOptionSelection());

		RepositoryOperation op2ver2 = readOperations.get(1);
		assertEquals("knob2", op2ver2.getKnobName());
		assertEquals("operationName2", op2ver2.getOperationName());
		assertEquals("InputName2", op2ver2.getInputName());
		assertEquals("InputValue2", op2ver2.getInputValue());

		assertEquals("TheOptionName2", op2ver2.getOptionName());
		assertEquals(2, op2ver2.getOptionNames().size());
		assertEquals("optionValue1", op2ver2.getOptionValue(op1.getOptionNames().get(0)));
		assertEquals("optionValue2", op2ver2.getOptionValue(op1.getOptionNames().get(1)));
		assertEquals("optionSelection2", op2ver2.getOptionSelection());
	}

	public void testComments() throws Exception {
		TaskComment comment1 = new TaskComment(new MockAttributeFactory(), 1);
		comment1.setAttachmentId("attachmentId1");
		comment1.setHasAttachment(true);
		comment1.addAttribute("attr1", new RepositoryTaskAttribute("attr1", "attr1Name", false));

		TaskComment comment2 = new TaskComment(new MockAttributeFactory(), 2);
		comment2.setAttachmentId("attachmentId2");
		comment2.setHasAttachment(false);
		comment2.addAttribute("attr2", new RepositoryTaskAttribute("attr2", "attr2Name", false));
		comment2.addAttribute("attr3", new RepositoryTaskAttribute("attr3", "attr3Name", true));

		XMLMemento memento = XMLMemento.createWriteRoot("Comments");
		List<TaskComment> comments = new ArrayList<TaskComment>();
		comments.add(comment1);
		comments.add(comment2);
		storage.addComments(memento, comments);
		List<TaskComment> returnedComments = storage.readComments(memento);
		assertEquals(2, returnedComments.size());

		assertEquals("attachmentId1", returnedComments.get(0).getAttachmentId());
		assertEquals(true, returnedComments.get(0).hasAttachment());
		assertEquals(1, returnedComments.get(0).getAttributes().size());
		assertEquals("attr1", returnedComments.get(0).getAttributes().get(0).getId());
		assertEquals("attr1Name", returnedComments.get(0).getAttributes().get(0).getName());

		assertEquals("attachmentId2", returnedComments.get(1).getAttachmentId());
		assertEquals(false, returnedComments.get(1).hasAttachment());
		assertEquals(2, returnedComments.get(1).getAttributes().size());
		assertEquals("attr2", returnedComments.get(1).getAttributes().get(0).getId());
		assertEquals("attr2Name", returnedComments.get(1).getAttributes().get(0).getName());
		assertEquals("attr3Name", returnedComments.get(1).getAttributes().get(1).getName());
	}

	public void testAttachments() throws Exception {

		List<RepositoryAttachment> attachments = new ArrayList<RepositoryAttachment>();
		RepositoryAttachment attachment1 = new RepositoryAttachment(new MockAttributeFactory());
		attachment1.setCreator("thecreator");
		attachment1.setObsolete(false);
		attachment1.setPatch(true);
		attachment1.setRepositoryKind("bugzilla");
		attachment1.setRepositoryUrl("repUrl");
		attachment1.setTaskId("taskid1");
		attachment1.addAttribute("attr1", new RepositoryTaskAttribute("attr1", "attr1Name", true));
		attachment1.addAttribute("attr2", new RepositoryTaskAttribute("attr2", "attr2Name", true));

		RepositoryAttachment attachment2 = new RepositoryAttachment(new MockAttributeFactory());
		attachment2.setCreator("thecreator2");
		attachment2.setObsolete(true);
		attachment2.setPatch(false);
		attachment2.setRepositoryKind("bugzilla2");
		attachment2.setRepositoryUrl("repUrl2");
		attachment2.setTaskId("taskid2");
		attachment2.addAttribute("attr3", new RepositoryTaskAttribute("attr3", "attr3Name", true));
		attachment2.addAttribute("attr4", new RepositoryTaskAttribute("attr4", "attr4Name", true));

		attachments.add(attachment1);
		attachments.add(attachment2);
		XMLMemento memento = XMLMemento.createWriteRoot("Attachments");
		storage.addAttachments(memento, attachments);
		List<RepositoryAttachment> readAttachments = storage.readAttachments(memento);

		assertEquals(2, readAttachments.size());
		RepositoryAttachment readAttachment = readAttachments.get(0);
		assertEquals(attachment1.getTaskId(), readAttachment.getTaskId());
		assertEquals(attachment1.getCreator(), readAttachment.getCreator());
		assertEquals(attachment1.isObsolete(), readAttachment.isObsolete());
		assertEquals(attachment1.isPatch(), readAttachment.isPatch());
		assertEquals(attachment1.getRepositoryKind(), readAttachment.getRepositoryKind());
		assertEquals(attachment1.getRepositoryUrl(), readAttachment.getRepositoryUrl());
		assertEquals(attachment1.getAttributeValue("attr1"), readAttachment.getAttributeValue("attr1"));
		assertEquals(attachment1.getAttributeValue("attr2"), readAttachment.getAttributeValue("attr2"));

		readAttachment = readAttachments.get(1);
		assertEquals(attachment2.getTaskId(), readAttachment.getTaskId());
		assertEquals(attachment2.getCreator(), readAttachment.getCreator());
		assertEquals(attachment2.isObsolete(), readAttachment.isObsolete());
		assertEquals(attachment2.isPatch(), readAttachment.isPatch());
		assertEquals(attachment2.getRepositoryKind(), readAttachment.getRepositoryKind());
		assertEquals(attachment2.getRepositoryUrl(), readAttachment.getRepositoryUrl());
		assertEquals(attachment2.getAttributeValue("attr3"), readAttachment.getAttributeValue("attr3"));
		assertEquals(attachment2.getAttributeValue("attr4"), readAttachment.getAttributeValue("attr4"));
	}
}
