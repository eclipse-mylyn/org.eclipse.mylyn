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
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.event.EventRepository;
import org.junit.Test;

/**
 * Unit tests of {@link EventRepository}
 */
public class EventRepositoryTest {

	/**
	 * Test default state of event repository
	 */
	@Test
	public void defaultState() {
		EventRepository repo = new EventRepository();
		assertEquals(0, repo.getId());
		assertNull(repo.getName());
		assertNull(repo.getUrl());
	}

	/**
	 * Test updating event repository fields
	 */
	@Test
	public void updateFields() {
		EventRepository repo = new EventRepository();
		assertEquals(4, repo.setId(4).getId());
		assertEquals("repo1", repo.setName("repo1").getName());
		assertEquals("url", repo.setUrl("url").getUrl());
	}
}
