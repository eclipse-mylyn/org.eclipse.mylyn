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

import org.eclipse.egit.github.core.Contributor;
import org.junit.Test;

/**
 * Unit tests of {@link Contributor}
 */
public class ContributorTest {

	/**
	 * Test default state of contributor
	 */
	@Test
	public void defaultState() {
		Contributor contributor = new Contributor();
		assertNull(contributor.getAvatarUrl());
		assertEquals(0, contributor.getContributions());
		assertEquals(0, contributor.getId());
		assertNull(contributor.getLogin());
		assertNull(contributor.getName());
		assertNull(contributor.getType());
		assertNull(contributor.getUrl());
	}

	/**
	 * Test updating contributor fields
	 */
	@Test
	public void updateFields() {
		Contributor contributor = new Contributor();
		assertEquals("aUrl", contributor.setAvatarUrl("aUrl").getAvatarUrl());
		assertEquals(10, contributor.setContributions(10).getContributions());
		assertEquals(4321, contributor.setId(4321).getId());
		assertEquals("user", contributor.setLogin("user").getLogin());
		assertEquals("U ser", contributor.setName("U ser").getName());
		assertEquals(Contributor.TYPE_ANONYMOUS,
				contributor.setType(Contributor.TYPE_ANONYMOUS).getType());
		assertEquals("url", contributor.setUrl("url").getUrl());
	}

}
