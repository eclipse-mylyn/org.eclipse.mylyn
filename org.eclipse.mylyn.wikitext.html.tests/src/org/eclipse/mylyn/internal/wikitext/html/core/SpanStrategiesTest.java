/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Sets;

public class SpanStrategiesTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void createNull() {
		thrown.expect(NullPointerException.class);
		new SpanStrategies(null);
	}

	@Test
	public void createEmpty() {
		SpanStrategies strategies = new SpanStrategies(Sets.<SpanType> newHashSet());
		assertNotNull(strategies.getStrategy(SpanType.BOLD));
	}

	@Test
	public void createNonEmpty() {
		SpanStrategies strategies = new SpanStrategies(Sets.newHashSet(SpanType.BOLD, SpanType.CODE));
		assertSupported(strategies, SpanType.BOLD);
		assertSupported(strategies, SpanType.CODE);
		for (SpanType spanType : SpanType.values()) {
			assertNotNull(strategies.getStrategy(spanType));
		}
	}

	@Test
	public void alternatives() {
		SpanStrategies strategies = new SpanStrategies(Sets.newHashSet(SpanType.BOLD));
		assertTrue(strategies.getStrategy(SpanType.STRONG) instanceof SubstitutionSpanStrategy);
	}

	private void assertSupported(SpanStrategies strategies, SpanType spanType) {
		assertTrue(strategies.getStrategy(spanType) instanceof SupportedSpanStrategy);
	}
}
