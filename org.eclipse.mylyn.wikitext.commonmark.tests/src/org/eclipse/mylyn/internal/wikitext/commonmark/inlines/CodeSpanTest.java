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

import static org.eclipse.mylyn.internal.wikitext.commonmark.inlines.Cursors.createCursor;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CodeSpanTest extends AbstractSourceSpanTest {

	public CodeSpanTest() {
		super(new CodeSpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("``one`"));
		assertNoInline(createCursor("two"));
		assertNoInline(createCursor("``"));
		assertCode(14, "this is code", "`this is code`");
		assertCode(12, "one\ntwo\n", "``one\ntwo\n``");
		assertCode(14, "one *two", "```one *two```");
		assertCode(14, "one *two` ", "``one *two` ``");
	}

	private void assertCode(int length, String codeText, String content) {
		Code code = assertInline(Code.class, 0, length, createCursor(content));
		assertEquals(codeText, code.getText());
	}
}
