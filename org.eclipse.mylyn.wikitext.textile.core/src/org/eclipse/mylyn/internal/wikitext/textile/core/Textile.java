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
package org.eclipse.mylyn.internal.wikitext.textile.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.util.MatcherAdaper;

/**
 * 
 * 
 * @author David Green
 */
public class Textile {

	private static final String REGEX_TEXTILE_CLASS_ID = "(?:\\(([^#\\)]+)?(?:#([^\\)]+))?\\))"; //$NON-NLS-1$

	private static final String REGEX_TEXTILE_STYLE = "(?:\\{([^\\}]+)\\})"; //$NON-NLS-1$

	private static final String REGEX_LANGUAGE = "(?:\\[([^\\]]+)\\])"; //$NON-NLS-1$

	public static final String REGEX_ATTRIBUTES = "(?:" + REGEX_TEXTILE_CLASS_ID + "|" + REGEX_TEXTILE_STYLE + "|" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ REGEX_LANGUAGE + "){0,3}"; //$NON-NLS-1$

	public static final String REGEX_BLOCK_ATTRIBUTES = "(\\(+)?(\\)+)?(\\<|\\>|\\=|\\<\\>)?" + REGEX_ATTRIBUTES; //$NON-NLS-1$

	public static final int ATTRIBUTES_GROUP_COUNT = 4;

	public static final int ATTRIBUTES_BLOCK_GROUP_COUNT = 7;

	private static final Pattern explicitBlockBeginPattern = Pattern.compile("(((h[1-6])|p|pre|bc|bq|table)|(fn([0-9]{1,2})))" //$NON-NLS-1$
			+ REGEX_ATTRIBUTES + "\\.\\.?\\s+.*"); //$NON-NLS-1$

	private static final Map<String, String> alignmentToStyle = new HashMap<String, String>();
	static {
		alignmentToStyle.put("<", "text-align: left;"); //$NON-NLS-1$ //$NON-NLS-2$
		alignmentToStyle.put(">", "text-align: right;"); //$NON-NLS-1$ //$NON-NLS-2$
		alignmentToStyle.put("=", "text-align: center;"); //$NON-NLS-1$ //$NON-NLS-2$
		alignmentToStyle.put("<>", "text-align: justify;"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static Attributes configureAttributes(Attributes attributes, Matcher matcher, int offset, boolean block) {
		return configureAttributes(new MatcherAdaper(matcher), attributes, offset, block);
	}

	private static void appendStyles(Attributes attributes, String cssStyles) {
		if (cssStyles == null || cssStyles.length() == 0) {
			return;
		}
		String styles = attributes.getCssStyle();
		if (styles == null) {
			attributes.setCssStyle(cssStyles);
		} else {
			if (styles.endsWith(";")) { //$NON-NLS-1$
				styles += " "; //$NON-NLS-1$
			} else {
				styles += "; "; //$NON-NLS-1$
			}
			styles += cssStyles;
			attributes.setCssStyle(styles);
		}
	}

	public static Attributes configureAttributes(org.eclipse.mylyn.wikitext.core.parser.util.Matcher matcher,
			Attributes attributes, int offset, boolean block) {
		if (offset < 1) {
			throw new IllegalArgumentException();
		}
		if (block) {
			// padding (left)
			{
				String padding = matcher.group(offset);
				if (padding != null) {
					appendStyles(attributes, "padding-left: " + padding.length() + "em;"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				++offset;
			}

			// padding (right)
			{
				String padding = matcher.group(offset);
				if (padding != null) {
					appendStyles(attributes, "padding-right: " + padding.length() + "em;"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				++offset;
			}

			// alignment
			{
				String alignment = matcher.group(offset);
				if (alignment != null) {
					appendStyles(attributes, alignmentToStyle.get(alignment));
				}
				++offset;
			}
		}

		String cssClass2 = matcher.group(offset);
		String id = matcher.group(offset + 1);
		String cssStyles2 = matcher.group(offset + 2);
		String language = matcher.group(offset + 3);

		if (id != null && attributes.getId() == null) {
			attributes.setId(id);
		}

		if (attributes.getCssClass() != null || cssClass2 != null) {
			attributes.setCssClass(attributes.getCssClass() == null ? cssClass2
					: cssClass2 == null ? attributes.getCssClass() : attributes.getCssClass() + ' ' + cssClass2);
		}
		appendStyles(attributes, cssStyles2);

		attributes.setLanguage(language);

		return attributes;
	}

	public static boolean explicitBlockBegins(String line, int offset) {
		if (offset != 0) {
			return false;
		}
		return explicitBlockBeginPattern.matcher(line).matches();
	}
}
