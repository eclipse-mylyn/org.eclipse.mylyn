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

package org.eclipse.mylyn.wikitext.commonmark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.LocatorImpl;
import org.junit.Test;

public class CommonMarkLanguageDocumentOffsetsTest {

	@Test
	public void blockQuoteWithBoldSpanUnix() {
		assertSpanOffsets(6, 15, "<blockquote><p>one <strong>two\nthree</strong> four</p></blockquote>",
				"> one **two\n> three** four");
	}

	@Test
	public void blockQuoteWithBoldSpanWindows() {
		assertSpanOffsets(6, 16, "<blockquote><p>one <strong>two\nthree</strong> four</p></blockquote>",
				"> one **two\r\n> three** four");
	}

	@Test
	public void blockQuoteWithBoldSpanMac() {
		assertSpanOffsets(6, 15, "<blockquote><p>one <strong>two\nthree</strong> four</p></blockquote>",
				"> one **two\r> three** four");
	}

	@Test
	public void blockQuoteWithNestedListAndEmphasis() {
		assertSpanOffsets(8, 16, "<blockquote><ul><li>one <em>two\nthree</em></li></ul></blockquote>",
				"> * one *two\r\n>   three*");
	}

	private void assertSpanOffsets(int offset, int length, String expectedHtml, String markup) {
		final AtomicReference<Locator> spanLocator = new AtomicReference<Locator>();
		CommonMarkLanguage language = new CommonMarkLanguage();
		MarkupParser parser = new MarkupParser(language);
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out) {

			@Override
			public void beginSpan(SpanType type, Attributes attributes) {
				assertNull(spanLocator.get());
				spanLocator.set(new LocatorImpl(getLocator()));
				super.beginSpan(type, attributes);
			}
		};
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(markup);

		assertEquals(expectedHtml, out.toString());

		Locator locator = spanLocator.get();
		assertNotNull(locator);
		assertEquals(offset, locator.getDocumentOffset());
		int actualLength = locator.getLineSegmentEndOffset() - locator.getLineCharacterOffset();
		assertEquals(length, actualLength);
	}
}
