/******************************************************************************
 *  Copyright (c) 2012 GitHub Inc.
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

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryIssue}
 */
public class RepositoryIssueTest {

	/**
	 * Test default state of issue
	 */
	@Test
	public void defaultState() {
		RepositoryIssue issue = new RepositoryIssue();
		assertNull(issue.getRepository());
	}

	/**
	 * Test updating issue fields
	 */
	@Test
	public void updateFields() {
		RepositoryIssue issue = new RepositoryIssue();
		Repository repo = new Repository();
		assertEquals(repo, issue.setRepository(repo).getRepository());
	}
}
