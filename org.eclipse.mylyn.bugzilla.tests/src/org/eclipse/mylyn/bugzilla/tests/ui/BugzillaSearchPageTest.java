/*******************************************************************************
 * Copyright (c) 2004, 2013 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.ui;

import junit.framework.TestCase;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.bugzilla.ui.search.BugzillaSearchPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * Test the bugzilla search dialog.
 * 
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class BugzillaSearchPageTest extends TestCase {

	private TaskRepository repository;

	@Override
	public void setUp() throws Exception {
		repository = BugzillaFixture.current().singleRepository();
	}

	/**
	 * Test that the search dialog is initialized properly with the given repository.
	 */
	public void testInit() throws Exception {
		BugzillaSearchPage page = new BugzillaSearchPage(repository);
		page.createControl(WorkbenchUtil.getShell());
		page.setVisible(true);

		/*
		 * This assertion will fail with a 0 product count if the options are
		 * not retrieved properly, throw an exception if the page is not
		 * initialized properly, or pass otherwise.
		 */
		assertFalse(page.getProductCount() == 0);
	}

}
