/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllIdeTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllIdeTests.class.getName());
		suite.addTestSuite(IdeStartupTest.class);
		suite.addTestSuite(IdePreferencesTest.class);
		return suite;
	}

}
