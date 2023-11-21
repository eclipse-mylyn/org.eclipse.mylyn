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

import com.google.common.base.Strings;

public class CodeSpan extends SourceSpan {

	Pattern pattern = Pattern.compile("(`+).*", Pattern.DOTALL | Pattern.MULTILINE);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '`' && (!cursor.hasPrevious() || cursor.getPrevious() != '`')) {
			Matcher matcher = cursor.matcher(pattern);
			if (matcher.matches()) {
				String openingBackticks = matcher.group(1);
				int backtickCount = openingBackticks.length();
				Pattern closingPattern = Pattern.compile("(?<!`)(" + Strings.repeat("`", backtickCount) + ")([^`]|$)",
						Pattern.DOTALL | Pattern.MULTILINE);
				cursor.advance(backtickCount);
				String textAtOffset = cursor.getTextAtOffset();
				cursor.rewind(backtickCount);

				Matcher closingMatcher = closingPattern.matcher(textAtOffset);
				if (closingMatcher.find()) {
					String codeText = textAtOffset.substring(0, closingMatcher.start());
					return Optional.of(new Code(cursor.getLineAtOffset(), cursor.getOffset(), backtickCount, codeText));
				}
			}
		}
		return Optional.empty();
	}
}
