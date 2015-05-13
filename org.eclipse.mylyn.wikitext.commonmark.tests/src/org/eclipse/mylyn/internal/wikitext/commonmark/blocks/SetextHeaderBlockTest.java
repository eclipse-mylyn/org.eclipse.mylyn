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

package org.eclipse.mylyn.internal.wikitext.commonmark.blocks;

import static org.eclipse.mylyn.internal.wikitext.commonmark.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
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
		assertContent("<h2>Heading Text</h2>", "Heading Text\n-------");
		assertContent("<h1>Heading Text</h1>", "Heading Text\n=");
		assertContent("<h1>Heading Text</h1>", "Heading Text\n====");
		assertContent("<h1>Heading <em>Text</em></h1>", "Heading *Text*\n====");
	}
}
