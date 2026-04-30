/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.builds.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.tests.support.BuildHarness;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class BuildsUrlHandlerTest {

	private IWorkbenchPage activePage;

	private BuildHarness harness;

	private BuildServer server;

	@BeforeEach
	void setUp() throws Exception {
		activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertNotNull(activePage);

		harness = new BuildHarness();
		server = harness.createServer();
	}

	@AfterEach
	void tearDown() throws Exception {
		harness.dispose();
	}

	@Test
	public void testOpenUrl() throws Exception {
		EditorHandle handler = BrowserUtil.openUrl(activePage, server.getUrl() + "/123", 0); //$NON-NLS-1$

		long startTime = System.currentTimeMillis();
		Display display = PlatformUI.getWorkbench().getDisplay();
		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) {
				if (handler.await(500, TimeUnit.MILLISECONDS)) {
					break;
				}
				assertTrue(System.currentTimeMillis() - startTime < 10 * 1000,
						"Expected editor did not open within 10 seconds");
			}
		}

		assertEquals(Status.OK_STATUS, handler.getStatus());
		assertEquals(BuildEditor.class, activePage.getActiveEditor().getClass());
	}
}
