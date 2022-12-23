/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.core;

import static java.util.concurrent.TimeUnit.HOURS;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Benjamin Muskalla
 */
public class TaskAttributeMetaDataTest extends TestCase {

	private TaskData data;

	@Override
	protected void setUp() throws Exception {
		TaskRepository taskRepository = new TaskRepository("kind", "url");
		data = new TaskData(new TaskAttributeMapper(taskRepository), MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL, "taskid");
	}

	public void testInitialRequiredAttribute() {
		TaskAttribute attribute = new TaskAttribute(data.getRoot(), "attributeId");
		boolean required = attribute.getMetaData().isRequired();
		assertFalse(required);
	}

	public void testLifecycleRequiredAttribute() {
		TaskAttribute attribute = new TaskAttribute(data.getRoot(), "attributeId");
		attribute.getMetaData().setRequired(true);
		assertTrue(attribute.getMetaData().isRequired());
		attribute.getMetaData().setRequired(false);
		assertFalse(attribute.getMetaData().isRequired());
	}

	public void testPrecision() throws Exception {
		TaskAttribute attribute = new TaskAttribute(data.getRoot(), "attributeId");
		TaskAttributeMetaData metaData = attribute.getMetaData();

		for (TimeUnit unit : TimeUnit.values()) {
			metaData.setPrecision(unit);
			assertEquals(unit, metaData.getPrecision());
		}

		metaData.setPrecision(null);
		assertNull(metaData.getPrecision());

		metaData.putValue(TaskAttribute.META_ATTRIBUTE_PRECISION, "blah");
		assertNull(metaData.getPrecision());

		metaData.putValue(TaskAttribute.META_ATTRIBUTE_PRECISION, HOURS.name());
		assertEquals(HOURS, metaData.getPrecision());
	}

}
