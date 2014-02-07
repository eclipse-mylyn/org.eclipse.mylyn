/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndSpanEvent;
import org.junit.Test;

import com.google.common.collect.Lists;

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
	public void matchesSpanWithColorAndFontSize() {
		assertTrue(strategy.matcher().matches(SpanType.SPAN,
				new Attributes(null, null, "color: blue;font-size: 10", null)));
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

		assertEquals(Lists.newArrayList(new BeginSpanEvent(SpanType.SPAN,
				new Attributes(null, null, "color: red", null)), new CharactersEvent("test"), new EndSpanEvent()),
				builder.getDocumentBuilderEvents().getEvents());
	}

	@Test
	public void spanStrategyBuildsHtml() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		SpanStrategy spanStrategy = strategy.spanStrategy();
		spanStrategy.beginSpan(builder, SpanType.SPAN, new Attributes(null, null, "color: red", null));
		builder.characters("test");
		spanStrategy.endSpan(builder);

		assertEquals("<font color=\"red\">test</font>", out.toString());
	}
}
