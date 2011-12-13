/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
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
