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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.egit.github.core.RepositoryId;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link RepositoryId}
 */
@SuppressWarnings("nls")
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
	 * @throws URISyntaxException
	 */
	@Test
	public void createFromInvalidUrl1() throws MalformedURLException, URISyntaxException {
		assertNull(RepositoryId.createFromUrl(new URI("http://github.com").toURL()));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	@Test
	public void createFromInvalidUrl2() throws MalformedURLException, URISyntaxException {
		assertNull(RepositoryId.createFromUrl(new URI("http://github.com/").toURL()));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	@Test
	public void createFromInvalidUrl3() throws MalformedURLException, URISyntaxException {
		assertNull(RepositoryId.createFromUrl(new URI("http://github.com/a").toURL()));
	}

	/**
	 * From from invalid URL
	 *
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	@Test
	public void createFromInvalidUrl4() throws MalformedURLException, URISyntaxException {
		assertNull(RepositoryId.createFromUrl(new URI("http://github.com/a/").toURL()));
	}

	/**
	 * From from valid URL
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void createFromStringUrl() throws MalformedURLException {
		RepositoryId id = RepositoryId.createFromUrl("http://github.com/user/project");
		assertNotNull(id);
		assertEquals("user", id.getOwner());
		assertEquals("project", id.getName());
	}

	/**
	 * From from valid URL
	 *
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	@Test
	public void createFromUrl() throws MalformedURLException, URISyntaxException {
		RepositoryId id = RepositoryId.createFromUrl(new URI(
				"http://github.com/user/project").toURL());
		assertNotNull(id);
		assertEquals("user", id.getOwner());
		assertEquals("project", id.getName());
	}

	/**
	 * Create with null owner
	 */
	@Test
	public void createNullOwner() {
		assertThrows(IllegalArgumentException.class, () -> RepositoryId.create(null, "my proj"));
	}

	/**
	 * Create with empty owner
	 */
	@Test
	public void createEmptyOwner() {
		assertThrows(IllegalArgumentException.class, () -> RepositoryId.create("", "myproj"));
	}

	/**
	 * Create with null name
	 */
	@Test
	public void createNullName() {
		assertThrows(IllegalArgumentException.class, () -> RepositoryId.create("user", null));
	}

	/**
	 * Create with empty name
	 */
	@Test
	public void createEmptyName() {
		assertThrows(IllegalArgumentException.class, () -> RepositoryId.create("user", ""));
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
