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

package org.eclipse.mylyn.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

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
		// XXX: refactor
		// suite.addTestSuite(TaskListStandaloneTest.class);
		// $JUnit-END$

		suite.addTest(org.eclipse.mylyn.wikitext.tests.HeadlessStandaloneTests.suite());

		return suite;
	}
}
