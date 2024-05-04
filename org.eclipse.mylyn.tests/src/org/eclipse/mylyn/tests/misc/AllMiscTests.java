/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tests.misc;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class AllMiscTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.tests");
		suite.addTestSuite(AssertionsEnabledTest.class);
		return suite;
	}

}
