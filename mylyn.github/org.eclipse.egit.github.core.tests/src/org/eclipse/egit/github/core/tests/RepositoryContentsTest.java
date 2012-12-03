/******************************************************************************
 *  Copyright (c) 2012 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.RepositoryContents;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryContents}
 */
public class RepositoryContentsTest {

	/**
	 * Test default state of contents
	 */
	@Test
	public void defaultState() {
		RepositoryContents contents = new RepositoryContents();
		assertNull(contents.getContent());
		assertNull(contents.getEncoding());
		assertNull(contents.getName());
		assertNull(contents.getPath());
		assertNull(contents.getSha());
		assertEquals(0, contents.getSize());
		assertNull(contents.getType());
	}

	/**
	 * Test updating contents fields
	 */
	@Test
	public void updateFields() {
		RepositoryContents contents = new RepositoryContents();
		assertEquals("abc", contents.setContent("abc").getContent());
		assertEquals("64", contents.setEncoding("64").getEncoding());
		assertEquals("file.txt", contents.setName("file.txt").getName());
		assertEquals("a/b", contents.setPath("a/b").getPath());
		assertEquals("abcdef", contents.setSha("abcdef").getSha());
		assertEquals(12345, contents.setSize(12345).getSize());
		assertEquals("dir", contents.setType("dir").getType());
	}
}
