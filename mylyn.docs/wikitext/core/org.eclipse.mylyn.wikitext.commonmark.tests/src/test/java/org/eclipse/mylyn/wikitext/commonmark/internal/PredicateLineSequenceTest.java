/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.Predicate;

import org.junit.Test;

@SuppressWarnings("nls")
public class PredicateLineSequenceTest {

	@Test(expected = NullPointerException.class)
	public void requiresDelegate() {
		assertNotNull(new PredicateLineSequence(null, x -> true));
	}

	@Test(expected = NullPointerException.class)
	public void requiresPredicate() {
		assertNotNull(new PredicateLineSequence(LineSequence.create(""), null));
	}

	@Test
	public void linesWithPredicate() {
		LineSequence originalLineSequence = LineSequence.create("one\ntwo");
		PredicateLineSequence lineSequence = new PredicateLineSequence(originalLineSequence, notTwoPredicate());
		assertEquals("one", lineSequence.getCurrentLine().getText());
		assertNull(lineSequence.getNextLine());
		lineSequence.advance();
		assertNull(lineSequence.getCurrentLine());
		assertNull(lineSequence.getNextLine());
		lineSequence.advance();
		assertEquals("two", originalLineSequence.getCurrentLine().getText());
	}

	@Test
	public void lookAhead() {
		LineSequence originalLineSequence = LineSequence.create("one\ntwo");
		PredicateLineSequence lineSequence = new PredicateLineSequence(originalLineSequence, notTwoPredicate());
		LineSequence lookAhead = lineSequence.lookAhead();
		assertEquals("one", lookAhead.getCurrentLine().getText());
		assertNull(lookAhead.getNextLine());
		lookAhead.advance();
		assertNull(lookAhead.getCurrentLine());
		assertNull(lookAhead.getNextLine());
		assertEquals("one", lineSequence.getCurrentLine().getText());
	}

	private Predicate<Line> notTwoPredicate() {
		return input -> !input.getText().equals("two");
	}
}
