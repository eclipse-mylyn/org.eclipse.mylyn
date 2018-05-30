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

import org.eclipse.egit.github.core.event.CreatePayload;
import org.junit.Test;

/**
 * Unit tests of {@link CreatePayload}
 */
public class CreatePayloadTest {

	/**
	 * Test default state of CreatePayload
	 */
	@Test
	public void defaultState() {
		CreatePayload payload = new CreatePayload();
		assertNull(payload.getRefType());
		assertNull(payload.getRef());
		assertNull(payload.getMasterBranch());
		assertNull(payload.getDescription());
	}

	/**
	 * Test updating CreatePayload fields
	 */
	@Test
	public void updateFields() {
		CreatePayload payload = new CreatePayload();
		assertEquals("branch", payload.setRefType("branch").getRefType());
		assertEquals("ref", payload.setRef("ref").getRef());
		assertEquals("master", payload.setMasterBranch("master").getMasterBranch());
		assertEquals("description",
				payload.setDescription("description").getDescription());
	}
}
