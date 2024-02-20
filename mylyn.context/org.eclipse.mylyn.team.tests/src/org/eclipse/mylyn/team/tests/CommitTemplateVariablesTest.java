/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.mylyn.internal.team.ui.templates.CommitTemplateVariables.TaskURL;
import org.eclipse.mylyn.tasks.tests.util.MockRepositoryConnectorTestCase;

@SuppressWarnings("nls")
public class CommitTemplateVariablesTest extends MockRepositoryConnectorTestCase {

	public void testTaskUrl() {
		assertEquals("http://mock-repo.com/tickets/123", new TaskURL().getValue(taskWithUrl));
		assertEquals("http://mock-repo-evolved.com/tickets/123", new TaskURL().getValue(taskWithBrowserUrl));
	}

}
