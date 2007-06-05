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

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.Task;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Ian Bull
 */
public class NewBugWizardTest extends TestCase {

	public void testPlatformOptions() throws Exception {

		
		RepositoryTaskData newReport = new RepositoryTaskData(new BugzillaAttributeFactory(), BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_220_URL, "1", Task.DEFAULT_TASK_KIND);

		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);
		BugzillaRepositoryConnector.setupNewBugAttributes(repository, newReport);
		BugzillaCorePlugin.getDefault().setPlatformOptions(newReport);

		String os = Platform.getOS();
		if (os.equals("win32"))
			assertEquals("Windows", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		else if (os.equals("solaris"))
			assertEquals("Solaris", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		else if (os.equals("qnx"))
			assertEquals("QNX-Photon", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		else if (os.equals("macosx"))
			assertEquals("Mac OS", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		else if (os.equals("linux"))
			assertEquals("Linux", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		else if (os.equals("hpux"))
			assertEquals("HP-UX", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());
		else if (os.equals("aix"))
			assertEquals("AIX", newReport.getAttribute(BugzillaReportElement.OP_SYS.getKeyString()).getValue());

		String platform = Platform.getOSArch();
		if (platform.equals("x86")) {
			if (os.equals("macosx"))
				assertEquals("Macintosh", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
			else
				assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		} else if (platform.equals("x86_64"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		else if (platform.equals("ia64"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		else if (platform.equals("ia64_32"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		else if (platform.equals("sparc"))
			assertEquals("Sun", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		else if (platform.equals("ppc")) {
			if (os.equals("macosx"))
				assertEquals("Macintosh", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
			else
				assertEquals("Power", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString()).getValue());
		}
	}

}
