/*******************************************************************************
 * Copyright (c) 2010, 2021 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.htmltext.util;

import java.awt.Color;

import org.eclipse.swt.graphics.RGB;

/**
 * Utitlity class for converting RGB colors to hex-colors.
 * 
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public final class ColorConverter {

	public static String convertRgbToHex(RGB rgb) {
		return new StringBuilder(toHex(rgb.red)).append(toHex(rgb.green)).append(toHex(rgb.blue)).toString();
	}

	private static String toHex(int color) {
		return new String(new char[] { "0123456789ABCDEF".charAt((color - color % 16) / 16), //$NON-NLS-1$
				"0123456789ABCDEF".charAt(color % 16) }, 0, 2); //$NON-NLS-1$
	}

	public static RGB convertHexToRgb(String hex) {
		Color color = Color.decode(hex.charAt(0) == '#' ? hex : "#" + hex); //$NON-NLS-1$
		return new RGB(color.getRed(), color.getGreen(), color.getBlue());
	}

}
