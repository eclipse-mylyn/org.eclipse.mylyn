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

import java.util.function.Function;

import org.junit.Test;

public class TransformLineSequenceTest {

	static final class UpperCaseTransform implements Function<Line, Line> {

		@Override
		public Line apply(Line line) {
			return new Line(line.getLineNumber(), line.getOffset(), line.getText().toUpperCase());
		}
	}

	private final Function<Line, Line> transform = new UpperCaseTransform();

	private final LineSequence delegate = LineSequence.create("first\nsecond");

	private final LineSequence lineSequence = new TransformLineSequence(delegate, transform);

	@Test(expected = NullPointerException.class)
	public void requiresDelegate() {
		assertNotNull(new TransformLineSequence(null, transform));
	}

	@Test(expected = NullPointerException.class)
	public void requiresTransform() {
		assertNotNull(new TransformLineSequence(delegate, null));
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
