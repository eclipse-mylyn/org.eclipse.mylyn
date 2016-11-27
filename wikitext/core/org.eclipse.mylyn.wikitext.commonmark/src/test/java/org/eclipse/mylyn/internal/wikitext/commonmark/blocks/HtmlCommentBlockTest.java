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

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
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
