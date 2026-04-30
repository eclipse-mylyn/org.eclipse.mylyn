/*******************************************************************************
 * Copyright (c) 2010, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests;

import org.eclipse.mylyn.builds.tests.core.BuildModelManagerTest;
import org.eclipse.mylyn.builds.tests.operations.RefreshOperationTest;
import org.eclipse.mylyn.builds.tests.ui.BuildsViewTest;
import org.eclipse.mylyn.builds.tests.util.BuildsUrlHandlerTest;
import org.eclipse.mylyn.builds.tests.util.JUnitResultGeneratorTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Steffen Pingel
 */
@Suite
@SelectClasses({ BuildModelManagerTest.class, JUnitResultGeneratorTest.class, RefreshOperationTest.class,
	BuildsUrlHandlerTest.class, BuildsViewTest.class })
public class AllBuildsTests {
}
