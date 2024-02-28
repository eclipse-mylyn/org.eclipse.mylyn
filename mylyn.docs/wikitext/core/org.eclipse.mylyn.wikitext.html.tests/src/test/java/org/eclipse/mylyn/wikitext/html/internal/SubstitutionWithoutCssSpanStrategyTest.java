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

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.event.BeginSpanEvent;
import org.eclipse.mylyn.wikitext.parser.builder.event.DocumentBuilderEvent;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class SubstitutionWithoutCssSpanStrategyTest {

	@Test(expected = NullPointerException.class)
	public void createNull() {
		new SubstitutionWithoutCssSpanStrategy(null);
	}

	@Test
	public void test() {
		SubstitutionWithoutCssSpanStrategy strategy = new SubstitutionWithoutCssSpanStrategy(SpanType.BOLD);
		EventDocumentBuilder builder = new EventDocumentBuilder();
		strategy.beginSpan(builder, SpanType.ITALIC, new Attributes("1", "class", "style", "lang"));
		List<DocumentBuilderEvent> events = builder.getDocumentBuilderEvents().getEvents();
		assertEquals(List.of(new BeginSpanEvent(SpanType.BOLD, new Attributes("1", "class", null, "lang"))), events);
	}
}
