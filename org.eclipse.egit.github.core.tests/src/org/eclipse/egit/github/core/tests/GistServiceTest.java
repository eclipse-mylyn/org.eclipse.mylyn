/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.IssueService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link GistService}
 */
@RunWith(MockitoJUnitRunner.class)
public class GistServiceTest {

	@Mock
	private GitHubClient gitHubClient;

	@Mock
	private GitHubResponse response;

	private GistService gistService;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		gistService = new GistService(gitHubClient);
	}

	/**
	 * Create service with null client
	 */
	@Test(expected = IllegalArgumentException.class)
	public void constructorNullArgument() {
		new GistService(null);
	}

	/**
	 * Create default service
	 */
	@Test
	public void defaultContructor() {
		assertNotNull(new GistService().getClient());
	}

	/**
	 * Get gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getGistNullId() throws IOException {
		gistService.getGist(null);
	}

	/**
	 * Get gist with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getGistEmptyId() throws IOException {
		gistService.getGist("");
	}

	/**
	 * Get gist with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void getGist() throws IOException {
		gistService.getGist("1");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/gists/1");
		verify(gitHubClient).get(request);
	}

	/**
	 * Delete gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteGistNullId() throws IOException {
		gistService.deleteGist(null);
	}

	/**
	 * Delete gist with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void deleteGistEmptyId() throws IOException {
		gistService.deleteGist("");
	}

	/**
	 * Delete gist with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteGist() throws IOException {
		gistService.deleteGist("1");
		verify(gitHubClient).delete("/gists/1");
	}

	/**
	 * Star gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void starGistNullId() throws IOException {
		gistService.starGist(null);
	}

	/**
	 * Star gist with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void starGistEmptyId() throws IOException {
		gistService.starGist("");
	}

	/**
	 * Star gist with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void starGist() throws IOException {
		gistService.starGist("1");
		verify(gitHubClient).put("/gists/1/star");
	}

	/**
	 * Unstar gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void unstarGistNullId() throws IOException {
		gistService.unstarGist(null);
	}

	/**
	 * Unstar gist with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void unstarGistEmptyId() throws IOException {
		gistService.unstarGist("");
	}

	/**
	 * Unstar gist with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void unstarGist() throws IOException {
		gistService.unstarGist("1");
		verify(gitHubClient).delete("/gists/1/star");
	}

	/**
	 * Is gist starred with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void isStarredGistNullId() throws IOException {
		gistService.isStarred(null);
	}

	/**
	 * Is gist starred with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void isStarredGistEmptyId() throws IOException {
		gistService.isStarred("");
	}

	/**
	 * Is gist starred with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void isStarredGist() throws IOException {
		gistService.isStarred("1");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/gists/1/star");
		verify(gitHubClient).get(request);
	}

	/**
	 * Fork gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void forkGistNullId() throws IOException {
		gistService.forkGist(null);
	}

	/**
	 * Fork gist with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void forkGistEmptyId() throws IOException {
		gistService.forkGist("");
	}

	/**
	 * Fork gist with valid id
	 *
	 * @throws IOException
	 */
	@Test
	public void forkGist() throws IOException {
		gistService.forkGist("1");
		verify(gitHubClient).post("/gists/1/fork", null, Gist.class);
	}

	/**
	 * Edit comment with null comment
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editGistCommentNullComment() throws IOException {
		gistService.editComment(null);
	}

	/**
	 * Edit comment
	 *
	 * @throws IOException
	 */
	@Test
	public void editGistComment() throws IOException {
		Comment comment = new Comment();
		comment.setId(48).setBody("new body");
		gistService.editComment(comment);
		verify(gitHubClient).post("/gists/comments/48", comment, Comment.class);
	}

	/**
	 * Get gist comment
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistComment() throws IOException {
		gistService.getComment(59);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/gists/comments/59");
		verify(gitHubClient).get(request);
	}

	/**
	 * Delete gist comment
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteGistComment() throws IOException {
		gistService.deleteComment(1234);
		verify(gitHubClient).delete("/gists/comments/1234");
	}

	/**
	 * Get starred gists
	 *
	 * @throws IOException
	 */
	@Test
	public void getStarredGists() throws IOException {
		List<Gist> starred = gistService.getStarredGists();
		assertNotNull(starred);
		assertEquals(0, starred.size());
	}

	/**
	 * Get iterator for starred gists
	 *
	 * @throws IOException
	 */
	@Test
	public void pageStarredGists() throws IOException {
		PageIterator<Gist> iterator = gistService.pageStarredGists();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/gists/starred"), iterator.getRequest()
				.generateUri());
	}

	/**
	 * Get gists for null login name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getGistsNullUser() throws IOException {
		gistService.getGists(null);
	}

	/**
	 * Get gists for empty login name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getGistsEmptyUser() throws IOException {
		gistService.getGists("");
	}

	/**
	 * Get gists for valid login name
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistsOK() throws IOException {
		gistService.getGists("test_user");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/users/test_user/gists"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Create gist with null model
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createGistNullGist() throws IOException {
		gistService.createGist(null);
	}

	/**
	 * Get gist with null user
	 *
	 * @throws IOException
	 */
	@Test
	public void createGistNullOwner() throws IOException {
		Gist gist = new Gist();
		gist.setOwner(null);
		gistService.createGist(gist);
		verify(gitHubClient).post("/gists", gist, Gist.class);
	}

	/**
	 * Update null gist
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateGistNullGist() throws IOException {
		gistService.updateGist(null);
	}

	/**
	 * Update gist with null id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void updateGistNullId() throws IOException {
		Gist gist = new Gist();
		gist.setId(null);
		gistService.updateGist(gist);
	}

	/**
	 * Update valid gist
	 *
	 * @throws IOException
	 */
	@Test
	public void updateGistOK() throws IOException {
		Gist gist = new Gist();
		gist.setId("123");
		gistService.updateGist(gist);
		verify(gitHubClient).post("/gists/123", gist, Gist.class);
	}

	/**
	 * Create comment for with null gist id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullGistId() throws IOException {
		gistService.createComment(null, "not null");
	}

	/**
	 * Create null comment
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createCommentNullComment() throws IOException {
		gistService.createComment("not null", null);
	}

	/**
	 * Create valid comment
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommentOK() throws IOException {
		gistService.createComment("1", "test_comment");

		Map<String, String> params = new HashMap<String, String>(1, 1);
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post("/gists/1/comments", params, Comment.class);
	}

	/**
	 * Get comments for null gist id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getCommentsNullGistId() throws IOException {
		gistService.getComments(null);
	}

	/**
	 * Get comment with valid gist id
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommentsOK() throws IOException {
		gistService.getComments("1");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/gists/1/comments"));
		verify(gitHubClient).get(request);
	}

	/**
	 * Page gists with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void pageUserGistsNullId() throws IOException {
		gistService.pageGists(null);
	}

	/**
	 * Page gists with empty id
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void pageUserGistsEmptyId() throws IOException {
		gistService.pageGists("");
	}

	/**
	 * Page gists with valid user
	 *
	 * @throws IOException
	 */
	@Test
	public void pageUserGists() throws IOException {
		PageIterator<Gist> iterator = gistService.pageGists("user");
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		assertEquals(Utils.page("/users/user/gists"), iterator.getRequest()
				.generateUri());
	}

	/**
	 * Page public gists
	 *
	 * @throws IOException
	 */
	@Test
	public void pagePublicGists() throws IOException {
		PageIterator<Gist> iterator = gistService.pagePublicGists();
		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
	}
}
