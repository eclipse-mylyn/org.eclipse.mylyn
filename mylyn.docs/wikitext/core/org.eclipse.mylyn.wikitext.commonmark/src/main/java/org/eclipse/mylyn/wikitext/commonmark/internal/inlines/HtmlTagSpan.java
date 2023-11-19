/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTagSpan extends SourceSpan {

	private static final String ATTRIBUTE_VALUE_QUOTED = "\"[^\"]*\"";

	private static final String ATTRIBUTE_VALUE_SINGLEQUOTED = "'[^']*'";

	private static final String ATTRIBUTE_VALUE_UNQUOTED = "[^\"'<>=]+";

	private static final String ATTRIBUTE_VALUE = "(?:" + ATTRIBUTE_VALUE_QUOTED + "|" + ATTRIBUTE_VALUE_SINGLEQUOTED
			+ "|" + ATTRIBUTE_VALUE_UNQUOTED + ")";

	private static final String ATTRIBUTE_NAME = "[a-zA-Z_][a-zA-Z0-9_:.-]*";

	private static final String ATTRIBUTE = "(?:\\s+" + ATTRIBUTE_NAME + "(?:\\s*=\\s*" + ATTRIBUTE_VALUE + ")?)";

	private static final String TAG = "<[a-zA-Z_][a-zA-Z_:0-9]*" + ATTRIBUTE + "*\\s*/?>";

	private static final String CLOSE_TAG = "</[a-zA-Z_][a-zA-Z_:0-9-]*\\s*>";

	private static final String COMMENT = "<!---->|<!--(?:-?[^>-])(?:-?[^-])*-->";

	private static final String PROCESSING_INSTRUCTION = "<\\?.*?\\?>";

	private static final String XML_DECLARATION = "<![A-Z]+(\\s+[^>]*)*>";

	private static final String CDATA = "<!\\[CDATA\\[.*?\\]\\]>";

	private static final String REGEX_TAG = "(" + TAG + "|" + CLOSE_TAG + "|" + COMMENT + "|" + PROCESSING_INSTRUCTION
			+ "|" + XML_DECLARATION + "|" + CDATA + ").*";

	private final Pattern tagPattern = Pattern.compile(REGEX_TAG, Pattern.DOTALL);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '<') {
			Matcher matcher = cursor.matcher(tagPattern);
			if (matcher.matches()) {
				return Optional.of(new HtmlTag(cursor.getLineAtOffset(), cursor.getOffset(), matcher.group(1)));
			}
		}
		return Optional.empty();
	}

}
