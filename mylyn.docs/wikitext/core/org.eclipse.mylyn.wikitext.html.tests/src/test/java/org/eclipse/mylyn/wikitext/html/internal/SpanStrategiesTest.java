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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.junit.Test;

@SuppressWarnings("nls")
public class SpanStrategiesTest {

	@Test(expected = NullPointerException.class)
	public void createNullElementTypes() {
		new SpanStrategies(null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void createNullSpanStrategies() {
		new SpanStrategies(Collections.emptySet(), null);
	}

	@Test
	public void createEmpty() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(), Collections.emptyList());
		assertNotNull(strategies.getStrategy(SpanType.BOLD, new Attributes()));
	}

	@Test
	public void createNonEmpty() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.BOLD, SpanType.CODE)),
				Collections.emptyList());
		assertSupported(strategies, SpanType.BOLD);
		assertSupported(strategies, SpanType.CODE);
		for (SpanType spanType : SpanType.values()) {
			assertNotNull(strategies.getStrategy(spanType, new Attributes()));
		}
	}

	@Test
	public void alternatives() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.BOLD)),
				Collections.emptyList());
		assertTrue(strategies.getStrategy(SpanType.STRONG, new Attributes()) instanceof SubstitutionSpanStrategy);
	}

	@Test
	public void spanWithFontWeightToBold() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.BOLD)),
				Collections.emptyList());
		SpanStrategy strategy = strategies.getStrategy(SpanType.SPAN,
				new Attributes(null, null, "font-weight:  bold", null));
		assertTrue(strategy instanceof SubstitutionWithoutCssSpanStrategy);
		assertEquals(SpanType.BOLD, ((SubstitutionWithoutCssSpanStrategy) strategy).getType());
	}

	@Test
	public void spanWithFontWeightToStrong() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.STRONG)),
				Collections.emptyList());
		SpanStrategy strategy = strategies.getStrategy(SpanType.SPAN,
				new Attributes(null, null, "font-weight:  bold", null));
		assertTrue(strategy instanceof SubstitutionWithoutCssSpanStrategy);
		assertEquals(SpanType.STRONG, ((SubstitutionWithoutCssSpanStrategy) strategy).getType());
	}

	@Test
	public void spanWithTextDecorationUnderlineToUnderlined() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.UNDERLINED)),
				Collections.emptyList());
		SpanStrategy strategy = strategies.getStrategy(SpanType.SPAN,
				new Attributes(null, null, "text-decoration:  underline;", null));
		assertTrue(strategy instanceof SubstitutionWithoutCssSpanStrategy);
		assertEquals(SpanType.UNDERLINED, ((SubstitutionWithoutCssSpanStrategy) strategy).getType());
	}

	@Test
	public void spanWithTextDecorationLinethroughToDeleted() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.DELETED)),
				Collections.emptyList());
		SpanStrategy strategy = strategies.getStrategy(SpanType.SPAN,
				new Attributes(null, null, "text-decoration:  line-through;", null));
		assertTrue(strategy instanceof SubstitutionWithoutCssSpanStrategy);
		assertEquals(SpanType.DELETED, ((SubstitutionWithoutCssSpanStrategy) strategy).getType());
	}

	@Test
	public void spanWithUnrecognizedCssToUnsupported() {
		SpanStrategies strategies = new SpanStrategies(new HashSet<>(Arrays.asList(SpanType.BOLD)),
				Collections.emptyList());
		assertTrue(strategies.getStrategy(SpanType.SPAN,
				new Attributes(null, null, "font-weight:unknown", null)) instanceof UnsupportedSpanStrategy);
	}

	private void assertSupported(SpanStrategies strategies, SpanType spanType) {
		assertTrue(strategies.getStrategy(spanType, new Attributes()) instanceof SupportedSpanStrategy);
	}
}
