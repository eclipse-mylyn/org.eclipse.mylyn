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

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchHit;
import org.eclipse.mylar.internal.bugzilla.ui.search.BugzillaSearchOperation;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 */
public class BugzillaQueryTest extends TestCase {

	private TaskRepository repository;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
	}

	public void testQueryBugs() throws Exception {
		BugzillaResultCollector collector = new BugzillaResultCollector();
		collector.setProgressMonitor(new NullProgressMonitor());
		BugzillaSearchOperation operation = new BugzillaSearchOperation(
				repository,
				"http://mylar.eclipse.org/bugs222/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=",
				null, collector, "-1");
		operation.run(new NullProgressMonitor());

		assertEquals(2, collector.getResults().size());
		for (BugzillaSearchHit hit : collector.getResults()) {
			assertTrue(hit.getDescription().startsWith("search-match-test"));
		}
	}

	// public void testRetrieveBug() throws Exception {
	// RepositoryTaskData taskData =
	// BugzillaRepositoryUtil.getBug(IBugzillaConstants.TEST_BUGZILLA_222_URL,
	// "", "", null, "UTF-8", 1);
	// assertNotNull(taskData);
	// }
}
