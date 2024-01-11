/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvents;
import org.eclipse.mylyn.wikitext.parser.builder.event.EndSpanEvent;
import org.junit.Test;

public class CompositeSpanStrategyTest {

	@Test(expected = NullPointerException.class)
	public void createNull() {
		new CompositeSpanStrategy(null);
	}

	@Test
	public void test() {
		CompositeSpanStrategy strategy = new CompositeSpanStrategy(
				new ArrayList<>(Arrays.asList(new SubstitutionWithoutCssSpanStrategy(SpanType.BOLD),
						new SubstitutionWithoutCssSpanStrategy(SpanType.ITALIC))));
		EventDocumentBuilder builder = new EventDocumentBuilder();
		strategy.beginSpan(builder, SpanType.DELETED, new Attributes());
		strategy.endSpan(builder);
		DocumentBuilderEvents events = builder.getDocumentBuilderEvents();
		assertEquals(
				List.of(new BeginSpanEvent(SpanType.BOLD, new Attributes()),
						new BeginSpanEvent(SpanType.ITALIC, new Attributes()), new EndSpanEvent(), new EndSpanEvent()),
				events.getEvents());

	}
}
