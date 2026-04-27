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

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Steffen Pingel
 */
@Suite
@SelectClasses({ ContextMementoMigratorTest.class, EditorRestoreTest.class, PerspectiveRestoreTest.class,
	RefactorRepositoryUrlOperationTest.class, TaskActivityTimingTest.class, TaskContextStoreTest.class,
	TaskEditorRestoreTest.class })
public class AllContextTasksTests {
}
