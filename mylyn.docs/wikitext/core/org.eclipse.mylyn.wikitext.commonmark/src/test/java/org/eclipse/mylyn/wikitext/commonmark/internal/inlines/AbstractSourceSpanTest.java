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

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.eclipse.mylyn.wikitext.commonmark.internal.spec.SimplifiedHtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;

abstract class AbstractSourceSpanTest {

	protected final SourceSpan span;

	public void assertNoInline(Cursor cursor) {
		assertNoInline(cursor, 0);
	}

	public void assertNoInline(Cursor cursor, int offset) {
		Optional<? extends Inline> optionalInline = span.createInline(cursor);
		assertFalse(optionalInline.isPresent());
		assertEquals(offset, cursor.getOffset());
	}

	@SuppressWarnings("unchecked")
	public <T extends Inline> T assertInline(Class<T> type, int offset, int length, Cursor cursor) {
		Optional<? extends Inline> optionalInline = span.createInline(cursor);
		assertTrue(optionalInline.isPresent());
		Inline inline = optionalInline.get();
		assertEquals(type, inline.getClass());
		assertEquals(offset, inline.getOffset());
		assertEquals(length, inline.getLength());

		return (T) inline;
	}

	public void assertParseToHtml(String expected, String markup) {
		StringWriter writer = new StringWriter();

		HtmlDocumentBuilder builder = new SimplifiedHtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		builder.beginDocument();

		InlineParser parser = new InlineParser(span, new AllCharactersSpan());
		List<Inline> inlines = parser.parse(ProcessingContext.builder().build(),
				new TextSegment(List.of(new Line(1, 0, markup))));
		for (Inline inline : inlines) {
			inline.emit(builder);
		}
		builder.endDocument();
		assertEquals(expected, writer.toString());
	}

	public AbstractSourceSpanTest(SourceSpan span) {
		this.span = requireNonNull(span);
	}
}
