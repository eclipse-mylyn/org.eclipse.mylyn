/*******************************************************************************
 * Copyright (c) 2017 Holger Staudacher and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Holger Staudacher - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ColorsTest {

	@Test
	public void asHexParsesRgb() {
		assertRgbToHex("rgb(255, 255, 255)", "#ffffff");
		assertRgbToHex("rgb(255, 23, 11)", "#ff170b");
		assertRgbToHex("rgb(42, 255, 1)", "#2aff01");
	}

	private void assertRgbToHex(String rgb, String expectedHex) {
		String actualHex = Colors.asHex(rgb);
		assertEquals(expectedHex, actualHex);
	}

	@Test
	public void asHexToHex() {
		String hex = Colors.asHex("#ffffff");

		assertEquals("#ffffff", hex);
	}

	@Test
	public void asHexReturnsOriginalValueWhenNotRgb() {
		String hex = Colors.asHex("foo");

		assertEquals("foo", hex);
	}

}
