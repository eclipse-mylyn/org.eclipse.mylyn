/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class AbstractTaskDataHandlerTest extends TestCase {

	private StubTaskDataHandler handler;

	private StubAttributeFactory factory;

	private RepositoryTaskData source;

	private RepositoryTaskData target;

	@Override
	protected void setUp() throws Exception {
		handler = new StubTaskDataHandler();

		factory = new StubAttributeFactory();
		source = new RepositoryTaskData(factory, "kind", "http://url", "1");
		target = new RepositoryTaskData(factory, "kind", "http://url", "2");
	}

	public void testCloneTaskCloneCommonAttributes() {
		source.setDescription("sourceDescription");
		handler.cloneTaskData(source, target);
		assertEquals("sourceDescription", target.getDescription());
		assertEquals("", target.getSummary());

		source.setSummary("sourceSummary");
		handler.cloneTaskData(source, target);
		assertEquals("sourceSummary", target.getSummary());
	}

	public void testCloneTaskDataAttributeWithValues() {
		source.addAttributeValue("key1", "value1");
		handler.cloneTaskData(source, target);
		assertEquals(null, target.getAttribute("key1"));

		target.setAttributeValue("key1", "value2");
		handler.cloneTaskData(source, target);
		assertEquals("value1", target.getAttributeValue("key1"));

		target.addAttributeValue("multi", "v1");
		target.addAttributeValue("multi", "v2");
		handler.cloneTaskData(source, target);
		assertEquals("value1", target.getAttributeValue("key1"));
		List<String> values = target.getAttributeValues("multi");
		assertEquals(2, values.size());
		assertEquals("v1", values.get(0));
		assertEquals("v2", values.get(1));
	}

	public void testCloneTaskDataAttributeWithOptions() {
		source.addAttributeValue("key", "o2");
		RepositoryTaskAttribute sourceAttribute = source.getAttribute("key");
		sourceAttribute.addOption("o1", "");
		sourceAttribute.addOption("o2", "");
		target.setAttributeValue("key", "");
		handler.cloneTaskData(source, target);
		assertEquals("o2", target.getAttributeValue("key"));

		// test target with options that don't contain
		RepositoryTaskAttribute targetAttribute = target.getAttribute("key");
		targetAttribute.addOption("o3", "");
		handler.cloneTaskData(source, target);
		assertEquals("", target.getAttributeValue("key"));

		// test target with options that contain value
		targetAttribute.addOption("o2", "");
		handler.cloneTaskData(source, target);
		assertEquals("o2", target.getAttributeValue("key"));

		// test multiple values
		sourceAttribute.addValue("o3");
		handler.cloneTaskData(source, target);
		List<String> values = targetAttribute.getValues();
		assertEquals(2, values.size());
		assertEquals("o2", values.get(0));
		assertEquals("o3", values.get(1));
	}

	public void testCloneTaskDifferentRepositoryTypesCloneCommonAttributes() {
		StubAttributeFactory targetFactory = new StubAttributeFactory();
		target = new RepositoryTaskData(targetFactory, "otherkind", "http://url", "2");

		source.setDescription("sourceDescription");
		handler.cloneTaskData(source, target);
		assertEquals("sourceDescription", target.getDescription());
		assertEquals("", target.getSummary());

		source.setSummary("sourceSummary");
		handler.cloneTaskData(source, target);
		assertEquals("sourceSummary", target.getSummary());
	}

	public void testCloneTaskDifferentRepositoryTypesCloneMappedAttribues() {
		StubAttributeFactory targetFactory = new StubAttributeFactory();
		target = new RepositoryTaskData(targetFactory, "otherkind", "http://url", "2");

		// key is not part of common schema
		source.setAttributeValue("key", "source");
		target.setAttributeValue("key", "target");
		handler.cloneTaskData(source, target);
		assertEquals("target", target.getAttributeValue("key"));

		// map key in source factory only
		factory.attributeMap.put(RepositoryTaskAttribute.PRIORITY, "key");
		handler.cloneTaskData(source, target);
		assertEquals("target", target.getAttributeValue("key"));

		// map key in target factory to different key
		targetFactory.attributeMap.put(RepositoryTaskAttribute.PRODUCT, "key");
		handler.cloneTaskData(source, target);
		assertEquals("target", target.getAttributeValue("key"));

		// map key in both factories
		targetFactory.attributeMap.put(RepositoryTaskAttribute.PRIORITY, "key");
		handler.cloneTaskData(source, target);
		assertEquals("source", target.getAttributeValue("key"));

	}

	private class StubAttributeFactory extends AbstractAttributeFactory {

		private static final long serialVersionUID = 1L;

		private final Map<String, String> attributeMap = new HashMap<String, String>();

		@Override
		public Date getDateForAttributeType(String attributeKey, String dateString) {
			// ignore
			return null;
		}

		@Override
		public String getName(String key) {
			// ignore
			return null;
		}

		@Override
		public boolean isHidden(String key) {
			// ignore
			return false;
		}

		@Override
		public boolean isReadOnly(String key) {
			// ignore
			return false;
		}

		@Override
		public String mapCommonAttributeKey(String key) {
			String mappedKey = attributeMap.get(key);
			return (mappedKey != null) ? mappedKey : key;
		}

	}

	private class StubTaskDataHandler extends AbstractTaskDataHandler {

		@Override
		public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind) {
			// ignore
			return null;
		}

		@Override
		public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
			// ignore
			return null;
		}

		@Override
		public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
				throws CoreException {
			// ignore
			return null;
		}

		@Override
		public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor)
				throws CoreException {
			// ignore
			return false;
		}

		@Override
		public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
				throws CoreException {
			// ignore
			return null;
		}

	}

}
