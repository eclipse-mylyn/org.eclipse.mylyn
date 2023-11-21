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
 *     Holger Voormann - initial API and implementation (tests for bug 279029)
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tracwiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.tracwiki.internal.token.WikiWordReplacementToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Holger Voormann
 */
public class WikiWordReplacementTokenTest {

	private static final String SUFFIX = ". And so on...";

	private static final String PREFIX = "A Wiki-Word: ";

	private Pattern pattern;

	@Before
	public void setUp() throws Exception {
		pattern = new TestWikiWordReplacementToken().createPattern();
	}

	@After
	public void tearDown() throws Exception {
		pattern = null;
	}

	@Test
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
