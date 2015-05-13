/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

public class CodeSpan extends SourceSpan {

	Pattern pattern = Pattern.compile("(`+).*", Pattern.DOTALL | Pattern.MULTILINE);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '`') {
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
		return Optional.absent();
	}
}
