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

import org.eclipse.egit.github.core.CommitStats;
import org.junit.Test;

/**
 *
 */
public class CommitStatsTest {

	/**
	 * Test default state of commit stats
	 */
	@Test
	public void defaultState() {
		CommitStats stats = new CommitStats();
		assertEquals(0, stats.getAdditions());
		assertEquals(0, stats.getDeletions());
		assertEquals(0, stats.getTotal());
	}

	/**
	 * Test updating commit stats fields
	 */
	@Test
	public void updateFields() {
		CommitStats stats = new CommitStats();
		assertEquals(10, stats.setAdditions(10).getAdditions());
		assertEquals(36, stats.setDeletions(36).getDeletions());
		assertEquals(123, stats.setTotal(123).getTotal());
	}
}
