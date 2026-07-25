/*******************************************************************************
 * *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *    See git history
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests of {@link GistService}
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("nls")
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
	@BeforeEach
	public void before() throws IOException {
		doReturn(response).when(gitHubClient).get(any(GitHubRequest.class));
		gistService = new GistService(gitHubClient);
	}

	/**
	 * Create service with null client
	 */
	@Test
	public void constructorNullArgument() {
		assertThrows(IllegalArgumentException.class, () -> new GistService(null));
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
	@Test
	public void getGistNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.getGist(null));
	}

	/**
	 * Get gist with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.getGist(""));
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
	@Test
	public void deleteGistNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.deleteGist(null));
	}

	/**
	 * Delete gist with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteGistEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.deleteGist(""));
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
	@Test
	public void starGistNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.starGist(null));
	}

	/**
	 * Star gist with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void starGistEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.starGist(""));
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
	@Test
	public void unstarGistNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.unstarGist(null));
	}

	/**
	 * Unstar gist with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void unstarGistEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.unstarGist(""));
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
	@Test
	public void isStarredGistNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.isStarred(null));
	}

	/**
	 * Is gist starred with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void isStarredGistEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.isStarred(""));
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
	@Test
	public void forkGistNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.forkGist(null));
	}

	/**
	 * Fork gist with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void forkGistEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.forkGist(""));
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
	@Test
	public void editGistCommentNullComment() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.editComment(null));
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
		assertEquals(Utils.page("/gists/starred"), iterator.getRequest().generateUri());
	}

	/**
	 * Get gists for null login name
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistsNullUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.getGists(null));
	}

	/**
	 * Get gists for empty login name
	 *
	 * @throws IOException
	 */
	@Test
	public void getGistsEmptyUser() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.getGists(""));
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
	@Test
	public void createGistNullGist() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.createGist(null));
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
	@Test
	public void updateGistNullGist() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.updateGist(null));
	}

	/**
	 * Update gist with null id
	 *
	 * @throws IOException
	 */
	@Test
	public void updateGistNullId() throws IOException {
		Gist gist = new Gist();
		gist.setId(null);
		assertThrows(IllegalArgumentException.class, () -> gistService.updateGist(gist));
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
	@Test
	public void createCommentNullGistId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.createComment(null, "not null"));
	}

	/**
	 * Create null comment
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommentNullComment() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.createComment("not null", null));
	}

	/**
	 * Create valid comment
	 *
	 * @throws IOException
	 */
	@Test
	public void createCommentOK() throws IOException {
		gistService.createComment("1", "test_comment");

		Map<String, String> params = new HashMap<>(1, 1);
		params.put(IssueService.FIELD_BODY, "test_comment");
		verify(gitHubClient).post("/gists/1/comments", params, Comment.class);
	}

	/**
	 * Get comments for null gist id
	 *
	 * @throws IOException
	 */
	@Test
	public void getCommentsNullGistId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.getComments(null));
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
	@Test
	public void pageUserGistsNullId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.pageGists(null));
	}

	/**
	 * Page gists with empty id
	 *
	 * @throws IOException
	 */
	@Test
	public void pageUserGistsEmptyId() throws IOException {
		assertThrows(IllegalArgumentException.class, () -> gistService.pageGists(""));
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
		assertEquals(Utils.page("/users/user/gists"), iterator.getRequest().generateUri());
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
