/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.tasks.tests.performance.TaskContainerTest;
import org.eclipse.mylyn.tasks.tests.performance.TaskListPerformanceTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTasksPerformanceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tests.performance");
		suite.addTestSuite(TaskContainerTest.class);
		suite.addTestSuite(TaskListPerformanceTest.class);
		return suite;
	}

}