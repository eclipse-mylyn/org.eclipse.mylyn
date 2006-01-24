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

package org.eclipse.mylar.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylar.bugzilla.tests.AllBugzillaTests;
import org.eclipse.mylar.core.tests.AllCoreTests;
import org.eclipse.mylar.core.util.MylarStatusHandler;
import org.eclipse.mylar.ide.MylarIdePlugin;
import org.eclipse.mylar.ide.tests.AllIdeTests;
import org.eclipse.mylar.java.tests.AllJavaTests;
import org.eclipse.mylar.monitor.tests.AllMonitorTests;
import org.eclipse.mylar.tasklist.tests.AllTasklistTests;
import org.eclipse.mylar.tests.integration.AllIntegrationTests;
import org.eclipse.mylar.tests.misc.AllMiscTests;
import org.eclipse.mylar.tests.xml.AllXmlTests;

/**
 * @author Mik Kersten
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.tests");

		MylarStatusHandler.setDumpErrors(true);
		MylarIdePlugin.getDefault().setResourceMonitoringEnabled(false);

		// TODO: the order of these tests matters, but shouldn't
		// $JUnit-BEGIN$
		suite.addTest(AllMonitorTests.suite());
		suite.addTest(AllIntegrationTests.suite());
		suite.addTest(AllCoreTests.suite());
		suite.addTest(AllIdeTests.suite());
		suite.addTest(AllJavaTests.suite());
		suite.addTest(AllTasklistTests.suite());
		suite.addTest(AllXmlTests.suite()); // HACK: first because it doesn't
											// clean up properly
		suite.addTest(AllBugzillaTests.suite());
		suite.addTest(AllMiscTests.suite());
		// $JUnit-END$
		return suite;
	}
}
