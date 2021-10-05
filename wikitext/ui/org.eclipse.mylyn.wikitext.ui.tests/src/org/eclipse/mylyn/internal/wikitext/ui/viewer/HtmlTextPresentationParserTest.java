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
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jface.text.TextPresentation;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.css.CssParser;
import org.eclipse.mylyn.wikitext.parser.css.ElementInfo;
import org.eclipse.mylyn.wikitext.parser.css.Stylesheet;
import org.eclipse.mylyn.wikitext.parser.css.Stylesheet.Receiver;
import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author David Green
 */
@HeadRequired
public class HtmlTextPresentationParserTest {

	private HtmlTextPresentationParser parser;

	@Before
	public void setUp() {
		parser = new HtmlTextPresentationParser();
		parser.setPresentation(new TextPresentation());
		parser.setDefaultFont(new Font(null, new FontData[] { new FontData("fake", 12, 0) }));
	}

	public void testAdjacentElementsSeparatedByWhitespace() throws Exception {
		parser.parse("<html><body><p><strong>one</strong> <em>two</em></p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("one two"));
	}

	/**
	 * Test for bug# 236367
	 */
	@Test
	public void testSignificantWhitespaceLossBug236367() throws SAXException, IOException {
		String html = new MarkupParser(new TextileLanguage()).parseToHtml("one *two* three *four* five *six* seven");

		parser.parse(html);
		String text = parser.getText();

		assertTrue(text.contains("one two three four five six seven"));
	}

	@Test
	public void testOrderedListBlockHandling() throws Exception {
		parser.parse("<html><body><ol><li> one </li><li>    two </li></ol></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("\t1. one\n\t2. two"));
	}

	@Test
	public void testOrderedListBlockHandling2() throws Exception {
		parser.parse("<html><body><ol><li> <b>one</b> </li><li>    two </li></ol></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("\t1. one\n\t2. two"));
	}

	@Test
	public void testUnorderedListBlockHandling() throws Exception {
		parser.setBulletChars(new char[] { 'A', 'B', 'C' });
		parser.parse("<html><body><ul><li> one</li><li>    two<ul><li>two.one</li></ul></li></ul></body></html>");

		String text = parser.getText();

		assertTrue(text.contains("\tA  one\n\tA  two\n\t\tB  two.one"));
	}

	@Test
	public void testParagraphBlockHandling() throws Exception {
		parser.parse("<html><body><p>first para\n\nwith some newlines</p>\n\n<p>second para</p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("first para with some newlines\n\nsecond para"));
	}

	@Test
	public void testParagraphBlockHandlingWithBR() throws Exception {
		parser.parse(
				"<html><body><p>first para<br/>\nwith one newline plus br</p>\n\n<p>second para</p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("first para\nwith one newline plus br\n\nsecond para"));
	}

	@Test
	public void testParagraphBlockHandlingWithBR2() throws Exception {
		parser.parse("<html><body><p>first para<br/>with one newline plus br</p>\n\n<p>second para</p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("first para\nwith one newline plus br\n\nsecond para"));
	}

	@Test
	public void testDefinitionList() throws Exception {
		parser.parse("<html><body><dl><dt>foo</dt><dd>bar baz</dd></dl></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("foo\n\tbar baz"));
	}

	@Test
	public void testNonParaText() throws Exception {
		parser.parse("<html><body><p>one</p>two<p>three</p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("one\n\ntwo\n\nthree"));
	}

	@Test
	public void testBlockQuoteParaWhitespace() throws Exception {
		parser.parse("<html><body><p>one</p><blockquote><p>two</p></blockquote><p>three</p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("one\n\n\ttwo\n\nthree"));
	}

	@Test
	public void testBR() throws Exception {
		parser.parse("<html><head></head><body><p>One<br/>Two</p></body></html>");
		String text = parser.getText();

		assertTrue(text.contains("One\nTwo"));
	}

	@Test
	public void testWhitespaceAfterTable() throws Exception {
		parser.parse(
				"<html><head></head><body>before<table><tr><td>in1</td></tr><tr><td>in2</td></tr></table>after</body></html>");
		String text = parser.getText();

		assertTrue(text.contains("in1 \t\nin2 \t\n\nafter"));
	}

	@Test
	public void testWhitespaceAfterEmdash() throws Exception {
		String html = "<html><body><p>Foo<br/>Bar &#8212; baz</p></body></html>";
		parser.parse(html);
		String text = parser.getText();

		assertTrue(Pattern.compile("Bar\\s\\S\\sbaz", Pattern.MULTILINE).matcher(text).find());
	}

	@Test
	public void defaultStyles() {
		assertDefaultCssStyles("font-family: Arial, Helvetica, sans-serif", "p");
		assertDefaultCssStyles(
				"font-family: Arial, Helvetica, sans-serif; font-size: 120%; font-weight: bold; color: #172f47", "h1");
		assertDefaultCssStyles(
				"font-family: Arial, Helvetica, sans-serif; font-size: 110%; font-weight: bold; color: #172f47", "h2");
		assertDefaultCssStyles("font-family: Arial, Helvetica, sans-serif; text-decoration: underline; color: blue",
				"a");
	}

	private Stylesheet defaultStylesheet() {
		try {
			try (Reader reader = HtmlTextPresentationParser.getDefaultStylesheetContent()) {
				return new CssParser().parse(reader);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void assertDefaultCssStyles(String expectedStyles, final String elementName) {
		Stylesheet stylesheet = defaultStylesheet();
		final List<String> styles = new ArrayList<>();

		Receiver receiver = rule -> styles.add(rule.name + ": " + rule.value);
		stylesheet.applyTo(new ElementInfo() {

			@Override
			public boolean hasId(String id) {
				return false;
			}

			@Override
			public boolean hasCssClass(String cssClass) {
				return false;
			}

			@Override
			public ElementInfo getParent() {
				return null;
			}

			@Override
			public String getLocalName() {
				return elementName;
			}
		}, receiver);
		assertEquals("element " + elementName, expectedStyles, styles.stream().collect(Collectors.joining("; ")));
	}
}
