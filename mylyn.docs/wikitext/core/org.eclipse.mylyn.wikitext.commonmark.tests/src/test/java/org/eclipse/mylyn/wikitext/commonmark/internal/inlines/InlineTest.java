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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.Locator;
import org.junit.Test;

public class InlineTest {

	@Test
	public void create() {
		Line line = new Line(3, 5, "text");
		Inline inline = new Inline(line, 8, 3) {

			@Override
			public void emit(DocumentBuilder builder) {
			}
		};
		assertSame(line, inline.getLine());
		assertEquals(8, inline.getOffset());
		assertEquals(3, inline.getLength());
		Locator locator = inline.getLocator();
		assertEquals(3, locator.getLineCharacterOffset());
		assertEquals(6, locator.getLineSegmentEndOffset());
		assertEquals(8, locator.getDocumentOffset());
		assertEquals(line.getOffset(), locator.getLineDocumentOffset());
		assertEquals(line.getText().length(), locator.getLineLength());
		assertEquals(line.getLineNumber() + 1, locator.getLineNumber());
	}

	@Test
	public void createContext() {
		ProcessingContextBuilder builder = ProcessingContext.builder();
		new Inline(new Line(1, 2, "text"), 0, 1) {

			@Override
			public void emit(DocumentBuilder builder) {
			}
		}.createContext(builder);
		ProcessingContext context = builder.build();
		assertTrue(context.isEmpty());
	}
}
