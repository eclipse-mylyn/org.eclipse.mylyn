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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BackslashEscapeSpanTest extends AbstractSourceSpanTest {

	public BackslashEscapeSpanTest() {
		super(new BackslashEscapeSpan());
	}

	@Test
	public void backslashEscapes() {
		assertNoInline(Cursors.createCursor("\\"));
		assertNoInline(Cursors.createCursor("\\a"));
		assertEscapedCharacter('\\', 0, 2, Cursors.createCursor("\\\\"));
		assertEscapedCharacter('*', 0, 2, Cursors.createCursor("\\*"));
		assertEscapedCharacter('_', 0, 2, Cursors.createCursor("\\_*"));
	}

	private void assertEscapedCharacter(char ch, int offset, int length, Cursor cursor) {
		EscapedCharacter escapedCharacter = assertInline(EscapedCharacter.class, offset, length, cursor);
		assertEquals(ch, escapedCharacter.getCharacter());
	}
}
