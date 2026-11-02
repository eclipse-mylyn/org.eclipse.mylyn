/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.internal.wikitext.ui.viewer.HtmlTextPresentationParser;
import org.eclipse.swt.graphics.RGB;

/**
 * @author David Green
 */
class Colors {

	static final RGB RGB_DEFAULT = new RGB(0, 0, 0);

	static final RGB RGB_COMMENT = new RGB(63, 95, 191);

	static final RGB RGB_SELECTOR = new RGB(42, 129, 128);

	static final RGB RGB_PROPERTY_NAME = new RGB(129, 0, 129);

	static final RGB RGB_PROPERTY_VALUE = new RGB(42, 0, 225);

	private static final Map<String, RGB> KEY_TO_RGB;

	private static final Map<String, RGB> KEY_TO_RGB_DARK;

	public static final String KEY_DEFAULT = "css.default"; //$NON-NLS-1$

	public static final String KEY_COMMENT = "css.comment"; //$NON-NLS-1$

	public static final String KEY_SELECTOR = "css.selector"; //$NON-NLS-1$

	public static final String KEY_PROPERTY_NAME = "css.property.name"; //$NON-NLS-1$

	public static final String KEY_PROPERTY_VALUE = "css.property.value"; //$NON-NLS-1$

	static {
		Map<String, RGB> tempKeyToRgb = new HashMap<>();
		tempKeyToRgb.put(KEY_DEFAULT, RGB_DEFAULT);
		tempKeyToRgb.put(KEY_COMMENT, RGB_COMMENT);
		tempKeyToRgb.put(KEY_SELECTOR, RGB_SELECTOR);
		tempKeyToRgb.put(KEY_PROPERTY_NAME, RGB_PROPERTY_NAME);
		tempKeyToRgb.put(KEY_PROPERTY_VALUE, RGB_PROPERTY_VALUE);
		KEY_TO_RGB = Collections.unmodifiableMap(tempKeyToRgb);
		KEY_TO_RGB_DARK = Map.of(KEY_DEFAULT, new RGB(224, 224, 224), KEY_COMMENT, new RGB(156, 204, 156), KEY_SELECTOR,
				new RGB(78, 201, 176), KEY_PROPERTY_NAME, new RGB(156, 220, 254), KEY_PROPERTY_VALUE,
				new RGB(230, 176, 140));
	}

	static Map<String, RGB> getKeyToRgb() {
		return HtmlTextPresentationParser.isDarkThemeActive() ? KEY_TO_RGB_DARK : KEY_TO_RGB;
	}

}
