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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.event.WatchPayload;
import org.junit.Test;

/**
 * Unit tests of {@link WatchPayload}
 */
public class WatchPayloadTest {

	/**
	 * Test default state of WatchPayload
	 */
	@Test
	public void defaultState() {
		WatchPayload payload = new WatchPayload();
		assertNull(payload.getAction());
	}

	/**
	 * Test updating WatchPayload fields
	 */
	@Test
	public void updateFields() {
		WatchPayload payload = new WatchPayload();
		assertEquals("create", payload.setAction("create").getAction());
	}
}
