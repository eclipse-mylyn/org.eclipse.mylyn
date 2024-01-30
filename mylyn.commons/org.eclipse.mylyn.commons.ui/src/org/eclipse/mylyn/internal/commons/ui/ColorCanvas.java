/*******************************************************************************
 * Copyright (c) 2004, 2011 Willian Mitsuda and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Willian Mitsuda - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;

/**
 * A tiny control just for color display
 * 
 * @author Willian Mitsuda
 * @deprecated use {@link ColorDialog} instead
 */
@Deprecated
public class ColorCanvas extends Canvas {

	private final Color color;

	public ColorCanvas(Composite parent, int style, RGB rgb) {
		super(parent, style);
		color = new Color(parent.getDisplay(), rgb);
		addPaintListener(e -> {
			e.gc.setBackground(color);
			e.gc.fillRectangle(getClientArea());
		});
		addDisposeListener(e -> color.dispose());
	}

	public RGB getRGB() {
		return color.getRGB();
	}

}
