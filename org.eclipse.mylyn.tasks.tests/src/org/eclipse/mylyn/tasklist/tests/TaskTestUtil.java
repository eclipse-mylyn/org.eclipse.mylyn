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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylar.bugzilla.core.AbstractRepositoryReportAttribute;
import org.eclipse.mylar.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.bugzilla.core.BugzillaReportAttribute;
import org.eclipse.mylar.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.internal.BugzillaReportElement;

/**
 * @author Mik Kersten
 */
public class TaskTestUtil {

	public static File getLocalFile(String path) {
		try {
			URL installURL = MylarTasksTestsPlugin.getDefault().getBundle().getEntry(path);
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Adaptred from Java Developers' almanac
	 */
	public static void copy(File source, File dest) throws IOException {
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(dest);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static void setBugTaskCompleted(BugzillaTask bugzillaTask, boolean completed) {
		BugzillaReport report = new BugzillaReport(1, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		bugzillaTask.setBugReport(report);
		AbstractRepositoryReportAttribute resolvedAttribute = new BugzillaReportAttribute(
				BugzillaReportElement.BUG_STATUS);
		if (completed) {			
			resolvedAttribute.setValue(BugzillaReport.VAL_STATUS_RESOLVED);
			Comment comment = new Comment(report, 1);
			AbstractRepositoryReportAttribute attribute = new BugzillaReportAttribute(BugzillaReportElement.CREATION_TS);
			attribute.setValue(Comment.creation_ts_date_format.format(new Date()));	
			comment.addAttribute(BugzillaReportElement.CREATION_TS, attribute);
			report.addComment(comment);
		} else {
			resolvedAttribute.setValue(BugzillaReport.VAL_STATUS_NEW);
		}

		report.addAttribute(BugzillaReportElement.BUG_STATUS, resolvedAttribute);
		report.addComment(new Comment(report, 1));
		// report.addComment(new Comment(report, 1, now, "author",
		// "author-name"));
	}
}
