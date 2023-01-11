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

import org.eclipse.egit.github.core.Key;
import org.junit.Test;

/**
 * Unit tests of {@link Key}
 */
public class KeyTest {

	/**
	 * Test default state of key
	 */
	@Test
	public void defaultState() {
		Key key = new Key();
		assertEquals(0, key.getId());
		assertNull(key.getKey());
		assertNull(key.getTitle());
		assertNull(key.getUrl());
	}

	/**
	 * Test updating key fields
	 */
	@Test
	public void updateFields() {
		Key key = new Key();
		assertEquals(75, key.setId(75).getId());
		assertEquals("a key", key.setKey("a key").getKey());
		assertEquals("b title", key.setTitle("b title").getTitle());
		assertEquals("/a/b", key.setUrl("/a/b").getUrl());
	}
}
