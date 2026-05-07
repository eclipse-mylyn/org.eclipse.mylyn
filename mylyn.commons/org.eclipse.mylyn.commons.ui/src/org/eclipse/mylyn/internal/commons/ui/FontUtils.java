/*******************************************************************************
 * Copyright (c) 2026 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * Some utility methods for working with fonts.
 * 
 * @since 4.11
 */
public class FontUtils {
	private static final Point POINT_ZERO = new Point(0, 0);

	/**
	 * Returns the font metrics for the font of the given control.
	 *
	 * @param control
	 * @return
	 */
	public static FontMetrics getFontMetrics(final Control control) {
		final GC gc = new GC(control);
		try {
			return gc.getFontMetrics();
		} finally {
			gc.dispose();
		}
	}

	/**
	 * Returns the pixel width and height of the given string when rendered in the font of the given control.
	 * @param control
	 * @param string
	 * @return
	 */
	public static Point getStringPixels(final Control control, final String string) {
		final GC gc = new GC(control);
		try {
			if (string == null || string.isEmpty()) {
				return POINT_ZERO;
			}
			return gc.stringExtent(string);
		} finally {
			gc.dispose();
		}
	}
}
