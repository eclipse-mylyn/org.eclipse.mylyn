/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class TaskMapperTest extends TestCase {

	private StubTaskAttributeMapper mapper;

	private TaskMapper source;

	private TaskMapper target;

	private TaskRepository taskRepository;

	@Override
	protected void setUp() throws Exception {
		taskRepository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		mapper = new StubTaskAttributeMapper(taskRepository);
		source = new TaskMapper(new TaskData(mapper, "kind", "http://url", "1"), true);
		target = new TaskMapper(new TaskData(mapper, "kind", "http://url", "2"), true);
	}

	public void testTaskSchema() {
		source.setReporter("reporter");

		TaskAttribute attribute = source.getTaskData().getRoot().getAttribute(TaskAttribute.USER_REPORTER);
		assertNotNull(attribute);
		assertEquals("reporter", attribute.getValue());
		assertEquals("Reporter", attribute.getMetaData().getLabel());
		assertEquals(TaskAttribute.TYPE_PERSON, attribute.getMetaData().getType());
	}

	public void testTaskAttachmentMapperSchema() {
		TaskAttribute attachmentAttribute = mapper.createTaskAttachment(source.getTaskData());
		TaskAttachmentMapper mapper = new TaskAttachmentMapper();
		mapper.setAuthor(taskRepository.createPerson("reporter"));
		mapper.applyTo(attachmentAttribute);

		TaskAttribute attribute = attachmentAttribute.getAttribute(TaskAttribute.ATTACHMENT_AUTHOR);
		assertNotNull(attribute);
		assertEquals("reporter", attribute.getValue());
		assertEquals("Author", attribute.getMetaData().getLabel());
		assertEquals(TaskAttribute.TYPE_PERSON, attribute.getMetaData().getType());
	}

	public void testTaskCommentMapperSchema() {
		TaskAttribute attachmentAttribute = mapper.createTaskAttachment(source.getTaskData());
		TaskCommentMapper mapper = new TaskCommentMapper();
		mapper.setAuthor(taskRepository.createPerson("reporter"));
		mapper.applyTo(attachmentAttribute);

		TaskAttribute attribute = attachmentAttribute.getAttribute(TaskAttribute.COMMENT_AUTHOR);
		assertNotNull(attribute);
		assertEquals("reporter", attribute.getValue());
		assertEquals("Author", attribute.getMetaData().getLabel());
		assertEquals(TaskAttribute.TYPE_PERSON, attribute.getMetaData().getType());
	}

	public void testCloneTaskCloneCommonAttributes() {
		source.setDescription("sourceDescription");
		target.setDescription("");
		target.merge(source);
		assertEquals("sourceDescription", target.getDescription());
		assertEquals(null, target.getSummary());

		source.setSummary("sourceSummary");
		target.setSummary("");
		target.merge(source);
		assertEquals("sourceSummary", target.getSummary());
	}

	public void testCloneTaskDataAttributeWithValues() {
		source.getTaskData().getRoot().createAttribute("key1").addValue("value1");
		target.merge(source);
		assertEquals(null, target.getTaskData().getRoot().getAttribute("key1"));

		target.getTaskData().getRoot().createAttribute("key1").addValue("value2");
		target.merge(source);
		assertEquals("value1", target.getTaskData().getRoot().getAttribute("key1").getValue());

		TaskAttribute attribute = source.getTaskData().getRoot().createAttribute("multi");
		attribute.addValue("v1");
		attribute.addValue("v2");
		target.getTaskData().getRoot().createAttribute("multi");
		target.merge(source);
		assertEquals("value1", target.getTaskData().getRoot().getAttribute("key1").getValue());
		List<String> values = target.getTaskData().getRoot().getAttribute("multi").getValues();
		assertEquals(2, values.size());
		assertEquals("v1", values.get(0));
		assertEquals("v2", values.get(1));
	}

	public void testCloneTaskDataAttributeWithOptions() {
		TaskAttribute sourceAttribute = source.getTaskData().getRoot().createAttribute("key");
		sourceAttribute.setValue("o2");
		sourceAttribute.putOption("o1", "");
		sourceAttribute.putOption("o2", "");
		target.getTaskData().getRoot().createAttribute("key");
		target.merge(source);
		assertEquals("o2", target.getTaskData().getRoot().getAttribute("key").getValue());

		// test target with options that don't contain value
		TaskAttribute targetAttribute = target.getTaskData().getRoot().getAttribute("key");
		targetAttribute.putOption("o3", "");
		target.merge(source);
		assertEquals("", target.getTaskData().getRoot().getAttribute("key").getValue());

		// test target with options that contain value
		targetAttribute.putOption("o2", "");
		target.merge(source);
		assertEquals("o2", target.getTaskData().getRoot().getAttribute("key").getValue());

		// test multiple values
		sourceAttribute.addValue("o3");
		target.merge(source);
		List<String> values = targetAttribute.getValues();
		assertEquals(2, values.size());
		assertEquals("o2", values.get(0));
		assertEquals("o3", values.get(1));
	}

	public void testCloneTaskDifferentRepositoryTypesCloneCommonAttributes() {
		target = new TaskMapper(new TaskData(mapper, "otherkind", "http://url", "2"), true);

		source.setDescription("sourceDescription");
		target.setDescription("");
		target.merge(source);
		assertEquals("sourceDescription", target.getDescription());
		assertEquals(null, target.getSummary());

		source.setSummary("sourceSummary");
		target.merge(source);
		assertEquals("sourceSummary", target.getSummary());
	}

	public void testCloneTaskDifferentRepositoryTypesCloneMappedAttribues() {
		StubTaskAttributeMapper targetMapper = new StubTaskAttributeMapper(taskRepository);
		target = new TaskMapper(new TaskData(targetMapper, "otherkind", "http://url", "2"), true);

		// key is not part of common schema
		source.getTaskData().getRoot().createAttribute("key").setValue("source");
		target.getTaskData().getRoot().createAttribute("key").setValue("target");
		target.merge(source);
		assertEquals("target", target.getTaskData().getRoot().getAttribute("key").getValue());

		// map key in source factory only
		mapper.attributeMap.put(TaskAttribute.COMPONENT, "key");
		target.merge(source);
		assertEquals("target", target.getTaskData().getRoot().getAttribute("key").getValue());

		// map key in target factory to different key
		targetMapper.attributeMap.put(TaskAttribute.PRODUCT, "key");
		target.merge(source);
		assertEquals("target", target.getTaskData().getRoot().getAttribute("key").getValue());

		// map key in both factories
		targetMapper.attributeMap.put(TaskAttribute.COMPONENT, "key");
		target.merge(source);
		assertEquals("source", target.getTaskData().getRoot().getAttribute("key").getValue());
	}

	public void testNoCreationOfAttributes() {
		target = new TaskMapper(new TaskData(mapper, "otherkind", "http://url", "2"));
		target.setDescription("abc");
		assertNull(target.getTaskData().getRoot().getAttribute(TaskAttribute.DESCRIPTION));
		assertEquals(0, target.getTaskData().getRoot().getAttributes().size());

		target = new TaskMapper(new TaskData(mapper, "otherkind", "http://url", "2"), false);
		target.setDescription("abc");
		assertNull(target.getTaskData().getRoot().getAttribute(TaskAttribute.DESCRIPTION));
		assertEquals(0, target.getTaskData().getRoot().getAttributes().size());
	}

	private static class StubTaskAttributeMapper extends TaskAttributeMapper {

		public StubTaskAttributeMapper(TaskRepository taskRepository) {
			super(taskRepository);
		}

		private final Map<String, String> attributeMap = new HashMap<>();

		@Override
		public String mapToRepositoryKey(TaskAttribute parent, String key) {
			String mappedKey = attributeMap.get(key);
			return mappedKey != null ? mappedKey : key;
		}

	}

	public void testApplyOwnerAndId() throws Exception {
		TaskTask task = new TaskTask("kind", "http://url", "1");
		TaskAttribute assigneeAttribute = source.getTaskData().getRoot().createAttribute(TaskAttribute.USER_ASSIGNED);
		assigneeAttribute.putOption("joel.user", "Joel K. User");
		assigneeAttribute.putOption("jacob.user", "Jacob F. User");

		assigneeAttribute.setValue("joel.user");
		assertOwnerAndId(task, "joel.user", "Joel K. User");

		assigneeAttribute.setValue("jacob.user");
		assertOwnerAndId(task, "jacob.user", "Jacob F. User");

		assigneeAttribute.putOption("jacob.user", "Jacob Frederick User");
		assertOwnerAndId(task, "jacob.user", "Jacob Frederick User");

		assigneeAttribute.clearOptions();
		assertOwnerAndId(task, "jacob.user", "jacob.user");

		assigneeAttribute.setValue("joel.user");
		assertOwnerAndId(task, "joel.user", "joel.user");
	}

	private void assertOwnerAndId(TaskTask task, String ownerId, String owner) {
		assertTrue(source.hasChanges(task));
		assertTrue(source.applyTo(task));
		assertEquals(owner, task.getOwner());
		assertEquals(ownerId, task.getOwnerId());
		assertFalse(source.hasChanges(task));
		assertFalse(source.applyTo(task));
	}

}
