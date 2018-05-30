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

import org.eclipse.egit.github.core.RepositoryBranch;
import org.eclipse.egit.github.core.TypedResource;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryBranch}
 */
public class RepositoryBranchTest {

	/**
	 * Test default state of branch
	 */
	@Test
	public void defaultState() {
		RepositoryBranch branch = new RepositoryBranch();
		assertNull(branch.getCommit());
		assertNull(branch.getName());
	}

	/**
	 * Test updating branch fields
	 */
	@Test
	public void updateFields() {
		RepositoryBranch branch = new RepositoryBranch();
		TypedResource commit = new TypedResource();
		commit.setUrl("a").setSha("1");
		assertEquals(commit, branch.setCommit(commit).getCommit());
		assertEquals("b1", branch.setName("b1").getName());
	}
}
