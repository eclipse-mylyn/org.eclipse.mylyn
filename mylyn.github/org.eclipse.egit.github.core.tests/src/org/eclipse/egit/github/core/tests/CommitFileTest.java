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
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.egit.github.core.CommitFile;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link CommitFile}
 */
@SuppressWarnings("nls")
public class CommitFileTest {

	/**
	 * Test default state of commit file
	 */
	@Test
	public void defaultState() {
		CommitFile file = new CommitFile();
		assertEquals(0, file.getAdditions());
		assertEquals(0, file.getChanges());
		assertEquals(0, file.getDeletions());
		assertNull(file.getBlobUrl());
		assertNull(file.getFilename());
		assertNull(file.getPatch());
		assertNull(file.getRawUrl());
		assertNull(file.getSha());
		assertNull(file.getStatus());
	}

	/**
	 * Test updating commit file fields
	 */
	@Test
	public void updateFields() {
		CommitFile file = new CommitFile();
		assertEquals(123, file.setAdditions(123).getAdditions());
		assertEquals(456, file.setChanges(456).getChanges());
		assertEquals(789, file.setDeletions(789).getDeletions());
		assertEquals("blob url", file.setBlobUrl("blob url").getBlobUrl());
		assertEquals("file.txt", file.setFilename("file.txt").getFilename());
		assertEquals("file.patch", file.setPatch("file.patch").getPatch());
		assertEquals("raw url", file.setRawUrl("raw url").getRawUrl());
		assertEquals("aaaaa", file.setSha("aaaaa").getSha());
		assertEquals("add", file.setStatus("add").getStatus());
	}
}
