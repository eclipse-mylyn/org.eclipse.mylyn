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

	static final Map<String, RGB> keyToRgb;

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
		keyToRgb = Collections.unmodifiableMap(tempKeyToRgb);
	}

}
