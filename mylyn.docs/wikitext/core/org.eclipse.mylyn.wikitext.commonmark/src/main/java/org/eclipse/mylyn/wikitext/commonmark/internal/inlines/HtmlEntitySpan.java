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

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;

public class HtmlEntitySpan extends SourceSpan {

	private final Pattern pattern = Pattern.compile("&(#x[a-f0-9]{1,8}|#[0-9]{1,8}|[a-z][a-z0-9]{1,31});.*", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '&') {
			Matcher matcher = cursor.matcher(pattern);
			if (matcher.matches()) {
				String ent = matcher.group(1);

				int offset = cursor.getOffset();
				int length = ent.length() + 2;
				Line lineAtOffset = cursor.getLineAtOffset();

				if (isInvalidUnicodeCodepoint(ent)) {
					return Optional.of(new Characters(lineAtOffset, offset, length, "\ufffd")); //$NON-NLS-1$
				}
				return Optional.of(new HtmlEntity(lineAtOffset, offset, length, ent));
			}
		}
		return Optional.empty();
	}

	protected boolean isInvalidUnicodeCodepoint(String ent) {
		if (ent.charAt(0) == '#') {
			try {
				int codePoint;
				char firstCharFollowingHash = ent.charAt(1);
				if (firstCharFollowingHash == 'x' || firstCharFollowingHash == 'X') {
					codePoint = Integer.parseInt(ent.substring(2), 16);
				} else {
					codePoint = Integer.parseInt(ent.substring(1));
				}
				return codePoint <= 0 || codePoint > 0xffff;
			} catch (NumberFormatException e) {
				return true;
			}
		}
		return false;
	}
}
