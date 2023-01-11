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

import org.eclipse.egit.github.core.GollumPage;
import org.junit.Test;

/**
 * Unit tests of {@link GollumPage}
 */
public class GollumPageTest {

	/**
	 * Test default state of GollumPage
	 */
	@Test
	public void defaultState() {
		GollumPage GollumPage = new GollumPage();
		assertNull(GollumPage.getAction());
		assertNull(GollumPage.getHtmlUrl());
		assertNull(GollumPage.getPageName());
		assertNull(GollumPage.getSha());
		assertNull(GollumPage.getTitle());
	}

	/**
	 * Test updating GollumPage fields
	 */
	@Test
	public void updateFields() {
		GollumPage GollumPage = new GollumPage();
		assertEquals("create", GollumPage.setAction("create").getAction());
		assertEquals("url://a", GollumPage.setHtmlUrl("url://a").getHtmlUrl());
		assertEquals("page", GollumPage.setPageName("page").getPageName());
		assertEquals("000", GollumPage.setSha("000").getSha());
		assertEquals("title", GollumPage.setTitle("title").getTitle());
	}
}
