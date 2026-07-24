/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.User;
import org.junit.Test;

/**
 * Unit tests of {@link Milestone}
 */
public class MilestoneTest {

	/**
	 * Test default state of milestone
	 */
	@Test
	public void defaultState() {
		Milestone milestone = new Milestone();
		assertEquals(0, milestone.getClosedIssues());
		assertNull(milestone.getCreatedAt());
		assertNull(milestone.getCreator());
		assertNull(milestone.getDescription());
		assertNull(milestone.getDueOn());
		assertEquals(0, milestone.getNumber());
		assertEquals(0, milestone.getOpenIssues());
		assertNull(milestone.getState());
		assertNull(milestone.getTitle());
		assertNull(milestone.getUrl());
	}

	/**
	 * Test updating milestone fields
	 */
	@Test
	public void updateFields() {
		Milestone milestone = new Milestone();
		assertEquals(1, milestone.setClosedIssues(1).getClosedIssues());
		assertEquals(new Date(10000), milestone.setCreatedAt(new Date(10000))
				.getCreatedAt());
		User creator = new User().setLogin("creator");
		assertEquals(creator, milestone.setCreator(creator).getCreator());
		assertEquals("desc", milestone.setDescription("desc").getDescription());
		assertEquals(new Date(20000), milestone.setDueOn(new Date(20000))
				.getDueOn());
		assertEquals(4, milestone.setNumber(4).getNumber());
		assertEquals(10, milestone.setOpenIssues(10).getOpenIssues());
		assertEquals("state", milestone.setState("state").getState());
		assertEquals("title", milestone.setTitle("title").getTitle());
		assertEquals("url", milestone.setUrl("url").getUrl());
	}

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		Milestone milestone = new Milestone();
		milestone.setCreatedAt(new Date(5000));
		milestone.getCreatedAt().setTime(0);
		assertTrue(milestone.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable due on date
	 */
	@Test
	public void getDueOn_ReferenceMutableObject() {
		Milestone milestone = new Milestone();
		milestone.setDueOn(new Date(2000));
		milestone.getDueOn().setTime(0);
		assertTrue(milestone.getDueOn().getTime() != 0);
	}

	/**
	 * Test non-mutable due on date
	 */
	@Test
	public void setDueOnReferenceMutableObject() {
		Milestone milestone = new Milestone();
		Date longTimeAgo = new Date(0L);
		milestone.setDueOn(longTimeAgo);
		longTimeAgo.setTime(10000L);
		assertTrue(milestone.getDueOn().getTime() == 0L);
	}
}
