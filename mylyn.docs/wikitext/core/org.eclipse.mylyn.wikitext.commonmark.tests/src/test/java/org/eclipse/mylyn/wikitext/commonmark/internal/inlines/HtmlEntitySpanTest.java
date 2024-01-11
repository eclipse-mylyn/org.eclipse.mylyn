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

public class HtmlEntitySpanTest extends AbstractSourceSpanTest {

	public HtmlEntitySpanTest() {
		super(new HtmlEntitySpan());
	}

	@Test
	public void createInline() {
		assertNoInline(createCursor("one"));
		assertNoInline(createCursor("&copy"));
		assertEntity(6, "copy", "&copy; ayyy");
		assertEntity(5, "xa0", "&xa0; ayyy;");
		assertEntity(6, "#160", "&#160;");
		assertEntity(6, "nbsp", "&nbsp;");
		assertEntity(6, "nbsp", "&nbsp; ab\ncd");
		assertEntity(5, "#x9", "&#x9;");
		assertEntity(5, "#X9", "&#X9;");
		assertEntity(7, "#x912", "&#x912;");
		assertCharacters(4, "\ufffd", "&#0;");
		assertCharacters(5, "\ufffd", "&#00;");
		assertCharacters(8, "\ufffd", "&#65536;");
		assertCharacters(5, "\ufffd", "&#x0;");
		assertCharacters(9, "\ufffd", "&#xfffff;");
	}

	private void assertCharacters(int length, String text, String content) {
		Characters characters = assertInline(Characters.class, 0, length, createCursor(content));
		assertEquals(text, characters.getText());
	}

	private void assertEntity(int length, String entity, String content) {
		HtmlEntity htmlEntity = assertInline(HtmlEntity.class, 0, length, createCursor(content));
		assertEquals(entity, htmlEntity.getText());
	}
}
