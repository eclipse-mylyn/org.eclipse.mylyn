/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.mylyn.commons.core.CoreUtil;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class CoreUtilTest {

	@Test
	public void testCompareNullNull() {
		assertEquals(0, CoreUtil.compare(null, null));
	}

	@Test
	public void testCompareNullString() {
		assertEquals(1, CoreUtil.compare(null, "abc"));
	}

	@Test
	public void testCompareNumbersBigger() {
		assertEquals(1, CoreUtil.compare(4, 2));
	}

	@Test
	public void testCompareNumbersEquals() {
		assertEquals(0, CoreUtil.compare(-4, -4));
	}

	@Test
	public void testCompareNumbersNull() {
		assertEquals(1, CoreUtil.compare(null, 2));
	}

	@Test
	public void testCompareNumbersSmaller() {
		assertEquals(-1, CoreUtil.compare(1, 2));
	}

	@Test
	public void testCompareStringNull() {
		assertEquals(-1, CoreUtil.compare("abc", null));
	}

	@Test
	public void testPropertyEquallsNullFalse() {
		assertFalse(CoreUtil.propertyEquals(false, null));
	}

	@Test
	public void testPropertyEquallsNullTrue() {
		assertTrue(CoreUtil.propertyEquals(true, null));
	}

	@Test
	public void testPropertyEqualsBooleanTrue() {
		assertTrue(CoreUtil.propertyEquals(true, Boolean.TRUE));
	}

	@Test
	public void testPropertyEqualsNumbe() {
		assertFalse(CoreUtil.propertyEquals(true, 1));
	}

	@Test
	public void testPropertyEqualsStringFalse() {
		assertFalse(CoreUtil.propertyEquals(false, "false"));
	}

	@Test
	public void testPropertyEqualsStringFalseUnexpected() {
		assertFalse(CoreUtil.propertyEquals(true, "false"));
	}

	@Test
	public void testPropertyEqualsStringTrue() {
		assertFalse(CoreUtil.propertyEquals(true, "true"));
	}

	@Test
	public void testAreEqualEqualStrings() {
		assertTrue(CoreUtil.areEqual("a", "a"));
	}

	@Test
	public void testAreEqualSameObject() {
		Object o = new Object();
		assertTrue(CoreUtil.areEqual(o, o));
	}

	@Test
	public void testAreEqualNull() {
		assertTrue(CoreUtil.areEqual(null, null));
	}

	@Test
	public void testAreEqualRightNotNull() {
		assertFalse(CoreUtil.areEqual(null, new Object()));
	}

	@Test
	public void testAreEqualLeftNotNull() {
		assertFalse(CoreUtil.areEqual(new Object(), null));
	}

	@Test
	public void testAreEqualUnequalObject() {
		assertFalse(CoreUtil.areEqual(1, "a"));
	}

	@Test
	public void testAreEqualUnequalStrings() {
		assertFalse(CoreUtil.areEqual("a", "b"));
	}


	@Test
	public void testGetRuntimeVersion() {
		assertEquals(new Version(1, 5, 0), CoreUtil.parseRuntimeVersion("1.5.0.2"));
	}

	@Test
	public void testGetRuntimeVersionShort() {
		assertEquals(new Version(1, 7, 0), CoreUtil.parseRuntimeVersion("1.7"));
	}

	@Test
	public void testGetRuntimeVersionLetters() {
		assertEquals(new Version(1, 7, 0, "CUSTOM"), CoreUtil.parseRuntimeVersion("1.7-CUSTOM"));
	}

	@Test
	public void testGetRuntimeVersionTrailingUnderscore() {
		assertEquals(new Version(1, 5, 0, "A"), CoreUtil.parseRuntimeVersion("1.5.0.1-A"));
	}

	@Test
	public void testGetRuntimeVersionQualifier() {
		assertEquals(new Version(1, 2, 1), CoreUtil.parseRuntimeVersion("1.2.1"));
	}

	@Test
	public void testGetRuntimeVersionJava21Ubuntu() {
		assertEquals(new Version(21, 0, 8), CoreUtil.parseRuntimeVersion("21.0.8+9-Ubuntu-0ubuntu124.04.1"));
	}

	@Test
	public void testGetRuntimeVersionJava22() {
		assertEquals(new Version(22, 0, 0), CoreUtil.parseRuntimeVersion("22+38"));
	}

	@Test
	public void testGetBadRuntimeVersion() {
		assertEquals(new Version(0, 0, 0), CoreUtil.parseRuntimeVersion("21.0.A"));
	}

	@Test
	public void testAsFileName() {
		assertEquals("abc", CoreUtil.asFileName("abc"));
		assertEquals("a.b.c", CoreUtil.asFileName("a.b.c"));
		assertEquals("", CoreUtil.asFileName(""));
	}

	@Test
	public void testAsFileNameSpaces() {
		assertEquals("%20%20", CoreUtil.asFileName("  "));
		assertEquals(".%20", CoreUtil.asFileName(". "));
	}

	@Test
	public void testAsFileNamePercent() {
		assertEquals("%25abc", CoreUtil.asFileName("%abc"));
		assertEquals("%2525abc", CoreUtil.asFileName("%25abc"));
	}

	@Test
	public void testDecode() {
		assertEquals("abc", CoreUtil.encode("abc"));
		assertEquals("%2D_", CoreUtil.encode("-"));
		assertEquals("abc%2D_123", CoreUtil.encode("abc-123"));
		assertEquals("", CoreUtil.encode(""));
	}

	@Test
	public void testDecodeInvalid() {
		try {
			String s = CoreUtil.decode("abc-123");
			fail("Expected IllegalArgumentException, got '" + s + "'");
		} catch (IllegalArgumentException e) {
		}
		try {
			String s = CoreUtil.decode("%Z_");
			fail("Expected IllegalArgumentException, got '" + s + "'");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testEncode() {
		assertEquals("abc", CoreUtil.decode("abc"));
		assertEquals("-", CoreUtil.decode("%2D_"));
		assertEquals("abc-123", CoreUtil.decode("abc%2D_123"));
		assertEquals("abc-123", CoreUtil.decode(CoreUtil.decode(CoreUtil.encode(CoreUtil.encode("abc-123")))));
	}

}
