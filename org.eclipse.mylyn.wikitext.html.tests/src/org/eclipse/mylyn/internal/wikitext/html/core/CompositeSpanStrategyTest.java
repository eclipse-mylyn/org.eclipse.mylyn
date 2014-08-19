/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvents;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndSpanEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CompositeSpanStrategyTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNull() {
		thrown.expect(NullPointerException.class);
		new CompositeSpanStrategy(null);
	}

	@Test
	public void test() {
		CompositeSpanStrategy strategy = new CompositeSpanStrategy(Lists.<SpanStrategy> newArrayList(
				new SubstitutionWithoutCssSpanStrategy(SpanType.BOLD), new SubstitutionWithoutCssSpanStrategy(
						SpanType.ITALIC)));
		EventDocumentBuilder builder = new EventDocumentBuilder();
		strategy.beginSpan(builder, SpanType.DELETED, new Attributes());
		strategy.endSpan(builder);
		DocumentBuilderEvents events = builder.getDocumentBuilderEvents();
		assertEquals(ImmutableList.of(new BeginSpanEvent(SpanType.BOLD, new Attributes()), new BeginSpanEvent(
				SpanType.ITALIC, new Attributes()), new EndSpanEvent(), new EndSpanEvent()), events.getEvents());

	}
}
