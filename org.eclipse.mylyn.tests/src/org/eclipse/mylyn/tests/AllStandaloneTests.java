/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.bugzilla.tests.TaskListStandaloneTest;
import org.eclipse.mylyn.context.tests.ContextTest;
import org.eclipse.mylyn.context.tests.DegreeOfInterestTest;

/**
 * @author Mik Kersten
 */
public class AllStandaloneTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests not requiring Eclipse Workbench");

		// $JUnit-BEGIN$
		// suite.addTestSuite(ContextExternalizerTest.class);
		suite.addTestSuite(DegreeOfInterestTest.class);
		suite.addTestSuite(ContextTest.class);
		suite.addTestSuite(TaskListStandaloneTest.class);
		// $JUnit-END$
		return suite;
	}
}
