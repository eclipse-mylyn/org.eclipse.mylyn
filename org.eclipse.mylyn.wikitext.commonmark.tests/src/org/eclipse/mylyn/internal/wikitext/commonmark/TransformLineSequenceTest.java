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

import com.google.common.base.Function;

public class TransformLineSequenceTest {

	static final class UpperCaseTransform implements Function<Line, Line> {

		@Override
		public Line apply(Line line) {
			return new Line(line.getLineNumber(), line.getOffset(), line.getText().toUpperCase());
		}
	}

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	private final Function<Line, Line> transform = new UpperCaseTransform();

	private final LineSequence delegate = LineSequence.create("first\nsecond");

	private final LineSequence lineSequence = new TransformLineSequence(delegate, transform);

	@Test
	public void requiresDelegate() {
		thrown.expect(NullPointerException.class);
		new TransformLineSequence(null, transform);
	}

	@Test
	public void requiresTransform() {
		thrown.expect(NullPointerException.class);
		new TransformLineSequence(delegate, null);
	}

	@Test
	public void getCurrentLine() {
		assertEquals("FIRST", lineSequence.getCurrentLine().getText());
		lineSequence.advance();
		assertEquals("SECOND", lineSequence.getCurrentLine().getText());
		lineSequence.advance();
		assertNull(lineSequence.getCurrentLine());
	}

	@Test
	public void getNextLine() {
		assertEquals("SECOND", lineSequence.getNextLine().getText());
		lineSequence.advance();
		assertNull(lineSequence.getNextLine());
	}

	@Test
	public void lookAhead() {
		lineSequence.advance();
		LineSequence lookAhead = lineSequence.lookAhead();
		assertEquals("SECOND", lookAhead.getCurrentLine().getText());
		lookAhead.advance();
		assertNull(lookAhead.getCurrentLine());
		assertEquals("SECOND", lineSequence.getCurrentLine().getText());
	}
}
