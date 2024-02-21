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
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static org.eclipse.mylyn.wikitext.commonmark.internal.inlines.Cursors.createCursor;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("nls")
public class CodeSpanTest extends AbstractSourceSpanTest {

	public CodeSpanTest() {
		super(new CodeSpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("``one`"));
		assertNoInline(createCursor("two"));
		assertNoInline(createCursor("``"));
		assertNoInline(createCursor("`one``"));
		assertCode(14, "this is code", "`this is code`");
		assertCode(12, "one\ntwo\n", "``one\ntwo\n``");
		assertCode(14, "one *two", "```one *two```");
		assertCode(14, "one *two` ", "``one *two` ``");
	}

	@Test
	public void createInlineBackticksMustMatch() {
		Cursor cursor = createCursor("``one`");
		cursor.advance();
		assertNoInline(cursor, 1);
	}

	private void assertCode(int length, String codeText, String content) {
		Code code = assertInline(Code.class, 0, length, createCursor(content));
		assertEquals(codeText, code.getText());
	}
}
