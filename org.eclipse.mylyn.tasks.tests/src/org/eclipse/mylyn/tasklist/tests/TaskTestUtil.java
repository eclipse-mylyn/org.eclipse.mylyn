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
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;

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
