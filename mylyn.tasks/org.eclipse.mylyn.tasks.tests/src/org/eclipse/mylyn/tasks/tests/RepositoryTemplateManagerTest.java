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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class RepositoryTemplateManagerTest {

	private RepositoryTemplateManager manager;

	@BeforeEach
	protected void setUp() throws Exception {
		manager = TasksUiPlugin.getRepositoryTemplateManager();
	}

	@Test
	public void testTemplateDeletion() {
		RepositoryTemplate template = new RepositoryTemplate("Mock", MockRepositoryConnector.REPOSITORY_URL, "", "", "",
				"", "", "", false, true);
		try {
			manager.addTemplate(MockRepositoryConnector.CONNECTOR_KIND, template);
			assertFalse(TaskRepositoryUtil.isAddAutomaticallyDisabled(MockRepositoryConnector.REPOSITORY_URL));
			TaskRepositoryUtil.disableAddAutomatically(MockRepositoryConnector.REPOSITORY_URL);
			assertTrue(TaskRepositoryUtil.isAddAutomaticallyDisabled(MockRepositoryConnector.REPOSITORY_URL));
		} finally {
			manager.removeTemplate(MockRepositoryConnector.CONNECTOR_KIND, template);
		}
	}

	@Test
	public void testStripSlashes() {
		RepositoryTemplate template = new RepositoryTemplate("Mock", MockRepositoryConnector.REPOSITORY_URL + "///", "",
				"", "", "", "", "", false, true);
		assertEquals(MockRepositoryConnector.REPOSITORY_URL, template.repositoryUrl);
	}

}
