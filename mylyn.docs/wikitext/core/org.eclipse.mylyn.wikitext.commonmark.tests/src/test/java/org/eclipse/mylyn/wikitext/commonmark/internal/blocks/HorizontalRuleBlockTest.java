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

import com.google.common.base.Strings;

public class HorizontalRuleBlockTest {

	private final HorizontalRuleBlock block = new HorizontalRuleBlock();

	@Test
	public void canStart() {
		assertFalse(block.canStart(LineSequence.create("")));
		assertFalse(block.canStart(LineSequence.create("a")));
		assertFalse(block.canStart(LineSequence.create("    ***")));
		for (char c : "*_-".toCharArray()) {
			String hrIndicator = Strings.repeat("" + c, 3);
			assertTrue(block.canStart(LineSequence.create("   " + hrIndicator)));
			assertTrue(block.canStart(LineSequence.create("  " + hrIndicator)));
			assertTrue(block.canStart(LineSequence.create(" " + hrIndicator)));
			assertFalse(block.canStart(LineSequence.create("    " + hrIndicator)));
			assertTrue(block.canStart(LineSequence.create(hrIndicator)));
			assertTrue(block.canStart(LineSequence.create(Strings.repeat("" + c, 4))));
			assertTrue(block.canStart(LineSequence.create(Strings.repeat("" + c, 14))));
		}

		// Bug 472390:
		assertFalse(block.canStart(LineSequence.create("\t***")));
	}

	@Test
	public void process() {
		assertContent("<p>one</p><hr/>", "one\n\n------\n");
		assertContent("<p>one</p><hr/>", "one\n\n---\n");
		assertContent("<p>one</p><hr/>", "one\n\n-  - -\n");
		assertContent("<p>one</p><hr/>", "one\n\n   ** *****\n");
	}

}
