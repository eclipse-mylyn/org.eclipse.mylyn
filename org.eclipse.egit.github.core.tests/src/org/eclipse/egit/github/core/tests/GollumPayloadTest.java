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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.GollumPage;
import org.eclipse.egit.github.core.event.GollumPayload;
import org.junit.Test;

/**
 * Unit tests of {@link GollumPayload}
 */
public class GollumPayloadTest {

	/**
	 * Test default state of GollumPayload
	 */
	@Test
	public void defaultState() {
		GollumPayload payload = new GollumPayload();
		assertNull(payload.getPages());
	}

	/**
	 * Test updating GollumPayload fields
	 */
	@Test
	public void updateFields() {
		GollumPayload payload = new GollumPayload();
		List<GollumPage> pages = new ArrayList<GollumPage>();
		pages.add(new GollumPage().setPageName("page"));
		assertEquals(pages, payload.setPages(pages).getPages());
	}
}
