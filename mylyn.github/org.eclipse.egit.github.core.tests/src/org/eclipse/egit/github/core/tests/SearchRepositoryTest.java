/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.SearchRepository;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests of {@link SearchRepository}
 */
public class SearchRepositoryTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

	/**
	 * Test default state of search repository
	 */
	@Test
	public void defaultState() {
		SearchRepository repo = new SearchRepository("a", "b");
		assertEquals("a/b", repo.generateId());
		assertNull(repo.getCreatedAt());
		assertNull(repo.getDescription());
		assertEquals(0, repo.getForks());
		assertNull(repo.getHomepage());
		assertEquals("a/b", repo.getId());
		assertNull(repo.getLanguage());
		assertEquals("b", repo.getName());
		assertEquals(0, repo.getOpenIssues());
		assertEquals("a", repo.getOwner());
		assertNull(repo.getPushedAt());
		assertEquals(0, repo.getSize());
		assertNull(repo.getUrl());
		assertEquals(0, repo.getWatchers());
		assertFalse(repo.isFork());
		assertFalse(repo.isHasDownloads());
		assertFalse(repo.isHasIssues());
		assertFalse(repo.isHasWiki());
		assertFalse(repo.isPrivate());
	}

	/**
	 * Verify search repository equality
	 */
	@Test
	public void equality() {
		SearchRepository repo1 = new SearchRepository("a", "b");
		SearchRepository repo2 = new SearchRepository("a", "c");
		SearchRepository repo3 = new SearchRepository("a", "b");
		assertTrue(repo1.equals(repo1));
		Object string = "repo1";
		assertFalse(repo1.equals(string));
		assertFalse(repo1.equals(repo2));
		assertTrue(repo1.equals(repo3));
		assertEquals(repo1.hashCode(), repo3.hashCode());
		assertEquals(repo1.toString(), repo3.toString());
	}

	/**
	 * Create with null owner
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNullOwner() {
		new SearchRepository(null, "name");
	}

	/**
	 * Create with empty owner
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createEmptyOwner() {
		new SearchRepository("", "name");
	}

	/**
	 * Create with null name
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNullName() {
		new SearchRepository("owner", null);
	}

	/**
	 * Create with empty name
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createEmptyName() {
		new SearchRepository("owner", "");
	}

	/**
	 * Verify repository with no generated id
	 */
	@Test
	public void noGeneratedId() {
		SearchRepository repo = gson.fromJson("{}", SearchRepository.class);
		assertNull(repo.generateId());
		repo = gson.fromJson("{name:\"\", owner:\"use\"}",
				SearchRepository.class);
		assertNull(repo.generateId());
		repo = gson.fromJson("{owner:\"\"}", SearchRepository.class);
		assertNull(repo.generateId());
	}

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		SearchRepository repository = gson.fromJson(
				"{createdAt : '2003-10-10'}", SearchRepository.class);
		repository.getCreatedAt().setTime(0);
		assertTrue(repository.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable pushed at date
	 */
	@Test
	public void getPushedAtReferenceMutableObject() {
		SearchRepository repository = gson.fromJson(
				"{pushedAt : '2003-10-10'}", SearchRepository.class);
		repository.getPushedAt().setTime(0);
		assertTrue(repository.getPushedAt().getTime() != 0);
	}
}
