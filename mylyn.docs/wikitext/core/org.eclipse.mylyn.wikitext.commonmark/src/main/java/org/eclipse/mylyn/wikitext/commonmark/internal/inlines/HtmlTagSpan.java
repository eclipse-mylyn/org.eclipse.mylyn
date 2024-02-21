/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTagSpan extends SourceSpan {

	private static final String ATTRIBUTE_VALUE_QUOTED = "\"[^\"]*\""; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE_SINGLEQUOTED = "'[^']*'"; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE_UNQUOTED = "[^\"'<>=]+"; //$NON-NLS-1$

	private static final String ATTRIBUTE_VALUE = "(?:" + ATTRIBUTE_VALUE_QUOTED + "|" + ATTRIBUTE_VALUE_SINGLEQUOTED //$NON-NLS-1$ //$NON-NLS-2$
			+ "|" + ATTRIBUTE_VALUE_UNQUOTED + ")"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String ATTRIBUTE_NAME = "[a-zA-Z_][a-zA-Z0-9_:.-]*"; //$NON-NLS-1$

	private static final String ATTRIBUTE = "(?:\\s+" + ATTRIBUTE_NAME + "(?:\\s*=\\s*" + ATTRIBUTE_VALUE + ")?)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String TAG = "<[a-zA-Z_][a-zA-Z_:0-9]*" + ATTRIBUTE + "*\\s*/?>"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String CLOSE_TAG = "</[a-zA-Z_][a-zA-Z_:0-9-]*\\s*>"; //$NON-NLS-1$

	private static final String COMMENT = "<!---->|<!--(?:-?[^>-])(?:-?[^-])*-->"; //$NON-NLS-1$

	private static final String PROCESSING_INSTRUCTION = "<\\?.*?\\?>"; //$NON-NLS-1$

	private static final String XML_DECLARATION = "<![A-Z]+(\\s+[^>]*)*>"; //$NON-NLS-1$

	private static final String CDATA = "<!\\[CDATA\\[.*?\\]\\]>"; //$NON-NLS-1$

	private static final String REGEX_TAG = "(" + TAG + "|" + CLOSE_TAG + "|" + COMMENT + "|" + PROCESSING_INSTRUCTION //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ "|" + XML_DECLARATION + "|" + CDATA + ").*"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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
