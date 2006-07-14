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

package org.eclipse.mylar.tests.integration;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.tests.TestingStatusNotifier;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllIntegrationTests {
	
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylar.tests.integration");

		MylarStatusHandler.setStatusNotifier(new TestingStatusNotifier());

		// $JUnit-BEGIN$
		suite.addTestSuite(TaskListFilterTest.class);
		suite.addTestSuite(DefaultPreferenceConfigTest.class);
		suite.addTestSuite(ExtensionPointInitializationTest.class);
		suite.addTestSuite(ChangeDataDirTest.class);
		// $JUnit-END$
		return suite;
	}
}
