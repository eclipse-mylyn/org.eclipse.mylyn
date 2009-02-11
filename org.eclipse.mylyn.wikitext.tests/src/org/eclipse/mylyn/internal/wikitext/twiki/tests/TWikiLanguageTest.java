/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.twiki.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;

public class TWikiLanguageTest extends TestCase {

	private TWikiLanguage language;

	private MarkupParser parser;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		language = new TWikiLanguage();
		parser = new MarkupParser(language);
	}

	public void testParagraphs() {
		String html = parser.parseToHtml("first para first line\nfirst para second line\n\nsecond para\n\nthird para");
		System.out.println(html);
		assertTrue(html.contains("<body><p>first para first line<br/>first para second line</p><p>second para</p><p>third para</p></body>"));
	}

	public void testBold() {
		String html = parser.parseToHtml("*bold* normal *bold and more bold* normal *not bold *");
		System.out.println(html);
		assertTrue(html.contains("<body><p><b>bold</b> normal <b>bold and more bold</b> normal *not bold *</p></body>"));
	}

	public void testItalic() {
		String html = parser.parseToHtml("_italic_ normal _italic and more italic_ normal _not italic _");
		System.out.println(html);
		assertTrue(html.contains("<body><p><i>italic</i> normal <i>italic and more italic</i> normal _not italic _</p></body>"));
	}

	public void testBoldItalic() {
		String html = parser.parseToHtml("__bolditalic__ normal __bolditalic and more bolditalic__ normal __not bolditalic __");
		System.out.println(html);
		assertTrue(html.contains("<body><p><b><i>bolditalic</i></b> normal <b><i>bolditalic and more bolditalic</i></b> normal <i>_not bolditalic _</i></p></body>"));
	}

	public void testFixedFont() {
		String html = parser.parseToHtml("=monospace= normal =monospace and more monospace= normal =not monospace =");
		System.out.println(html);
		assertTrue(html.contains("<body><p><tt>monospace</tt> normal <tt>monospace and more monospace</tt> normal =not monospace =</p></body>"));
	}

	public void testBoldFixedFont() {
		String html = parser.parseToHtml("==boldmonospace== normal ==boldmonospace and more monospace== normal ==not boldmonospace ==");
		System.out.println(html);
		assertTrue(html.contains("<p><b><tt>boldmonospace</tt></b> normal <b><tt>boldmonospace and more monospace</tt></b> normal <tt>=not boldmonospace =</tt></p></body>"));
	}

	public void testVerbatimBlock() {
		String html = parser.parseToHtml("a para\n\n<verbatim>\nsome *escaped* text _no twiki here_!! not even <b>html</b>\n</verbatim>\n\nnormal para");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a para</p><p>\nsome *escaped* text _no twiki here_!! not even &lt;b>html&lt;/b>\n</p><p>normal para</p></body>"));
	}

	public void testLiteralBlock() {
		String html = parser.parseToHtml("a para\n\n<literal>\nsome *escaped* text _no twiki here_!! <b>html</b> is allowed\n</literal>\n\nnormal para");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a para</p><p>\nsome *escaped* text _no twiki here_!! <b>html</b> is allowed\n</p><p>normal para</p></body>"));
	}

	public void testHorizontalRule() {
		String html = parser.parseToHtml("a para\n------\nmore text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a para</p><hr/><p>more text</p></body>"));
	}

	public void testHeadings() {
		for (String prefix : new String[] { "!!", "" }) {
			for (int level = 1; level <= 6; ++level) {
				String markup = "---";
				for (int x = 0; x < level; ++x) {
					markup += "+";
				}
				markup += prefix;
				markup += " heading text  \t ";

				String html = parser.parseToHtml(markup);
				System.out.println(html);
				assertTrue(html.contains("<body><h" + level + " id=\"headingtext\">heading text</h" + level
						+ "></body>"));
			}
		}
	}

	public void testDefinitionLists() {
		String markup = "normal para1\n   $ first : def1 more text\n   $ second : def2 more text\nnormal para2";
		String html = parser.parseToHtml(markup);
		System.out.println(html);
		assertTrue(html.contains("<body><p>normal para1</p><dl><dt>first </dt><dd>def1 more text</dd><dt>second </dt><dd>def2 more text</dd></dl><p>normal para2</p></body>"));
	}

	public void testTableOfContents() {
		String html = parser.parseToHtml("---+ outline\n%TOC%\n---+ f1\n---++ f1.1\n---++ f1.2\n---+ f2\n---+ f3\n---++ f3.1");
		System.out.println(html);
		assertTrue(html.contains("<body><h1 id=\"outline\">outline</h1><ol><li><a href=\"#outline\">outline</a></li><li><a href=\"#f1\">f1</a><ol><li><a href=\"#f11\">f1.1</a></li><li><a href=\"#f12\">f1.2</a></li></ol></li><li><a href=\"#f2\">f2</a></li><li><a href=\"#f3\">f3</a><ol><li><a href=\"#f31\">f3.1</a></li></ol></li></ol><h1 id=\"f1\">f1</h1><h2 id=\"f11\">f1.1</h2><h2 id=\"f12\">f1.2</h2><h1 id=\"f2\">f2</h1><h1 id=\"f3\">f3</h1><h2 id=\"f31\">f3.1</h2></body>"));
	}

	public void testWikiWord() {
		String html = parser.parseToHtml("A WikiWord points somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A <a href=\"/cgi-bin/view/Main/WikiWord\">WikiWord</a> points somewhere</p></body>"));
	}

	public void testNoAutolink() {
		String html = parser.parseToHtml("<noautolink>A WikiWord points somewhere but not this one!</noautolink>");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A WikiWord points somewhere but not this one!</p></body>"));
	}

	public void testWikiWordEscaped() {
		String html = parser.parseToHtml("A !WikiWord points somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A WikiWord points somewhere</p></body>"));
	}

	public void testList() {
		String html = parser.parseToHtml("   * one\n   * two\n      * two.one\n      * two.two\n   * three");
		System.out.println(html);
		assertTrue(html.contains("<body><ul><li>one</li><li>two<ul><li>two.one</li><li>two.two</li></ul></li><li>three</li></ul></body>"));
	}

	public void testListWithContinuation() {
		String html = parser.parseToHtml("   * one\n     continuation on one\n   * two\n      * two.one\n      * two.two\n        continuation on two.two\n   * three");
		System.out.println(html);
		assertTrue(html.contains("<body><ul><li>one continuation on one</li><li>two<ul><li>two.one</li><li>two.two continuation on two.two</li></ul></li><li>three</li></ul></body>"));
	}

	public void testListNumeric() {
		String html = parser.parseToHtml("   1. one\n   1. two\n      1. two.one\n      1. two.two\n   1. three");
		System.out.println(html);
		assertTrue(html.contains("<body><ol><li>one</li><li>two<ol><li>two.one</li><li>two.two</li></ol></li><li>three</li></ol></body>"));
	}

	public void testListNumericLowerAlpha() {
		String html = parser.parseToHtml("   a. one\n   a. two\n      a. two.one\n      a. two.two\n   a. three");
		System.out.println(html);
		assertTrue(html.contains("<body><ol style=\"list-style: lower-alpha;\"><li>one</li><li>two<ol style=\"list-style: lower-alpha;\"><li>two.one</li><li>two.two</li></ol></li><li>three</li></ol></body>"));
	}

	public void testListNumericUpperAlpha() {
		String html = parser.parseToHtml("   A. one\n   A. two\n      A. two.one\n      A. two.two\n   A. three");
		System.out.println(html);
		assertTrue(html.contains("<body><ol style=\"list-style: upper-alpha;\"><li>one</li><li>two<ol style=\"list-style: upper-alpha;\"><li>two.one</li><li>two.two</li></ol></li><li>three</li></ol></body>"));
	}

	public void testListNumericLowerRoman() {
		String html = parser.parseToHtml("   i. one\n   i. two\n      i. two.one\n      i. two.two\n   i. three");
		System.out.println(html);
		assertTrue(html.contains("<body><ol style=\"list-style: lower-roman;\"><li>one</li><li>two<ol style=\"list-style: lower-roman;\"><li>two.one</li><li>two.two</li></ol></li><li>three</li></ol></body>"));
	}

	public void testListNumericUpperRoman() {
		String html = parser.parseToHtml("   I. one\n   I. two\n      I. two.one\n      I. two.two\n   I. three");
		System.out.println(html);
		assertTrue(html.contains("<body><ol style=\"list-style: upper-roman;\"><li>one</li><li>two<ol style=\"list-style: upper-roman;\"><li>two.one</li><li>two.two</li></ol></li><li>three</li></ol></body>"));
	}

	public void testListNumericWithBulleted() {
		String html = parser.parseToHtml("   1. one\n   * two");
		System.out.println(html);
		assertTrue(html.contains("<body><ol><li>one</li></ol><ul><li>two</li></ul></body>"));
	}

	public void testLink() {
		String html = parser.parseToHtml("a [[http://link]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"http://link\">http://link</a> to somewhere</p></body>"));
	}

	public void testLinkWithText() {
		String html = parser.parseToHtml("a [[http://link][alt text]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"http://link\">alt text</a> to somewhere</p></body>"));
	}

	public void testLinkInternal() {
		String html = parser.parseToHtml("a [[internal link]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"/cgi-bin/view/Main/InternalLink\">internal link</a> to somewhere</p></body>"));
	}

	public void testLinkInternal2() {
		String html = parser.parseToHtml("a [[InternalLink]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"/cgi-bin/view/Main/InternalLink\">InternalLink</a> to somewhere</p></body>"));
	}

	public void testLinkInternalWithText() {
		String html = parser.parseToHtml("a [[internal link][alt text]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"/cgi-bin/view/Main/InternalLink\">alt text</a> to somewhere</p></body>"));
	}

	public void testLinkEscaped() {
		String html = parser.parseToHtml("a ![[http://nolink]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a [[http://nolink]] to somewhere</p></body>"));
	}

	public void testLinkMailto() {
		String html = parser.parseToHtml("a [[mailto:someone@example.com]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"mailto:someone@example.com\">someone@example.com</a> to somewhere</p></body>"));
	}

	public void testLinkMailtoWithText() {
		String html = parser.parseToHtml("a [[mailto:someone@example.com][who?]] to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"mailto:someone@example.com\">who?</a> to somewhere</p></body>"));
	}

	public void testLinkImpliedMailto() {
		String html = parser.parseToHtml("a someone@example.com to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"mailto:someone@example.com\">someone@example.com</a> to somewhere</p></body>"));
	}

	public void testIconUrls() {
		for (String iconType : new String[] { "help", "tip", "warning" }) {
			String html = parser.parseToHtml("a %ICON{\"" + iconType + "\"}% text");
			System.out.println(html);
			assertTrue(html.contains("<body><p>a <img border=\"0\" src=\"TWikiDocGraphics/" + iconType
					+ ".gif\"/> text</p></body>"));
		}
	}

}
