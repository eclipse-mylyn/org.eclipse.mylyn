/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.internal.parser.html.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.internal.parser.html.AbstractSaxHtmlParser;
import org.eclipse.mylyn.wikitext.internal.parser.html.HtmlParser;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.NoOpDocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("nls")
public class HtmlParserTest {
	static class EndEvent {

		private final String name;

		public EndEvent(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final EndEvent END_BLOCK = new EndEvent("end block");

	private static final EndEvent END_SPAN = new EndEvent("end span");

	private AbstractSaxHtmlParser parser;

	@Before
	public final void initializeParser() {
		parser = createParser();
	}

	protected AbstractSaxHtmlParser createParser() {
		return new HtmlParser();
	}

	@Test
	public void empty() {
		assertParse("", "<html/>");
	}

	@Test
	public void blockOrderedList() {
		assertParse("<ol><li>item</li><li>item</li></ol>", "<body><ol><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListArabic() {
		assertParse("<ol style=\"list-style-type: decimal;\"><li>item</li><li>item</li></ol>",
				"<body><ol type=\"1\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListLoweralpha() {
		assertParse("<ol style=\"list-style-type: lower-alpha;\"><li>item</li><li>item</li></ol>",
				"<body><ol type=\"a\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListUpperalpha() {
		assertParse("<ol style=\"list-style-type: upper-alpha;\"><li>item</li><li>item</li></ol>",
				"<body><ol type=\"A\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListLowerroman() {
		assertParse("<ol style=\"list-style-type: lower-roman;\"><li>item</li><li>item</li></ol>",
				"<body><ol type=\"i\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListUpperroman() {
		assertParse("<ol style=\"list-style-type: upper-roman;\"><li>item</li><li>item</li></ol>",
				"<body><ol type=\"I\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListArabicCss() {
		assertParse("<ol style=\"list-style-type: decimal;\"><li>item</li><li>item</li></ol>",
				"<body><ol style=\"list-style-type: decimal;\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListLoweralphaCss() {
		assertParse("<ol style=\"list-style-type: lower-alpha;\"><li>item</li><li>item</li></ol>",
				"<body><ol style=\"list-style-type: lower-alpha;\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListUpperalphaCss() {
		assertParse("<ol style=\"list-style-type: upper-alpha;\"><li>item</li><li>item</li></ol>",
				"<body><ol style=\"list-style-type: upper-alpha;\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListLowerromanCss() {
		assertParse("<ol style=\"list-style-type: lower-roman;\"><li>item</li><li>item</li></ol>",
				"<body><ol style=\"list-style-type: lower-roman;\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockOrderedListUpperromanCss() {
		assertParse("<ol style=\"list-style-type: upper-roman;\"><li>item</li><li>item</li></ol>",
				"<body><ol style=\"list-style-type: upper-roman;\"><li>item</li><li>item</li></ol></body>");
	}

	@Test
	public void blockCode() {
		assertParse("<pre><code>some\ncode</code></pre>", "<body><pre><code>some\ncode</code></pre></body>");
	}

	@Test
	public void spanMark() {
		assertParse("<mark>marked text</mark>", "<body><mark>marked text</mark></body>");
	}

	@Test
	public void blockCodeEventOrder() {
		assertParseEventOrder("<body><pre><code>some\ncode</code></pre></body>", BlockType.CODE, "some\ncode",
				END_BLOCK);
	}

	@Test
	public void blockPreformattedEventOrder() {
		assertParseEventOrder("<body><pre>some\ncode</pre></body>", BlockType.PREFORMATTED, "some\ncode", END_BLOCK);
	}

	@Test
	public void blockDeletedDel() {
		assertParseEventOrder("<body><del>lorem</del></body>", SpanType.DELETED, "lorem", END_SPAN);
	}

	@Test
	public void blockDeletedStrike() {
		assertParseEventOrder("<body><strike>lorem</strike></body>", SpanType.DELETED, "lorem", END_SPAN);
	}

	@Test
	public void blockDeletedS() {
		assertParseEventOrder("<body><s>lorem</s></body>", SpanType.DELETED, "lorem", END_SPAN);
	}

	@Test
	public void combinedEventOrder() {
		assertParseEventOrder(
				"<body><p>text <code>some code</code> more <i>italic <b>bold italic</b></i></p><pre><code>block code</code></pre></body>",
				BlockType.PARAGRAPH, "text ", SpanType.CODE, "some code", END_SPAN, " more ", SpanType.ITALIC,
				"italic ", SpanType.BOLD, "bold italic", END_SPAN, END_SPAN, END_BLOCK, BlockType.CODE, "block code",
				END_BLOCK);
	}

	@Test
	public void fontTagWithStyles() {
		assertParse("<span id=\"id123\" style=\"color: blue; font-size: 10; font-family: monospace;\">text</span>",
				"<body><font size=\"10\" face=\"monospace\" color=\"blue\" unknown=\"test\" id=\"id123\">text</font></body>");
	}

	@Test
	public void fontTagWithIdNoStyles() {
		assertParse("<span id=\"id123\">text</span>", "<body><font unknown=\"test\" id=\"id123\">text</font></body>");
	}

	@Test
	public void fontTagWithoutStylesOrId() {
		assertParse("text", "<body><font what=\"is this?\">text</font></body>");
	}

	@Test
	public void paragraphWithBr() {
		assertParse("<p>first<br/>second</p>", "<body><p>first<br/>\nsecond</p></body>");
	}

	@Test
	public void horizontalRule() {
		assertParse("<p>first</p><hr/><p>second</p>", "<body><p>first</p>\n<hr/><p>second</p></body>");
	}

	@Test
	public void blockMark() {
		assertParseEventOrder("<body><mark>lorem</mark></body>", SpanType.MARK, "lorem", END_SPAN);
	}

	private void assertParseEventOrder(String content, Object... expectedEventTypes) {
		final List<Object> actualEventTypes = new ArrayList<>();
		DocumentBuilder builder = new NoOpDocumentBuilder() {
			@Override
			public void beginBlock(BlockType type, Attributes attributes) {
				actualEventTypes.add(type);
			}

			@Override
			public void beginSpan(SpanType type, Attributes attributes) {
				actualEventTypes.add(type);
			}

			@Override
			public void characters(String text) {
				actualEventTypes.add(text);
			}

			@Override
			public void endBlock() {
				actualEventTypes.add(END_BLOCK);
			}

			@Override
			public void endSpan() {
				actualEventTypes.add(END_SPAN);
			}
		};
		parse(content, builder);
		assertEquals(List.of(expectedEventTypes), actualEventTypes);
	}

	@Test
	public void blockPreformatted() {
		assertParse("<pre>some\ncode</pre>", "<body><pre>some\ncode</pre></body>");
	}

	@Test
	public void paragraphWithSpacePreserved() {
		assertParse("<p>foo bar</p>", "<body><p>foo bar</p></body>");
	}

	@Test
	public void preformattedWithSpacePreserved() {
		assertParse("<pre>foo bar</pre>", "<body><pre>foo bar</pre></body>");
	}

	@Test
	public void paragraphWithExtraSpacesCollapsed() {
		assertParse("<p>foo bar</p>", "<body><p>foo  bar</p></body>");
	}

	@Test
	public void preformattedWithExtraSpacesPreserved() {
		assertParse("<pre>foo  bar</pre>", "<body><pre>foo  bar</pre></body>");
	}

	@Test
	public void paragraphWithIdeographicSpacePreserved() {
		assertParse("<p>foo　bar</p>", "<body><p>foo　bar</p></body>");
	}

	@Test
	public void preformattedWithIdeographicSpacePreserved() {
		assertParse("<pre>foo　bar</pre>", "<body><pre>foo　bar</pre></body>");
	}

	private void assertParse(String expected, String content) {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = new HtmlDocumentBuilder(out);
		parse(content, builder);
		assertEquals(expected, out.toString());
	}

	protected void parse(String content, DocumentBuilder builder) {
		try {
			parser.parse(new InputSource(new StringReader(content)), builder, false);
		} catch (IOException | SAXException e) {
			throw new RuntimeException(e);
		}
	}

}
