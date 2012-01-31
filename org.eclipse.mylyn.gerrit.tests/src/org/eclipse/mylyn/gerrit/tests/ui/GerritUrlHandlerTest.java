/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.ui;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class GerritUrlHandlerTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestFixture.resetTaskListAndRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		TestFixture.resetTaskListAndRepositories();
	}

	public void testOpenUrl() throws Exception {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertNotNull(activePage);

		TaskRepository repository = GerritFixture.GERRIT_2_2_2.singleRepository();
		EditorHandle handler = BrowserUtil.openUrl(activePage, repository.getUrl() + "/1", 0); //$NON-NLS-1$

		long startTime = System.currentTimeMillis();
		Display display = PlatformUI.getWorkbench().getDisplay();
		while (!display.isDisposed()) {
			if (!display.readAndDispatch()) {
				if (handler.await(500, TimeUnit.MILLISECONDS)) {
					break;
				}
				assertTrue("Expected editor did not open within 10 seconds",
						System.currentTimeMillis() - startTime < 10 * 1000);
			}
		}

		assertEquals(Status.OK_STATUS, handler.getStatus());
		assertEquals(TaskEditor.class, activePage.getActiveEditor().getClass());
	}

}
