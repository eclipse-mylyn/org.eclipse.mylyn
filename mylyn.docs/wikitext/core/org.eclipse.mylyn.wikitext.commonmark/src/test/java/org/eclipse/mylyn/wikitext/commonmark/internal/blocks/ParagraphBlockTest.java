/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static org.eclipse.mylyn.wikitext.commonmark.internal.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.junit.Test;

public class ParagraphBlockTest {

	private final ParagraphBlock block = new ParagraphBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(LineSequence.create("")));
		assertFalse(block.canStart(LineSequence.create("\none")));
		assertTrue(block.canStart(LineSequence.create("one")));
	}

	@Test
	public void assertParagraph() {
		assertContent("<p>first para second line</p><p>second para fourth line here</p>",
				"first para\nsecond line\n\nsecond para\nfourth line here\n\n\n");
		assertContent("<p>first para second line</p>", "first para\n    second line");
	}

	@Test
	public void paragraphNewlines() {
		for (String newline : List.of("\n", "\r", "\r\n")) {
			assertContent("<p>p1 first p1 second p1 third</p><p>p2 first</p>",
					"p1 first" + newline + "p1 second" + newline + "p1 third" + newline + newline + "p2 first");
		}
	}
}
