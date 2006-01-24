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

package org.eclipse.mylar.tests.tasklist;

import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;

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
		BugzillaTask task = new BugzillaTask("handle", "description", true, true);
		BugReport report = new BugReport(1, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		task.setBugReport(report);
		assertNull(task.getCompletionDate());

		Date now = new Date();
		report.addComment(new Comment(report, 1, now, "author", "author-name"));
		assertNull(task.getCompletionDate());

		Attribute resolvedAttribute = new Attribute(BugReport.ATTR_STATUS);
		resolvedAttribute.setValue(BugReport.VAL_STATUS_RESOLVED);
		report.addAttribute(resolvedAttribute);
		assertEquals(now, task.getCompletionDate());

	}

}
