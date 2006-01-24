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

import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;

/**
 * @author Mik Kersten
 */
public class TaskTestUtil {

	public static void setBugTaskCompleted(BugzillaTask bugzillaTask, boolean completed) {
		BugReport report = new BugReport(1, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		bugzillaTask.setBugReport(report);
		Attribute resolvedAttribute = new Attribute(BugReport.ATTR_STATUS);
		if (completed) {
			resolvedAttribute.setValue(BugReport.VAL_STATUS_RESOLVED);
		} else {
			resolvedAttribute.setValue(BugReport.VAL_STATUS_NEW);
		}
		report.addAttribute(resolvedAttribute);
		
		Date now = new Date();
		report.addComment(new Comment(report, 1, now, "author", "author-name"));
	}
}
