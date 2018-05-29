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

import org.eclipse.egit.github.core.event.ForkApplyPayload;
import org.junit.Test;

/**
 * Unit tests of {@link ForkApplyPayload}
 */
public class ForkApplyPayloadTest {

	/**
	 * Test default state of ForkApplyPayload
	 */
	@Test
	public void defaultState() {
		ForkApplyPayload payload = new ForkApplyPayload();
		assertNull(payload.getHead());
		assertNull(payload.getBefore());
		assertNull(payload.getAfter());
	}

	/**
	 * Test updating ForkApplyPayload fields
	 */
	@Test
	public void updateFields() {
		ForkApplyPayload payload = new ForkApplyPayload();
		assertEquals("head", payload.setHead("head").getHead());
		assertEquals("000", payload.setBefore("000").getBefore());
		assertEquals("001", payload.setAfter("001").getAfter());
	}
}
