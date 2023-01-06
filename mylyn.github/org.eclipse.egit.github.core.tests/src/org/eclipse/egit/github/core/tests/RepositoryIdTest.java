/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.egit.github.core.RepositoryId;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryId}
 */
public class RepositoryIdTest {

	/**
	 * Create with null id string
	 */
	@Test
	public void createFromNullString() {
		assertNull(RepositoryId.createFromId(null));
	}

	/**
	 * Create with empty id string
	 */
	@Test
	public void createFromEmptyString() {
		assertNull(RepositoryId.createFromId(""));
	}

	/**
	 * Create from valid id string
	 */
	@Test
	public void createFromString() {
		RepositoryId id = RepositoryId.createFromId("a/b");
		assertNotNull(id);
		assertEquals("a", id.getOwner());
		assertEquals("b", id.getName());
	}

	/**
	 * Create from null URL
	 */
	@Test
	public void createFromNullUrl() {
		assertNull(RepositoryId.createFromUrl((URL) null));
	}

	/**
	 * Create from null URL
	 */
	@Test
	public void createFromNullStringUrl() {
		assertNull(RepositoryId.createFromUrl((String) null));
	}

	/**
	 * Create from malformed string URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromMalformedStringUrl() throws MalformedURLException {
		assertNull(RepositoryId.createFromUrl("http://:http//"));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromInvalidUrl1() throws MalformedURLException {
		assertNull(RepositoryId.createFromUrl(new URL("http://github.com")));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromInvalidUrl2() throws MalformedURLException {
		assertNull(RepositoryId.createFromUrl(new URL("http://github.com/")));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromInvalidUrl3() throws MalformedURLException {
		assertNull(RepositoryId.createFromUrl(new URL("http://github.com/a")));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromInvalidUrl4() throws MalformedURLException {
		assertNull(RepositoryId.createFromUrl(new URL("http://github.com/a/")));
	}

	/**
	 * From from valid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromStringUrl() throws MalformedURLException {
		RepositoryId id = RepositoryId
				.createFromUrl("http://github.com/user/project");
		assertNotNull(id);
		assertEquals("user", id.getOwner());
		assertEquals("project", id.getName());
	}

	/**
	 * From from valid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromUrl() throws MalformedURLException {
		RepositoryId id = RepositoryId.createFromUrl(new URL(
				"http://github.com/user/project"));
		assertNotNull(id);
		assertEquals("user", id.getOwner());
		assertEquals("project", id.getName());
	}

	/**
	 * Create with null owner
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNullOwner() {
		RepositoryId.create(null, "my proj");
	}

	/**
	 * Create with empty owner
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createEmptyOwner() {
		RepositoryId.create("", "myproj");
	}

	/**
	 * Create with null name
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNullName() {
		RepositoryId.create("user", null);
	}

	/**
	 * Create with empty name
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createEmptyName() {
		RepositoryId.create("user", "");
	}

	/**
	 * Create from static method
	 */
	@Test
	public void create() {
		RepositoryId id = RepositoryId.create("own", "project");
		assertEquals("own", id.getOwner());
		assertEquals("project", id.getName());
	}

	/**
	 * Verify repository id equality
	 */
	@Test
	public void equality() {
		RepositoryId id1 = new RepositoryId("auser", "bproject");
		RepositoryId id2 = new RepositoryId("auser", "bproject");
		RepositoryId id3 = new RepositoryId("auser", "bproject2");
		assertTrue(id1.equals(id1));
		assertTrue(id1.equals(id2));
		assertFalse(id1.equals(id3));
		Object string = "content";
		assertFalse(id1.equals(string));
		assertEquals(id1.hashCode(), id2.hashCode());
		assertEquals(id1.toString(), id2.toString());
	}
}
