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

import org.eclipse.egit.github.core.GistFile;
import org.junit.Test;

/**
 * Unit tests of {@link GistFile}
 */
public class GistFileTest {

	/**
	 * Test default state of gist file
	 */
	@Test
	public void defaultState() {
		GistFile file = new GistFile();
		assertEquals(0, file.getSize());
		assertNull(file.getContent());
		assertNull(file.getRawUrl());
		assertNull(file.getFilename());
	}

	/**
	 * Test updating gist file fields
	 */
	@Test
	public void updateFields() {
		GistFile file = new GistFile();
		assertEquals(100, file.setSize(100).getSize());
		assertEquals("content", file.setContent("content").getContent());
		assertEquals("rawUrl", file.setRawUrl("rawUrl").getRawUrl());
		assertEquals("name", file.setFilename("name").getFilename());
	}
}
