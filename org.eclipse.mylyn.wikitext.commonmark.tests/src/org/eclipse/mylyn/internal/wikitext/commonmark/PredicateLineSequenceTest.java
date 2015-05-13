/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class PredicateLineSequenceTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void requiresDelegate() {
		thrown.expect(NullPointerException.class);
		new PredicateLineSequence(null, Predicates.<Line> alwaysTrue());
	}

	@Test
	public void requiresPredicate() {
		thrown.expect(NullPointerException.class);
		new PredicateLineSequence(LineSequence.create(""), null);
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
		return new Predicate<Line>() {

			@Override
			public boolean apply(Line input) {
				return !input.getText().equals("two");
			}
		};
	}
}
