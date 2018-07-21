/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.internal.commons.ui.E4ThemeColor;
import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

public class E4ThemeColorTest {

	@Test
	public void testRgbHex() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("#00FF00"); //$NON-NLS-1$
		assertEquals(new RGB(0, 255, 0), rgb);
	}

	@Test
	public void testHexGradient() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("#FF0000 #0000FF"); //$NON-NLS-1$
		assertEquals(new RGB(0, 0, 255), rgb);
	}

	@Test
	public void testHexGradientWithPercent() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("#FF0000 #00FF00 100%"); //$NON-NLS-1$
		assertEquals(new RGB(0, 255, 0), rgb);
	}

	@Test
	public void testHexGradientWithPercentVertical() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("#00FF00 #FF0000 100% false"); //$NON-NLS-1$
		assertEquals(new RGB(255, 0, 0), rgb);
	}

	@Test
	public void testRgb() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("rgb(238, 238, 238)"); //$NON-NLS-1$
		assertEquals(new RGB(238, 238, 238), rgb);
	}

	@Test
	public void testRGBGradient() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("rgb(210, 210, 210) rgb(238, 238, 238)"); //$NON-NLS-1$
		assertEquals(new RGB(238, 238, 238), rgb);
	}

	@Test
	public void testRGBGradientWithPercent() {
		RGB rgb = E4ThemeColor.getRGBFromCssString("rgb(210, 210, 210) rgb(238, 238, 238) 100.0%"); //$NON-NLS-1$
		assertEquals(new RGB(238, 238, 238), rgb);
	}

}
