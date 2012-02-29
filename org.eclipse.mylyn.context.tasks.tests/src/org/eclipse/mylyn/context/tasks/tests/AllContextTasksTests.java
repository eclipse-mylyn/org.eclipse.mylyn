/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tasks.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllContextTasksTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllContextTasksTests.class.getName());
		suite.addTestSuite(ContextMementoMigratorTest.class);
		suite.addTestSuite(EditorRestoreTest.class);
		suite.addTestSuite(PerspectiveRestoreTest.class);
		suite.addTestSuite(RefactorRepositoryUrlOperationTest.class);
		suite.addTestSuite(TaskActivityTimingTest.class);
		suite.addTestSuite(TaskContextStoreTest.class);
		return suite;
	}

}
