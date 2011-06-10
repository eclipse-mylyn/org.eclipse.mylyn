/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;

/**
 * Unit tests of {@link UserService}
 */
public class UserTest extends LiveTest {

	/**
	 * Test fetching currently authenticated user
	 * 
	 * @throws Exception
	 */
	@Test
	public void fetchCurrentUser() throws Exception {
		assertNotNull("Test requires user", client.getUser());

		UserService service = new UserService(client);
		User user = service.getUser();
		assertNotNull(user);
		assertEquals(client.getUser(), user.getLogin());
		assertNotNull(user.getAvatarUrl());
		assertNotNull(user.getCreatedAt());
		assertNotNull(user.getPlan());
	}

	/**
	 * Test fetching user by login name
	 * 
	 * @throws Exception
	 */
	@Test
	public void fetchExplicitUser() throws Exception {
		assertNotNull("Test requires user", client.getUser());

		UserService service = new UserService(client);
		User user = service.getUser(client.getUser());
		assertNotNull(user);
		assertEquals(client.getUser(), user.getLogin());
		assertNotNull(user.getAvatarUrl());
		assertNotNull(user.getCreatedAt());
	}

	/**
	 * Test fetching followers
	 * 
	 * @throws Exception
	 */
	@Test
	public void fetchFollowers() throws Exception {
		assertNotNull("Test requires user", client.getUser());
		UserService service = new UserService(client);
		List<User> users = service.getFollowers();
		assertNotNull(users);
		assertFalse(users.isEmpty());
		for (User user : users) {
			assertNotNull(user.getId());
			assertNotNull(user.getLogin());
		}
	}

	/**
	 * Test fetching followed users
	 * 
	 * @throws Exception
	 */
	@Test
	public void fetchFollowing() throws Exception {
		assertNotNull("Test requires user", client.getUser());
		UserService service = new UserService(client);
		List<User> users = service.getFollowing();
		assertNotNull(users);
		assertFalse(users.isEmpty());
		for (User user : users) {
			assertNotNull(user.getId());
			assertNotNull(user.getLogin());
		}
	}

	/**
	 * Test finding out if user is followed
	 * 
	 * @throws Exception
	 */
	@Test
	public void isFollowed() throws Exception {
		assertNotNull("Test requires user", client.getUser());
		UserService service = new UserService(client);
		List<User> users = service.getFollowing();
		assertNotNull(users);
		assertFalse(users.isEmpty());
		assertTrue(service.isFollowing(users.get(0).getLogin()));
		assertFalse(service.isFollowing(client.getUser()));
	}
}
