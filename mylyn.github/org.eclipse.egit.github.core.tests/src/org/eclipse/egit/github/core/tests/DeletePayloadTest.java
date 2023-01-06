/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Jason Tsay (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.event.DeletePayload;
import org.junit.Test;

/**
 * Unit tests of {@link DeletePayload}
 */
public class DeletePayloadTest {

	/**
	 * Test default state of DeletePayload
	 */
	@Test
	public void defaultState() {
		DeletePayload payload = new DeletePayload();
		assertNull(payload.getRefType());
		assertNull(payload.getRef());
	}

	/**
	 * Test updating DeletePayload fields
	 */
	@Test
	public void updateFields() {
		DeletePayload payload = new DeletePayload();
		assertEquals("branch", payload.setRefType("branch").getRefType());
		assertEquals("ref", payload.setRef("ref").getRef());
	}
}
