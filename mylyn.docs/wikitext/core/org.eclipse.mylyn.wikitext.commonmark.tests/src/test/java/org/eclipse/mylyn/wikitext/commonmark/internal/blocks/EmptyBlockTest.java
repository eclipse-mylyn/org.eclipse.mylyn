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
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.junit.Test;

@SuppressWarnings("nls")
public class EmptyBlockTest {

	@Test
	public void canStart() {
		assertCanStart(false, "one");
		assertCanStart(true, "\n");
	}

	@Test
	public void process() {
		LineSequence lineSequence = LineSequence.create("\n2");
		new EmptyBlock().process(ProcessingContext.builder().build(), null, lineSequence);
		assertEquals("2", lineSequence.getCurrentLine().getText());
	}

	private void assertCanStart(boolean expected, String lineContent) {
		EmptyBlock emptyBlock = new EmptyBlock();
		LineSequence lineSequence = LineSequence.create(lineContent);
		assertEquals(expected, emptyBlock.canStart(lineSequence));
	}
}
