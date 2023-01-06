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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.egit.github.core.Key;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests of {@link UserService}
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private GitHubClient client;

	@Mock
	private GitHubResponse response;

	private UserService service;

	/**
	 * Test case set up
	 *
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		doReturn(response).when(client).get(any(GitHubRequest.class));
		service = new UserService(client);
	}

	/**
	 * Create service using default constructor
	 */
	@Test
	public void constructor() {
		assertNotNull(new UserService().getClient());
	}

	/**
	 * Get user with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getUserNullName() throws IOException {
		service.getUser(null);
	}

	/**
	 * Get user with empty name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getUserEmptyName() throws IOException {
		service.getUser("");
	}

	/**
	 * Get current user
	 *
	 * @throws IOException
	 */
	@Test
	public void getCurrentUser() throws IOException {
		service.getUser();
		GitHubRequest request = new GitHubRequest();
		request.setUri("/user");
		verify(client).get(request);
	}

	/**
	 * Get user
	 *
	 * @throws IOException
	 */
	@Test
	public void getUser() throws IOException {
		service.getUser("beauser");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/users/beauser");
		verify(client).get(request);
	}

	/**
	 * Edit user with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editUserNullUser() throws IOException {
		service.editUser(null);
	}

	/**
	 * Edit user
	 *
	 * @throws IOException
	 */
	@Test
	public void editUser() throws IOException {
		User user = new User().setName("user1").setBlog("blog");
		service.editUser(user);
		verify(client).post("/user", user, User.class);
	}

	/**
	 * Get current followers
	 *
	 * @throws IOException
	 */
	@Test
	public void getCurrentFollowers() throws IOException {
		service.getFollowers();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/followers"));
		verify(client).get(request);
	}

	/**
	 * Get followers with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getFollowersNullName() throws IOException {
		service.getFollowers(null);
	}

	/**
	 * Get followers with empty name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getFollowersEmptyName() throws IOException {
		service.getFollowers("");
	}

	/**
	 * Get followers
	 *
	 * @throws IOException
	 */
	@Test
	public void getFollowers() throws IOException {
		service.getFollowers("beauser");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/users/beauser/followers"));
		verify(client).get(request);
	}

	/**
	 * Get current following
	 *
	 * @throws IOException
	 */
	@Test
	public void getCurrentFollowing() throws IOException {
		service.getFollowing();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/following"));
		verify(client).get(request);
	}

	/**
	 * Get following with null name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getFollowingNullName() throws IOException {
		service.getFollowing(null);
	}

	/**
	 * Get following with empty name
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getFollowingEmptyName() throws IOException {
		service.getFollowing("");
	}

	/**
	 * Get following
	 *
	 * @throws IOException
	 */
	@Test
	public void getFollowing() throws IOException {
		service.getFollowing("beauser");
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/users/beauser/following"));
		verify(client).get(request);
	}

	/**
	 * Is following with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void isFollowingNullUser() throws IOException {
		service.isFollowing(null);
	}

	/**
	 * Is following with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void isFollowingEmptyUser() throws IOException {
		service.isFollowing("");
	}

	/**
	 * Is following
	 *
	 * @throws IOException
	 */
	@Test
	public void isFollowing() throws IOException {
		service.isFollowing("beauser");
		GitHubRequest request = new GitHubRequest();
		request.setUri("/user/following/beauser");
		verify(client).get(request);
	}

	/**
	 * Follow with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void followNullUser() throws IOException {
		service.follow(null);
	}

	/**
	 * Follow with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void followEmptyUser() throws IOException {
		service.follow("");
	}

	/**
	 * Follow
	 *
	 * @throws IOException
	 */
	@Test
	public void follow() throws IOException {
		service.follow("abc");
		verify(client).put("/user/following/abc");
	}

	/**
	 * Unfollow with null user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void unfollowNullUser() throws IOException {
		service.unfollow(null);
	}

	/**
	 * Unfollow with empty user
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void unfollowEmptyUser() throws IOException {
		service.unfollow("");
	}

	/**
	 * Unfollow
	 *
	 * @throws IOException
	 */
	@Test
	public void unfollow() throws IOException {
		service.unfollow("abc");
		verify(client).delete("/user/following/abc");
	}

	/**
	 * Get emails
	 *
	 * @throws IOException
	 */
	@Test
	public void getEmails() throws IOException {
		service.getEmails();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/emails"));
		verify(client).get(request);
	}

	/**
	 * Add email with null emails
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addEmailNullEmails() throws IOException {
		service.addEmail((String[]) null);
	}

	/**
	 * Add email with empty emails
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addEmailEmptyEmails() throws IOException {
		service.addEmail(new String[0]);
	}

	/**
	 * Add email
	 *
	 * @throws IOException
	 */
	@Test
	public void addEmail() throws IOException {
		String[] emails = new String[] { "t@es.t" };
		service.addEmail(emails);
		verify(client).post("/user/emails", emails, null);
	}

	/**
	 * Remove email with null emails
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void removeEmailNullEmails() throws IOException {
		service.removeEmail((String[]) null);
	}

	/**
	 * Remove email with empty emails
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void removeEmailEmptyEmails() throws IOException {
		service.removeEmail(new String[0]);
	}

	/**
	 * Remove email
	 *
	 * @throws IOException
	 */
	@Test
	public void removeEmail() throws IOException {
		String[] emails = new String[] { "t@es.t" };
		service.removeEmail(emails);
		verify(client).delete("/user/emails", emails);
	}

	/**
	 * Get keys
	 *
	 * @throws IOException
	 */
	@Test
	public void getKeys() throws IOException {
		service.getKeys();
		GitHubRequest request = new GitHubRequest();
		request.setUri(Utils.page("/user/keys"));
		verify(client).get(request);
	}

	/**
	 * Get key
	 *
	 * @throws IOException
	 */
	@Test
	public void getKey() throws IOException {
		service.getKey(4);
		GitHubRequest request = new GitHubRequest();
		request.setUri("/user/keys/4");
		verify(client).get(request);
	}

	/**
	 * Create key
	 *
	 * @throws IOException
	 */
	@Test
	public void createKey() throws IOException {
		Key key = new Key().setId(5);
		service.createKey(key);
		verify(client).post("/user/keys", key, Key.class);
	}

	/**
	 * Edit key with null key
	 *
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void editKeyNullKey() throws IOException {
		service.editKey(null);
	}

	/**
	 * Edit key
	 *
	 * @throws IOException
	 */
	@Test
	public void editKey() throws IOException {
		Key key = new Key().setId(12);
		service.editKey(key);
		verify(client).post("/user/keys/12", key, Key.class);
	}

	/**
	 * Delete key
	 *
	 * @throws IOException
	 */
	@Test
	public void deleteKey() throws IOException {
		service.deleteKey(6);
		verify(client).delete("/user/keys/6");
	}
}
