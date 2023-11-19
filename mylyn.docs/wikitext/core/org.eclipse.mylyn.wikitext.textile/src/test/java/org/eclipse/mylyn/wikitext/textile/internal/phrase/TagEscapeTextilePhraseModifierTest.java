/*******************************************************************************
 * Copyright (c) 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.textile.internal.phrase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TagEscapeTextilePhraseModifierTest {

	@Test
	public void matches() {
		assertMatch(0, "a", "<notextile>a</notextile>");
		assertMatch(1, "a", " <notextile>a</notextile>");
		assertMatch(0, "", "<notextile></notextile>");
		assertMatch(5, "*MyType==[1]==*", "asdf <notextile>*MyType==[1]==*</notextile>");
	}

	private void assertMatch(int offset, String content, String markup) {
		String regex = new TagEscapeTextilePhraseModifier().getPattern(0);
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(markup);
		assertTrue(matcher.find());
		assertEquals(offset, matcher.start());
		assertEquals(content, matcher.group(1));
	}
}
