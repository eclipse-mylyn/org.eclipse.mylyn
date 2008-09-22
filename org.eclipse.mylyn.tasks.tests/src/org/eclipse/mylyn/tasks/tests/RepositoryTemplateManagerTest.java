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

import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

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
			manager.addTemplate(MockRepositoryConnector.REPOSITORY_KIND, template);
			assertFalse(TaskRepositoryUtil.isAddAutomaticallyDisabled(MockRepositoryConnector.REPOSITORY_URL));
			TaskRepositoryUtil.disableAddAutomatically(MockRepositoryConnector.REPOSITORY_URL);
			assertTrue(TaskRepositoryUtil.isAddAutomaticallyDisabled(MockRepositoryConnector.REPOSITORY_URL));
		} finally {
			manager.removeTemplate(MockRepositoryConnector.REPOSITORY_KIND, template);
		}
	}

}
