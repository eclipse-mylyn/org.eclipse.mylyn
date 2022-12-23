/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import static org.eclipse.mylyn.internal.tasks.ui.wizards.Messages.SelectRepositoryConnectorPage_Select_a_task_repository_type;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.internal.tasks.ui.ConnectorBrand;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositorySettingsPage;
import org.junit.Before;
import org.junit.Test;

public class NewRepositoryWizardTest {

	private NewRepositoryWizard wizard;

	@Before
	public void setUp() {
		wizard = new NewRepositoryWizard();
		wizard.addPages();
	}

	@Test
	public void testGetNextPageIWizardPage() {
		SelectRepositoryConnectorPage page = (SelectRepositoryConnectorPage) wizard
				.getPage(SelectRepositoryConnectorPage_Select_a_task_repository_type);
		page.setConnectorBrand(new ConnectorBrand(new MockRepositoryConnector(), "org.mylyn"));

		wizard.getNextPage(null);
		assertNull(wizard.getBrand());
		wizard.getNextPage(page);
		assertEquals("org.mylyn", wizard.getBrand());
	}

	@Test
	public void testSetsBrandOnSettingsPage() {
		MockRepositorySettingsPage settingsPage = navigateWithBrands("org.mylyn");
		assertEquals("org.mylyn", settingsPage.getBrand());
	}

	public void testSetsBrandOnSettingsPageNoBrand() {
		MockRepositorySettingsPage settingsPage = navigateWithBrands((String) null);
		assertEquals("", settingsPage.getBrand());
	}

	@Test
	public void testSetsBrandOnSettingsPageBrandChangeToNull() {
		MockRepositorySettingsPage settingsPage = navigateWithBrands("org.mylyn", null);
		assertEquals("", settingsPage.getBrand());
	}

	@Test
	public void testSetsBrandOnSettingsPageBrandChangeFromNull() {
		MockRepositorySettingsPage settingsPage = navigateWithBrands(null, "org.mylyn");
		assertEquals("org.mylyn", settingsPage.getBrand());
	}

	@Test
	public void testSetsBrandOnSettingsPageBrandChange() {
		MockRepositorySettingsPage settingsPage = navigateWithBrands("org.eclipse", "org.mylyn");
		assertEquals("org.mylyn", settingsPage.getBrand());
	}

	private MockRepositorySettingsPage navigateWithBrands(String... brands) {
		SelectRepositoryConnectorPage selectionPage = (SelectRepositoryConnectorPage) wizard
				.getPage(SelectRepositoryConnectorPage_Select_a_task_repository_type);
		for (String brand : brands) {
			selectionPage.setConnectorBrand(new ConnectorBrand(new MockRepositoryConnector(), brand));
			wizard.getNextPage(selectionPage);
		}
		return (MockRepositorySettingsPage) wizard.getNextPage(selectionPage);
	}
}
