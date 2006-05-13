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

package org.eclipse.mylar.tasklist.tests;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.provisional.bugzilla.core.AbstractRepositoryReportAttribute;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.provisional.bugzilla.core.BugzillaReportAttribute;
import org.eclipse.mylar.provisional.bugzilla.core.Comment;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		// ignore
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// ignore
		super.tearDown();
	}

	public void testCompletionDate() {
		BugzillaTask task = new BugzillaTask("handle", "description", true);
		BugzillaReport report = new BugzillaReport(1, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		task.setBugReport(report);
		assertNull(task.getCompletionDate());

		Calendar calendar = Calendar.getInstance();
		Date now = new Date(calendar.getTimeInMillis());

		Comment comment = new Comment(report, 1);
		AbstractRepositoryReportAttribute attribute = new BugzillaReportAttribute(BugzillaReportElement.BUG_WHEN);
		attribute.setValue(Comment.creation_ts_date_format.format(now));
		comment.addAttribute(BugzillaReportElement.BUG_WHEN, attribute);
		report.addComment(comment);
		assertNull(task.getCompletionDate());

		AbstractRepositoryReportAttribute resolvedAttribute = new BugzillaReportAttribute(
				BugzillaReportElement.BUG_STATUS);
		resolvedAttribute.setValue(BugzillaReport.VAL_STATUS_RESOLVED);
		report.addAttribute(BugzillaReportElement.BUG_STATUS, resolvedAttribute);
		assertNotNull(task.getCompletionDate());
		assertEquals(Comment.creation_ts_date_format.format(now), Comment.creation_ts_date_format.format(task
				.getCompletionDate()));

	}

}
