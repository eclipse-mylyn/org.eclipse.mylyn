/*******************************************************************************
 * Copyright (c) 2012, 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.TaskAttributeDiff;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataDiff;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskAttributeDiff;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class TaskDataDiffTest extends TestCase {

	private RepositoryModel model;

	private TaskRepository repository;

	private TaskAttributeMapper mapper;

	private TaskData newData;

	private TaskData oldData;

	@Override
	protected void setUp() throws Exception {
		ITask task = new TaskTask("kind", "url", "1");
		repository = new TaskRepository(task.getConnectorKind(), task.getRepositoryUrl());
		mapper = new TaskAttributeMapper(repository);
		IRepositoryManager repositoryManager = new TaskRepositoryManager();
		repositoryManager.addRepository(repository);
		TaskList taskList = new TaskList();
		taskList.addTask(task);
		model = new RepositoryModel(taskList, repositoryManager);

		newData = new TaskData(mapper, repository.getConnectorKind(), repository.getUrl(), "1");
		oldData = new TaskData(mapper, repository.getConnectorKind(), repository.getUrl(), "1");
	}

	@Test
	public void testGetChangedAttributes() {
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		assertEquals(Collections.emptySet(), diff.getChangedAttributes());
	}

	@Test
	public void testGetChangedAttributesMultiple() {
		TaskAttribute attributeCustom = newData.getRoot().createAttribute("custom");
		attributeCustom.setValue("1");
		TaskAttribute attributeSummary = newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attributeSummary.setValue("1");
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		assertEquals(Collections.singleton(new TaskAttributeDiff(null, attributeSummary)), diff.getChangedAttributes());
	}

	@Test
	public void testGetChangedAttributesMultipleKind() {
		TaskAttribute attributeCustom = newData.getRoot().createAttribute("custom");
		attributeCustom.setValue("1");
		attributeCustom.getMetaData().setKind("kind");
		TaskAttribute attributeSummary = newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attributeSummary.setValue("1");
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		List<TaskAttributeDiff> expected = Arrays.asList(new TaskAttributeDiff(null, attributeSummary),
				new TaskAttributeDiff(null, attributeCustom));
		assertEquals(new LinkedHashSet<ITaskAttributeDiff>(expected), diff.getChangedAttributes());
	}

	@Test
	public void testGetChangedAttributesSummary() {
		TaskAttribute attribute = newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attribute.setValue("text");
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		assertEquals(Collections.singleton(new TaskAttributeDiff(null, attribute)), diff.getChangedAttributes());
	}

	@Test
	public void testGetChangedAttributesSummaryEmptyValue() {
		newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		assertEquals(Collections.emptySet(), diff.getChangedAttributes());
	}

	@Test
	public void testHasChanges() {
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		assertFalse(diff.hasChanged());
	}

	@Test
	public void testHasChangesAttribute() {
		newData.getRoot().createAttribute(TaskAttribute.SUMMARY).setValue("text");
		TaskDataDiff diff = new TaskDataDiff(model, newData, oldData);
		assertTrue(diff.hasChanged());
	}
}
