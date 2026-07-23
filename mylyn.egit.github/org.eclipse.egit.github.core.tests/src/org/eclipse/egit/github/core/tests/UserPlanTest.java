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

import org.eclipse.egit.github.core.UserPlan;
import org.junit.Test;

/**
 * Unit tests of {@link UserPlan}
 */
public class UserPlanTest {

	/**
	 * Test default state of user plan
	 */
	@Test
	public void defaultState() {
		UserPlan plan = new UserPlan();
		assertEquals(0, plan.getCollaborators());
		assertNull(plan.getName());
		assertEquals(0, plan.getPrivateRepos());
		assertEquals(0, plan.getSpace());
	}

	/**
	 * Test updating user plan fields
	 */
	@Test
	public void updateFields() {
		UserPlan plan = new UserPlan();
		assertEquals(10, plan.setCollaborators(10).getCollaborators());
		assertEquals("bronze", plan.setName("bronze").getName());
		assertEquals(20, plan.setPrivateRepos(20).getPrivateRepos());
		assertEquals(300, plan.setSpace(300).getSpace());
	}
}
