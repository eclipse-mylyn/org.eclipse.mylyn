/******************************************************************************
 * *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.egit.github.core.MergeStatus;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link MergeStatus}
 */
@SuppressWarnings("nls")
public class MergeStatusTest {

	/**
	 * Test default status of merge status
	 */
	@Test
	public void defaultState() {
		MergeStatus status = new MergeStatus();
		assertFalse(status.isMerged());
		assertNull(status.getMessage());
		assertNull(status.getSha());
	}

	/**
	 * Test updating merge status fields
	 */
	@Test
	public void updateFields() {
		MergeStatus status = new MergeStatus();
		assertTrue(status.setMerged(true).isMerged());
		assertEquals("message", status.setMessage("message").getMessage());
		assertEquals("aabbcc", status.setSha("aabbcc").getSha());
	}
}
