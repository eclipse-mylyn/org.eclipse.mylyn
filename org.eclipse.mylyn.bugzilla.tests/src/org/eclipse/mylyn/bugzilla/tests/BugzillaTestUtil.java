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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.tests.MylarTasksTestsPlugin;

/**
 * @author Mik Kersten
 */
public class BugzillaTestUtil {

	private static BugzillaAttributeFactory attributeFactory = new BugzillaAttributeFactory();

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
		RepositoryTaskData report = new RepositoryTaskData(new BugzillaAttributeFactory(),
				BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.ECLIPSE_BUGZILLA_URL, "1", Task.DEFAULT_TASK_KIND);
		bugzillaTask.setTaskData(report);
		RepositoryTaskAttribute resolvedAttribute = attributeFactory.createAttribute(BugzillaReportElement.BUG_STATUS
				.getKeyString());
		if (completed) {
			resolvedAttribute.setValue(IBugzillaConstants.VALUE_STATUS_RESOLVED);
			TaskComment taskComment = new TaskComment(new BugzillaAttributeFactory(), 1);
			RepositoryTaskAttribute attribute = attributeFactory.createAttribute(BugzillaReportElement.BUG_WHEN
					.getKeyString());
			attribute.setValue(new SimpleDateFormat(BugzillaAttributeFactory.comment_creation_ts_format).format(new Date()));
			taskComment.addAttribute(BugzillaReportElement.BUG_WHEN.getKeyString(), attribute);
			report.addComment(taskComment);
		} else {
			resolvedAttribute.setValue("NEW");
		}

		report.addAttribute(BugzillaReportElement.BUG_STATUS.getKeyString(), resolvedAttribute);
	}

}
