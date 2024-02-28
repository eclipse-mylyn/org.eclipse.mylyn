/*******************************************************************************
 * Copyright (c) 2011, 2024 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.creole.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.creole.CreoleLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Igor Malinin
 * @author David Green
 */
@SuppressWarnings({ "nls", "restriction" })
public class CreoleLanguageTest {

	private MarkupParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new MarkupParser(new CreoleLanguage());
	}

	@Test
	public void testDiscoverable() {
		MarkupLanguage language = ServiceLocator.getInstance(CreoleLanguageTest.class.getClassLoader())
				.getMarkupLanguage("Creole");
		assertNotNull(language);
		assertTrue(language instanceof CreoleLanguage);
	}

	@Test
	public void testParagraph() throws Exception {
		String html = parser.parseToHtml("a paragraph\n\nanother paragraph\\\\\nwith\n2 lines");

		assertEquals("<p>a paragraph</p><p>another paragraph<br/>\nwith\n2 lines</p>", content(html));
	}

	@Test
	public void testHeadings() {
		for (int level = 1; level <= 6; ++level) {
			String delimiter = repeat(level, "=");

			String[] headingMarkupSamples = { delimiter + " heading text ", delimiter + "\t heading text \t",
					delimiter + "heading text" + delimiter, delimiter + "\t heading text \t" + delimiter,
					delimiter + "heading text" + delimiter + " \t ", "\t " + delimiter + "heading text", };

			for (String headingMarkup : headingMarkupSamples) {
				String html = parser.parseToHtml(headingMarkup + "\n\npara");

				assertEquals("<h" + level + " id=\"headingtext\">heading text</h" + level + ">" + "<p>para</p>",
						content(html));
			}

			for (String headingMarkup : headingMarkupSamples) {
				String html = parser.parseToHtml("para\n" + headingMarkup + "\npara");

				assertEquals("<p>para</p>" + "<h" + level + " id=\"headingtext\">heading text</h" + level + ">"
						+ "<p>para</p>", content(html));
			}
		}
	}

	@Test
	public void testBold() {
		String html = parser.parseToHtml("normal **bold text** normal");

		assertEquals("<p>normal <b>bold text</b> normal</p>", content(html));
	}

	@Test
	public void testBold_LineStart() {
		String html = parser.parseToHtml("**bold text** normal");

		assertEquals("<p><b>bold text</b> normal</p>", content(html));
	}

	@Test
	public void testBold_LineEnd() {
		String html = parser.parseToHtml("normal **bold text**");

		assertEquals("<p>normal <b>bold text</b></p>", content(html));
	}

	@Test
	public void testBoldNotList() {
		String html = parser.parseToHtml("**bold text** normal");

		assertEquals("<p><b>bold text</b> normal</p>", content(html));
	}

	@Test
	public void testItalic() {
		String html = parser.parseToHtml("normal //italic text// normal");

		assertEquals("<p>normal <i>italic text</i> normal</p>", content(html));
	}

	@Test
	public void testItalic_LineStart() {
		String html = parser.parseToHtml("//italic text// normal");

		assertEquals("<p><i>italic text</i> normal</p>", content(html));
	}

	@Test
	public void testItalic_LineEnd() {
		String html = parser.parseToHtml("normal //italic text//");

		assertEquals("<p>normal <i>italic text</i></p>", content(html));
	}

	@Test
	public void testBoldItalic() {
		String html = parser.parseToHtml("normal **//bold italic text//** normal");

		assertEquals("<p>normal <b><i>bold italic text</i></b> normal</p>", content(html));
	}

	@Test
	public void testItalicBold() {
		String html = parser.parseToHtml("normal //**italic bold text**// normal");

		assertEquals("<p>normal <i><b>italic bold text</b></i> normal</p>", content(html));
	}

	@Test
	public void testItalicLink() {
		String html = parser.parseToHtml("normal //http://example.com// normal");

		assertEquals("<p>normal <i><a href=\"http://example.com\">http://example.com</a></i> normal</p>",
				content(html));
	}

	@Test
	public void testItalicLink2() {
		String html = parser.parseToHtml("normal //http://example.com https://example.com// normal");

		assertEquals("<p>normal <i><a href=\"http://example.com\">http://example.com</a>"
				+ " <a href=\"https://example.com\">https://example.com</a></i> normal</p>", content(html));
	}

	@Test
	public void testBoldItalicMixed() {
		String html = parser.parseToHtml("normal **bold** or //italic// or **//both//** or //**both**// normal");

		assertEquals("<p>normal <b>bold</b> or <i>italic</i> or <b><i>both</i></b> or <i><b>both</b></i> normal</p>",
				content(html));
	}

	@Test
	public void testBoldItalicEOL() {
		String html = parser.parseToHtml("normal **bold** or //italic// or **//both//bold");

		assertEquals("<p>normal <b>bold</b> or <i>italic</i> or <b><i>both</i>bold</b></p>", content(html));
	}

	@Test
	public void testMonospace() {
		String html = parser.parseToHtml("normal ##monospace text## normal");

		assertEquals("<p>normal <tt>monospace text</tt> normal</p>", content(html));
	}

	@Test
	public void testSuperscript() {
		String html = parser.parseToHtml("normal ^^superscript text^^ normal");

		assertEquals("<p>normal <sup>superscript text</sup> normal</p>", content(html));
	}

	@Test
	public void testSubscript() {
		String html = parser.parseToHtml("normal ,,subscript text,, normal");

		assertEquals("<p>normal <sub>subscript text</sub> normal</p>", content(html));
	}

	@Test
	public void testUnderlined() {
		String html = parser.parseToHtml("normal __underlined text__ normal");

		assertEquals("<p>normal <u>underlined text</u> normal</p>", content(html));
	}

	@Test
	public void testLinkInternal() {
		String html = parser.parseToHtml("[[internal link]]");

		assertEquals("<p><a href=\"internal_link\">internal link</a></p>", content(html));
	}

	@Test
	public void testLinkExternal() {
		String html = parser.parseToHtml("[[http://example.com|external link]]");

		assertEquals("<p><a href=\"http://example.com\">external link</a></p>", content(html));
	}

	@Test
	public void testListNotBold() {
		String html = parser.parseToHtml("*first\n**second **bold**\n***third");

		assertEquals("<ul><li>first<ul><li>second <b>bold</b><ul><li>third</li></ul></li></ul></li></ul>",
				content(html));
	}

	@Test
	public void testNowiki() {
		String html = parser.parseToHtml("{{{\nnowiki\ntext\n}}}");

		assertEquals("<pre>nowiki\ntext</pre>", content(html));
	}

	@Test
	public void testTable() {
		String html = parser.parseToHtml("|=a|=b|=c|\n|x|y|z|");

		assertEquals("<table><tr><th>a</th><th>b</th><th>c</th></tr><tr><td>x</td><td>y</td><td>z</td></tr></table>",
				content(html));
	}

	@Test
	public void testTildeEscape() {
		String html = parser.parseToHtml("~~ escape ~* escape ~= escape ~| escape ~# escape");

		assertEquals("<p>~ escape * escape = escape | escape # escape</p>", content(html));
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
