/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Resources for use by the WikiText UI.
 * 
 * 
 * @author David Green
 */
public class WikiTextUiResources {

	// IMPOARTANT IMPLEMENTATION NOTE: 
	//   these resources must work in a stand-alone SWT application.  See bug# 245759 

	private static final String KEY_COLOR_REGISTRY = WikiTextUiResources.class.getName() + "#colorRegistry"; //$NON-NLS-1$

	/**
	 * the color of a horizontal rule
	 */
	public static final String COLOR_HR = "HR"; //$NON-NLS-1$

	/**
	 * the color of a horizontal rule shadow
	 */
	public static final String COLOR_HR_SHADOW = "HR_SHADOW"; //$NON-NLS-1$

	/**
	 * get colors for use in the UI
	 * 
	 * @see #COLOR_HR
	 * @see #COLOR_HR_SHADOW
	 */
	public static ColorRegistry getColors() {
		ColorRegistry colorRegistry = (ColorRegistry) Display.getCurrent().getData(KEY_COLOR_REGISTRY);
		if (colorRegistry == null) {
			colorRegistry = new ColorRegistry();

			colorRegistry.put(COLOR_HR, new RGB(132, 132, 132));
			colorRegistry.put(COLOR_HR_SHADOW, new RGB(206, 206, 206));

			Display.getCurrent().setData(KEY_COLOR_REGISTRY, colorRegistry);
		}
		return colorRegistry;
	}
}
