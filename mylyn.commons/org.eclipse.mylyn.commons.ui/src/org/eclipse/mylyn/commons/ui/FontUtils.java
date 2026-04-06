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

package org.eclipse.mylyn.commons.ui;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;

/**
 * @since 4.11
 */
public class FontUtils {
	public static FontMetrics getFontMetrics(Control control) {
		GC gc = new GC(control);
		try {
			return gc.getFontMetrics();
		} finally {
			gc.dispose();
		}
	}
}
