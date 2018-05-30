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

import java.util.Date;

import org.eclipse.egit.github.core.CommitUser;
import org.junit.Test;

/**
 * Unit tests of {@link CommitUser}
 */
public class CommitUserTest {

	/**
	 * Test default state of commit user
	 */
	@Test
	public void defaultState() {
		CommitUser user = new CommitUser();
		assertNull(user.getDate());
		assertNull(user.getEmail());
		assertNull(user.getName());
	}

	/**
	 * Test updating commit user fields
	 */
	@Test
	public void updateFields() {
		CommitUser user = new CommitUser();
		assertEquals(new Date(123456), user.setDate(new Date(123456)).getDate());
		assertEquals("a@b.com", user.setEmail("a@b.com").getEmail());
		assertEquals("a user", user.setName("a user").getName());
	}
}
