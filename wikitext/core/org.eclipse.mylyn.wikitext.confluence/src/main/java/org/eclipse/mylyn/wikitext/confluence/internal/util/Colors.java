/*******************************************************************************
 * Copyright (c) 2017 Holger Staudacher and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Holger Staudacher - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colors {

	private static final Pattern RGB_PATTERN = Pattern
			.compile("rgb\\(\\s*(\\d{1,3}+)\\s*,\\s*(\\d{1,3}+)\\s*,\\s*(\\d{1,3}+)\\s*\\)");

	public static String asHex(String color) {
		Matcher rgbMatcher = RGB_PATTERN.matcher(color);
		if (rgbMatcher.matches()) {
			return formatRgbAsHex(asInt(rgbMatcher.group(1)), asInt(rgbMatcher.group(2)), asInt(rgbMatcher.group(3)));
		}
		return color;
	}

	private static int asInt(String value) {
		return Integer.parseInt(value);
	}

	private static String formatRgbAsHex(int r, int g, int b) {
		return String.format("#%02x%02x%02x", r, g, b);
	}

	private Colors() {
		// prevent instantiation
	}

}
