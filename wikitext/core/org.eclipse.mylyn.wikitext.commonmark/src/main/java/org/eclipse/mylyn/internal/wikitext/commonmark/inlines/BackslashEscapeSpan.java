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

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;

public class BackslashEscapeSpan extends SourceSpan {

	private static CharMatcher ESCAPABLE = CharMatcher.anyOf("!\"\\#$%&'()*+,-./:;<=>?@[]^_`{|}~");

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '\\' && cursor.hasNext()) {
			if (cursor.getNext() == '\n') {
				return Optional.of(new HardLineBreak(cursor.getLineAtOffset(), cursor.getOffset(), 2));
			} else if (ESCAPABLE.matches(cursor.getNext())) {
				return Optional.of(new EscapedCharacter(cursor.getLineAtOffset(), cursor.getOffset(), cursor.getNext()));
			}
		}
		return Optional.absent();
	}

}
