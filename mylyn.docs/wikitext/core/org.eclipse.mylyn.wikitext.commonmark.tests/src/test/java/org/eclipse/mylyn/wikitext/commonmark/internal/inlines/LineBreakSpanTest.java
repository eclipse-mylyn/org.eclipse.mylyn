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

import static org.eclipse.mylyn.wikitext.commonmark.internal.inlines.Cursors.createCursor;

import org.junit.Test;

@SuppressWarnings("nls")
public class LineBreakSpanTest extends AbstractSourceSpanTest {

	public LineBreakSpanTest() {
		super(new LineBreakSpan());
	}

	@Test
	public void createInline() {
		assertInline(SoftLineBreak.class, 1, 1, createCursor("a\nb", 1));
		assertInline(SoftLineBreak.class, 1, 2, createCursor("a \nb", 1));
		assertInline(SoftLineBreak.class, 1, 2, createCursor("a \nb\nc", 1));
		assertInline(HardLineBreak.class, 1, 4, createCursor("a   \nb", 1));
		assertInline(HardLineBreak.class, 1, 3, createCursor("a \\\nb", 1));
		assertNoInline(createCursor("one"));
	}
}
