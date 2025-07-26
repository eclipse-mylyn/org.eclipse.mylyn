/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class LinkProviderTest extends TestCase {

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

	private String defaultTimeout;

	private LinkProviderStub provider;

	@Override
	protected void setUp() throws Exception {
		defaultTimeout = System.getProperty(ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, "");
		provider = new LinkProviderStub();
		TasksUiPlugin.getDefault().addRepositoryLinkProvider(provider);
	}

	@Override
	protected void tearDown() throws Exception {
		TasksUiPlugin.getDefault().removeRepositoryLinkProvider(provider);
		System.setProperty(ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, defaultTimeout);
	}

	public void testTimeout() {
		System.setProperty(ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, "500");

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

	public void testTimeoutInfinite() {
		if (Platform.ARCH_X86_64.equals(Platform.getOSArch()) && Platform.OS_MACOSX.equals(Platform.getOS())) {
			System.err.println("Skipping LinkProviderTest.testTimeoutInfinite() on Intel Macs");
			return;
		}
		System.setProperty(ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, "50");

		provider.timeout = 40;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(1, provider.executions);

		System.setProperty(ITasksCoreConstants.PROPERTY_LINK_PROVIDER_TIMEOUT, "-1");

		provider.timeout = 0;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(2, provider.executions);

		provider.timeout = 60;
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(3, provider.executions);

		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(4, provider.executions);
	}

}
