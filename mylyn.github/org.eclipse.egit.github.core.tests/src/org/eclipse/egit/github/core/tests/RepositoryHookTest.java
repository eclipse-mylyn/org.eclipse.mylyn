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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.RepositoryHookResponse;
import org.junit.Test;

/**
 * Unit test of {@link RepositoryHook}
 */
public class RepositoryHookTest {

	/**
	 * Test default state of hook
	 */
	@Test
	public void defaultState() {
		RepositoryHook hook = new RepositoryHook();
		assertFalse(hook.isActive());
		assertNull(hook.getConfig());
		assertNull(hook.getCreatedAt());
		assertEquals(0, hook.getId());
		assertNull(hook.getLastResponse());
		assertNull(hook.getName());
		assertNull(hook.getUpdatedAt());
		assertNull(hook.getUrl());
	}

	/**
	 * Test updating hook fields
	 */
	@Test
	public void updateFields() {
		RepositoryHook hook = new RepositoryHook();
		assertTrue(hook.setActive(true).isActive());
		Map<String, String> config = Collections.singletonMap("a", "b");
		assertEquals(config, hook.setConfig(config).getConfig());
		assertEquals(new Date(1234), hook.setCreatedAt(new Date(1234))
				.getCreatedAt());
		assertEquals(150, hook.setId(150).getId());
		RepositoryHookResponse response = new RepositoryHookResponse();
		assertEquals(response, hook.setLastResponse(response).getLastResponse());
		assertEquals("cihook", hook.setName("cihook").getName());
		assertEquals(new Date(4455), hook.setUpdatedAt(new Date(4455))
				.getUpdatedAt());
		assertEquals("url", hook.setUrl("url").getUrl());
	}
}
