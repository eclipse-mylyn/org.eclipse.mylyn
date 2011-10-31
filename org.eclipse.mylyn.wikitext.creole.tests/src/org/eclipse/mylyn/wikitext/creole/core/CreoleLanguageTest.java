/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.creole.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

/**
 * @author Igor Malinin
 * @author David Green
 */
public class CreoleLanguageTest extends TestCase {

	private MarkupParser parser;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		parser = new MarkupParser(new CreoleLanguage());
	}

	public void testParagraph() throws Exception {
		String html = parser.parseToHtml("a paragraph\n\nanother paragraph\\\\\nwith\n2 lines");
		TestUtil.println("HTML:" + html);
		assertEquals("<p>a paragraph</p><p>another paragraph<br/>\nwith\n2 lines</p>", content(html));
	}

	public void testHeadings() {
		for (int level = 1; level <= 6; ++level) {
			String delimiter = repeat(level, "=");

			String[] headingMarkupSamples = new String[] { delimiter + " heading text ",
					delimiter + "\t heading text \t", delimiter + "heading text" + delimiter,
					delimiter + "\t heading text \t" + delimiter, delimiter + "heading text" + delimiter + " \t ",
					"\t " + delimiter + "heading text", };

			for (String headingMarkup : headingMarkupSamples) {
				String html = parser.parseToHtml(headingMarkup + "\n\npara");
				TestUtil.println(html);
				assertEquals("<h" + level + " id=\"headingtext\">heading text</h" + level + ">" + "<p>para</p>",
						content(html));
			}

			for (String headingMarkup : headingMarkupSamples) {
				String html = parser.parseToHtml("para\n" + headingMarkup + "\npara");
				TestUtil.println(html);
				assertEquals("<p>para</p>" + "<h" + level + " id=\"headingtext\">heading text</h" + level + ">"
						+ "<p>para</p>", content(html));
			}
		}
	}

	public void testBold() {
		String html = parser.parseToHtml("normal **bold text** normal");
		TestUtil.println(html);
		assertEquals("<p>normal <b>bold text</b> normal</p>", content(html));
	}

	public void testBold_LineStart() {
		String html = parser.parseToHtml("**bold text** normal");
		TestUtil.println(html);
		assertEquals("<p><b>bold text</b> normal</p>", content(html));
	}

	public void testBold_LineEnd() {
		String html = parser.parseToHtml("normal **bold text**");
		TestUtil.println(html);
		assertEquals("<p>normal <b>bold text</b></p>", content(html));
	}

	public void testBoldNotList() {
		String html = parser.parseToHtml("**bold text** normal");
		TestUtil.println(html);
		assertEquals("<p><b>bold text</b> normal</p>", content(html));
	}

	public void testItalic() {
		String html = parser.parseToHtml("normal //italic text// normal");
		TestUtil.println(html);
		assertEquals("<p>normal <i>italic text</i> normal</p>", content(html));
	}

	public void testItalic_LineStart() {
		String html = parser.parseToHtml("//italic text// normal");
		TestUtil.println(html);
		assertEquals("<p><i>italic text</i> normal</p>", content(html));
	}

	public void testItalic_LineEnd() {
		String html = parser.parseToHtml("normal //italic text//");
		TestUtil.println(html);
		assertEquals("<p>normal <i>italic text</i></p>", content(html));
	}

	public void testBoldItalic() {
		String html = parser.parseToHtml("normal **//bold italic text//** normal");
		TestUtil.println(html);
		assertEquals("<p>normal <b><i>bold italic text</i></b> normal</p>", content(html));
	}

	public void testItalicBold() {
		String html = parser.parseToHtml("normal //**italic bold text**// normal");
		TestUtil.println(html);
		assertEquals("<p>normal <i><b>italic bold text</b></i> normal</p>", content(html));
	}

	public void testItalicLink() {
		String html = parser.parseToHtml("normal //http://example.com// normal");
		TestUtil.println(html);
		assertEquals("<p>normal <i><a href=\"http://example.com\">http://example.com</a></i> normal</p>", content(html));
	}

	public void testItalicLink2() {
		String html = parser.parseToHtml("normal //http://example.com https://example.com// normal");
		TestUtil.println(html);
		assertEquals("<p>normal <i><a href=\"http://example.com\">http://example.com</a>"
				+ " <a href=\"https://example.com\">https://example.com</a></i> normal</p>", content(html));
	}

	public void testBoldItalicMixed() {
		String html = parser.parseToHtml("normal **bold** or //italic// or **//both//** or //**both**// normal");
		TestUtil.println(html);
		assertEquals("<p>normal <b>bold</b> or <i>italic</i> or <b><i>both</i></b> or <i><b>both</b></i> normal</p>",
				content(html));
	}

	public void testBoldItalicEOL() {
		String html = parser.parseToHtml("normal **bold** or //italic// or **//both//bold");
		TestUtil.println(html);
		assertEquals("<p>normal <b>bold</b> or <i>italic</i> or <b><i>both</i>bold</b></p>", content(html));
	}

	public void testMonospace() {
		String html = parser.parseToHtml("normal ##monospace text## normal");
		TestUtil.println(html);
		assertEquals("<p>normal <tt>monospace text</tt> normal</p>", content(html));
	}

	public void testSuperscript() {
		String html = parser.parseToHtml("normal ^^superscript text^^ normal");
		TestUtil.println(html);
		assertEquals("<p>normal <sup>superscript text</sup> normal</p>", content(html));
	}

	public void testSubscript() {
		String html = parser.parseToHtml("normal ,,subscript text,, normal");
		TestUtil.println(html);
		assertEquals("<p>normal <sub>subscript text</sub> normal</p>", content(html));
	}

	public void testUnderlined() {
		String html = parser.parseToHtml("normal __underlined text__ normal");
		TestUtil.println(html);
		assertEquals("<p>normal <u>underlined text</u> normal</p>", content(html));
	}

	public void testLinkInternal() {
		String html = parser.parseToHtml("[[internal link]]");
		TestUtil.println(html);
		assertEquals("<p><a href=\"internal_link\">internal link</a></p>", content(html));
	}

	public void testLinkExternal() {
		String html = parser.parseToHtml("[[http://example.com|external link]]");
		TestUtil.println(html);
		assertEquals("<p><a href=\"http://example.com\">external link</a></p>", content(html));
	}

	public void testListNotBold() {
		String html = parser.parseToHtml("*first\n**second **bold**\n***third");
		TestUtil.println(html);
		assertEquals("<ul><li>first<ul><li>second <b>bold</b><ul><li>third</li></ul></li></ul></li></ul>",
				content(html));
	}

	public void testNowiki() {
		String html = parser.parseToHtml("{{{\nnowiki\ntext\n}}}");
		TestUtil.println(html);
		assertEquals("<pre>nowiki\ntext\n</pre>", content(html));
	}

	public void testTable() {
		String html = parser.parseToHtml("|=a|=b|=c|\n|x|y|z|");
		TestUtil.println(html);
		assertEquals("<table><tr><th>a</th><th>b</th><th>c</th></tr><tr><td>x</td><td>y</td><td>z</td></tr></table>",
				content(html));
	}

	private String repeat(int i, String string) {
		StringBuilder buf = new StringBuilder(string.length() * i);
		for (int x = 0; x < i; ++x) {
			buf.append(string);
		}
		return buf.toString();
	}

	private String content(String html) {
		int begin = html.indexOf("<body>") + 6;
		int end = html.lastIndexOf("</body>");
		return html.substring(begin, end);
	}

}
