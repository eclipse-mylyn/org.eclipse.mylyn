/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import com.google.common.collect.ImmutableMap;

public class SaxTaskBuilderTest {

	private SaxTaskBuilder builder;

	@Before
	public void setup() {
		TaskList taskList = new TaskList();
		IRepositoryManager repositoryManager = mock(IRepositoryManager.class);
		doReturn(mock(AbstractRepositoryConnector.class)).when(repositoryManager)
				.getRepositoryConnector("connector.kind");
		doReturn(mock(LocalRepositoryConnector.class)).when(repositoryManager).getRepositoryConnector("local");
		builder = new SaxTaskBuilder(new RepositoryModel(taskList, repositoryManager), repositoryManager);
	}

	@Test
	public void minimalTask() throws Exception {
		Attributes elementAttributes = createAttributes(ImmutableMap.of(TaskListExternalizationConstants.KEY_HANDLE,
				"1", TaskListExternalizationConstants.KEY_TASK_ID, "100",
				TaskListExternalizationConstants.KEY_REPOSITORY_URL, "http://example.com",
				TaskListExternalizationConstants.KEY_CONNECTOR_KIND, "connector.kind"));

		builder.beginItem(elementAttributes);

		assertTrue(builder.getErrors().isOK());
		AbstractTask task = builder.getItem();

		assertNotNull(task);
		assertTrue(task instanceof TaskTask);
		assertEquals("http://example.com-100", task.getHandleIdentifier());
		assertEquals("100", task.getTaskId());
		assertEquals("http://example.com", task.getRepositoryUrl());
		assertEquals("", task.getSummary());
		assertEquals(PriorityLevel.P3.toString(), task.getPriority());
		assertNull(task.getTaskKey());
	}

	@Test
	public void simpleTask() throws Exception {
		ImmutableMap.Builder<String, String> attributesBuilder = ImmutableMap.builder();
		attributesBuilder.put(TaskListExternalizationConstants.KEY_HANDLE, "1");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_TASK_ID, "100");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_REPOSITORY_URL, "http://example.com");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_LABEL, "Simple Task");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_KEY, "EX-100");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_PRIORITY, PriorityLevel.P1.toString());
		attributesBuilder.put(TaskListExternalizationConstants.KEY_CONNECTOR_KIND, "connector.kind");

		builder.beginItem(createAttributes(attributesBuilder.build()));

		assertTrue(builder.getErrors().isOK());
		AbstractTask task = builder.getItem();

		assertNotNull(task);
		assertTrue(task instanceof TaskTask);
		assertEquals("http://example.com-100", task.getHandleIdentifier());
		assertEquals("100", task.getTaskId());
		assertEquals("http://example.com", task.getRepositoryUrl());
		assertEquals("Simple Task", task.getSummary());
		assertEquals("EX-100", task.getTaskKey());
		assertEquals(PriorityLevel.P1.toString(), task.getPriority());
	}

	@Test
	public void taskWithAttributes() throws Exception {
		Attributes elementAttributes = createAttributes(ImmutableMap.of(TaskListExternalizationConstants.KEY_HANDLE,
				"1", TaskListExternalizationConstants.KEY_TASK_ID, "100",
				TaskListExternalizationConstants.KEY_REPOSITORY_URL, "http://example.com",
				TaskListExternalizationConstants.KEY_CONNECTOR_KIND, "connector.kind"));

		builder.beginItem(elementAttributes);
		putAttribute("att1", "value1");
		putAttribute("att2", "value2");

		assertTrue(builder.getErrors().isOK());
		AbstractTask task = builder.getItem();

		assertNotNull(task);
		assertTrue(task instanceof TaskTask);
		assertEquals(2, task.getAttributes().size());
		assertEquals("value1", task.getAttribute("att1"));
		assertEquals("value2", task.getAttribute("att2"));
	}

	@Test
	public void invalidConnectorKind() throws Exception {
		Attributes elementAttributes = createAttributes(ImmutableMap.of(TaskListExternalizationConstants.KEY_HANDLE,
				"1", TaskListExternalizationConstants.KEY_TASK_ID, "100",
				TaskListExternalizationConstants.KEY_REPOSITORY_URL, "http://example.com",
				TaskListExternalizationConstants.KEY_CONNECTOR_KIND, "invalid"));

		builder.beginItem(elementAttributes);
		assertFalse(builder.getErrors().isOK());
	}

	@Test
	public void localTask() throws Exception {
		ImmutableMap.Builder<String, String> attributesBuilder = ImmutableMap.builder();
		attributesBuilder.put(TaskListExternalizationConstants.KEY_HANDLE, "1");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_TASK_ID, "100");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_REPOSITORY_URL, "local");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_LABEL, "Simple Task");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_KEY, "EX-100");
		attributesBuilder.put(TaskListExternalizationConstants.KEY_PRIORITY, PriorityLevel.P1.toString());
		attributesBuilder.put(TaskListExternalizationConstants.KEY_CONNECTOR_KIND, "local");

		builder.beginItem(createAttributes(attributesBuilder.build()));

		assertTrue(builder.getErrors().isOK());
		AbstractTask task = builder.getItem();

		assertNotNull(task);
		assertTrue(task instanceof LocalTask);
		assertEquals("local-100", task.getHandleIdentifier());
		assertEquals("100", task.getTaskId());
		assertEquals("local", task.getRepositoryUrl());
		assertEquals("Simple Task", task.getSummary());
		assertEquals(PriorityLevel.P1.toString(), task.getPriority());
		assertNull(task.getTaskKey());
	}

	private void putAttribute(String key, String value) {
		builder.startAttribute(createAttributes(ImmutableMap.of(TaskListExternalizationConstants.KEY_KEY, key)));
		builder.acceptAttributeValueContent(value.toCharArray(), 0, value.length());
		builder.endAttribute();
	}

	private AttributesImpl createAttributes(Map<String, String> attributes) {
		AttributesImpl xmlAttributes = new AttributesImpl();
		for (String key : attributes.keySet()) {
			xmlAttributes.addAttribute("", key, key, "", attributes.get(key));
		}
		return xmlAttributes;
	}

}
