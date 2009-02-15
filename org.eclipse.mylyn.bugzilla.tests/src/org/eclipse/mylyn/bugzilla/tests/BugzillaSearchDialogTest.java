/*******************************************************************************
 * Copyright (c) 2004, 2008 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Shell;

/**
 * Test the bugzilla search dialog.
 * 
 * @author Jeff Pound
 */
public class BugzillaSearchDialogTest extends TestCase {

	private TaskRepositoryManager manager;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		manager = TasksUiPlugin.getRepositoryManager();
		assertNotNull(manager);
		manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null) {
			manager.clearRepositories(TasksUiPlugin.getDefault().getRepositoriesFilePath());
		}
	}

	/**
	 * Test that the search dialog is initialized properly with the given repository.
	 * 
	 * @throws Exception
	 */
	public void testSearchDialogInit() throws Exception {
		TaskRepository repo = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND,
				IBugzillaTestConstants.TEST_BUGZILLA_222_URL);
		repo.setVersion(IBugzillaConstants.BugzillaServerVersion.SERVER_222.toString());
		manager.addRepository(repo);
		BugzillaSearchPage page = new BugzillaSearchPage(repo);
		Shell shell = BugzillaTestPlugin.getDefault().getWorkbench().getDisplay().getShells()[0];
		page.createControl(shell);
		page.setVisible(true);

		/*
		 * This assertion will fail with a 0 product count if the options are
		 * not retrieved properly, throw an exception if the page is not
		 * initialized properly, or pass otherwise.
		 */
		assertFalse(page.getProductCount() == 0);
	}
}
