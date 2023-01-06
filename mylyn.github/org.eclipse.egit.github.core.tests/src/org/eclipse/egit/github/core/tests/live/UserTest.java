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
package org.eclipse.egit.github.core.tests.live;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.egit.github.core.Key;
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

	/**
	 * Test adding, fetching, and deleting an e-mail address to a user account
	 *
	 * @throws Exception
	 */
	@Test
	public void addFetchDeleteEmail() throws Exception {
		checkUser();

		String email1 = "first" + System.nanoTime() + "@email.com";
		UserService service = new UserService(client);

		List<String> emails = service.getEmails();
		assertFalse(emails.contains(email1));
		service.addEmail(email1);

		emails = service.getEmails();
		assertTrue(emails.contains(email1));

		service.removeEmail(email1);
		emails = service.getEmails();
		assertFalse(emails.contains(email1));
	}

	/**
	 * Test adding, fetching, and deleting an e-mail addresses to a user account
	 *
	 * @throws Exception
	 */
	@Test
	public void addFetchDeleteEmails() throws Exception {
		checkUser();

		String email1 = "first" + System.nanoTime() + "@email.com";
		String email2 = "second" + System.nanoTime() + "@email.com";
		UserService service = new UserService(client);

		List<String> emails = service.getEmails();
		assertFalse(emails.contains(email1));
		assertFalse(emails.contains(email2));

		service.addEmail(email1, email2);
		emails = service.getEmails();
		assertTrue(emails.contains(email1));
		assertTrue(emails.contains(email2));

		service.removeEmail(email1, email2);
		emails = service.getEmails();
		assertFalse(emails.contains(email1));
		assertFalse(emails.contains(email2));
	}

	/**
	 * Test adding, fetching, and deleting a key to a user account
	 *
	 * @throws Exception
	 */
	@Test
	public void addFetchDeleteKey() throws Exception {
		checkUser();

		Key key = new Key();
		key.setTitle("key" + System.currentTimeMillis());
		key.setKey("ssh-rsa " + System.nanoTime());
		UserService service = new UserService(client);

		Key created = service.createKey(key);
		assertNotNull(created);
		assertNotNull(created.getUrl());
		assertEquals(key.getTitle(), created.getTitle());
		assertEquals(key.getKey(), created.getKey());

		Key fetched = service.getKey(created.getId());
		assertNotNull(fetched);
		assertEquals(created.getUrl(), fetched.getUrl());
		assertEquals(key.getTitle(), fetched.getTitle());
		assertEquals(key.getKey(), fetched.getKey());

		service.deleteKey(created.getId());
	}
}
