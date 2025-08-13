/*******************************************************************************
 * Copyright (c) 2015, 2024 Max Rydhal Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.parser.TableCellAttributes;

/**
 * Internal class to provide package internal support to the language
 */
public class LanguageSupport {
	private static final Pattern COLUMN_FORMATTING_SPLITTER = Pattern.compile("([0-9]+)\\*(.*)"); //$NON-NLS-1$

	private static final Pattern ALIGN_PATTERN = Pattern.compile("((?:\\.)?)([<^>])"); //$NON-NLS-1$

	private static Pattern keyValuePattern = Pattern.compile("(.*)=\"(.*)\""); //$NON-NLS-1$

	private static final Pattern SPLIT_PATTERN = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //$NON-NLS-1$

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

		String[] valpairs = SPLIT_PATTERN.split(rawFormat);
		for (String pair : valpairs) {
			Matcher matcher = keyValuePattern.matcher(pair.trim());

			String key, value;

			if (matcher.find()) {
				key = matcher.group(1);
				value = matcher.group(2);
				properties.put(key, value);
			} else // could not parse key/value pairs
				if (positionalParameters.isEmpty()) {
					//no more positional items left - ignoring
				} else {
					properties.put(positionalParameters.remove(0), pair.trim());
				}
		}

		return properties;
	}

	public static List<TableCellAttributes> computeColumnsAttributeList(String cols) {
		if (cols != null) {
			List<TableCellAttributes> result = new ArrayList<>();
			for (String c : cols.split(",")) { //$NON-NLS-1$
				Matcher matcher = COLUMN_FORMATTING_SPLITTER.matcher(c);
				if (matcher.matches()) {
					int repeat = Integer.parseInt(matcher.group(1));
					for (int i = 0; i < repeat; i++) {
						result.add(convertToTableCellAttributes(matcher.group(2)));
					}
				} else {
					result.add(convertToTableCellAttributes(c));
				}
			}
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	private static TableCellAttributes convertToTableCellAttributes(String columnFormat) {
		TableCellAttributes result = new TableCellAttributes();

		Matcher alignMatcher = ALIGN_PATTERN.matcher(columnFormat);
		int start = 0;
		while (alignMatcher.find(start)) {
			String align = alignMatcher.group(2);
			if (".".equals(alignMatcher.group(1))) { //$NON-NLS-1$
				if ("<".equals(align)) { //$NON-NLS-1$
					result.setValign("top"); //$NON-NLS-1$
				} else if ("^".equals(align)) { //$NON-NLS-1$
					result.setValign("middle"); //$NON-NLS-1$
				} else if (">".equals(align)) { //$NON-NLS-1$
					result.setValign("bottom"); //$NON-NLS-1$
				}
			} else if ("<".equals(align)) { //$NON-NLS-1$
				result.setAlign("left"); //$NON-NLS-1$
			} else if ("^".equals(align)) { //$NON-NLS-1$
				result.setAlign("center"); //$NON-NLS-1$
			} else if (">".equals(align)) { //$NON-NLS-1$
				result.setAlign("right"); //$NON-NLS-1$
			}
			start = alignMatcher.end();
		}

		return result;
	}

	public static List<TableCellAttributes> createDefaultColumnsAttributeList(int size) {
		List<TableCellAttributes> result = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			result.add(new TableCellAttributes());
		}
		return result;
	}

	public static int computeHeadingLevel(int initialLevel, AsciiDocContentState state) {
		String attributeValue = state.getAttributeOrValue(AsciiDocContentState.ATTRIBUTE_LEVELOFFSET, "0"); //$NON-NLS-1$
		int levelOffset;
		try {
			levelOffset = Integer.parseInt(attributeValue);
		} catch (NumberFormatException e) {
			return initialLevel;
		}
		return Math.max(1, Math.min(initialLevel + levelOffset, 6));
	}

}