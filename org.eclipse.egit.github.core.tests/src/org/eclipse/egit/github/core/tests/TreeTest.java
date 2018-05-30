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

import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.junit.Test;

/**
 * Unit tests of {@link Tree}
 */
public class TreeTest {

	/**
	 * Test default state of tree
	 */
	@Test
	public void defaultState() {
		Tree tree = new Tree();
		assertNull(tree.getSha());
		assertNull(tree.getTree());
		assertNull(tree.getUrl());
	}

	/**
	 * Test updating tree fields
	 */
	@Test
	public void updateFields() {
		Tree tree = new Tree();
		assertEquals("1234", tree.setSha("1234").getSha());
		List<TreeEntry> entries = Collections.singletonList(new TreeEntry());
		assertEquals(entries, tree.setTree(entries).getTree());
		assertEquals("url", tree.setUrl("url").getUrl());
	}
}
