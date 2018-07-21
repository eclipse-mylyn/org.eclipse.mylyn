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
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

/**
 * @author Steffen Pingel
 */
public class RepositoryTemplateManagerTest extends TestCase {

	private RepositoryTemplateManager manager;

	@Override
	protected void setUp() throws Exception {
		manager = TasksUiPlugin.getRepositoryTemplateManager();
	}

	public void testTemplateDeletion() {
		RepositoryTemplate template = new RepositoryTemplate("Mock", MockRepositoryConnector.REPOSITORY_URL, "", "",
				"", "", "", "", false, true);
		try {
			manager.addTemplate(MockRepositoryConnector.CONNECTOR_KIND, template);
			assertFalse(TaskRepositoryUtil.isAddAutomaticallyDisabled(MockRepositoryConnector.REPOSITORY_URL));
			TaskRepositoryUtil.disableAddAutomatically(MockRepositoryConnector.REPOSITORY_URL);
			assertTrue(TaskRepositoryUtil.isAddAutomaticallyDisabled(MockRepositoryConnector.REPOSITORY_URL));
		} finally {
			manager.removeTemplate(MockRepositoryConnector.CONNECTOR_KIND, template);
		}
	}

	public void testStripSlashes() {
		RepositoryTemplate template = new RepositoryTemplate("Mock", MockRepositoryConnector.REPOSITORY_URL + "///",
				"", "", "", "", "", "", false, true);
		assertEquals(MockRepositoryConnector.REPOSITORY_URL, template.repositoryUrl);
	}

}
