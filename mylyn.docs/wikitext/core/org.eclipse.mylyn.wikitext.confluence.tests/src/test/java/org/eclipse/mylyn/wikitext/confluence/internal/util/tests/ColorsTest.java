/*******************************************************************************
 * Copyright (c) 2017, 2024 Holger Staudacher and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Holger Staudacher - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.util.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.confluence.internal.util.Colors;
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
