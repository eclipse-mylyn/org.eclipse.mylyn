/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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
package org.eclipse.mylyn.wikitext.textile.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.textile.internal.Textile;
import org.junit.Test;

/**
 * @author David Green
 */
public class TextileTest {
	@Test
	public void testExplicitHeaderStartsNewBlock() {
		for (int x = 1; x <= 6; ++x) {
			assertTrue(Textile.explicitBlockBegins("h" + x + ". ", 0));
			assertTrue(Textile.explicitBlockBegins("h" + x + ". asfsdfds", 0));
			assertTrue(Textile.explicitBlockBegins("h" + x + "(#id-foo). ", 0));
			assertFalse(Textile.explicitBlockBegins("h" + x + ".", 0));
			assertFalse(Textile.explicitBlockBegins(" h" + x + ". ", 1));
		}
	}

	@Test
	public void testExplicitFootnoteStartsNewBlock() {
		for (int x = 0; x <= 9; ++x) {
			assertTrue(Textile.explicitBlockBegins("fn" + x + ". ", 0));
			assertTrue(Textile.explicitBlockBegins("fn" + x + x + ". ", 0));
			assertTrue(Textile.explicitBlockBegins("fn" + x + ". asfsdfds", 0));
			assertFalse(Textile.explicitBlockBegins("fn" + x + ".", 0));
			assertFalse(Textile.explicitBlockBegins("fn" + x + x + ".", 0));
			assertFalse(Textile.explicitBlockBegins(" fn" + x + ". ", 1));
		}
	}

	@Test
	public void testExplicitOtherStartsNewBlock() {
		String[] types = new String[] { "pre", "bc", "bq", "p", "table", "###" };
		for (String type : types) {
			assertTrue(Textile.explicitBlockBegins(type + ". ", 0));
			assertTrue(Textile.explicitBlockBegins(type + ". asfsdfds", 0));
			assertTrue(Textile.explicitBlockBegins(type + ".. ", 0));
			assertTrue(Textile.explicitBlockBegins(type + ".. asfsdfds", 0));
			assertTrue(Textile.explicitBlockBegins(type + "(cssClass). ", 0));
			assertFalse(Textile.explicitBlockBegins(type + ".", 0));
			assertFalse(Textile.explicitBlockBegins(" " + type + ". ", 1));
		}
	}

	@Test
	public void testAttributes() {
		Pattern pattern = Pattern.compile(Textile.REGEX_ATTRIBUTES);
		String[] values = new String[] { "(someClass)", "(#someId)", "{someStyle}", "[someLanguage]",
				"(someClass)(#someId){someStyle}", "{someStyle}(someClass)(#someId)" };
		String[][] verify = new String[][] { { null, "someClass", null, null, null },
				{ null, null, "someId", null, null }, { null, null, null, "someStyle", null },
				{ null, null, null, null, "someLanguage" }, { null, "someClass", "someId", "someStyle", null },
				{ null, "someClass", "someId", "someStyle", null } };

		int i = 0;
		for (String value : values) {
			Matcher matcher = pattern.matcher(value);

			if (matcher.matches()) {

				for (int x = 1; x <= matcher.groupCount(); ++x) {

					assertEquals(verify[i][x], matcher.group(x));
				}
			} else {

			}
			++i;
		}
	}

	@Test
	public void testLeftAlignment() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1<. foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("text-align: left;", attributes.getCssStyle());
	}

	@Test
	public void testRightAlignment() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1>. foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("text-align: right;", attributes.getCssStyle());
	}

	@Test
	public void testCenterAlignment() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1=. foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("text-align: center;", attributes.getCssStyle());
	}

	@Test
	public void testJustifiedAlignment() {

		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1<>. foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("text-align: justify;", attributes.getCssStyle());
	}

	@Test
	public void testLeftPadding() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1((. foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("padding-left: 2em;", attributes.getCssStyle());
	}

	@Test
	public void testRightPadding() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1))). foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("padding-right: 3em;", attributes.getCssStyle());
	}

	@Test
	public void testLeftPaddingCssClass() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1(((foo). foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("padding-left: 2em;", attributes.getCssStyle());
		assertEquals("foo", attributes.getCssClass());
	}

	@Test
	public void testRightPaddingCssStyles() {
		Pattern pattern = Pattern.compile("h1" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\. (.*)?");

		Matcher matcher = pattern.matcher("h1){color: red;}. foo");
		assertTrue(matcher.matches());

		Attributes attributes = new Attributes();
		Textile.configureAttributes(attributes, matcher, 1, true);

		assertEquals("padding-right: 1em; color: red;", attributes.getCssStyle());
	}

}
