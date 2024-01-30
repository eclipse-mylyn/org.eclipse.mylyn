/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class CoreUtilTest extends TestCase {

	public void testCompareNullNull() {
		assertEquals(0, CoreUtil.compare(null, null));
	}

	public void testCompareNullString() {
		assertEquals(1, CoreUtil.compare(null, "abc"));
	}

	public void testCompareNumbersBigger() {
		assertEquals(1, CoreUtil.compare(4, 2));
	}

	public void testCompareNumbersEquals() {
		assertEquals(0, CoreUtil.compare(-4, -4));
	}

	public void testCompareNumbersNull() {
		assertEquals(1, CoreUtil.compare(null, 2));
	}

	public void testCompareNumbersSmaller() {
		assertEquals(-1, CoreUtil.compare(1, 2));
	}

	public void testCompareStringNull() {
		assertEquals(-1, CoreUtil.compare("abc", null));
	}

	public void testPropertyEquallsNullFalse() {
		assertFalse(CoreUtil.propertyEquals(false, null));
	}

	public void testPropertyEquallsNullTrue() {
		assertTrue(CoreUtil.propertyEquals(true, null));
	}

	public void testPropertyEqualsBooleanTrue() {
		assertTrue(CoreUtil.propertyEquals(true, Boolean.TRUE));
	}

	public void testPropertyEqualsNumbe() {
		assertFalse(CoreUtil.propertyEquals(true, 1));
	}

	public void testPropertyEqualsStringFalse() {
		assertFalse(CoreUtil.propertyEquals(false, "false"));
	}

	public void testPropertyEqualsStringFalseUnexpected() {
		assertFalse(CoreUtil.propertyEquals(true, "false"));
	}

	public void testPropertyEqualsStringTrue() {
		assertFalse(CoreUtil.propertyEquals(true, "true"));
	}

	public void testAreEqualEqualStrings() {
		assertTrue(CoreUtil.areEqual("a", "a"));
	}

	public void testAreEqualSameObject() {
		Object o = new Object();
		assertTrue(CoreUtil.areEqual(o, o));
	}

	public void testAreEqualNull() {
		assertTrue(CoreUtil.areEqual(null, null));
	}

	public void testAreEqualRightNotNull() {
		assertFalse(CoreUtil.areEqual(null, new Object()));
	}

	public void testAreEqualLeftNotNull() {
		assertFalse(CoreUtil.areEqual(new Object(), null));
	}

	public void testAreEqualUnequalObject() {
		assertFalse(CoreUtil.areEqual(1, "a"));
	}

	public void testAreEqualUnequalStrings() {
		assertFalse(CoreUtil.areEqual("a", "b"));
	}

	public void testGetRuntimeVersion() {
		String oldValue = System.setProperty("java.runtime.version", "1.5.0_2");
		try {
			assertEquals(new Version(1, 5, 0, "2"), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue);
		}
	}

	public void testGetRuntimeVersionShort() {
		String oldValue = System.setProperty("java.runtime.version", "1.7");
		try {
			assertEquals(new Version(1, 7, 0), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue);
		}
	}

	public void testGetRuntimeVersionLetters() {
		String oldValue = System.setProperty("java.runtime.version", "1.7-CUSTOM");
		try {
			assertEquals(new Version(1, 7, 0), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue);
		}
	}

	public void testGetRuntimeVersionTrailingUnderscore() {
		String oldValue = System.setProperty("java.runtime.version", "1.5.0_");
		try {
			assertEquals(new Version(1, 5, 0), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue);
		}
	}

	public void testGetRuntimeVersionNoQualifier() {
		String oldValue = System.setProperty("java.runtime.version", "1.2.0");
		try {
			assertEquals(new Version(1, 2, 0), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue);
		}
	}

	public void testGetRuntimeVersionProperty() {
		String oldValue1 = System.setProperty("java.runtime.version", "1.2.0");
		String oldValue2 = System.setProperty("java.version", "1.3.0");
		try {
			assertEquals(new Version(1, 2, 0), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue1);
			System.setProperty("java.version", oldValue2);
		}
	}

	public void testGetRuntimeVersionPropertyNull() {
		String oldValue1 = System.clearProperty("java.runtime.version");
		String oldValue2 = System.setProperty("java.version", "1.3.0");
		try {
			assertEquals(new Version(1, 3, 0), CoreUtil.getRuntimeVersion());
		} finally {
			System.setProperty("java.runtime.version", oldValue1);
			System.setProperty("java.version", oldValue2);
		}
	}

	public void testGetRuntimeVersionMatch() {
		String oldValue = System.setProperty("java.runtime.version", "1.6.0_26");
		try {
			assertFalse(new VersionRange("[0.0.0,1.6.0.25]").isIncluded(CoreUtil.getRuntimeVersion()));
			assertTrue(new VersionRange("[0.0.0,1.6.0.26]").isIncluded(CoreUtil.getRuntimeVersion()));
		} finally {
			System.setProperty("java.runtime.version", oldValue);
		}
	}

	public void testAsFileName() {
		assertEquals("abc", CoreUtil.asFileName("abc"));
		assertEquals("a.b.c", CoreUtil.asFileName("a.b.c"));
		assertEquals("", CoreUtil.asFileName(""));
	}

	public void testAsFileNameSpaces() {
		assertEquals("%20%20", CoreUtil.asFileName("  "));
		assertEquals(".%20", CoreUtil.asFileName(". "));
	}

	public void testAsFileNamePercent() {
		assertEquals("%25abc", CoreUtil.asFileName("%abc"));
		assertEquals("%2525abc", CoreUtil.asFileName("%25abc"));
	}

	public void testDecode() {
		assertEquals("abc", CoreUtil.encode("abc"));
		assertEquals("%2D_", CoreUtil.encode("-"));
		assertEquals("abc%2D_123", CoreUtil.encode("abc-123"));
		assertEquals("", CoreUtil.encode(""));
	}

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

	public void testEncode() {
		assertEquals("abc", CoreUtil.decode("abc"));
		assertEquals("-", CoreUtil.decode("%2D_"));
		assertEquals("abc-123", CoreUtil.decode("abc%2D_123"));
		assertEquals("abc-123", CoreUtil.decode(CoreUtil.decode(CoreUtil.encode(CoreUtil.encode("abc-123")))));
	}

}
