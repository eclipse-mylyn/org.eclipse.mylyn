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

import org.eclipse.mylyn.tasks.tests.AllTasksPerformanceTests;

/**
 * @author Steffen Pingel
 */
public class AllPerformanceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Performance tests for org.eclipse.mylyn.tests");
		suite.addTest(AllTasksPerformanceTests.suite());
		return suite;
	}

}
