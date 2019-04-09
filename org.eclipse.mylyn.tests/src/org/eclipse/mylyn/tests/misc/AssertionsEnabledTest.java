/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.misc;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
public class AssertionsEnabledTest extends TestCase {

	public AssertionsEnabledTest(String name) {
		super(name);
	}

	public void testAssertionsEnabled() {
		try {
			assert false;
			fail("run all test with assertions: \"java -enableassertions\"");
		} catch (AssertionError e) {
			// Oasswertions enabled
		}
	}
}
