/*******************************************************************************
 * Copyright (c) 2004, 2025 Tasktop Technologies and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.util.SetSystemProperty;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class LinkProviderTest {

	public class LinkProviderStub extends AbstractTaskRepositoryLinkProvider {

		int executions = 0;

		int timeout = 0;

		@Override
		public TaskRepository getTaskRepository(IResource resource, IRepositoryManager repositoryManager) {
			executions++;
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException e) {
				fail();
			}
			return null;
		}
	}

	private LinkProviderStub provider;

	@BeforeEach
	protected void setUp() throws Exception {
		provider = new LinkProviderStub();
		TasksUiPlugin.getDefault().addRepositoryLinkProvider(provider);
	}

	@AfterEach
	protected void tearDown() throws Exception {
		TasksUiPlugin.getDefault().removeRepositoryLinkProvider(provider);
	}

	@Test
	@SetSystemProperty(key = ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, value = "500")
	public void testTimeout() {
		provider.timeout = 10;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(1, provider.executions);

		// should cause provider to get removed
		provider.timeout = 2000;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(2, provider.executions);

		// provider should no longer get called
		provider.timeout = 10;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(2, provider.executions);
	}

	@Test
	@DisabledOnOs(OS.MAC)
	@SetSystemProperty(key = ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, value = "-1")
	public void testTimeoutInfinite() {
		provider.timeout = 0;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(1, provider.executions);

		provider.timeout = 60;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(2, provider.executions);

		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(3, provider.executions);
	}

}
