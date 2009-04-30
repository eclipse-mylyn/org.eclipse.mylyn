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
package org.eclipse.mylyn.wikitext.confluence.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

/**
 * 
 * 
 * @author David Green
 */
public class ConfluenceLanguageTest extends TestCase {

	private MarkupParser parser;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initParser();
	}

	private void initParser() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new ConfluenceLanguage());
	}

	public void testIsDetectingRawHyperlinks() {
		assertTrue(parser.getMarkupLanguage().isDetectingRawHyperlinks());
	}

	public void testParagraph() throws Exception {
		String html = parser.parseToHtml("a paragraph\n\nanother paragraph\nwith\n2 lines");
		System.out.println("HTML:" + html);
		assertTrue(Pattern.compile(
				"<body><p>a paragraph</p><p>another paragraph<br/>\\s*with<br/>\\s*2 lines</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testHeadings() {
		for (int x = 1; x <= 6; ++x) {
			initParser();
			String html = parser.parseToHtml("h" + x + ". a heading\n\nwith a para");
			System.out.println("HTML:" + html);
			assertTrue(Pattern.compile(
					"<body><h" + x + " id=\"aheading\">a heading</h" + x + "><p>with a para</p></body>",
					Pattern.MULTILINE).matcher(html).find());

			html = parser.parseToHtml("h" + x + ". a heading\nwith a para");
			System.out.println("HTML:" + html);
			assertTrue(Pattern.compile(
					"<body><h" + x + " id=\"aheading\">a heading</h" + x + "><p>with a para</p></body>",
					Pattern.MULTILINE).matcher(html).find());

			html = parser.parseToHtml("  h" + x + ". a heading\n\nwith a para");
			System.out.println("HTML:" + html);
			assertTrue(Pattern.compile(
					"<body><h" + x + " id=\"aheading\">a heading</h" + x + "><p>with a para</p></body>",
					Pattern.MULTILINE).matcher(html).find());
		}
	}

	public void testBlockQuote() {
		String html = parser.parseToHtml("bq. a multiline\nblock quote\n\nwith a para");
		System.out.println("HTML:" + html);
		assertTrue(Pattern.compile(
				"<body><blockquote><p>a multiline<br/>\\s*block quote</p></blockquote><p>with a para</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testBlockQuoteExtended() {
		String html = parser.parseToHtml("{quote}\na multiline\nblock quote\n\nwith two paras\n{quote}\nanother para");
		System.out.println("HTML:" + html);
		assertTrue(Pattern.compile(
				"<body><blockquote><p>a multiline<br/>\\s*block quote</p><p>with two paras</p></blockquote><p>another para</p></body>",
				Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	public void testBlockQuoteExtended2() {
		String html = parser.parseToHtml("{quote}this is a quote{quote}\nsome more text");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><blockquote><p>this is a quote</p></blockquote><p>some more text</p></body>"));
	}

	public void testBlockQuoteExtendedUnclosed() {
		String html = parser.parseToHtml("{quote}\na multiline\nblock quote\n\nwith two paras\n");
		System.out.println("HTML:" + html);
		assertTrue(Pattern.compile(
				"<body><blockquote><p>a multiline<br/>\\s*block quote</p><p>with two paras</p></blockquote></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testBlockQuoteExtendedLeadingSpaces() {
		String html = parser.parseToHtml("     {quote}\na multiline\nblock quote\n    {quote}\nmore text");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><blockquote><p>a multiline<br/>block quote</p></blockquote><p>more text</p></body>"));
	}

	public void testBlockQuoteExtendedBreaksPara() {
		String html = parser.parseToHtml("a para\n{quote}quoted{quote}new para");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><p>a para</p><blockquote><p>quoted</p></blockquote><p>new para</p></body>"));
	}

	public void testBlockQuoteWithBulletedList() {
		String html = parser.parseToHtml("{quote}\ntext\n* a list\n* second item\nmore text\n{quote}\nanother para");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><blockquote><p>text</p><ul><li>a list</li><li>second item</li></ul><p>more text</p></blockquote><p>another para</p></body>"));
	}

	public void testSimplePhraseModifiers() throws IOException {
		Object[][] pairs = new Object[][] { { "*", "strong" }, { "_", "em" }, { "??", "cite" }, { "-", "del" },
				{ "+", "u" }, { "^", "sup" }, { "~", "sub" }, };
		for (Object[] pair : pairs) {
			initParser();
			String html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0]);
			System.out.println("HTML: \n" + html);
			assertTrue(html.contains("<p>a paragraph with <" + pair[1] + ">content foo bar baz</" + pair[1] + "></p>"));
		}
	}

	public void testMonospaced() {
		String html = parser.parseToHtml("a paragraph with {{content foo bar baz}}");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>a paragraph with <tt>content foo bar baz</tt></p>"));
	}

	public void testLineBreak() {
		String html = parser.parseToHtml("a paragraph with an arbitrary\\\\line break");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<body><p>a paragraph with an arbitrary<br/>\\s*line break</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testEndash() {
		String html = parser.parseToHtml("an endash -- foo");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("endash &#8211; foo"));
	}

	public void testEmdash() {
		String html = parser.parseToHtml("an emdash --- foo");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("emdash &#8212; foo"));
	}

	public void testHorizontalRule() {
		String html = parser.parseToHtml("an hr ---- foo");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("hr <hr/> foo"));
	}

	public void testHyperlink() {
		String html = parser.parseToHtml("a [http://example.com] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	public void testHyperlinkWithTitle() {
		String html = parser.parseToHtml("a [Example|http://example.com] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example</a> hyperlink</p></body>"));
	}

	public void testHyperlinkWithTitle2() {
		String html = parser.parseToHtml("a [Example Two | http://example.com] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example Two</a> hyperlink</p></body>"));
	}

	public void testHyperlinkHash() {
		String html = parser.parseToHtml("a [Example|#example] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"#example\">Example</a> hyperlink</p></body>"));
	}

	public void testHyperlinkHash2() {
		String html = parser.parseToHtml("a [#example] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"#example\">example</a> hyperlink</p></body>"));
	}

	public void testHyperlinkWithTip() {
		String html = parser.parseToHtml("a [example | http://example.com | title is here] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\" title=\"title is here\">example</a> hyperlink</p></body>"));
	}

	public void testHyperlinkImplied() {
		String html = parser.parseToHtml("a http://example.com hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	public void testHyperlinkImpliedNegativeMatch() {
		String html = parser.parseToHtml("a http://example.com. hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a>. hyperlink</p></body>"));
	}

	public void testHyperlinkImpliedNegativeMatch2() {
		String html = parser.parseToHtml("a http://example.com) hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a>) hyperlink</p></body>"));
	}

	public void testHyperlinkWithSpaces() {
		String html = parser.parseToHtml("a [ http://example.com ] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	public void testHyperlinkWithTitleAndSpace() {
		String html = parser.parseToHtml("a [Example Two | http://example.com ] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example Two</a> hyperlink</p></body>"));
	}

	public void testNamedAnchor() {
		String html = parser.parseToHtml("a {anchor:a23423} named anchor");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <span id=\"a23423\"></span> named anchor</p></body>"));
	}

	public void testListUnordered() throws IOException {
		String html = parser.parseToHtml("* a list\n* with two lines");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ul>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ul>"));
	}

	public void testListOrdered() throws IOException {
		String html = parser.parseToHtml("# a list\n# with two lines");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ol>"));
	}

	public void testListNested() throws IOException {
		String html = parser.parseToHtml("# a list\n## nested\n## nested2\n# level1\n\npara");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol>"));
		assertTrue(html.contains("<li>a list"));
		assertTrue(html.contains("<li>nested"));
		assertTrue(html.contains("</ol>"));
	}

	public void testListMixed() throws IOException {
		// test for bug# 47
		String html = parser.parseToHtml("# first\n* second");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol><li>first</li></ol><ul><li>second</li></ul>"));
	}

	public void testListNestedMixed() throws IOException {
		String html = parser.parseToHtml("# a list\n#* nested\n#* nested2\n# level1\n\npara");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol><li>a list<ul><li>nested</li><li>nested2</li></ul></li><li>level1</li></ol>"));
	}

	public void testImage() {
		String html = parser.parseToHtml("an !image.png! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithFullUrl() {
		String html = parser.parseToHtml("an !http://www.foo.com/bin/image.png! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img border=\"0\" src=\"http://www.foo.com/bin/image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesAlignRight() {
		String html = parser.parseToHtml("an !image.png|align=right! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img align=\"right\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesAlignLeft() {
		String html = parser.parseToHtml("an !image.png|align=left! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img align=\"left\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesAlignMiddle() {
		String html = parser.parseToHtml("an !image.png|align=middle! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img align=\"middle\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesAlignCenter() {
		String html = parser.parseToHtml("an !image.png|align=center! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img align=\"center\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesAlt() {
		String html = parser.parseToHtml("an !image.png|alt= some alt text! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img alt=\"some alt text\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesBorder() {
		String html = parser.parseToHtml("an !image.png|border=5! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img border=\"5\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesWidth() {
		String html = parser.parseToHtml("an !image.png|width=5! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img width=\"5\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesHeight() {
		String html = parser.parseToHtml("an !image.png|height=5! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img height=\"5\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageWithAttributesHeightBadValue() {
		String html = parser.parseToHtml("an !image.png|height=5a! image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>an <img border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	public void testImageNegativeMatch() {
		// Issue 67: https://textile-j.dev.java.net/issues/show_bug.cgi?id=67
		String html = parser.parseToHtml("I really like ice cream! Yay!");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>I really like ice cream! Yay!</p></body>"));
	}

	public void testTable() {
		String html = parser.parseToHtml("|a|row|not header|");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><td>a</td><td>row</td><td>not header</td></tr></table></body>"));
	}

	public void testTableWithHeader() {
		String html = parser.parseToHtml("||a||header||row||\n|a|row|not header|");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><th>a</th><th>header</th><th>row</th></tr><tr><td>a</td><td>row</td><td>not header</td></tr></table></body>"));
	}

	public void testTableNestedWithHeader() {
		String html = parser.parseToHtml("a para\n||a||header||row||\n|a|row|not header|\ntail");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a para</p><table><tr><th>a</th><th>header</th><th>row</th></tr><tr><td>a</td><td>row</td><td>not header</td></tr></table><p>tail</p></body>"));
	}

	public void testTableWithLinkAndPipes() {
		// test for bug# 244240
		String html = parser.parseToHtml("| [Website|https://textile-j.dev.java.net/] |");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><td> <a href=\"https://textile-j.dev.java.net/\">Website</a> </td></tr></table></body>"));
	}

	public void testTableWithLinkAndPipes2() {
		// test for bug# 244240
		String html = parser.parseToHtml("| [Website|https://textile-j.dev.java.net/] | another cell | [Eclipse|http://www.eclipse.org] |");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><td> <a href=\"https://textile-j.dev.java.net/\">Website</a> </td><td> another cell </td><td> <a href=\"http://www.eclipse.org\">Eclipse</a> </td></tr></table></body>"));
	}

	public void testPreformattedExtended() {
		String html = parser.parseToHtml("{noformat}\na multiline\n\tpreformatted\n\nwith two paras\n{noformat}\nanother para");
		System.out.println("HTML:" + html);
		assertTrue(Pattern.compile(
				"body><pre>a multiline\\s+preformatted\\s+with two paras\\s+</pre><p>another para</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testPreformattedExtended2() {
		String html = parser.parseToHtml("{noformat}\na multiline\n\tpreformatted\n\nwith two paras{noformat}another para");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><pre>a multiline"));
		assertTrue(html.contains("</pre><p>another para</p></body>"));
		assertTrue(Pattern.compile("with two paras\\s*</pre>", Pattern.MULTILINE).matcher(html).find());
	}

	public void testBlockCode() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{code:language=Java}\n"
				+ "public class Foo {\n" + "}\n" + "{code}" + "\n" + "More text...");
		System.out.println("HTML:" + html);

		assertTrue(Pattern.compile("<p>Some text</p><pre><code>", Pattern.MULTILINE).matcher(html).find());
		assertTrue(html.contains("<code>\npublic class Foo {\n"));
		assertTrue(html.contains("</code></pre><p>More text...</p>"));
	}

	public void testBlockCode2() {
		String html = parser.parseToHtml("{code}some code{code}more text");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><pre><code>some code"));
		assertTrue(html.contains("</code></pre><p>more text</p></body>"));
	}

	public void testBlockCodeJava() {
		String html = parser.parseToHtml("{code:Java}some code{code}more text");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><pre><code class=\"java code-java\">some code"));
		assertTrue(html.contains("</code></pre><p>more text</p></body>"));
	}

	public void testNote() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{note:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{note}" + "\n"
				+ "More text...");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<p>Some text</p><div class=\"note\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{note"));
	}

	public void testNote2() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		parser.setBuilder(builder);
		parser.parse("{note}this is a note {note}\n\n* one thing\n* two things");
		String html = out.toString();
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><div class=\"note\"><p>this is a note </p></div><ul><li>one thing</li><li>two things</li></ul></body>"));
	}

	public void testNote3() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		parser.setBuilder(builder);
		parser.parse("{note}this is a note {note}*bold* text\nfoo\n\nbar");
		String html = out.toString();
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><div class=\"note\"><p>this is a note </p></div><p><strong>bold</strong> text<br/>foo</p><p>bar</p></body>"));
	}

	public void testNote4() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		parser.setBuilder(builder);
		parser.parse("abc{note}this is a note {note}*bold* text\nfoo\n\nbar");
		String html = out.toString();
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<body><p>abc</p><div class=\"note\"><p>this is a note </p></div><p><strong>bold</strong> text<br/>foo</p><p>bar</p></body>"));
	}

	public void testInfo() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{info:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{info}" + "\n"
				+ "More text...");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<p>Some text</p><div class=\"info\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{info"));
	}

	public void testWarning() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{warning:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{warning}" + "\n"
				+ "More text...");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<p>Some text</p><div class=\"warning\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{warning"));
	}

	public void testTip() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{tip:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{tip}" + "\n"
				+ "More text...");
		System.out.println("HTML:" + html);
		assertTrue(html.contains("<p>Some text</p><div class=\"tip\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{tip"));
	}

	public void testTipToDocBook() {
		StringWriter out = new StringWriter();
		parser.setBuilder(new DocBookDocumentBuilder(out));
		parser.parse("h1. a header\n" + "\n" + "Some text\n" + "{tip:title=A Title}\n" + "the body of the note\n"
				+ "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{tip}" + "\n"
				+ "More text...");
		String docbook = out.toString();
		System.out.println("DocBook: " + docbook);
		assertTrue(docbook.contains("<tip><title>A Title</title><para>the body of"));
		assertTrue(docbook.contains("paragraphs or <emphasis role=\"bold\">other</emphasis> <emphasis>textile</emphasis> <citation>markup</citation></para></tip>"));
	}

	public void testTableOfContents() throws IOException {
		String html = parser.parseToHtml("h1. Table Of Contents\n\n{toc}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		System.out.println("HTML: \n" + html);

		assertTrue(html.contains("<a href=\"#Subhead2\">"));
		assertTrue(html.contains("<h2 id=\"Subhead2\">"));
		assertTrue(html.contains("href=\"#Subhead4\""));
		assertTrue(html.contains("<h3 id=\"Subhead4\">"));
	}

	public void testTableOfContentsWithMaxLevel() throws IOException {
		String html = parser.parseToHtml("h1. Table Of Contents\n\n{toc:maxLevel=2}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		System.out.println("HTML: \n" + html);

		assertTrue(html.contains("<a href=\"#Subhead2\">"));
		assertTrue(html.contains("<h2 id=\"Subhead2\">"));
		assertFalse(html.contains("href=\"#Subhead4\""));
		assertTrue(html.contains("<h3 id=\"Subhead4\">"));
	}

	public void testBoldItalicsBold() {
		String html = parser.parseToHtml("*bold _ital ics_ bold*");
		System.out.println(html);
		assertTrue(html.contains("<strong>bold <em>ital ics</em> bold</strong>"));
	}

	public void testItalicsBold() {
		String html = parser.parseToHtml("_italics *bol d* italics_");
		System.out.println(html);
		assertTrue(html.contains("<em>italics <strong>bol d</strong> italics</em>"));
	}

	public void testBoldItalics() {
		String html = parser.parseToHtml("*_bold and italic_ not just bold*");
		System.out.println(html);
		assertTrue(html.contains("<strong><em>bold and italic</em> not just bold</strong>"));
	}

	public void testInlineQuote() {
		String html = parser.parseToHtml("a paragraph {quote}with inline{quote} quote");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a paragraph <q>with inline</q> quote</p></body>"));
	}

	public void testInlineQuoteWithBullets() {
		String html = parser.parseToHtml("* a bullet {quote}with inline{quote} quote");
		System.out.println(html);
		assertTrue(html.contains("<body><ul><li>a bullet <q>with inline</q> quote</li></ul></body>"));
	}

	public void testInlineQuoteWithBullets2() {
		String html = parser.parseToHtml("* {quote}a bullet with inline{quote} quote");
		System.out.println(html);
		assertTrue(html.contains("<body><ul><li><q>a bullet with inline</q> quote</li></ul></body>"));
	}

	public void testInlineQuoteNegativeMatch() {
		String html = parser.parseToHtml("a paragraph {quote}with inline quote");
		System.out.println(html);
		assertTrue(html.contains("<body><p>a paragraph {quote}with inline quote</p></body>"));
	}

	public void testInlineQuoteNegativeMatch2() {
		String html = parser.parseToHtml("{quote}a paragraph with {quote}inline quote{quote}");
		System.out.println(html);
		assertTrue(html.contains("<body><blockquote><p>a paragraph with </p></blockquote><p>inline quote{quote}</p></body>"));
	}

	public void testColor() {
		String html = parser.parseToHtml("{color:red}\na paragraph\n\nanother paragraph\n{color}\ntext");
		System.out.println(html);
		assertTrue(html.contains("<body><div style=\"color: red;\"><p>a paragraph</p><p>another paragraph</p></div><p>text</p></body>"));
	}

	public void testColor2() {
		String html = parser.parseToHtml("{color:red}a paragraph\n\nanother paragraph{color}text");
		System.out.println(html);
		assertTrue(html.contains("<body><div style=\"color: red;\"><p>a paragraph</p><p>another paragraph</p></div><p>text</p></body>"));
	}

	public void testColor3() {
		String html = parser.parseToHtml("text {color:red}more text{color} text");
		System.out.println(html);
		assertTrue(html.contains("<body><p>text </p><div style=\"color: red;\"><p>more text</p></div><p> text</p></body>"));
	}
}
