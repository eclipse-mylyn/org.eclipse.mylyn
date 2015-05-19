/*******************************************************************************
 * Copyright (c) 2015, 2016 Max Rydhal Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal class to provide package internal support to the language
 */
public class LanguageSupport {

	private static Pattern keyValuePattern = Pattern.compile("(.*)=\"(.*)\""); //$NON-NLS-1$

	/**
	 * Helper method for parsing AsciiDoc format string (
	 *
	 * <pre>
	 * [name=test, class=ruby]
	 * </pre>
	 *
	 * ) into a Map of key/value pairs. Supports positional parameters too.
	 *
	 * @param rawFormat
	 *            The raw format string found in AsciiDoc source without brackets.
	 * @param positionalParameters
	 *            a list of strings for the positional parameters (i.e. "alt", "width", "height" for images)
	 * @param defaultValueKey
	 *            the key to use if no parameters found (i.e. "alt" for images)
	 * @return
	 */
	public static Map<String, String> parseFormattingProperties(String rawFormat, List<String> positionalParameters) {
		Map<String, String> properties = new HashMap<>();

		if (rawFormat == null || rawFormat.trim().length() == 0) {
			return properties;
		}

		// TODO: handle escaped strings and default sequence of parameters
		// i.e. sunset,100,200,title="test"
		String[] valpairs = rawFormat.split(","); //$NON-NLS-1$
		for (String pair : valpairs) {
			Matcher matcher = keyValuePattern.matcher(pair.trim());

			String key, value;

			if (matcher.find()) {
				key = matcher.group(1);
				value = matcher.group(2);
				properties.put(key, value);
			} else {
				// could not parse key/value pairs
				if (positionalParameters.isEmpty()) {
					//no more positional items left - ignoring
				} else {
					properties.put(positionalParameters.remove(0), pair.trim());
				}
			}
		}

		return properties;
	}

}
