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
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.junit.Test;

public class EmptyBlockTest {

	@Test
	public void canStart() {
		assertCanStart(false, "one");
		assertCanStart(true, "\n");
	}

	@Test
	public void process() {
		LineSequence lineSequence = LineSequence.create("\n2");
		new EmptyBlock().process(ProcessingContext.empty(), null, lineSequence);
		assertEquals("2", lineSequence.getCurrentLine().getText());
	}

	private void assertCanStart(boolean expected, String lineContent) {
		EmptyBlock emptyBlock = new EmptyBlock();
		LineSequence lineSequence = LineSequence.create(lineContent);
		assertEquals(expected, emptyBlock.canStart(lineSequence));
	}
}
