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
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.jface.text.TextPresentation;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.xml.sax.SAXException;

/**
 * 
 * 
 * @author David Green
 */
public class HtmlTextPresentationParserTest extends TestCase {

	private HtmlTextPresentationParser parser;

	@Override
	public void setUp() {
		parser = new HtmlTextPresentationParser();
		parser.setPresentation(new TextPresentation());
		parser.setDefaultFont(new Font(null, new FontData[] { new FontData("fake", 12, 0) }));
	}

	public void testAdjacentElementsSeparatedByWhitespace() throws Exception {
		parser.parse("<html><body><p><strong>one</strong> <em>two</em></p></body></html>");
		String text = parser.getText();
		System.out.println("text: '" + text + "'");
		assertTrue(text.contains("one two"));
	}

	/**
	 * Test for bug# 236367
	 */
	public void testSignificantWhitespaceLossBug236367() throws SAXException, IOException {
		String html = new MarkupParser(new TextileLanguage()).parseToHtml("one *two* three *four* five *six* seven");
		System.out.println("HTML: " + html);
		parser.parse(html);
		String text = parser.getText();
		System.out.println("text: '" + text + "'");
		assertTrue(text.contains("one two three four five six seven"));
	}

	public void testOrderedListBlockHandling() throws Exception {
		parser.parse("<html><body><ol><li> one </li><li>    two </li></ol></body></html>");
		String text = parser.getText();
		System.out.println("text:\n" + text);
		assertTrue(text.contains("\t1. one\n\t2. two"));
	}

	public void testOrderedListBlockHandling2() throws Exception {
		parser.parse("<html><body><ol><li> <b>one</b> </li><li>    two </li></ol></body></html>");
		String text = parser.getText();
		System.out.println("text: '" + text + "'");
		assertTrue(text.contains("\t1. one\n\t2. two"));
	}

	public void testUnorderedListBlockHandling() throws Exception {
		parser.setBulletChars(new char[] { 'A', 'B', 'C' });
		parser.parse("<html><body><ul><li> one</li><li>    two<ul><li>two.one</li></ul></li></ul></body></html>");

		String text = parser.getText();
		System.out.println("text:\n" + text);
		assertTrue(text.contains("\tA  one\n\tA  two\n\t\tB  two.one"));
	}

	public void testParagraphBlockHandling() throws Exception {
		parser.parse("<html><body><p>first para\n\nwith some newlines</p>\n\n<p>second para</p></body></html>");
		String text = parser.getText();
		System.out.println("text: '" + text + "'");
		assertTrue(text.contains("first para with some newlines\n\nsecond para"));
	}

	public void testParagraphBlockHandlingWithBR() throws Exception {
		parser.parse("<html><body><p>first para<br/>\nwith one newline plus br</p>\n\n<p>second para</p></body></html>");
		String text = parser.getText();
		System.out.println("text: '" + text + "'");
		assertTrue(text.contains("first para\nwith one newline plus br\n\nsecond para"));
	}

	public void testParagraphBlockHandlingWithBR2() throws Exception {
		parser.parse("<html><body><p>first para<br/>with one newline plus br</p>\n\n<p>second para</p></body></html>");
		String text = parser.getText();
		System.out.println("text: '" + text + "'");
		assertTrue(text.contains("first para\nwith one newline plus br\n\nsecond para"));
	}

	public void testDefinitionList() throws Exception {
		parser.parse("<html><body><dl><dt>foo</dt><dd>bar baz</dd></dl></body></html>");
		String text = parser.getText();
		System.out.println("text:\n" + text + "'");
		assertTrue(text.contains("foo\n\tbar baz"));
	}

	public void testNonParaText() throws Exception {
		parser.parse("<html><body><p>one</p>two<p>three</p></body></html>");
		String text = parser.getText();
		System.out.println("text:\n" + text);
		assertTrue(text.contains("one\n\ntwo\n\nthree"));
	}

	public void testBlockQuoteParaWhitespace() throws Exception {
		parser.parse("<html><body><p>one</p><blockquote><p>two</p></blockquote><p>three</p></body></html>");
		String text = parser.getText();
		System.out.println("text:\n" + text);
		assertTrue(text.contains("one\n\n\ttwo\n\nthree"));
	}

	public void testBR() throws Exception {
		parser.parse("<html><head></head><body><p>One<br/>Two</p></body></html>");
		String text = parser.getText();
		System.out.println("text:\n" + text);
		assertTrue(text.contains("One\nTwo"));
	}

	public void testWhitespaceAfterTable() throws Exception {
		parser.parse("<html><head></head><body>before<table><tr><td>in1</td></tr><tr><td>in2</td></tr></table>after</body></html>");
		String text = parser.getText();
		System.out.println("text:\n" + text);
		assertTrue(text.contains("in1 \t\nin2 \t\n\nafter"));
	}
}
