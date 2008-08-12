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
package org.eclipse.mylyn.wikitext.tracwiki.core;

import java.io.IOException;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

/**
 * 
 * 
 * @author David Green
 */
public class TracWikiLanguageTest extends TestCase {

	private MarkupParser parser;

	public void setUp() {
		parser = new MarkupParser(new TracWikiLanguage());
	}

	public void testParagraphs() throws IOException {
		String html = parser.parseToHtml("first para\nnew line\n\nsecond para\n\n\n\n");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>first para\\s*new line</p><p>second para</p></body>", Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testBoldItalic() {
		String html = parser.parseToHtml("normal '''''bold italic text''''' normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <b><i>bold italic text</i></b> normal</p></body>")
				.matcher(html)
				.find());
	}

	public void testBold() {
		String html = parser.parseToHtml("normal '''bold text''' normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <b>bold text</b> normal</p></body>").matcher(html).find());
	}

	public void testBoldEscaped() {
		String html = parser.parseToHtml("normal '''!'''bold text''' normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <b>'''bold text</b> normal</p></body>").matcher(html).find());
	}

	public void testItalic() {
		String html = parser.parseToHtml("normal ''italic text'' normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <i>italic text</i> normal</p></body>").matcher(html).find());
	}

	public void testDeleted() {
		String html = parser.parseToHtml("normal --test text-- normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <del>test text</del> normal</p></body>").matcher(html).find());
	}

	public void testUnderlined() {
		String html = parser.parseToHtml("normal __test text__ normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <u>test text</u> normal</p></body>").matcher(html).find());
	}

	public void testSuperscript() {
		String html = parser.parseToHtml("normal ^test text^ normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <sup>test text</sup> normal</p></body>").matcher(html).find());
	}

	public void testSubscript() {
		String html = parser.parseToHtml("normal ,,test text,, normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <sub>test text</sub> normal</p></body>").matcher(html).find());
	}

	public void testEscapedWithBacktick() {
		String html = parser.parseToHtml("normal `test text` normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <tt>test text</tt> normal</p></body>").matcher(html).find());
	}

	public void testEscapedWithCurlys() {
		String html = parser.parseToHtml("normal {{test text}} normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <tt>test text</tt> normal</p></body>").matcher(html).find());
	}

	public void testHeadings() {
		for (int x = 1; x <= 6; ++x) {
			String delimiter = repeat(x, "=");
			String html = parser.parseToHtml(delimiter + "heading text" + delimiter
					+ "\nfirst para\nfirst para line2\n\nsecond para\n\nthird para");
			System.out.println(html);
			assertTrue(Pattern.compile(
					"<body><h" + x + ">heading text</h" + x
							+ "><p>first para\\s*first para line2</p><p>second para</p><p>third para</p></body>",
					Pattern.MULTILINE).matcher(html).find());

			html = parser.parseToHtml(delimiter + "heading text" + delimiter + " #with-id-" + x
					+ "\nfirst para\nfirst para line2\n\nsecond para\n\nthird para");
			System.out.println(html);
			assertTrue(Pattern.compile(
					"<body><h" + x + " id=\"with-id-" + x + "\">heading text</h" + x
							+ "><p>first para\\s*first para line2</p><p>second para</p><p>third para</p></body>",
					Pattern.MULTILINE).matcher(html).find());
		}
	}

	private String repeat(int i, String string) {
		StringBuilder buf = new StringBuilder(string.length() * i);
		for (int x = 0; x < i; ++x) {
			buf.append(string);
		}
		return buf.toString();
	}

	public void testLineBreak() {
		String html = parser.parseToHtml("normal text[[BR]]normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal text<br/>\\s*normal</p></body>").matcher(html).find());
	}

	public void testListUnordered() throws IOException {
		String html = parser.parseToHtml(" * a list\n * with two lines");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ul>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ul>"));
	}

	public void testListOrdered() throws IOException {
		String html = parser.parseToHtml(" 1. a list\n 2. with two lines");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ol>"));
	}

	public void testListNested() throws IOException {
		String html = parser.parseToHtml(" 1. a list\n  1. nested\n  1. nested2\n 1. level1\n\npara");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol>"));
		assertTrue(html.contains("<li>a list"));
		assertTrue(html.contains("<li>nested"));
		assertTrue(html.contains("</ol>"));
	}

	public void testListNestedMixed() throws IOException {
		String html = parser.parseToHtml(" 1. a list\n  * nested\n  * nested2\n 1. level1\n\npara");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol><li>a list<ul><li>nested</li><li>nested2</li></ul></li><li>level1</li></ol>"));
	}

	public void testPreformatted() throws IOException {
		String html = parser.parseToHtml("first para\n\n{{{\n\tpreformatted text\n\nspanning multilple lines\n}}}\nsecond para");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>first para</p><pre>\\s*?\tpreformatted text\\s*spanning multilple lines\\s*</pre><p>second para</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testPreformattedNextToPara() throws IOException {
		String html = parser.parseToHtml("first para\n{{{\n\tpreformatted text\n\nspanning multilple lines\n}}}\nsecond para");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>first para</p><pre>\\s*?\tpreformatted text\\s*spanning multilple lines\\s*</pre><p>second para</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testQuoteBlock() throws IOException {
		String html = parser.parseToHtml("" + ">> second level\n" + ">> second level line 2\n" + "> first level\n"
				+ "new para\n" + "");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><blockquote><blockquote><p>second level<br/>\\s*second level line 2</p></blockquote><p>first level</p></blockquote><p>new para</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testQuoteBlockFollowingPara() throws IOException {
		String html = parser.parseToHtml("" + "normal para\n" + "> quoted\n" + "new para\n" + "");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>normal para</p><blockquote><p>quoted</p></blockquote><p>new para</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testQuoteBlockWithSpaces() throws IOException {
		String html = parser.parseToHtml("" + "normal para\n" + "  quoted\n" + "  first level\n" + "new para\n" + "");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>normal para</p><blockquote><p>quoted<br/>\\s*first level</p></blockquote><p>new para</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testTableBlock() {
		String html = parser.parseToHtml("" + "normal para\n" + "||a table||row with three||columns||\n"
				+ "||another||row||||\n" + "new para\n" + "");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body>" + "<p>normal para</p>" + "<table>" + "<tr>" + "<td>a table</td>" + "<td>row with three</td>"
						+ "<td>columns</td>" + "</tr>" + "<tr>" + "<td>another</td>" + "<td>row</td>" + "<td></td>"
						+ "</tr>" + "</table>" + "<p>new para</p></body>", Pattern.MULTILINE).matcher(html).find());
	}

	public void testHyperlink() {
		String html = parser.parseToHtml("a normal para http://www.example.com with a hyperlink");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>a normal para <a href=\"http://www.example.com\">http://www.example.com</a> with a hyperlink</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testHyperlinkWithTitle() {
		String html = parser.parseToHtml("a normal para [http://www.example.com Example ] with a hyperlink");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>a normal para <a href=\"http://www.example.com\">Example</a> with a hyperlink</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testHyperlinkWithoutTitle() {
		String html = parser.parseToHtml("a normal para [http://www.example.com] with a hyperlink");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>a normal para <a href=\"http://www.example.com\">http://www.example.com</a> with a hyperlink</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}
}
