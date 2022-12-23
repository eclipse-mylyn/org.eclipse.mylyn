/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;

/**
 * @author Mik Kersten
 */
public class AllResourcesTests {

	public static Test suite() {
		ContextTestUtil.triggerContextUiLazyStart();

		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.resources.tests");
		suite.addTestSuite(ResourceChangeMonitorTest.class);
		suite.addTestSuite(ResourcePatternExclusionStrategyTest.class);
		suite.addTestSuite(ResourceModificationDateExclusionStrategyTest.class);
		suite.addTestSuite(ResourceContextTest.class);
		suite.addTestSuite(ResourcesUiTest.class);
		return suite;
	}

}
