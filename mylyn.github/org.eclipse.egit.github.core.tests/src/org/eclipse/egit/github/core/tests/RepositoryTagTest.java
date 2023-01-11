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

import org.eclipse.egit.github.core.RepositoryTag;
import org.eclipse.egit.github.core.TypedResource;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryTag}
 */
public class RepositoryTagTest {

	/**
	 * Test default state of tag
	 */
	@Test
	public void defaultState() {
		RepositoryTag tag = new RepositoryTag();
		assertNull(tag.getCommit());
		assertNull(tag.getName());
		assertNull(tag.getTarballUrl());
		assertNull(tag.getZipballUrl());
	}

	/**
	 * Test updating tag fields
	 */
	@Test
	public void updateFields() {
		RepositoryTag tag = new RepositoryTag();
		TypedResource commit = new TypedResource();
		commit.setUrl("a").setSha("1");
		assertEquals(commit, tag.setCommit(commit).getCommit());
		assertEquals("t1", tag.setName("t1").getName());
		assertEquals("tar", tag.setTarballUrl("tar").getTarballUrl());
		assertEquals("zip", tag.setZipballUrl("zip").getZipballUrl());
	}
}
