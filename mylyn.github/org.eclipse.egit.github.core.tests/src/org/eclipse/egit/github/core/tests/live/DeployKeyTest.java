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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.egit.github.core.Key;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.DeployKeyService;
import org.junit.Test;

/**
 * Unit tests of {@link DeployKeyService}
 */
public class DeployKeyTest extends LiveTest {

	/**
	 * Create, fetch, and delete repository deploy key
	 * 
	 * @throws Exception
	 */
	@Test
	public void createFetchDeleteKey() throws Exception {
		checkUser();
		assertNotNull("Repo is required for test", writableRepo);

		RepositoryId repo = RepositoryId.create(client.getUser(), writableRepo);
		DeployKeyService service = new DeployKeyService(client);
		Key key = new Key();
		key.setTitle("key" + System.currentTimeMillis());
		key.setKey("ssh-rsa " + System.nanoTime());
		Key created = service.createKey(repo, key);
		assertNotNull(created);
		assertEquals(key.getTitle(), created.getTitle());
		assertEquals(key.getKey(), created.getKey());
		assertNotNull(created.getUrl());
		assertTrue(created.getId() > 0);

		Key fetch = service.getKey(repo, created.getId());
		assertNotNull(fetch);
		assertEquals(created.getTitle(), fetch.getTitle());
		assertEquals(created.getKey(), fetch.getKey());
		assertEquals(created.getUrl(), fetch.getUrl());
		assertEquals(created.getId(), fetch.getId());

		Key found = null;
		List<Key> keys = service.getKeys(repo);
		assertNotNull(keys);
		assertFalse(keys.isEmpty());
		for (Key possible : service.getKeys(repo)) {
			if (created.getId() == possible.getId()) {
				found = possible;
				break;
			}
		}
		assertNotNull(found);
		assertEquals(created.getTitle(), found.getTitle());
		assertEquals(created.getKey(), found.getKey());
		assertEquals(created.getUrl(), found.getUrl());
		assertEquals(created.getId(), found.getId());

		service.deleteKey(repo, created.getId());
		try {
			service.getKey(repo, created.getId());
			fail("Request exception not thrown");
		} catch (RequestException e) {
			assertEquals(404, e.getStatus());
		}
	}

	/**
	 * Create, edit and delete a deploy key
	 * 
	 * @throws Exception
	 */
	@Test
	public void createEditDeleteKey() throws Exception {
		checkUser();
		assertNotNull("Repo is required for test", writableRepo);

		RepositoryId repo = RepositoryId.create(client.getUser(), writableRepo);
		DeployKeyService service = new DeployKeyService(client);
		Key key = new Key();
		key.setTitle("key" + System.currentTimeMillis());
		key.setKey("ssh-rsa " + System.nanoTime());
		Key created = service.createKey(repo, key);
		assertNotNull(created);
		assertEquals(key.getTitle(), created.getTitle());
		assertEquals(key.getKey(), created.getKey());
		assertNotNull(created.getUrl());
		assertTrue(created.getId() > 0);

		Key fetch = service.getKey(repo, created.getId());
		assertNotNull(fetch);
		assertEquals(created.getTitle(), fetch.getTitle());
		assertEquals(created.getKey(), fetch.getKey());
		assertEquals(created.getUrl(), fetch.getUrl());
		assertEquals(created.getId(), fetch.getId());
		fetch.setTitle("new title");

		Key edited = service.editKey(repo, fetch);
		assertNotNull(edited);
		assertNotSame(fetch, edited);
		assertEquals(fetch.getTitle(), edited.getTitle());
		assertEquals(fetch.getKey(), edited.getKey());
		assertEquals(fetch.getUrl(), edited.getUrl());
		assertEquals(fetch.getId(), edited.getId());

		service.deleteKey(repo, created.getId());
		try {
			service.getKey(repo, created.getId());
			fail("Request exception not thrown");
		} catch (RequestException e) {
			assertEquals(404, e.getStatus());
		}
	}
}
