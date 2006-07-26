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

package org.eclipse.mylar.bugzilla.tests.headless;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.NewBugzillaReportWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.DuplicateDetectionData;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * 
 * @author Jeff Pound
 */
public class BugzillaDuplicateDetectionTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
	}

	public void testDuplicateDetection() throws Exception {
		String stackTrace = "java.lang.NullPointerException\njeff.testing.stack.trace.functionality";
		String fakeStackTrace = "thisisnotreallyastacktrace";
		int numMatches = 2;

		NewBugzillaReportWizard wizard = new NewBugzillaReportWizard(repository, null);
		DuplicateDetectionData dupData = new DuplicateDetectionData();
		dupData.setStackTrace(stackTrace);

		List<AbstractRepositoryTask> tasks = wizard.searchForDuplicates(dupData);
		assertNotNull(tasks);
		assertEquals(numMatches, tasks.size());
		
		dupData.setStackTrace(fakeStackTrace);
		tasks = wizard.searchForDuplicates(dupData);
		assertNotNull(tasks);
		assertEquals(0, tasks.size());
	}
}
