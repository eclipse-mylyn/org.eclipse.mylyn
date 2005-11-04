/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllBugzillaTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.bugzilla.test");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(BugzillaNewBugParserTestCDT.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestEquinox.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestGMT.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestPlatform.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestVE.class));
		suite.addTest(new TestSuite(BugzillaParserTestNoBug.class));
		suite.addTest(new TestSuite(BugzillaProductParserTest.class));
		
		// TODO: enable
//		suite.addTest(new TestSuite(BugzillaParserTest.class));
		//$JUnit-END$
		return suite;
	}
}
