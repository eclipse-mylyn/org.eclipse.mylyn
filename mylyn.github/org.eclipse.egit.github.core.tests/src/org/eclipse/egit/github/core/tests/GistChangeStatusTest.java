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

import org.eclipse.egit.github.core.GistChangeStatus;
import org.junit.Test;

/**
 * Unit tests of {@link GistChangeStatus}
 */
public class GistChangeStatusTest {

	/**
	 * Test default state of gist change status
	 */
	@Test
	public void defaultState() {
		GistChangeStatus change = new GistChangeStatus();
		assertEquals(0, change.getAdditions());
		assertEquals(0, change.getDeletions());
		assertEquals(0, change.getTotal());
	}

	/**
	 * Test updating gist change status fields
	 */
	@Test
	public void updateFields() {
		GistChangeStatus change = new GistChangeStatus();
		assertEquals(50, change.setAdditions(50).getAdditions());
		assertEquals(200, change.setDeletions(200).getDeletions());
		assertEquals(123, change.setTotal(123).getTotal());
	}
}
