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
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.AbstractBugzillaWizardPage;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Ian Bull
 */
public class NewBugWizardTest extends TestCase {

	public void testPlatformOptions() throws Exception {

		NewBugzillaReport newReport = new NewBugzillaReport(IBugzillaConstants.TEST_BUGZILLA_220_URL, 1);
		AbstractBugzillaWizardPage page = new TestWizardDataPage();
		TaskRepository repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);
		BugzillaRepositoryUtil.setupNewBugAttributes(repository.getUrl(), repository.getUserName(), repository.getPassword(), newReport, null); 
		page.setPlatformOptions(newReport);

		String os = Platform.getOS();
		if (os.equals("win32"))
			assertEquals("Windows", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		else if (os.equals("solaris"))
			assertEquals("Solaris", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		else if (os.equals("qnx"))
			assertEquals("QNX-Photon", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		else if (os.equals("macosx"))
			assertEquals("Mac OS", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		else if (os.equals("linux"))
			assertEquals("Linux", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		else if (os.equals("hpux"))
			assertEquals("HP-UX", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());
		else if (os.equals("aix"))
			assertEquals("AIX", newReport.getAttribute(BugzillaReportElement.OP_SYS).getValue());

		String platform = Platform.getOSArch();
		if (platform.equals("x86"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		else if (platform.equals("x86_64"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		else if (platform.equals("ia64"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		else if (platform.equals("ia64_32"))
			assertEquals("PC", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		else if (platform.equals("sparc"))
			assertEquals("Sun", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());
		else if (platform.equals("ppc"))
			assertEquals("Power", newReport.getAttribute(BugzillaReportElement.REP_PLATFORM).getValue());

	}

	static class TestWizardDataPage extends AbstractBugzillaWizardPage {

		public TestWizardDataPage() {
			super("", "", "", PlatformUI.getWorkbench());
		}
	}

}
