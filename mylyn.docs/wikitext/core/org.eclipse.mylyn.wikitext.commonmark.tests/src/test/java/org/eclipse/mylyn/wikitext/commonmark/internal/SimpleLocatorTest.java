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

import org.eclipse.mylyn.wikitext.parser.Locator;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class SimpleLocatorTest {

	@Test
	public void createFromLine() {
		Line line = new Line(2, 104, "one two");
		Locator simpleLocator = new SimpleLocator(line);
		assertEquals(104, simpleLocator.getDocumentOffset());
		assertEquals(0, simpleLocator.getLineCharacterOffset());
		assertEquals(104, simpleLocator.getLineDocumentOffset());
		assertEquals(7, simpleLocator.getLineLength());
		assertEquals(3, simpleLocator.getLineNumber());
		assertEquals(7, simpleLocator.getLineSegmentEndOffset());
	}

	@Test
	public void createFromLineWithSegmentOffset() {
		Line line = new Line(2, 104, "one two");
		Locator simpleLocator = new SimpleLocator(line, 2, 6);
		assertEquals(106, simpleLocator.getDocumentOffset());
		assertEquals(2, simpleLocator.getLineCharacterOffset());
		assertEquals(104, simpleLocator.getLineDocumentOffset());
		assertEquals(7, simpleLocator.getLineLength());
		assertEquals(3, simpleLocator.getLineNumber());
		assertEquals(6, simpleLocator.getLineSegmentEndOffset());
	}
}
