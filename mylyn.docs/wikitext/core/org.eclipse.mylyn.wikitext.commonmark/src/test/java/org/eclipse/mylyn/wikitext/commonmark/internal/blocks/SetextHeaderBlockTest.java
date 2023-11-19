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

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.junit.Test;

public class SetextHeaderBlockTest {

	private final SetextHeaderBlock block = new SetextHeaderBlock();

	@Test
	public void canStart() {
		assertTrue(block.canStart(LineSequence.create("Heading\n-")));
		assertTrue(block.canStart(LineSequence.create("Heading\n=")));
		assertTrue(block.canStart(LineSequence.create("Heading\n  =")));
		assertTrue(block.canStart(LineSequence.create("Heading\n   =")));
		assertFalse(block.canStart(LineSequence.create("Heading\n    =")));
		assertTrue(block.canStart(LineSequence.create("Heading\n=====")));
		assertTrue(block.canStart(LineSequence.create("Heading Text\n-----")));
		assertFalse(block.canStart(LineSequence.create("Heading\n\n=====")));
		assertTrue(block.canStart(LineSequence.create("   Heading\n=====")));
		assertFalse(block.canStart(LineSequence.create("    Heading\n=====")));
	}

	@Test
	public void process() {
		assertContent("<h2 id=\"heading-text\">Heading Text</h2>", "Heading Text\n-------");
		assertContent("<h1 id=\"heading-text\">Heading Text</h1>", "Heading Text\n=");
		assertContent("<h1 id=\"heading-text\">Heading Text</h1>", "Heading Text\n====");
		assertContent("<h1 id=\"heading-text\">Heading <em>Text</em></h1>", "Heading *Text*\n====");
	}
}
