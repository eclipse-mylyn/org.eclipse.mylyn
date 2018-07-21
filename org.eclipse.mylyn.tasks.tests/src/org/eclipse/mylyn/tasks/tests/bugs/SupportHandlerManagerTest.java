/*******************************************************************************
 * Copyright (c) 2012, 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.bugs;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.tasks.bugs.AttributeTaskMapper;
import org.eclipse.mylyn.internal.tasks.bugs.SupportHandlerManager;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProduct;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProviderManager;
import org.eclipse.mylyn.internal.tasks.bugs.SupportRequest;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ProductStatus;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorWithTaskDataHandler;

/**
 * @author Frank Becker
 */
public class SupportHandlerManagerTest extends TestCase {

	private TaskRepository mockRepository;

	private MockRepositoryConnectorWithTaskDataHandler mockRepositoryConnector;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.setValue(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED, false);

		TaskTestUtil.resetTaskListAndRepositories();
		mockRepositoryConnector = new MockRepositoryConnectorWithTaskDataHandler();
		TasksUiPlugin.getRepositoryManager().addRepositoryConnector(mockRepositoryConnector);
		mockRepository = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(mockRepository);
	}

	@Override
	public void tearDown() throws Exception {
	}

	public void testSupportHandlerManager() throws CoreException {
		SupportHandlerManager handlerManager = new SupportHandlerManager();
		SupportProviderManager providerManager = new SupportProviderManager();
		SupportProduct product = providerManager.getProduct("org.eclipse.mylyn.tasks.tests.productnormal");
		IStatus status = new ProductStatus(product);
		SupportRequest request = new SupportRequest(providerManager, status, product);
		handlerManager.preProcess(request);
		assertTrue(((AttributeTaskMapper) request.getDefaultContribution()).isMappingComplete());
		IProgressMonitor monitor = new NullOperationMonitor();
		handlerManager.process(request.getDefaultContribution(), monitor);
		assertNotNull(request.getDefaultContribution());
		AttributeTaskMapper mapper = ((AttributeTaskMapper) request.getDefaultContribution());
		assertTrue(!mapper.isHandled());
		TaskData taskData = null;
		taskData = mapper.createTaskData(monitor);
		assertNotNull(taskData);
		mapper.setTaskData(taskData);
		handlerManager.postProcess(mapper, monitor);
		TaskAttribute serv = taskData.getRoot().getMappedAttribute(TaskAttribute.SEVERITY);
		assertEquals("enhancement", serv.getValue());
	}

	public void testSeverityDefinedInExtensionPoint() throws CoreException {
		SupportHandlerManager handlerManager = new SupportHandlerManager();
		SupportProviderManager providerManager = new SupportProviderManager();
		SupportProduct product = providerManager.getProduct("org.eclipse.mylyn.tasks.tests.productseverity");
		IStatus status = new ProductStatus(product);
		SupportRequest request = new SupportRequest(providerManager, status, product);
		handlerManager.preProcess(request);
		assertTrue(((AttributeTaskMapper) request.getDefaultContribution()).isMappingComplete());
		IProgressMonitor monitor = new NullOperationMonitor();
		handlerManager.process(request.getDefaultContribution(), monitor);
		assertNotNull(request.getDefaultContribution());
		AttributeTaskMapper mapper = ((AttributeTaskMapper) request.getDefaultContribution());
		assertTrue(!mapper.isHandled());
		TaskData taskData = null;
		taskData = mapper.createTaskData(monitor);
		assertNotNull(taskData);
		mapper.setTaskData(taskData);
		handlerManager.postProcess(mapper, monitor);
		TaskAttribute serv = taskData.getRoot().getAttribute(TaskAttribute.SEVERITY);
		assertEquals("blocker", serv.getValue());
	}

}
