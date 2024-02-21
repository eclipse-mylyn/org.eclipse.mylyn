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
public class IndentedCodeBlockTest {

	private final IndentedCodeBlock block = new IndentedCodeBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(LineSequence.create("   code")));
		assertTrue(block.canStart(LineSequence.create("    code")));
		assertTrue(block.canStart(LineSequence.create("     code")));
		assertFalse(block.canStart(LineSequence.create(" code")));
		assertFalse(block.canStart(LineSequence.create("  code")));
		assertFalse(block.canStart(LineSequence.create("non-blank\n    code")));
		assertTrue(block.canStart(LineSequence.create("\tcode")));
		assertTrue(block.canStart(LineSequence.create("\t code")));
		assertTrue(block.canStart(LineSequence.create(" \tcode")));
		assertTrue(block.canStart(LineSequence.create("  \tcode")));
		assertTrue(block.canStart(LineSequence.create("   \tcode")));
	}

	@Test
	public void process() {
		assertContent("<pre><code>code\n</code></pre>", "    code");
		assertContent("<pre><code>code\n</code></pre>", "\tcode");
		assertContent("<pre><code> code\n</code></pre>", "\t code");
		assertContent("<pre><code>code  \n</code></pre>", "\tcode  ");
		assertContent("<pre><code>\tcode\n</code></pre>", "    \tcode");
		assertContent("<pre><code>one\ntwo\n</code></pre><p>three</p>", "    one\n    two\n three");
		assertContent("<pre><code>one\n\nthree\n</code></pre>", "    one\n\n    three");
		assertContent("<pre><code>one\n  \nthree\n</code></pre>", "    one\n      \n    three");

		// Bug 472395:
		assertContent("<pre><code>\t\tcode\n</code></pre>", "\t\t\tcode");
	}
}
