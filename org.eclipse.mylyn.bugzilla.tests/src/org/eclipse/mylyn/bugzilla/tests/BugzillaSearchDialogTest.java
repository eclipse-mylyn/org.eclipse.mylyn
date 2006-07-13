/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepositoryManager;
import org.eclipse.mylar.tasks.core.TaskRepository;
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
		manager = MylarTaskListPlugin.getRepositoryManager();
		assertNotNull(manager);
		manager.clearRepositories();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (manager != null) {
			manager.clearRepositories();
		}
	}

	/**
	 * Test that the search dialog is initialized properly with the given
	 * repository.
	 * 
	 * @throws Exception
	 */
	public void testSearchDialogInit() throws Exception {
		TaskRepository repo = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL, IBugzillaConstants.BugzillaServerVersion.SERVER_222
						.toString());
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
