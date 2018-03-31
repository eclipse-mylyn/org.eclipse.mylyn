/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.junit.Test;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

public class PotentialBracketEndDelimiterTest {

	private final Line line = new Line(0, 1, "test");

	@Test
	public void escapedChars() {
		Pattern pattern = Pattern.compile(PotentialBracketEndDelimiter.ESCAPED_CHARS);
		assertTrue(pattern.matcher("\\\\").matches());
		assertTrue(pattern.matcher("\\<").matches());
		assertTrue(pattern.matcher("\\>").matches());
		assertTrue(pattern.matcher("\\(").matches());
		assertTrue(pattern.matcher("\\)").matches());
		assertTrue(pattern.matcher("\\:").matches());
		assertTrue(pattern.matcher("\\\"").matches());
		assertTrue(pattern.matcher("\\]").matches());
		assertTrue(pattern.matcher("\\[").matches());
		assertTrue(pattern.matcher("\\!").matches());
		assertFalse(pattern.matcher("\\a").matches());
	}

	@Test
	public void bracketUriPart() {
		Pattern pattern = Pattern.compile(PotentialBracketEndDelimiter.BRACKET_URI_PART);
		assertTrue(pattern.matcher("<one two>").matches());
		assertTrue(pattern.matcher("<>").matches());
		assertFalse(pattern.matcher("<one two").matches());
		assertFalse(pattern.matcher("<one two>>").matches());
		Matcher matcher = pattern.matcher("<one two\\>>");
		assertTrue(matcher.matches());
		assertEquals("one two\\>", matcher.group(1));
	}

	@Test
	public void nobracketUriPart() {
		Pattern pattern = Pattern.compile(PotentialBracketEndDelimiter.NOBRACKET_URI_PART);
		assertTrue(pattern.matcher("onetwo").matches());
		assertFalse(pattern.matcher("one two").matches());
		assertFalse(pattern.matcher("one(two(three))").matches());
		assertTrue(pattern.matcher("/one/two(threefour\\))").matches());

		assertMatch(1, "/one/two(three\\))", pattern, "/one/two(three\\))");
		assertMatch(1, "foo(and\\(bar\\))", pattern, "foo(and\\(bar\\))");
		assertMatch(1, "foo\\)\\:", pattern, "foo\\)\\:");

	}

	@Test
	public void uriPart() {
		Pattern pattern = Pattern.compile(PotentialBracketEndDelimiter.URI_PART);
		assertMatch(2, "/one/two", pattern, "/one/two");
		assertMatch(2, "/one/(two)", pattern, "/one/(two)");
		assertMatch(1, "/one/two", pattern, "</one/two>");
		assertMatch(2, "foo(and\\(bar\\))", pattern, "foo(and\\(bar\\))");
		assertMatch(2, "foo\\)\\:", pattern, "foo\\)\\:");
	}

	@Test
	public void titlePart() {
		Pattern pattern = Pattern.compile(PotentialBracketEndDelimiter.TITLE_PART);
		assertMatch(1, "one two ('\\\" three", pattern, "\"one two ('\\\" three\"");
		assertMatch(2, "one two \\\"\\' three", pattern, "'one two \\\"\\' three'");
		assertMatch(3, "one two \"\\'\\) three", pattern, "(one two \"\\'\\) three)");
		assertMatch(3, "one two (three (four\\)\\)", pattern, "(one two (three (four\\)\\))");
		assertFalse(pattern.matcher("\"one").matches());
		assertFalse(pattern.matcher("one\"").matches());
		assertFalse(pattern.matcher("one'").matches());
		assertFalse(pattern.matcher("'one").matches());
		assertFalse(pattern.matcher("(one").matches());
		assertFalse(pattern.matcher("one)").matches());
	}

	@Test
	public void linkEndPattern() {
		Pattern pattern = new PotentialBracketEndDelimiter(line, 0).endPattern;
		Matcher matcher = pattern.matcher("(/uri \"a title\")");
		assertTrue(matcher.matches());
		assertEquals("/uri", matcher.group(2));
		assertEquals("a title", matcher.group(3));

		matcher = pattern.matcher("(</uri to here> (one two (three (four\\)\\)))");
		assertTrue(matcher.matches());
		assertEquals("/uri to here", matcher.group(1));
		assertEquals("one two (three (four\\)\\)", matcher.group(5));

		assertMatch(2, "one(two\\(three\\))", pattern, "(one(two\\(three\\)))");
		assertMatch(2, "foo\\)\\:", pattern, "(foo\\)\\:)");
	}

	@Test
	public void referenceDefinitionEndPattern() {
		Pattern pattern = new PotentialBracketEndDelimiter(line, 0).referenceDefinitionEndPattern;
		Matcher matcher = pattern.matcher(":\n      /url\n           'the title'");
		assertTrue(matcher.matches());
		assertEquals("/url", matcher.group(2));
		assertEquals("the title", matcher.group(4));
	}

	@Test
	public void unescape() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		assertEquals("(", delimiter.unescapeBackslashEscapes("\\("));
		assertEquals("abc(def", delimiter.unescapeBackslashEscapes("abc\\(def"));
	}

	@Test
	public void eligibleForReferenceDefinitionImage() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		PotentialBracketDelimiter openingDelimiter = new PotentialBracketDelimiter(line, 0, 2, "![");
		assertFalse(delimiter.eligibleForReferenceDefinition(openingDelimiter, Cursors.createCursor("![")));
	}

	@Test
	public void eligibleForReferenceDefinitionLinkStartOfLine() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		assertTrue(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 0, 1, "["),
				Cursors.createCursor("[")));
		assertTrue(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 2, 1, "["),
				Cursors.createCursor("a\n[")));
	}

	@Test
	public void eligibleForReferenceDefinitionLinkIndented() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		assertTrue(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 3, 1, "["),
				Cursors.createCursor("a\n [")));
		assertTrue(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 5, 1, "["),
				Cursors.createCursor("a\n   [")));
		assertFalse(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 6, 1, "["),
				Cursors.createCursor("a\n    [")));
		assertFalse(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 4, 1, "["),
				Cursors.createCursor("    [")));
		assertTrue(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 3, 1, "["),
				Cursors.createCursor("   [")));
		assertFalse(delimiter.eligibleForReferenceDefinition(new PotentialBracketDelimiter(line, 3, 1, "["),
				Cursors.createCursor("a  [")));
	}

	@Test
	public void replaceHtmlEntities() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		Escaper escaper = UrlEscapers.urlFormParameterEscaper();
		assertEquals("asf", delimiter.replaceHtmlEntities("asf", escaper));
		assertEquals("&amp", delimiter.replaceHtmlEntities("&amp", escaper));
		assertEquals("&amp ;", delimiter.replaceHtmlEntities("&amp ;", escaper));
		assertEquals("%26", delimiter.replaceHtmlEntities("&amp;", escaper));
		assertEquals("a%26", delimiter.replaceHtmlEntities("a&amp;", escaper));
		assertEquals("a%26b", delimiter.replaceHtmlEntities("a&amp;b", escaper));
		assertEquals("%C3%A4", delimiter.replaceHtmlEntities("&auml;", escaper));
		assertEquals("&", delimiter.replaceHtmlEntities("&amp;", null));
		assertEquals("\"", delimiter.replaceHtmlEntities("&quot;", null));
		assertEquals("\u00e4", delimiter.replaceHtmlEntities("&auml;", null));
		assertEquals("&xdfsldk;", delimiter.replaceHtmlEntities("&xdfsldk;", null));
		assertEquals("&0;", delimiter.replaceHtmlEntities("&0;", null));
	}

	@Test
	public void precededByBackslashEscape() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		assertFalse(delimiter.precededByBackslashEscape(Cursors.createCursor("["), 0));
		assertFalse(delimiter.precededByBackslashEscape(Cursors.createCursor("\\\\["), 2));
		assertFalse(delimiter.precededByBackslashEscape(Cursors.createCursor("\\\\\\\\["), 4));
		assertTrue(delimiter.precededByBackslashEscape(Cursors.createCursor("\\["), 1));
		assertTrue(delimiter.precededByBackslashEscape(Cursors.createCursor("a\\["), 2));
		assertTrue(delimiter.precededByBackslashEscape(Cursors.createCursor("\\\\\\["), 3));
	}

	@Test
	public void unescapeBackslashEscapeBrackets() {
		PotentialBracketEndDelimiter delimiter = new PotentialBracketEndDelimiter(line, 0);
		assertEquals("a[b", delimiter.toReferenceName("a\\[b"));
		assertEquals("a]b", delimiter.toReferenceName("a\\]b"));
		assertEquals("a\\!b", delimiter.toReferenceName("a\\!b"));
		assertEquals("a\\![b c", delimiter.toReferenceName("a\\![b\n c"));
	}

	private void assertMatch(int group, String expected, Pattern pattern, String input) {
		Matcher matcher = pattern.matcher(input);
		assertTrue(matcher.matches());
		assertEquals(expected, matcher.group(group));
	}
}
