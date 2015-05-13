/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import static org.eclipse.mylyn.internal.tasks.ui.wizards.Messages.SelectRepositoryConnectorPage_Select_a_task_repository_type;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.tasks.ui.ConnectorBrand;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.junit.Test;

public class NewRepositoryWizardTest {

	@Test
	public void testGetNextPageIWizardPage() {
		NewRepositoryWizard wizard = new NewRepositoryWizard(null);
		wizard.addPages();
		SelectRepositoryConnectorPage page = (SelectRepositoryConnectorPage) wizard.getPage(SelectRepositoryConnectorPage_Select_a_task_repository_type);
		page.setConnectorBrand(new ConnectorBrand(new MockRepositoryConnector(), "org.mylyn"));

		wizard.getNextPage(null);
		assertNull(wizard.getBrand());
		try {
			wizard.getNextPage(page);
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().startsWith("The connector implementation is incomplete"));
		}
		assertEquals("org.mylyn", wizard.getBrand());
	}
}
