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

import com.google.common.base.CharMatcher;

public class PotentialEmphasisSpan extends SourceSpan {

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if ((c == '_' || c == '*') && !currentPositionIsEscaped(cursor)) {
			int length = lengthMatching(cursor, c);

			boolean leftFlanking = isLeftFlanking(cursor, length);
			boolean rightFlanking = isRightFlanking(cursor, length);

			boolean canOpen = leftFlanking;
			boolean canClose = rightFlanking;
			if (c == '_') {
				canOpen = leftFlanking && (!rightFlanking || isPunctuation(charBefore(cursor)));
				canClose = rightFlanking && (!leftFlanking || isPunctuation(charAfter(cursor, length)));
			}
			return Optional.of(new PotentialEmphasisDelimiter(cursor.getLineAtOffset(), cursor.getOffset(), length,
					cursor.getTextAtOffset(length), canOpen, canClose));
		}
		return Optional.empty();
	}

	boolean isLeftFlanking(Cursor cursor, int length) {
		char charBefore = charBefore(cursor);
		char charAfter = charAfter(cursor, length);
		return !isWhitespace(charAfter)
				&& !(isPunctuation(charAfter) && !isWhitespace(charBefore) && !isPunctuation(charBefore));
	}

	private char charAfter(Cursor cursor, int length) {
		return cursor.hasNext(length) ? cursor.getNext(length) : '\n';
	}

	private char charBefore(Cursor cursor) {
		return cursor.hasPrevious() ? cursor.getPrevious() : '\n';
	}

	boolean isRightFlanking(Cursor cursor, int length) {
		char charBefore = charBefore(cursor);
		char charAfter = charAfter(cursor, length);
		return !isWhitespace(charBefore)
				&& !(isPunctuation(charBefore) && !isWhitespace(charAfter) && !isPunctuation(charAfter));

	}

	private boolean isWhitespace(char c) {
		return CharMatcher.whitespace().matches(c);
	}

	private boolean isPunctuation(char c) {
		String punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`,{|}~";
		return punctuation.indexOf(c) >= 0;
	}

	private boolean currentPositionIsEscaped(Cursor cursor) {
		int backslashCount = 0;
		for (int x = 1; cursor.hasPrevious(x) && cursor.getPrevious(x) == '\\'; ++x) {
			++backslashCount;
		}
		return backslashCount % 2 == 1;
	}

	private int lengthMatching(Cursor cursor, char c) {
		int x = 1;
		while (cursor.hasNext(x) && cursor.getNext(x) == c) {
			++x;
		}
		return x;
	}

	static boolean isLetterOrDigit(char previous) {
		return (previous >= '0' && previous <= '9') || (previous >= 'A' && previous <= 'Z')
				|| (previous >= 'a' && previous <= 'z');
	}
}
