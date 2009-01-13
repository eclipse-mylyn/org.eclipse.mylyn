/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.misc;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllMiscTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tests");
		// $JUnit-BEGIN$
		// suite.addTestSuite(SharedTaskFolderTest.class);
		// suite.addTestSuite(BugzillaSearchPluginTest.class);
		suite.addTestSuite(UrlExclusionTest.class);
		suite.addTestSuite(AssertionsEnabledTest.class);
		suite.addTestSuite(HypertextStructureBridgeTest.class);
		suite.addTestSuite(GetFaviconForUrlTest.class);
		//suite.addTestSuite(BugzillaStackTraceTest.class);
		// $JUnit-END$
		return suite;
	}

}
