/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
		suite.addTestSuite(TaskEditorRestoreTest.class);
		return suite;
	}

}
