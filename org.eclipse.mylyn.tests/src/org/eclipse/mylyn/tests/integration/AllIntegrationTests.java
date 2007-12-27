/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllIntegrationTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tests.integration");

		// $JUnit-BEGIN$
		// XXX: Put Back
		//suite.addTestSuite(TaskListFilterTest.class);
		suite.addTestSuite(DefaultPreferenceConfigTest.class);
		suite.addTestSuite(ExtensionPointInitializationTest.class);
		suite.addTestSuite(ChangeDataDirTest.class);
		suite.addTest(RepositoryConnectorsTest.suite());
		suite.addTestSuite(WebRepositoryConnectorTest.class);
		// $JUnit-END$
		return suite;
	}
}
