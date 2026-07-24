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

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.event.GistPayload;
import org.junit.Test;

/**
 * Unit tests of {@link GistPayload}
 */
public class GistPayloadTest {

	/**
	 * Test default state of GistPayload
	 */
	@Test
	public void defaultState() {
		GistPayload payload = new GistPayload();
		assertNull(payload.getAction());
		assertNull(payload.getGist());
	}

	/**
	 * Test updating GistPayload fields
	 */
	@Test
	public void updateFields() {
		GistPayload payload = new GistPayload();
		Gist gist = new Gist().setId("id");
		assertEquals("create", payload.setAction("create").getAction());
		assertEquals(gist, payload.setGist(gist).getGist());
	}
}
