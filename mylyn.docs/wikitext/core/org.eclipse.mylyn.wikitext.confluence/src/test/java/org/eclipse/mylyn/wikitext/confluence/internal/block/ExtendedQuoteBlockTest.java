/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.block;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExtendedQuoteBlockTest {

	@Test
	public void beginNestingWithoutANestedBlock() {
		ExtendedQuoteBlock block = new ExtendedQuoteBlock();
		assertEquals(false, block.beginNesting());
	}

	@Test
	public void canResumeWithoutANestedBlock() {
		ExtendedQuoteBlock block = new ExtendedQuoteBlock();
		assertEquals(false, block.canResume("some line", 1));
	}

	@Test
	public void finadCloseOffsetWithoutANestBlock() {
		ExtendedQuoteBlock block = new ExtendedQuoteBlock();
		assertEquals(-1, block.findCloseOffset("some line", 1));
	}
}
