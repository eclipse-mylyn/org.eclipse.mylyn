/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class HtmlParserTest {
	static class EndEvent {

		private final String name;

		public EndEvent(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(EndEvent.class).add("name", name).toString();
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
	public void blockCode() {
		assertParse("<pre><code>some\ncode</code></pre>", "<body><pre><code>some\ncode</code></pre></body>");
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

	private void assertParseEventOrder(String content, Object... expectedEventTypes) {
		final List<Object> actualEventTypes = Lists.newArrayList();
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
		assertEquals(ImmutableList.copyOf(expectedEventTypes), actualEventTypes);
	}

	@Test
	public void blockPreformatted() {
		assertParse("<pre>some\ncode</pre>", "<body><pre>some\ncode</pre></body>");
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
		} catch (IOException e) {
			throw Throwables.propagate(e);
		} catch (SAXException e) {
			throw Throwables.propagate(e);
		}
	}

}
