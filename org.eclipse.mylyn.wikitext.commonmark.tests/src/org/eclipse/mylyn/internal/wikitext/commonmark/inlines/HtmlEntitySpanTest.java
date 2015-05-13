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
	}

	private void assertEntity(int length, String entity, String content) {
		HtmlEntity htmlEntity = assertInline(HtmlEntity.class, 0, length, createCursor(content));
		assertEquals(entity, htmlEntity.getText());
	}
}
