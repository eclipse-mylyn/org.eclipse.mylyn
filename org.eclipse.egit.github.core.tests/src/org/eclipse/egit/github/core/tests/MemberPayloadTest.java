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
import org.eclipse.egit.github.core.event.MemberPayload;
import org.junit.Test;

/**
 * Unit tests of {@link MemberPayload}
 */
public class MemberPayloadTest {

	/**
	 * Test default state of MemberPayload
	 */
	@Test
	public void defaultState() {
		MemberPayload payload = new MemberPayload();
		assertNull(payload.getMember());
		assertNull(payload.getAction());
	}

	/**
	 * Test updating MemberPayload fields
	 */
	@Test
	public void updateFields() {
		MemberPayload payload = new MemberPayload();
		User member = new User().setLogin("member");
		assertEquals(member, payload.setMember(member).getMember());
		assertEquals("create", payload.setAction("create").getAction());
	}
}
