/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client;

import static org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion.isVersion24x;
import static org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion.isVersion26OrLater;
import static org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion.isVersion29OrLater;
import static org.eclipse.mylyn.internal.gerrit.core.client.GerritVersion.parseGerritVersion;
import junit.framework.TestCase;

import org.junit.Test;
import org.osgi.framework.Version;

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
	public void testIsVersion26OrLater() throws Exception {
		assertFalse(isVersion26OrLater(parseGerritVersion("2.5.9")));
		assertFalse(isVersion26OrLater(parseGerritVersion("2.5.9-q")));
		assertTrue(isVersion26OrLater(parseGerritVersion("2.6")));
		assertTrue(isVersion26OrLater(parseGerritVersion("2.6-q")));
		assertTrue(isVersion26OrLater(parseGerritVersion("2.6.0")));
		assertTrue(isVersion26OrLater(parseGerritVersion("2.6.0-q")));
		assertTrue(isVersion26OrLater(parseGerritVersion("2.6.1")));
		assertTrue(isVersion26OrLater(parseGerritVersion("2.6.1-q")));
	}

	@Test
	public void testIsVersion29OrLater() throws Exception {
		assertFalse(isVersion29OrLater(parseGerritVersion("2.8.9")));
		assertFalse(isVersion29OrLater(parseGerritVersion("2.8.9-q")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9-q")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9.0")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9.0-q")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9.0-rc2")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9.1")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9.1-q")));
		assertTrue(isVersion29OrLater(parseGerritVersion("2.9.1-rc2")));
	}

	@Test
	public void testIsVersion24x() throws Exception {
		assertFalse(isVersion24x(parseGerritVersion("2.3.9")));
		assertFalse(isVersion24x(parseGerritVersion("2.3.9-q")));
		assertTrue(isVersion24x(parseGerritVersion("2.4")));
		assertTrue(isVersion24x(parseGerritVersion("2.4-q")));
		assertTrue(isVersion24x(parseGerritVersion("2.4.0")));
		assertTrue(isVersion24x(parseGerritVersion("2.4.0-q")));
		assertTrue(isVersion24x(parseGerritVersion("2.4.1")));
		assertTrue(isVersion24x(parseGerritVersion("2.4.1-q")));
		assertFalse(isVersion24x(parseGerritVersion("2.5")));
		assertFalse(isVersion24x(parseGerritVersion("2.5-q")));
		assertFalse(isVersion24x(parseGerritVersion("2.5.0")));
		assertFalse(isVersion24x(parseGerritVersion("2.5.0-q")));
	}
}
