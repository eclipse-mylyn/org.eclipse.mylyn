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

public class HtmlType1BlockTest {

	private final HtmlType1Block block = new HtmlType1Block();

	@Test
	public void canStart() {
		for (String tagName : new String[] { "script", "pre", "style" }) {
			assertCanStart(true, "<" + tagName);
			assertCanStart(true, " <" + tagName);
			assertCanStart(true, "   <" + tagName);
			assertCanStart(false, "    <" + tagName);

			assertCanStart(true, "<" + tagName + ">");
			assertCanStart(true, "<" + tagName + "> ");
			assertCanStart(true, "<" + tagName + ">with some text");
			assertCanStart(false, "<" + tagName + "/>");
			assertCanStart(true, "<" + tagName + "></" + tagName + ">");
			assertCanStart(true, "<" + tagName + "></" + tagName + " >");
			assertCanStart(true, "<" + tagName + ">  sdf</" + tagName + " >");
		}
	}

	private void assertCanStart(boolean expected, String string) {
		assertEquals(expected, block.canStart(LineSequence.create(string)));
	}
}
