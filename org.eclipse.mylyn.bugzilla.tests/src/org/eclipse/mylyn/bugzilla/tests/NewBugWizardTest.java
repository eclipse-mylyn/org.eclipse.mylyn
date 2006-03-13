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
import java.io.FileReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.bugzilla.core.BugReport;
import org.eclipse.mylar.core.tests.support.FileTool;
import org.eclipse.mylar.internal.bugzilla.core.NewBugModel;
import org.eclipse.mylar.internal.bugzilla.core.internal.NewBugParser;
import org.eclipse.mylar.internal.bugzilla.ui.wizard.AbstractBugzillaWizardPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Ian Bull
 */
public class NewBugWizardTest extends TestCase {

	public void testPlatformOptions() throws Exception {

		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path("testdata/pages/cdt-page.html"));
		Reader in = new FileReader(f);

		NewBugModel model = new NewBugModel();
		AbstractBugzillaWizardPage page = new TestWizardDataPage();
		new NewBugParser(in).parseBugAttributes(model, true); // ** TRUE vs
																// FALSE **
		page.setPlatformOptions(model);

		String os = Platform.getOS();
		if (os.equals("win32"))
			assertEquals("Windows All", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());
		else if (os.equals("solaris"))
			assertEquals("Solaris", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());
		else if (os.equals("qnx"))
			assertEquals("QNX-Photon", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());
		else if (os.equals("macosx"))
			assertEquals("MacOS X", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());
		else if (os.equals("linux"))
			assertEquals("Linux", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());
		else if (os.equals("hpux"))
			assertEquals("HP-UX", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());
		else if (os.equals("aix"))
			assertEquals("AIX", model.getAttribute(BugReport.ATTRIBUTE_OS).getValue());

		String platform = Platform.getOSArch();
		if (platform.equals("x86"))
			assertEquals("PC", model.getAttribute(BugReport.ATTRIBUTE_PLATFORM).getValue());
		else if (platform.equals("x86_64"))
			assertEquals("PC", model.getAttribute(BugReport.ATTRIBUTE_PLATFORM).getValue());
		else if (platform.equals("ia64"))
			assertEquals("PC", model.getAttribute(BugReport.ATTRIBUTE_PLATFORM).getValue());
		else if (platform.equals("ia64_32"))
			assertEquals("PC", model.getAttribute(BugReport.ATTRIBUTE_PLATFORM).getValue());
		else if (platform.equals("sparc"))
			assertEquals("Sun", model.getAttribute(BugReport.ATTRIBUTE_PLATFORM).getValue());
		else if (platform.equals("ppc"))
			assertEquals("Power", model.getAttribute(BugReport.ATTRIBUTE_PLATFORM).getValue());

	}

	static class TestWizardDataPage extends AbstractBugzillaWizardPage {

		public TestWizardDataPage() {
			super("", "", "", PlatformUI.getWorkbench());
		}
	}

}
