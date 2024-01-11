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

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.junit.Test;

public class HtmlCommentBlockTest {

	private final HtmlCommentBlock block = new HtmlCommentBlock();

	@Test
	public void canStart() {
		assertCanStart(true, "<!-- a comment -->");
		assertCanStart(true, "<!-- <");
		assertCanStart(true, "<!--");
		assertCanStart(true, "<!-- <-");
		assertCanStart(true, "<!--<-");
		assertCanStart(false, "<!-->");
		assertCanStart(false, "<!--->");
		assertCanStart(true, "<!-- ->");
		assertCanStart(false, "<!-- -- -->");
	}

	@Test
	public void closePattern() {
		assertClosePattern(true, "-->");
		assertClosePattern(true, "   -->");
		assertClosePattern(true, "  - -->");
		assertClosePattern(false, "   --->");
	}

	private void assertClosePattern(boolean expected, String content) {
		assertEquals(expected, block.closePattern().matcher(content).find());
	}

	private void assertCanStart(boolean expected, String content) {
		assertEquals(expected, block.canStart(LineSequence.create(content)));
	}
}
