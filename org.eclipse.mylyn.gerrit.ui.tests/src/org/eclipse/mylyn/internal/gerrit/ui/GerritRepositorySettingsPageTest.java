/*******************************************************************************
 * Copyright (c) 2014 Michael Keppler and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Michael Keppler - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;

public class GerritRepositorySettingsPageTest {

	private GerritRepositorySettingsPage page;

	@Before
	public void setUp() {
		TaskRepository repository = new TaskRepository(GerritConnector.CONNECTOR_KIND, "http://localhost"); //$NON-NLS-1$
		page = new GerritRepositorySettingsPage(repository);
		Composite parent = new Composite(WorkbenchUtil.getShell(), SWT.DEFAULT);
		page.createControl(parent);
	}

	/**
	 * This creates a new settings page, inserts the given URL, and fetches the URL back for validation
	 */
	private String getUrlFromPage(String urlText) {
		page.setUrl(urlText);
		return page.getRepositoryUrl();
	}

	/**
	 * Verify that copy-paste URLs of specific gerrit reviews are accepted as repository url.
	 */
	@Test
	public void testGetRepositoryUrlRemoveFragmentPart() {
		assertEquals("https://git.eclipse.org/r", getUrlFromPage("https://git.eclipse.org/r/#/c/23883/"));
	}

	@Test
	public void testGetRepositoryUrlNoFragmentPart() {
		assertEquals("https://git.eclipse.org/r", getUrlFromPage("https://git.eclipse.org/r/"));
	}

}
