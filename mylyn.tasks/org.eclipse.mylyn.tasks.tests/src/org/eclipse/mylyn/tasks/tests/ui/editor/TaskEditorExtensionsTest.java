/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui.editor;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorExtensions;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;

/**
 * @author Leo Dos Santos
 */
public class TaskEditorExtensionsTest extends TestCase {

	private static final String ID_TEXTILE_EXTENSION = "org.eclipse.mylyn.tasks.tests.editor.mock.textile";

	private TaskRepository repository;

	private TaskData taskData;

	@Override
	protected void setUp() throws Exception {
		repository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);
		taskData = new TaskData(new TaskAttributeMapper(repository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "taskId");
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getRepositoryManager().removeRepository(repository);
	}

	public void testMarkupAssociationFromRepository() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, ID_TEXTILE_EXTENSION);
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockTextileEditorExtension);
	}

	public void testMarkupAssociationFromAttribute() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, "none");
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setMediaType("text/plain; markup=MockWiki");
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockWikiEditorExtension);
	}

	public void testMarkupAssociationFromBoth() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, ID_TEXTILE_EXTENSION);
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setMediaType("text/plain; markup=MockWiki");
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockWikiEditorExtension);
	}

	public void testMarkupAssociationComplexMediaType() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, ID_TEXTILE_EXTENSION);
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setMediaType("text/plain; markup=MockWiki; charset=iso-8859-1");
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockWikiEditorExtension);
	}

	public void testMarkupAssociationNotMarkupMediaType() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, ID_TEXTILE_EXTENSION);
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setMediaType("text/plain; notreallyamarkup=MockWiki");
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockTextileEditorExtension);
	}

	public void testMarkupAssociationNoAssociation() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, "none");
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertNull(extension);
	}

	public void testBaseMarkupAssociation() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, ID_TEXTILE_EXTENSION);
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setMediaType("text/plain; markup=SpecialMockWiki; base-markup=MockWiki");
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockWikiEditorExtension);
	}

	public void testBaseMarkupAssociationNoMarkup() {
		TaskEditorExtensions.setTaskEditorExtensionId(repository, ID_TEXTILE_EXTENSION);
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setMediaType("text/plain; base-markup=MockWiki");
		AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(repository, attribute);
		assertTrue(extension instanceof MockTextileEditorExtension);
	}

}
