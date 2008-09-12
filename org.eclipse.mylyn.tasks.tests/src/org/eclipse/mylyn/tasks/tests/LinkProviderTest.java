/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class LinkProviderTest extends TestCase {

	public class LinkProviderStub extends AbstractTaskRepositoryLinkProvider {

		int executions = 0;

		@Override
		public TaskRepository getTaskRepository(IResource resource, IRepositoryManager repositoryManager) {
			executions++;
			try {
				Thread.sleep(5010);
			} catch (InterruptedException e) {
				fail();
			}
			return null;
		}
	}

	public void testTimeout() {
		LinkProviderStub provider = new LinkProviderStub();
		TasksUiPlugin.getDefault().addRepositoryLinkProvider(provider);
		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(1, provider.executions);

		TasksUiPlugin.getDefault().getRepositoryForResource(ResourcesPlugin.getWorkspace().getRoot());
		assertEquals(1, provider.executions);
	}

}
