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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Optional;

public class LineBreakSpan extends SourceSpan {

	private final Pattern pattern = Pattern.compile("( *(\\\\)?\n).*", Pattern.DOTALL);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '\n' || c == ' ') {
			Matcher matcher = cursor.matcher(pattern);
			if (matcher.matches()) {
				int length = matcher.group(1).length();
				if (length > 2 || matcher.group(2) != null) {
					return Optional.of(new HardLineBreak(cursor.getLineAtOffset(), cursor.getOffset(), length));
				}
				return Optional.of(new SoftLineBreak(cursor.getLineAtOffset(), cursor.getOffset(), length));
			}
		}
		return Optional.absent();
	}

}
