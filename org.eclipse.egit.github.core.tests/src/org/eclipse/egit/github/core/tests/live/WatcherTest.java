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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.WatcherService;
import org.junit.Test;

/**
 * Unit tests of {@link WatcherService}
 */
public class WatcherTest extends LiveTest {

	/**
	 * Test getting watchers of a repository
	 * 
	 * @throws Exception
	 */
	@Test
	public void getWatchers() throws Exception {
		WatcherService service = new WatcherService(client);
		List<User> watchers = service.getWatchers(new RepositoryId("defunkt",
				"dotjs"));
		assertNotNull(watchers);
		assertFalse(watchers.isEmpty());
		for (User watcher : watchers) {
			assertNotNull(watcher);
			assertNotNull(watcher.getLogin());
		}
	}

	/**
	 * Test getting repositories watched by a user
	 * 
	 * @throws Exception
	 */
	@Test
	public void getWatched() throws Exception {
		WatcherService service = new WatcherService(client);
		List<Repository> watched = service.getWatched("defunkt");
		assertNotNull(watched);
		assertFalse(watched.isEmpty());
		for (Repository repo : watched) {
			assertNotNull(repo);
			assertNotNull(repo.getName());
			assertNotNull(repo.getOwner());
		}
	}

	/**
	 * Test if current user is watching a repository
	 * 
	 * @throws Exception
	 */
	@Test
	public void isWatching() throws Exception {
		checkUser();
		WatcherService service = new WatcherService(client);
		List<Repository> watched = service.getWatched();
		assertNotNull(watched);
		assertFalse(watched.isEmpty());
		for (Repository repo : watched) {
			assertNotNull(repo);
			assertNotNull(repo.getName());
			assertNotNull(repo.getOwner());
			assertTrue(service.isWatching(repo));
		}
	}
}
