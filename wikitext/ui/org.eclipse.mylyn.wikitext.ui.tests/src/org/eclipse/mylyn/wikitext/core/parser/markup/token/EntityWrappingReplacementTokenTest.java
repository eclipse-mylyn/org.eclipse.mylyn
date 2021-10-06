/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.token.EntityWrappingReplacementToken;
import org.junit.Test;

/**
 * @author David Green
 */
public class EntityWrappingReplacementTokenTest {
	@Test
	public void testFindQuotesAtStartOfLine() {
		EntityWrappingReplacementToken token = new EntityWrappingReplacementToken("\"", "<", ">");

		Pattern pattern = Pattern.compile(token.getPattern(0));
		Matcher matcher = pattern.matcher("\"some text\" more text");
		assertTrue(matcher.find());
		assertEquals(0, matcher.start());
	}

	@Test
	public void testFindQuotesOffsetInLine() {
		EntityWrappingReplacementToken token = new EntityWrappingReplacementToken("\"", "<", ">");

		Pattern pattern = Pattern.compile(token.getPattern(0));
		Matcher matcher = pattern.matcher("  \"some text\" more text");
		assertTrue(matcher.find());
		assertEquals(2, matcher.start());
	}
}
