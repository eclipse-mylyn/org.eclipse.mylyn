/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.GistService;
import org.junit.Test;

/**
 * @author Kevin Sawicki (kevin@github.com)
 */
public class GistTest extends LiveTest {

	/**
	 * Test list a user's gists
	 * 
	 * @throws IOException
	 */
	@Test
	public void listGists() throws IOException {
		checkUser();

		GistService service = new GistService(client);
		Collection<Gist> gists = service.getGists(client.getUser());
		assertNotNull(gists);
		assertFalse(gists.isEmpty());
		for (Gist gist : gists) {
			assertNotNull(gist);
			assertNotNull(gist.getCreatedAt());
			assertNotNull(gist.getId());
			assertNotNull(gist.getFiles());
			for (GistFile file : gist.getFiles().values()) {
				assertNotNull(file);
			}
			List<Comment> comments = service.getComments(gist.getId());
			assertNotNull(comments);
			for (Comment comment : comments) {
				assertNotNull(comment);
				assertNotNull(comment.getUrl());
				assertNotNull(comment.getCreatedAt());
				assertNotNull(comment.getUpdatedAt());
				assertNotNull(comment.getUser());
			}
		}
	}

	/**
	 * Test creating and deleting a Gist
	 * 
	 * @throws IOException
	 */
	@Test
	public void createDeleteGist() throws IOException {
		checkUser();

		Gist gist = new Gist().setDescription("testing");
		gist.setPublic(false);
		GistFile file = new GistFile().setContent("content");
		gist.setFiles(Collections.singletonMap("foo.txt", file));
		GistService service = new GistService(client);
		Gist created = service.createGist(gist);
		assertNotNull(created);
		assertNotNull(created.getId());
		service.deleteGist(created.getId());
	}

	/**
	 * Test creating and deleting a Gist comment
	 * 
	 * @throws IOException
	 */
	@Test
	public void createDeleteGistComment() throws IOException {
		checkUser();

		Gist gist = new Gist().setDescription("testing");
		gist.setPublic(false);
		GistFile file = new GistFile().setContent("content");
		gist.setFiles(Collections.singletonMap("foo.txt", file));
		GistService service = new GistService(client);
		Gist created = service.createGist(gist);
		assertNotNull(created);
		assertNotNull(created.getId());
		try {
			Comment comment = service.createComment(created.getId(),
					"test comment");
			assertNotNull(comment);
			service.deleteComment(comment.getId());
		} finally {
			service.deleteGist(created.getId());
		}
	}

	/**
	 * Test starring, unstarring, and checking if a gist if starred
	 * 
	 * @throws IOException
	 */
	@Test
	public void starUnstarGist() throws IOException {
		checkUser();

		Gist gist = new Gist().setDescription("star test");
		gist.setPublic(false);
		GistFile file = new GistFile().setContent("content");
		gist.setFiles(Collections.singletonMap("foo.txt", file));
		GistService service = new GistService(client);
		Gist created = service.createGist(gist);
		assertNotNull(created);
		String id = created.getId();
		assertNotNull(id);
		try {
			List<Gist> starred = service.getStarredGists();
			assertNotNull(starred);
			for (Gist star : starred)
				assertFalse(id.equals(star.getId()));
			assertFalse(service.isStarred(id));
			service.starGist(id);
			assertTrue(service.isStarred(id));
			starred = service.getStarredGists();
			assertNotNull(starred);
			boolean gistStarred = false;
			for (Gist star : starred) {
				gistStarred = id.equals(star.getId());
				if (gistStarred)
					break;
			}
			assertTrue(gistStarred);
			service.unstarGist(id);
			assertFalse(service.isStarred(id));
		} finally {
			service.deleteGist(id);
		}
	}

	/**
	 * Test paging through public gists
	 * 
	 * @throws Exception
	 */
	@Test
	public void twoPublicGistPages() throws Exception {
		GistService service = new GistService(client);
		PageIterator<Gist> pages = service.pagePublicGists(10);
		assertNotNull(pages);
		assertTrue(pages.hasNext());
		Collection<Gist> gists = pages.next();
		assertNotNull(gists);
		assertTrue(gists.size() > 0);
		Set<String> ids = new HashSet<String>();
		for (Gist gist : gists) {
			assertNotNull(gist);
			assertNotNull(gist.getId());
			assertFalse(ids.contains(gist.getId()));
			ids.add(gist.getId());
		}
		assertTrue(pages.hasNext());
		gists = pages.next();
		assertNotNull(gists);
		assertTrue(gists.size() > 0);
		for (Gist gist : gists) {
			assertNotNull(gist);
			assertNotNull(gist.getId());
			assertFalse(ids.contains(gist.getId()));
			ids.add(gist.getId());
		}
	}

	/**
	 * Test forking a gist
	 * 
	 * @throws Exception
	 */
	@Test
	public void forkGist() throws Exception {
		checkUser();

		GistService service = new GistService(client);
		Gist forked = service.forkGist("1");
		assertNotNull(forked);
		assertNotNull(forked.getId());
		assertFalse("1".equals(forked.getId()));
		assertNotNull(forked.getDescription());
		service.deleteGist(forked.getId());
	}
}
