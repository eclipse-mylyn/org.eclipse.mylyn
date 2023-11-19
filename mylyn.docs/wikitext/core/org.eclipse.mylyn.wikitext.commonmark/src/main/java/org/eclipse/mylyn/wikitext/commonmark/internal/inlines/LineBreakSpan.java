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
		return Optional.empty();
	}

}
