/******************************************************************************
 *  Copyright (c) 2018 Frédéric Cilia
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Frédéric Cilia - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.RepositoryMerging;
import org.junit.Test;

/**
 * Unit test of {@link RepositoryMerging}
 */
public class RepositoryMergingTest {

	/**
	 * Test default state of merging
	 */
	@Test
	public void defaultState() {
		RepositoryMerging merging = new RepositoryMerging();
		assertNull(merging.getBase());
		assertNull(merging.getCommitMessage());
		assertNull(merging.getHead());
	}

	/**
	 * Test updating merging fields
	 */
	@Test
	public void updateFields() {
		RepositoryMerging merging = new RepositoryMerging();
		assertEquals("baseMerging", merging.setBase("baseMerging").getBase());
		assertEquals("headMerging", merging.setHead("headMerging").getHead());
		assertEquals("messageMerging",
				merging.setCommitMessage("messageMerging").getCommitMessage());
	}
}
