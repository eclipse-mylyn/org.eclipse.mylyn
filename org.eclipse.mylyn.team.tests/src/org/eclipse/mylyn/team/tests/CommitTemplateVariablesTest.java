/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import org.eclipse.mylyn.internal.team.ui.templates.CommitTemplateVariables.TaskURL;
import org.eclipse.mylyn.tasks.tests.util.MockRepositoryConnectorTestCase;

public class CommitTemplateVariablesTest extends MockRepositoryConnectorTestCase {

	public void testTaskUrl() {
		assertEquals("http://mock-repo.com/tickets/123", new TaskURL().getValue(taskWithUrl));
		assertEquals("http://mock-repo-evolved.com/tickets/123", new TaskURL().getValue(taskWithBrowserUrl));
	}

}
