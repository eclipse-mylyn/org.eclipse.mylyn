/*******************************************************************************
 * Copyright (c) 2014, 2024 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.Arrays;

import org.eclipse.mylyn.wikitext.html.HtmlLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndSpanEvent;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class FontElementStrategyTest {

	private final FontElementStrategy strategy = new FontElementStrategy();

	@Test
	public void hasMatcher() {
		assertNotNull(strategy.matcher());
	}

	@Test
	public void matchesSpanWithColor() {
		assertTrue(strategy.matcher().matches(SpanType.SPAN, new Attributes(null, null, "color: red", null)));
	}

	@Test
	public void matchesSpanWithFontSize() {
		assertTrue(strategy.matcher().matches(SpanType.SPAN, new Attributes(null, null, "font-size: 10", null)));
	}

	@Test
	public void matchesSpanWithFontFamily() {
		assertTrue(
				strategy.matcher().matches(SpanType.SPAN, new Attributes(null, null, "font-family: something", null)));
	}

	@Test
	public void matchesSpanWithColorAndFontSize() {
		assertTrue(strategy.matcher()
				.matches(SpanType.SPAN, new Attributes(null, null, "color: blue;font-size: 10", null)));
	}

	@Test
	public void matchesSpanNegative() {
		assertFalse(strategy.matcher().matches(SpanType.SPAN, new Attributes(null, null, "unrecognized: rule", null)));
	}

	@Test
	public void matchesSpansNegative() {
		for (SpanType spanType : SpanType.values()) {
			if (spanType != SpanType.SPAN) {
				assertFalse(strategy.matcher().matches(spanType, new Attributes()));
			}
		}
	}

	@Test
	public void hasSpanStrategy() {
		assertNotNull(strategy.spanStrategy());
	}

	@Test
	public void spanStrategyBuildsNonHtml() {
		EventDocumentBuilder builder = new EventDocumentBuilder();
		SpanStrategy spanStrategy = strategy.spanStrategy();
		spanStrategy.beginSpan(builder, SpanType.SPAN, new Attributes(null, null, "color: red", null));
		builder.characters("test");
		spanStrategy.endSpan(builder);

		assertEquals(
				Arrays.asList(new BeginSpanEvent(SpanType.SPAN, new Attributes(null, null, "color: red", null)),
						new CharactersEvent("test"), new EndSpanEvent()),
				builder.getDocumentBuilderEvents().getEvents());
	}

	@Test
	public void spanStrategyBuildsHtml() {
		assertHtmlFromSpanWithCss("test", "");
		assertHtmlFromSpanWithCss("test", "unknown:rule");
		assertHtmlFromSpanWithCss("<font color=\"red\">test</font>", "color: red");
		assertHtmlFromSpanWithCss("<font color=\"blue\" size=\"15px\">test</font>", "color: blue;font-size: 15px");
		assertHtmlFromSpanWithCss("<font face=\"monospace\" size=\"15px\">test</font>",
				"font-size: 15px;font-family: monospace");
	}

	@Test
	public void stateful() {
		assertNotSame(strategy.spanStrategy(), strategy.spanStrategy());
	}

	@Test
	public void spanStrategyNestedSpansBuildsCorrectHtml() {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = HtmlLanguage.builder()
				.add(BlockType.PARAGRAPH)
				.addSpanFont()
				.name("Test")
				.create()
				.createDocumentBuilder(out);
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "color:blue;", null));
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "", null));
		builder.beginSpan(SpanType.SPAN, new Attributes(null, null, "font-size: 15px", null));
		builder.characters("test");
		builder.endSpan();
		builder.endSpan();
		builder.endSpan();

		assertEquals("<font color=\"blue\"><font size=\"15px\">test</font></font>", out.toString());
	}

	void assertHtmlFromSpanWithCss(String expectedHtml, String spanCssStyle) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		SpanStrategy spanStrategy = strategy.spanStrategy();
		spanStrategy.beginSpan(builder, SpanType.SPAN, new Attributes(null, null, spanCssStyle, null));
		builder.characters("test");
		spanStrategy.endSpan(builder);

		assertEquals(expectedHtml, out.toString());
	}
}
