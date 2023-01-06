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
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Date;

import org.eclipse.egit.github.core.Application;
import org.eclipse.egit.github.core.Authorization;
import org.junit.Test;

/**
 * Unit tests of {@link Authorization}
 */
public class AuthorizationTest {

	/**
	 * Test default state of authorization
	 */
	@Test
	public void defaultState() {
		Authorization auth = new Authorization();
		assertNull(auth.getApp());
		assertNull(auth.getCreatedAt());
		assertEquals(0, auth.getId());
		assertNull(auth.getNote());
		assertNull(auth.getNoteUrl());
		assertNull(auth.getScopes());
		assertNull(auth.getToken());
		assertNull(auth.getUpdatedAt());
		assertNull(auth.getUrl());
	}

	/**
	 * Test updating application fields
	 */
	@Test
	public void updateFields() {
		Authorization auth = new Authorization();
		Application app = new Application();
		assertEquals(app, auth.setApp(app).getApp());
		assertEquals(new Date(2500), auth.setCreatedAt(new Date(2500))
				.getCreatedAt());
		assertEquals(123, auth.setId(123).getId());
		assertEquals("note", auth.setNote("note").getNote());
		assertEquals("noteUrl", auth.setNoteUrl("noteUrl").getNoteUrl());
		assertEquals(Collections.singletonList("repo"),
				auth.setScopes(Collections.singletonList("repo")).getScopes());
		assertEquals("token", auth.setToken("token").getToken());
		assertEquals(new Date(8000), auth.setUpdatedAt(new Date(8000))
				.getUpdatedAt());
		assertEquals("url", auth.setUrl("url").getUrl());
	}
}
