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

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static org.eclipse.mylyn.wikitext.commonmark.internal.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.junit.Test;

@SuppressWarnings("nls")
public class AtxHeaderBlockTest {

	AtxHeaderBlock block = new AtxHeaderBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(LineSequence.create("")));
		assertTrue(block.canStart(LineSequence.create("#")));
		assertTrue(block.canStart(LineSequence.create("# ")));
		assertTrue(block.canStart(LineSequence.create("# #")));
		assertTrue(block.canStart(LineSequence.create("# Y")));
		assertTrue(block.canStart(LineSequence.create("# Y #")));
		assertTrue(block.canStart(LineSequence.create("## Y")));
		assertTrue(block.canStart(LineSequence.create("### Y")));
		assertTrue(block.canStart(LineSequence.create("#### Y")));
		assertTrue(block.canStart(LineSequence.create("##### Y")));
		assertTrue(block.canStart(LineSequence.create("###### Y")));
		assertFalse(block.canStart(LineSequence.create("####### Y")));
		assertTrue(block.canStart(LineSequence.create("# Y#")));
		assertFalse(block.canStart(LineSequence.create("#Y")));

		// Bug 472386:
		assertTrue(block.canStart(LineSequence.create("# #Y")));
		assertTrue(block.canStart(LineSequence.create("   # Y")));
		assertFalse(block.canStart(LineSequence.create("\t# Y")));
	}

	@Test
	public void basic() {
		assertContent("<h2 id=\"one-two\">One Two</h2>", "## One Two");
		assertContent("<h2 id=\"one-two\">One Two</h2>", "## One Two #####   ");
		assertContent("<h2 id=\"one-two\">One Two#</h2>", "## One Two#");
		assertContent("<h2 id=\"one-two\">#One #Two</h2>", "## #One #Two");
		assertContent("<p>One</p><h1 id=\"two\">two</h1><p>Three</p>", "One\n# two\nThree");
		assertContent("<h2></h2>", "##");
		assertContent("<h2></h2>", "## ##");
	}

	@Test
	public void withNestedInlines() {
		assertContent("<h2 id=\"one-two-three\">One <em>Two</em> \\<strong>three</strong></h2>",
				"## One *Two* \\\\__three__ ##");
	}

}
