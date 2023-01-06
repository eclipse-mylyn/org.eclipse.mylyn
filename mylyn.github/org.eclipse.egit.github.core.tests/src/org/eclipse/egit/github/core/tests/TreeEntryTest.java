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

import org.eclipse.egit.github.core.TreeEntry;
import org.junit.Test;

/**
 * Unit tests of {@link TreeEntry}
 */
public class TreeEntryTest {

	/**
	 * Test default state of tree entry
	 */
	@Test
	public void defaultState() {
		TreeEntry entry = new TreeEntry();
		assertNull(entry.getMode());
		assertNull(entry.getPath());
		assertNull(entry.getSha());
		assertEquals(0, entry.getSize());
		assertNull(entry.getType());
		assertNull(entry.getUrl());
	}

	/**
	 * Test updating tree entry fields
	 */
	@Test
	public void updateFields() {
		TreeEntry entry = new TreeEntry();
		assertEquals("rw", entry.setMode("rw").getMode());
		assertEquals("file1.txt", entry.setPath("file1.txt").getPath());
		assertEquals("0ab", entry.setSha("0ab").getSha());
		assertEquals(400, entry.setSize(400).getSize());
		assertEquals("blob", entry.setType("blob").getType());
		assertEquals("url", entry.setUrl("url").getUrl());
	}
}
