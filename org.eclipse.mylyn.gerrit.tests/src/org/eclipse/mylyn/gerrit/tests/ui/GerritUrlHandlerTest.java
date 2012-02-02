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
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tests.util.TestFixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/**
 * @author Steffen Pingel
 */
public class GerritUrlHandlerTest extends TestCase {

	private IWorkbenchPage activePage;

	@Override
	protected void setUp() throws Exception {
		activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		assertNotNull(activePage);
	}

	@Override
	protected void tearDown() throws Exception {
		TestFixture.resetTaskListAndRepositories();
	}

	public void testOpenUrl() throws Exception {
		// needs to be a repository that is not protected by HTTP auth to avoid browser popup in case of test failure
		TaskRepository repository = GerritFixture.GERRIT_2_2_1.singleRepository();
		repository.setCredentials(AuthenticationType.REPOSITORY, null, false);
		EditorHandle handler = BrowserUtil.openUrl(activePage, repository.getUrl() + "/1", 0); //$NON-NLS-1$		
		assertNull("Expected an editor instance, got a browser instance", handler.getAdapter(IWebBrowser.class));

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

	public void atestOpenUrlInvalid() throws Exception {
		// needs to be a repository that is not protected by HTTP auth to avoid browser popup
		TaskRepository repository = GerritFixture.GERRIT_2_2_1.singleRepository();
		EditorHandle handler = BrowserUtil.openUrl(activePage, repository.getUrl() + "/abc", 0); //$NON-NLS-1$
		assertNotNull("Expected a browser instance, got: " + handler.getClass(), handler.getAdapter(IWebBrowser.class));
		assertEquals(Status.OK_STATUS, handler.getStatus());
	}

}
