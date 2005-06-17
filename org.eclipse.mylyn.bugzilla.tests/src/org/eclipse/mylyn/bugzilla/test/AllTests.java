/*******************************************************************************
 * Copyright (c) 2003 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.test;

import junit.framework.Test;
import junit.framework.TestSuite;

//TODO add tests for 2.18 bugzilla

/**
 * @author tanya
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.bugzilla.test");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(BugzillaNewBugParserTestCDT.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestEquinox.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestGMT.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestPlatform.class));
		suite.addTest(new TestSuite(BugzillaNewBugParserTestVE.class));
		suite.addTest(new TestSuite(BugzillaParserTest.class));
		suite.addTest(new TestSuite(BugzillaParserTestNoBug.class));
		suite.addTest(new TestSuite(
				BugzillaProductParser1ProductHipikatTest.class));
		suite.addTest(new TestSuite(BugzillaProductParserTest.class));
		//$JUnit-END$
		return suite;
	}
}