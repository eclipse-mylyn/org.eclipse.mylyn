/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.block.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.confluence.internal.block.ExtendedQuoteBlock;
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
