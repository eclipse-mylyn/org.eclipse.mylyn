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
package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author David Green
 */
public class EntityWrappingReplacementTokenTest extends TestCase {

	public void testFindQuotesAtStartOfLine() {
		EntityWrappingReplacementToken token = new EntityWrappingReplacementToken("\"", "<", ">");

		Pattern pattern = Pattern.compile(token.getPattern(0));
		Matcher matcher = pattern.matcher("\"some text\" more text");
		assertTrue(matcher.find());
		assertEquals(0, matcher.start());
	}

	public void testFindQuotesOffsetInLine() {
		EntityWrappingReplacementToken token = new EntityWrappingReplacementToken("\"", "<", ">");

		Pattern pattern = Pattern.compile(token.getPattern(0));
		Matcher matcher = pattern.matcher("  \"some text\" more text");
		assertTrue(matcher.find());
		assertEquals(2, matcher.start());
	}
}
