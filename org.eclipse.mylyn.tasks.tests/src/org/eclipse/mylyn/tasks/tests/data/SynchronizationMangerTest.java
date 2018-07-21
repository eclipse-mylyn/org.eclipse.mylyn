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

package org.eclipse.mylyn.tasks.tests.data;

import java.util.Collections;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.core.data.SynchronizationManger;
import org.eclipse.mylyn.internal.tasks.core.data.TaskAttributeDiff;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataDiff;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import junit.framework.TestCase;

public class SynchronizationMangerTest extends TestCase {

	private SynchronizationManger manager;

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
		manager = new SynchronizationManger(model);
	}

	@Test
	public void testHasChangedAttributes() {
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertFalse(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesWithoutKind() {
		TaskAttribute attributeCustom = newData.getRoot().createAttribute("custom");
		attributeCustom.setValue("1");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertFalse(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesWithKind() {
		TaskAttribute attributeCustom = newData.getRoot().createAttribute("custom");
		attributeCustom.setValue("1");
		attributeCustom.getMetaData().setKind("kind");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesWithChangedValues() {
		TaskAttribute attributeOld = oldData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attributeOld.setValue("1");
		TaskAttribute attributeNew = newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attributeNew.setValue("2");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesWithChangedCustomValues() {
		TaskAttribute attributeOld = oldData.getRoot().createAttribute("custom");
		attributeOld.setValue("1");
		attributeOld.getMetaData().setKind("kind");
		TaskAttribute attributeNew = newData.getRoot().createAttribute("custom");
		attributeNew.setValue("2");
		attributeNew.getMetaData().setKind("kind");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesWithUnchangedCustomValues() {
		TaskAttribute attributeOld = oldData.getRoot().createAttribute("custom");
		attributeOld.setValue("1");
		attributeOld.getMetaData().setKind("kind");
		TaskAttribute attributeNew = newData.getRoot().createAttribute("custom");
		attributeNew.setValue("1");
		attributeNew.getMetaData().setKind("kind");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertFalse(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesMultiple() {
		TaskAttribute attributeCustom = newData.getRoot().createAttribute("custom");
		attributeCustom.setValue("1");
		TaskAttribute attributeSummary = newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attributeSummary.setValue("1");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesWithComments() {
		TaskAttribute attributeComment = newData.getRoot().createAttribute("custom");
		attributeComment.setValue("1");
		attributeComment.getMetaData().setType(TaskAttribute.TYPE_COMMENT);
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testGetChangedAttributesWithComments() {
		TaskAttribute attributeComment = newData.getRoot().createAttribute("custom");
		attributeComment.setValue("1");
		attributeComment.getMetaData().setType(TaskAttribute.TYPE_COMMENT);
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertEquals(1, diff.getNewComments().size());
		assertEquals(attributeComment, diff.getNewComments().iterator().next().getTaskAttribute());
	}

	@Test
	public void testHasChangedAttributesWithAttachments() {
		TaskAttribute attributeAttachment = newData.getRoot().createAttribute("custom");
		attributeAttachment.setValue("1");
		attributeAttachment.getMetaData().setType(TaskAttribute.TYPE_ATTACHMENT);
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testGetChangedAttributesWithAttachments() {
		TaskAttribute attributeAttachment = newData.getRoot().createAttribute("custom");
		attributeAttachment.setValue("1");
		attributeAttachment.getMetaData().setType(TaskAttribute.TYPE_ATTACHMENT);
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertEquals(Collections.singleton(attributeAttachment), diff.getNewAttachments());
	}

	@Test
	public void testGetChangedAttributesMultipleKind() {
		TaskAttribute attributeCustom = newData.getRoot().createAttribute("custom");
		attributeCustom.setValue("1");
		attributeCustom.getMetaData().setKind("kind");
		TaskAttribute attributeSummary = newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attributeSummary.setValue("1");
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertEquals(ImmutableSet.of(new TaskAttributeDiff(null, attributeSummary),
				new TaskAttributeDiff(null, attributeCustom)), diff.getChangedAttributes());
		assertTrue(diff.hasChanged());
	}

	@Test
	public void testHasChangedAttributesSummaryEmptyValue() {
		newData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		TaskDataDiff diff = manager.createDiff(newData, oldData, new NullProgressMonitor());
		assertFalse(diff.hasChanged());
	}

}
