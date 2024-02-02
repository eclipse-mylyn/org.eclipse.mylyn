/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import static org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion.isVersion2112OrLater;
import static org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion.parseGerritVersion;

import org.junit.Test;
import org.osgi.framework.Version;

import junit.framework.TestCase;

public class GerritVersionTest extends TestCase {

	@Test
	public void testParse_null() throws Exception {
		try {
			parseGerritVersion(null);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testParse_empty() throws Exception {
		try {
			parseGerritVersion("");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testParse_invalid() throws Exception {
		try {
			parseGerritVersion("invalid");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().startsWith("Unrecognized version"));
		}
	}

	@Test
	public void testParse_21() throws Exception {
		Version v = parseGerritVersion("2.1");
		assertEquals(2, v.getMajor());
		assertEquals(1, v.getMinor());
		assertEquals(0, v.getMicro());
		assertTrue(v.getQualifier().isEmpty());
	}

	@Test
	public void testParse_254() throws Exception {
		Version v = parseGerritVersion("2.5.4");
		assertEquals(2, v.getMajor());
		assertEquals(5, v.getMinor());
		assertEquals(4, v.getMicro());
		assertTrue(v.getQualifier().isEmpty());
	}

	@Test
	public void testParse_26rc3() throws Exception {
		Version v = parseGerritVersion("2.6-rc3");
		assertEquals(2, v.getMajor());
		assertEquals(6, v.getMinor());
		assertEquals(0, v.getMicro());
		assertEquals("rc3", v.getQualifier());
	}

	@Test
	public void testParse_27rc2637g76c7890() throws Exception {
		Version v = parseGerritVersion("2.7-rc2-637-g76c7890");
		assertEquals(2, v.getMajor());
		assertEquals(7, v.getMinor());
		assertEquals(0, v.getMicro());
		assertEquals("rc2-637-g76c7890", v.getQualifier());
	}

	@Test
	public void testParse_V221NQT012() throws Exception {
		Version v = parseGerritVersion("V2.2.1-NQT-012");
		assertEquals(2, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(1, v.getMicro());
		assertEquals("NQT-012", v.getQualifier());
	}

	@Test
	public void testParse_123q() throws Exception {
		Version v = parseGerritVersion("1.2.3-q");
		assertEquals(1, v.getMajor());
		assertEquals(2, v.getMinor());
		assertEquals(3, v.getMicro());
		assertEquals("q", v.getQualifier());
	}

	@Test
	public void testParse_27xxx31() throws Exception {
		Version v = parseGerritVersion("2.7-xxx3.1");
		assertEquals(2, v.getMajor());
		assertEquals(7, v.getMinor());
		assertEquals(0, v.getMicro());
		assertEquals("xxx3", v.getQualifier()); // '.1' is lost as '.' cannot be part of a qualifier
	}

	@Test
	public void testParse_2861() throws Exception {
		Version v = parseGerritVersion("2.8.6.1");
		assertEquals(2, v.getMajor());
		assertEquals(8, v.getMinor());
		assertEquals(6, v.getMicro());
		assertEquals("1", v.getQualifier());
	}

	@Test
	public void testIsVersion2112OrLater() throws Exception {
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.9.9")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.9.9-q")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10-q")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10.0")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10.0-q")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10.0-rc2")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10.1")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10.1-q")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.10.1-rc2")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.11")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.11.0")));
		assertFalse(isVersion2112OrLater(parseGerritVersion("2.11-rc2")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.2")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.2-q")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.2")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.2-q")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.2-rc2")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.3")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.3-q")));
		assertTrue(isVersion2112OrLater(parseGerritVersion("2.11.3-rc2")));
	}

}
