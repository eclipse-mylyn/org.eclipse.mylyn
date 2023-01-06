/******************************************************************************
 *  Copyright (c) 2018 Frédéric Cilia
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Frédéric Cilia - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.RepositoryMergingResponse;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryMergingResponse}
 */
public class RepositoryMergingResponseTest {

	/**
	 * Test default state of response
	 */
	@Test
	public void defaultState() {
		RepositoryMergingResponse response = new RepositoryMergingResponse();
		assertNull(response.getSha());
		assertNull(response.getNodeId());
		assertNull(response.getCommit());
		assertNull(response.getUrl());
		assertNull(response.getHtmlUrl());
		assertNull(response.getCommentsUrl());
		assertNull(response.getAuthor());
		assertNull(response.getCommitter());
		assertNull(response.getParents());
	}

	/**
	 * Test updating response fields
	 */
	@Test
	public void updateFields() {
		RepositoryMergingResponse response = new RepositoryMergingResponse();
		assertEquals("sha", response.setSha("sha").getSha());
		assertEquals("nodeId", response.setNodeId("nodeId").getNodeId());
		User author = new User().setLogin("author");
		assertEquals(author, response.setAuthor(author).getAuthor());
		Commit gitCommit = new Commit().setSha("abc");
		assertEquals(gitCommit, response.setCommit(gitCommit).getCommit());
		User committer = new User().setLogin("committer");
		assertEquals(committer, response.setCommitter(committer).getCommitter());
		assertEquals("url", response.setUrl("url").getUrl());
		assertEquals("htmlUrl", response.setHtmlUrl("htmlUrl").getHtmlUrl());
		assertEquals("commentsUrl",
				response.setCommentsUrl("commentsUrl").getCommentsUrl());
		assertEquals(new ArrayList<Commit>(),
				response.setParents(new ArrayList<Commit>()).getParents());

	}
}
