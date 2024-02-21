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

import com.google.common.base.CharMatcher;

public class BackslashEscapeSpan extends SourceSpan {

	private static CharMatcher ESCAPABLE = CharMatcher.anyOf("!\"\\#$%&'()*+,-./:;<=>?@[]^_`{|}~"); //$NON-NLS-1$

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '\\' && cursor.hasNext()) {
			if (cursor.getNext() == '\n') {
				return Optional.of(new HardLineBreak(cursor.getLineAtOffset(), cursor.getOffset(), 2));
			} else if (ESCAPABLE.matches(cursor.getNext())) {
				return Optional
						.of(new EscapedCharacter(cursor.getLineAtOffset(), cursor.getOffset(), cursor.getNext()));
			}
		}
		return Optional.empty();
	}

}
