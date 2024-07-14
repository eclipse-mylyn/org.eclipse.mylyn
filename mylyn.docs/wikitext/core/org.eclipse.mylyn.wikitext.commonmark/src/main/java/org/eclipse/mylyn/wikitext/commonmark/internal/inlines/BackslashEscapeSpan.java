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

import java.util.Arrays;
import java.util.Optional;

public class BackslashEscapeSpan extends SourceSpan {

	private static char[] ESCAPABLE;
	static {
		ESCAPABLE = "!\"\\#$%&'()*+,-./:;<=>?@[]^_`{|}~".toCharArray(); //$NON-NLS-1$
		Arrays.sort(ESCAPABLE);
	}

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		char c = cursor.getChar();
		if (c == '\\' && cursor.hasNext()) {
			if (cursor.getNext() == '\n') {
				return Optional.of(new HardLineBreak(cursor.getLineAtOffset(), cursor.getOffset(), 2));
			} else if (isEscapable(cursor.getNext())) {
				return Optional
						.of(new EscapedCharacter(cursor.getLineAtOffset(), cursor.getOffset(), cursor.getNext()));
			}
		}
		return Optional.empty();
	}

	private boolean isEscapable(char c) {
		return Arrays.binarySearch(ESCAPABLE, c) >= 0;
	}
}
