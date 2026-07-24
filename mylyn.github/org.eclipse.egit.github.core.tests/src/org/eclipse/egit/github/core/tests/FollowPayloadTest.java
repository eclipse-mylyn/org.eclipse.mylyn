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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.event.FollowPayload;
import org.junit.Test;

/**
 * Unit tests of {@link FollowPayload}
 */
public class FollowPayloadTest {

	/**
	 * Test default state of FollowPayload
	 */
	@Test
	public void defaultState() {
		FollowPayload payload = new FollowPayload();
		assertNull(payload.getTarget());
	}

	/**
	 * Test updating FollowPayload fields
	 */
	@Test
	public void updateFields() {
		FollowPayload payload = new FollowPayload();
		User target = new User().setName("target");
		assertEquals(target, payload.setTarget(target).getTarget());
	}
}
