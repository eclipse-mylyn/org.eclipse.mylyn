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

	private TracWikiLanguage markupLanaguage;

	@Override
	public void setUp() {
		markupLanaguage = new TracWikiLanguage();
		parser = new MarkupParser(markupLanaguage);
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

	// test for bug 263015
	public void testItalic2() {
		String html = parser.parseToHtml("normal ''italic''-''italic'' normal");
		System.out.println(html);
		assertTrue(html.contains("<body><p>normal <i>italic</i>-<i>italic</i> normal</p></body>"));
	}

	public void testDeleted() {
		String html = parser.parseToHtml("normal --test text-- normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <del>test text</del> normal</p></body>").matcher(html).find());
	}

	public void testDeleted2() {
		String html = parser.parseToHtml("normal ---test text-- normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal ---test text-- normal</p></body>").matcher(html).find());
	}

	public void testDeleted3() {
		String html = parser.parseToHtml("normal --test text--- normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal --test text--- normal</p></body>").matcher(html).find());
	}

	public void testDeleted_AtStartOfLine() {
		String html = parser.parseToHtml("--test text-- normal");
		System.out.println(html);
		assertTrue(html.contains("<body><p><del>test text</del> normal</p></body>"));
	}

	public void testDeleted_AtEndOfLine() {
		String html = parser.parseToHtml("normal --test text--");
		System.out.println(html);
		assertTrue(html.contains("<body><p>normal <del>test text</del></p></body>"));
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
					"<body><h" + x + " id=\"headingtext\">heading text</h" + x
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

	public void testListNumericWithBulleted() {
		String html = parser.parseToHtml("   1. one\n   * two");
		System.out.println(html);
		assertTrue(html.contains("<body><ol><li>one</li></ol><ul><li>two</li></ul></body>"));
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

	public void testPreformattedInline() throws IOException {
		String html = parser.parseToHtml("first para {{{ preformatted text }}} more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>first para <tt> preformatted text </tt> more text</p></body>"));
	}

	public void testPreformattedInline2() throws IOException {
		String html = parser.parseToHtml("first para {{{ preformatted text }}} and {{{ more code }}} more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>first para <tt> preformatted text </tt> and <tt> more code </tt> more text</p></body>"));
	}

	public void testPreformattedInline3() throws IOException {
		String html = parser.parseToHtml("{{{ preformatted text }}}");
		System.out.println(html);
		assertTrue(html.contains("<body><p><tt> preformatted text </tt></p></body>"));
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

	public void testInternalHyperlinkWithTitle() {
		String html = parser.parseToHtml("a normal para [wiki:ISO9000 ISO 9000] with a hyperlink");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a normal para <a href=\"ISO9000\">ISO 9000</a> with a hyperlink</p></body>"));
	}

	public void testInternalHyperlinkWithoutTitle() {
		String html = parser.parseToHtml("a normal para [wiki:ISO9000] with a hyperlink");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a normal para <a href=\"ISO9000\">ISO9000</a> with a hyperlink</p></body>"));
	}

	public void testWikiWord() {
		markupLanaguage.setInternalLinkPattern("https://foo.bar/wiki/{0}");
		String html = parser.parseToHtml("A WikiWord points somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A <a href=\"https://foo.bar/wiki/WikiWord\">WikiWord</a> points somewhere</p></body>"));
	}

	public void testWikiWordNegativeMatch() {
		markupLanaguage.setInternalLinkPattern("https://foo.bar/wiki/{0}");
		String html = parser.parseToHtml("A noWikiWord points somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A noWikiWord points somewhere</p></body>"));
	}

	public void testWikiWordAtLineStart() {
		markupLanaguage.setInternalLinkPattern("https://foo.bar/wiki/{0}");
		String html = parser.parseToHtml("WikiWord points somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p><a href=\"https://foo.bar/wiki/WikiWord\">WikiWord</a> points somewhere</p></body>"));
	}

	public void testWikiWordAtLineEnd() {
		markupLanaguage.setInternalLinkPattern("https://foo.bar/wiki/{0}");
		String html = parser.parseToHtml("a WikiWord");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a <a href=\"https://foo.bar/wiki/WikiWord\">WikiWord</a></p></body>"));
	}

	public void testWikiWordNoAutolink() {
		markupLanaguage.setAutoLinking(false);
		String html = parser.parseToHtml("A WikiWord points somewhere but not this one!");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A WikiWord points somewhere but not this one!</p></body>"));
	}

	public void testWikiWordEscaped() {
		String html = parser.parseToHtml("A !WikiWord points somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A WikiWord points somewhere</p></body>"));
	}

	public void testTicketLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A ticket #1 or ticket:1 to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A ticket <a href=\"http://trac.edgewall.org/ticket/1\">#1</a> or <a href=\"http://trac.edgewall.org/ticket/1\">ticket:1</a> to somewhere</p></body>"));
	}

	public void testTicketLinkWithComment() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A ticket comment:1:ticket:2 to somewhere");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A ticket <a href=\"http://trac.edgewall.org/ticket/2#comment:1\">comment:1:ticket:2</a> to somewhere</p></body>"));
	}

	public void testReportLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A report:1 about something");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A <a href=\"http://trac.edgewall.org/report/1\">report:1</a> about something</p></body>"));
	}

	public void testChangesetLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A changeset r1 or [1] or [1/trunk] or changeset:1 or changeset:1/trunk more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A changeset <a href=\"http://trac.edgewall.org/changeset/1\">r1</a> or <a href=\"http://trac.edgewall.org/changeset/1\">[1]</a> or <a href=\"http://trac.edgewall.org/changeset/1/trunk\">[1/trunk]</a> or <a href=\"http://trac.edgewall.org/changeset/1\">changeset:1</a> or <a href=\"http://trac.edgewall.org/changeset/1/trunk\">changeset:1/trunk</a> more text</p></body>"));
	}

	public void testRevisionLogLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A revision log r1:3, [1:3] or log:@1:3, log:trunk@1:3, [2:5/trunk] more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A revision log <a href=\"http://trac.edgewall.org/log/?revs=1-3\">r1:3</a>, <a href=\"http://trac.edgewall.org/log/?revs=1-3\">[1:3]</a> or <a href=\"http://trac.edgewall.org/log/?revs=1-3\">log:@1:3</a>, <a href=\"http://trac.edgewall.org/log/trunk?revs=1-3\">log:trunk@1:3</a>, <a href=\"http://trac.edgewall.org/log/trunk?revs=2-5\">[2:5/trunk]</a> more text</p></body>"));
	}

	public void testMilestoneLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A milestone:1.0 more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A <a href=\"http://trac.edgewall.org/milestone/1.0\">milestone:1.0</a> more text</p></body>"));
	}

	public void testTicketAttachmentLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A attachment:foobar.txt:ticket:12345 more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A <a href=\"http://trac.edgewall.org/ticket/12345/foobar.txt\">attachment:foobar.txt:ticket:12345</a> more text</p></body>"));
	}

	public void testSourceLink() {
		markupLanaguage.setServerUrl("http://trac.edgewall.org");
		String html = parser.parseToHtml("A source:/trunk/COPYING or source:/trunk/COPYING@200 or source:/trunk/COPYING@200#L26 more text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A <a href=\"http://trac.edgewall.org/browser/trunk/COPYING\">source:/trunk/COPYING</a> or <a href=\"http://trac.edgewall.org/browser/trunk/COPYING?rev=200\">source:/trunk/COPYING@200</a> or <a href=\"http://trac.edgewall.org/browser/trunk/COPYING?rev=200#L26\">source:/trunk/COPYING@200#L26</a> more text</p></body>"));
	}

}
