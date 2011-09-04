/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.SearchRepository;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests of {@link SearchRepository}
 */
public class RepositoryTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

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
