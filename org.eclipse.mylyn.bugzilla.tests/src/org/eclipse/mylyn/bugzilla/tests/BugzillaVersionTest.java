/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaVersion;

/**
 * @author Frank Becker
 */
public class BugzillaVersionTest extends TestCase {

	public final static BugzillaVersion BUGZILLA_2_18_1 = new BugzillaVersion("2.18.1");

	public final static BugzillaVersion BUGZILLA_2_18_2 = new BugzillaVersion("2.18.2");

	public final static BugzillaVersion BUGZILLA_2_20_3 = new BugzillaVersion("2.20.3");

	public final static BugzillaVersion BUGZILLA_3_0_4 = new BugzillaVersion("3.0.4");

	public void testwrongVersion() throws Exception {
		try {
			new BugzillaVersion("3.2.X");
			fail("NumberFormatException expected!");
		} catch (Exception e) {
			assertTrue(e instanceof NumberFormatException);
			assertEquals("For input string: \"X\"", e.getMessage());
		}
	}

	public void testCompareAll() throws Exception {
		assertEquals(0, BUGZILLA_2_18_1.compareTo(BUGZILLA_2_18_1));
		assertEquals(-1, BUGZILLA_2_18_1.compareTo(BUGZILLA_2_18_2));
		assertEquals(1, BUGZILLA_2_18_2.compareTo(BUGZILLA_2_18_1));

		assertEquals(-1, BUGZILLA_2_18_1.compareTo(BUGZILLA_2_20_3));
		assertEquals(1, BUGZILLA_3_0_4.compareMajorMinorOnly(BUGZILLA_2_20_3));
	}

	public void testCompareMajorMinorOnly() throws Exception {
		assertEquals(0, BUGZILLA_2_18_1.compareMajorMinorOnly(BUGZILLA_2_18_1));
		assertEquals(0, BUGZILLA_2_18_1.compareMajorMinorOnly(BUGZILLA_2_18_2));
		assertEquals(0, BUGZILLA_2_18_2.compareMajorMinorOnly(BUGZILLA_2_18_1));

		assertEquals(-1, BUGZILLA_2_18_1.compareMajorMinorOnly(BUGZILLA_2_20_3));
		assertEquals(1, BUGZILLA_3_0_4.compareMajorMinorOnly(BUGZILLA_2_20_3));
	}

	public void testToString() throws Exception {
		assertEquals("2.18.1", BUGZILLA_2_18_1.toString());
		assertEquals("3.0.4", BUGZILLA_3_0_4.toString());
	}
}
