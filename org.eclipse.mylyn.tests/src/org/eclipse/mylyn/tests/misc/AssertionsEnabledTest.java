package org.eclipse.mylyn.tests.misc;

/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

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
