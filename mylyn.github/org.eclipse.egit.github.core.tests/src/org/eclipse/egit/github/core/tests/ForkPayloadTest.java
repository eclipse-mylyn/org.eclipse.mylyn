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

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.ForkPayload;
import org.junit.Test;

/**
 * Unit tests of {@link ForkPayload}
 */
public class ForkPayloadTest {

	/**
	 * Test default state of ForkPayload
	 */
	@Test
	public void defaultState() {
		ForkPayload payload = new ForkPayload();
		assertNull(payload.getForkee());
	}

	/**
	 * Test updating ForkPayload fields
	 */
	@Test
	public void updateFields() {
		ForkPayload payload = new ForkPayload();
		Repository forkee = new Repository().setDescription("forkee");
		assertEquals(forkee, payload.setForkee(forkee).getForkee());
	}
}
