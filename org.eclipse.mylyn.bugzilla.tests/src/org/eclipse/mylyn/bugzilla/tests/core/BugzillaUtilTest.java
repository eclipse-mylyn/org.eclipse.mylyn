/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaUtil;
import org.junit.Test;

public class BugzillaUtilTest extends TestCase {

	@Test
	public void testValidTimezoneRemove() throws CoreException {
		assertEquals("2013-10-21 18:50:29", BugzillaUtil.removeTimezone("2013-10-21 18:50:29 +0000"));
		assertEquals("2013-10-21 18:50:29", BugzillaUtil.removeTimezone("2013-10-21 18:50:29 CEST"));
		assertEquals("2013-10-21 18:50:29", BugzillaUtil.removeTimezone("2013-10-21 18:50:29 GMT+02:00"));
	}

	@Test
	public void testInvalidTimezoneRemove() {
		try {
			BugzillaUtil.removeTimezone("2013-10-21T18:50:29");
			fail("Expected CoreException");
		} catch (CoreException e) {
			assertEquals("2013-10-21T18:50:29 is not a valid time", e.getMessage());
		}
	}
}
