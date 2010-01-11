/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Holger Voormann - initial API and implementation (tests for bug 279029)
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tracwiki.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.wikitext.tracwiki.core.token.WikiWordReplacementToken;

/**
 * @author Holger Voormann
 */
public class WikiWordReplacementTokenTest extends TestCase {

	private static final String SUFFIX = ". And so on...";

	private static final String PREFIX = "A Wiki-Word: ";

	private Pattern pattern;

	@Override
	protected void setUp() throws Exception {
		pattern = new TestWikiWordReplacementToken().createPattern();
	}

	@Override
	protected void tearDown() throws Exception {
		pattern = null;
	}

	public void testRegex() throws Exception {
		assertIsWikiWord("WikiWord");
		assertIsWikiWord("WikiWordExtra");

		assertIsNotWikiWord("Word");
		assertIsNotWikiWord("XML");
		assertIsNotWikiWord("HTML");
		assertIsNotWikiWord("XML-based");
		assertIsNotWikiWord("time-aligned");
		assertIsNotWikiWord("X-Ray");
		assertIsNotWikiWord("XRay");
		assertIsNotWikiWord("eClass");
		assertIsNotWikiWord("AbbA");
		assertIsNotWikiWord("H2O");
		assertIsNotWikiWord("Not-Wiki-Word");
		assertIsNotWikiWord("WIkiWord");
		assertIsNotWikiWord("Wiki-Word");
		assertIsNotWikiWord("Wi-kiWord");
		assertIsNotWikiWord("Ww3Word");
		assertIsNotWikiWord("WikiWWWord");

		assertPartialWikiWord("WikiWordX");
		assertPartialWikiWord("WikiWordNOT");
		assertPartialWikiWord("WikiWo-rd");
		assertPartialWikiWord("1WikiWord");
		assertPartialWikiWord("WikiWord2");
		assertPartialWikiWord("O2WikiWord");
	}

	private void assertIsWikiWord(String wikiWord) {
		Matcher matcher = pattern.matcher(PREFIX + wikiWord + SUFFIX);
		assertTrue(matcher.find());
		assertEquals(PREFIX.length(), matcher.start());
		assertEquals(PREFIX.length() + wikiWord.length(), matcher.end());
	}

	private void assertIsNotWikiWord(String wikiWord) {
		Matcher matcher = pattern.matcher(PREFIX + wikiWord + SUFFIX);
		assertFalse(matcher.find());
	}

	private void assertPartialWikiWord(String wikiWord) {
		Matcher matcher = pattern.matcher(PREFIX + wikiWord + SUFFIX);
		assertTrue(matcher.find());
		assertFalse(PREFIX.length() == matcher.start() && PREFIX.length() + wikiWord.length() == matcher.end());
	}

	private class TestWikiWordReplacementToken extends WikiWordReplacementToken {

		private static final int NOT_USED = -1;

		private Pattern createPattern() {
			return Pattern.compile(getPattern(NOT_USED));
		}

	}

}
