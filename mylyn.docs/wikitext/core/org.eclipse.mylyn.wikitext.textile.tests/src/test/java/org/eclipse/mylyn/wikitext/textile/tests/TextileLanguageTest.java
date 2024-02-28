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
package org.eclipse.mylyn.wikitext.textile.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Test;

/**
 * NOTE: most textile test cases can be found in {@link MarkupParserTest}
 *
 * @author David Green
 * @see TextileLanguageTasksTest
 */
@SuppressWarnings("nls")
public class TextileLanguageTest extends AbstractMarkupGenerationTest<TextileLanguage> {

	private static final String REGEX_NEWLINE = "(?:\\s*?^)";

	@Override
	protected TextileLanguage createMarkupLanguage() {
		return new TextileLanguage();
	}

	@Test
	public void testDiscoverable() {
		MarkupLanguage language = ServiceLocator.getInstance(TextileLanguageTest.class.getClassLoader())
				.getMarkupLanguage("Textile");
		assertNotNull(language);
		assertTrue(language instanceof TextileLanguage);
	}

	@Test
	public void testIsDetectingRawHyperlinks() {
		assertFalse(markupLanguage.isDetectingRawHyperlinks());
	}

	@Test
	public void testSimpleHeaders() {
		String html = parser.parseToHtml("h1. a header\n\nh2. another header");

		assertTrue(html.contains("<h1 id=\"aheader\">a header</h1>"));
		assertTrue(html.contains("<h2 id=\"anotherheader\">another header</h2>"));
	}

	@Test
	public void testMultilineBlockCode() {
		String html = parser.parseToHtml("bc. one\ntwo\n\nthree");

		assertTrue(Pattern.compile("<pre><code>one\\s+^two\\s+^</code>\\s*</pre>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	@Test
	public void testPhraseModifierWorksAtStartOfLine() {
		String html = parser.parseToHtml("-a phrase modifier- at the start of a line");

		assertTrue(html.contains("<del>a phrase modifier</del> at"));
	}

	@Test
	public void testPhraseModifierWorksAtEndOfLine() {
		String html = parser.parseToHtml("at the start of a line: -a phrase modifier-");

		assertTrue(html.contains("line: <del>a phrase modifier</del>"));
	}

	@Test
	public void testPhraseModifierSingleChar() {
		String html = parser.parseToHtml("a single character phrase modifier -b- is there");

		assertTrue(html.contains("modifier <del>b</del>"));
	}

	@Test
	public void testPhraseModifierFalsePositives() {
		String html = parser.parseToHtml("this is - not a phrase modifier- and -neither is this - so there");

		assertTrue(!html.contains("<del>"));
	}

	@Test
	public void testPhraseModifierFalsePositives2() {
		String html = parser.parseToHtml("this is - not a phrase modifier - ");

		assertTrue(!html.contains("<del>"));
	}

	@Test
	public void testBlockCodeAtEndOfDocument() {
		String html = parser.parseToHtml("bc. one\ntwo\n");

		assertTrue(Pattern.compile("<pre><code>one\\s*^two\\s*^</code></pre>", Pattern.MULTILINE | Pattern.UNIX_LINES)
				.matcher(html)
				.find());
	}

	@Test
	public void testBlockCodeAtEndOfDocument2() {
		String html = parser.parseToHtml("bc. one\ntwo");

		assertTrue(Pattern.compile("<pre><code>one\\s*^two\\s*^</code></pre>", Pattern.MULTILINE | Pattern.UNIX_LINES)
				.matcher(html)
				.find());
	}

	@Test
	public void testPhraseModifierStrong() {
		String html = parser.parseToHtml("*strong text*");

		assertTrue(html.contains("<strong>strong text</strong>"));
	}

	@Test
	public void testPhraseModifiers() {
		String html = parser.parseToHtml(
				"_emphasis_ *strong text* __italic__ **bold** ??citation?? -deleted text- +inserted text+ ^superscript^ ~subscript~ %spanned text% @code text@");

		assertTrue(html.contains("<em>emphasis</em>"));
		assertTrue(html.contains("<strong>strong text</strong>"));
		assertTrue(html.contains("<i>italic</i>"));
		assertTrue(html.contains("<b>bold</b>"));
		assertTrue(html.contains("<cite>citation</cite>"));
		assertTrue(html.contains("<del>deleted text</del>"));
		assertTrue(html.contains("<ins>inserted text</ins>"));
		assertTrue(html.contains("<sup>superscript</sup>"));
		assertTrue(html.contains("<sub>subscript</sub>"));
		assertTrue(html.contains("<span>spanned text</span>"));
		assertTrue(html.contains("<code>code text</code>"));
	}

	@Test
	public void testPhraseModifiersWithNonWordCharacters() {
		String html = parser.parseToHtml(
				"_emphasis:_ *strong text:* __italic:__ **bold:** ??citation:?? -deleted text:- +inserted text:+ ^superscript:^ ~subscript:~ %spanned text:% @code text:@");

		assertTrue(html.contains("<em>emphasis:</em>"));
		assertTrue(html.contains("<strong>strong text:</strong>"));
		assertTrue(html.contains("<i>italic:</i>"));
		assertTrue(html.contains("<b>bold:</b>"));
		assertTrue(html.contains("<cite>citation:</cite>"));
		assertTrue(html.contains("<del>deleted text:</del>"));
		assertTrue(html.contains("<ins>inserted text:</ins>"));
		assertTrue(html.contains("<sup>superscript:</sup>"));
		assertTrue(html.contains("<sub>subscript:</sub>"));
		assertTrue(html.contains("<span>spanned text:</span>"));
		assertTrue(html.contains("<code>code text:</code>"));
	}

	@Test
	public void testDeleted() {
		String html = parser.parseToHtml("one -two three-four five- six");

		assertTrue(html.contains("<del>two three-four five</del>"));
	}

	@Test
	public void testAdjacentPhraseModifiers() {
		String html = parser.parseToHtml("_emphasis_ *strong text*");

		assertTrue(html.contains("<em>emphasis</em>"));
		assertTrue(html.contains("<strong>strong text</strong>"));
	}

	@Test
	public void testPhraseModifiersEnclosingText() {
		String html = parser.parseToHtml("_emphasis_ some text *strong text*");

		assertTrue(Pattern.compile(".*?<em>emphasis</em>\\s*some text\\s*<strong>strong text</strong>.*",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).matches());
	}

	@Test
	public void testPhraseModifierCode() {
		String html = parser.parseToHtml("@code1@:");

		assertTrue(Pattern.compile(".*?<code>code1</code>:.*", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.matches());
	}

	@Test
	public void testCodeWithCurlyBrace() {
		String html = parser.parseToHtml("for example: @{{bug|244618}}@");

		assertTrue(html.contains("<p>for example: <code>{{bug|244618}}</code></p>"));
	}

	/**
	 * bug 276395 Incorrect quotation characters inside code
	 */
	@Test
	public void testPhraseModifierCodeWithNestedMarkup() {
		String html = parser.parseToHtml("a @code 'test' or \"test\" or *b* or <b>bo</b> sample@ more");

		assertTrue(html.contains(
				"<body><p>a <code>code 'test' or \"test\" or *b* or &lt;b&gt;bo&lt;/b&gt; sample</code> more</p></body>"));
	}

	@Test
	public void testRelativeUrlNoBase() {
		String html = parser.parseToHtml("\"An URL\":foo/bar.html");

		assertTrue(html.contains("<a href=\"foo/bar.html\">An URL</a>"));
	}

	@Test
	public void testGlossaryValidHtml() {
		String html = parser.parseToHtml(
				"h1. Foo\n\none TWO(Two Ways Out) and MDD(Model-Driven Development)\n\nh1. Glossary\n\n{glossary}");

		assertFalse(html.contains("<p><dl"));
		assertTrue(html.contains("</h1><dl"));
	}

	@Test
	public void testLineStartingWithDeletedPhraseModifier() {
		String html = parser.parseToHtml("-this is deleted text-");

		assertTrue(html.contains("<p><del>this is deleted text</del></p>"));
	}

	@Test
	public void testListItemWithDeletedText() {
		String html = parser.parseToHtml("- this is a list item with -deleted text-");

		assertTrue(html.contains("<p>- this is a list item with <del>deleted text</del></p>"));
	}

	@Test
	public void testListItemWithDeletedText2() {
		String html = parser.parseToHtml("* this is a list item with -deleted text-");

		assertTrue(html.contains("<ul><li>this is a list item with <del>deleted text</del></li></ul>"));
	}

	@Test
	public void testHtmlEntityEncoding() {
		String html = parser.parseToHtml("Some A&BC Thing");

		assertTrue(html.contains("A&amp;BC"));
	}

	@Test
	public void testHtmlEntityEncoding2() {
		String html = parser.parseToHtml("Some A&BC Thing; two");

		assertTrue(html.contains("<p>Some A&amp;BC Thing; two</p>"));
	}

	@Test
	public void testHtmlEntityEncoding3() {
		String html = parser.parseToHtml("Some A&BCThing; two");

		assertTrue(html.contains("<p>Some A&BCThing; two</p>"));
	}

	@Test
	public void testHtmlEntityEncoding4() {
		String html = parser.parseToHtml("Some A&#60; two");

		assertTrue(html.contains("<p>Some A&#60; two</p>"));
	}

	@Test
	public void testHtmlEntityEncoding5() {
		String html = parser.parseToHtml("Some A&#x27; two");

		assertTrue(html.contains("<p>Some A&#x27; two</p>"));
	}

	@Test
	public void testParagraphs() throws IOException {
		String html = parser.parseToHtml("first para\nnew line\n\nsecond para\n\n\n\n");

		assertTrue(
				Pattern.compile("<body><p>first para<br/>\\s*new line</p><p>second para</p></body>", Pattern.MULTILINE)
				.matcher(html.toString())
				.find());
	}

	@Test
	public void testParagraphWithId() throws IOException {
		String html = parser.parseToHtml("p(#ab). first para");

		assertTrue(html.contains("<p id=\"ab\">first para</p>"));
	}

	@Test
	public void testParagraphWithClass() throws IOException {
		String html = parser.parseToHtml("p(foo). first para");

		assertTrue(html.contains("<p class=\"foo\">first para</p>"));
	}

	@Test
	public void testParagraphWithClassAndId() throws IOException {
		String html = parser.parseToHtml("p(foo#ab). first para");

		assertTrue(html.contains("<p id=\"ab\" class=\"foo\">first para</p>"));
	}

	@Test
	public void testParagraphWithClassAndIdAndStyle() throws IOException {
		String html = parser.parseToHtml("p(foo#ab){color:black;}. first para");

		assertTrue(html.contains("<p id=\"ab\" class=\"foo\" style=\"color:black;\">first para</p>"));
	}

	@Test
	public void testParagraphLeftAligned() throws IOException {
		String html = parser.parseToHtml("p<. first para");

		assertTrue(html.contains("<p style=\"text-align: left;\">first para</p>"));
	}

	@Test
	public void testParagraphWithNestedList() throws IOException {
		String html = parser.parseToHtml("first para\n# numeric list\nfirst para second line");

		// NOTE: textile dialect doesn't nest lists because that is invalid XHTML.  Instead
		// the paragraph is terminated for the list.
		assertTrue(html
				.contains("<body><p>first para</p><ol><li>numeric list</li></ol><p>first para second line</p></body>"));
	}

	@Test
	public void testPreformattedDoesntMarkupContent() throws IOException {
		String html = parser.parseToHtml("pre. \n|_. a|_. table|_. header|\n|a|table|row|\n|a|table|row|\n");

		assertTrue(Pattern.compile(
				"<body><pre>\\s*\\|_. a\\|_. table\\|_. header\\|\\s+\\|a\\|table\\|row\\|\\s+\\|a\\|table\\|row\\|\\s+</pre></body>",
				Pattern.MULTILINE).matcher(html.toString()).find());
	}

	@Test
	public void testHeading1() throws IOException {
		String html = parser.parseToHtml("h1(#ab). heading1\n\nnew para\n\na para");

		assertTrue(Pattern
				.compile("<body><h1 id=\"ab\">heading1</h1><p>new para</p><p>a para</p></body>", Pattern.MULTILINE)
				.matcher(html.toString())
				.find());
	}

	@Test
	public void testHeadingMultiline() throws IOException {
		String html = parser.parseToHtml("h1. heading1\nsecondline\n\na para");

		assertTrue(Pattern
				.compile("<body><h1 id=\"heading1\">heading1\\s+secondline</h1><p>a para</p></body>", Pattern.MULTILINE)
				.matcher(html.toString())
				.find());
	}

	@Test
	public void testHeading0NoHeading() throws IOException {
		String html = parser.parseToHtml("h0. heading0\n\nnew para\n\na para");

		assertTrue(Pattern.compile("<body><p>h0. heading0</p><p>new para</p><p>a para</p></body>", Pattern.MULTILINE)
				.matcher(html.toString())
				.find());
	}

	@Test
	public void testMultilinePreformatted() throws IOException {
		String html = parser.parseToHtml("pre. one\ntwo\n\nthree");

		assertTrue(Pattern.compile("<pre>one\\s+^two\\s+^</pre><p>three</p>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html.toString())
				.find());
	}

	@Test
	public void testBlockQuote() throws IOException {
		String html = parser.parseToHtml("bq. one\ntwo\n\nthree");

		assertTrue(Pattern
				.compile("<blockquote>\\s*<p>one<br/>\\s*two</p>\\s*</blockquote>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	@Test
	public void testBlockQuoteWithCitation() throws IOException {
		String html = parser.parseToHtml("bq.:http://www.example.com some text");

		assertTrue(Pattern.compile("<blockquote cite=\"http://www.example.com\">\\s*<p>some text</p>\\s*</blockquote>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	/**
	 * bug 304765
	 */
	@Test
	public void testBlockQuote_bug304765() {
		assertMarkup(
				"<blockquote><p>src/<br/>  main/<br/>    java/  (Java src folder)<br/>      META-INF<br/>     file.txt</p><p>a</p></blockquote>",
				"""
				bq.. src/
				  main/
				    java/  (Java src folder)
				      META-INF
				     file.txt

				 \s
				a""");

	}

	@Test
	public void testBlockCode() throws IOException {
		String html = parser.parseToHtml("bc. one\ntwo\n\nthree");

		assertTrue(Pattern.compile("<pre><code>one\\s+two\\s+</code></pre>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	/**
	 * bug 320007
	 */
	@Test
	public void testBlockCodeWithTabs() throws IOException {
		String html = parser.parseToHtml("bc. one\n\ttwo\n\nthree");

		assertTrue(html.contains("one\n\ttwo"));
	}

	/**
	 * bug 320007
	 */
	@Test
	public void testBlockCodeWithTabsFormatted() throws IOException {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder documentBuilder = new HtmlDocumentBuilder(out, true);
		parser.setBuilder(documentBuilder);
		parser.parse("bc. one\n\ttwo\n\nthree");
		String html = out.toString();

		assertTrue(html.contains("one\n\ttwo"));
	}

	@Test
	public void testBlockCodeWithEmbeddedHtmlTags() throws IOException {
		String html = parser.parseToHtml("bc. \nhere is <a href=\"#\">a working example</a>\n\n");

		assertTrue(Pattern.compile(
				"<body><pre><code>\\s*here is &lt;a href=\"#\"&gt;a working example&lt;/a&gt;\\s+</code></pre></body>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	@Test
	public void testBlockCodeWithLeadingNewline() throws IOException {
		String html = parser.parseToHtml("bc. \none\ntwo\n\nthree");

		assertTrue(Pattern.compile("<pre><code>one\\s+two\\s+</code></pre>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	@Test
	public void testBlockCodeWithLeadingNewlines() throws IOException {
		String html = parser.parseToHtml("bc.. \n\none\ntwo\np. three");

		assertTrue(
				Pattern.compile("<pre><code>(\\r|\\n)+one\\s+two\\s+</code></pre>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	@Test
	public void testFootnote() throws IOException {
		String html = parser.parseToHtml("See foo[1].\n\nfn1. Foo.");

		// assert on footnote target
		assertTrue(
				Pattern.compile("<p id=\"___fn[^\"]+\" class=\"footnote\"><sup>1</sup> Foo.</p>").matcher(html).find());
		// assert on footnote reference
		assertTrue(
				Pattern.compile("<sup class=\"footnote\"><a href=\"#___fn[^\"]+\">1</a></sup>").matcher(html).find());
	}

	@Test
	public void testFootnoteRefNoFootnote() throws IOException {
		markupLanguage.setPreprocessFootnotes(true);
		String html = parser.parseToHtml("See foo[1].\n\nNo such footnote!");

		assertTrue(html.contains("<body><p>See foo[1].</p><p>No such footnote!</p></body>"));
	}

	@Test
	public void testListUnordered() throws IOException {
		String html = parser.parseToHtml("* a list\n* with two lines");

		assertTrue(html.contains("<ul>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ul>"));
	}

	@Test
	public void testListUnordered2() throws IOException {
		assertMarkup(
				"<ul><li>a list<ul><li>with several lines<ul><li>foo</li></ul></li><li> <cite>foo</cite> intentional two spaces leading content</li></ul></li></ul>",
				"* a list\n** with several lines\n*** foo\n**  ??foo?? intentional two spaces leading content");
	}

	@Test
	public void testListOrdered() throws IOException {
		String html = parser.parseToHtml("# a list\n# with two lines");

		assertTrue(html.contains("<ol>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ol>"));
	}

	@Test
	public void testListNested() throws IOException {
		String html = parser.parseToHtml("# a list\n## nested\n## nested2\n# level1\n\npara");

		assertTrue(html.contains("<ol>"));
		assertTrue(html.contains("<li>a list"));
		assertTrue(html.contains("<li>nested"));
		assertTrue(html.contains("</ol>"));
	}

	@Test
	public void testListMixed() throws IOException {
		// test for bug# 47
		String html = parser.parseToHtml("# first\n* second");

		assertTrue(html.contains("<ol><li>first</li></ol><ul><li>second</li></ul>"));
	}

	@Test
	public void testListNestedMixed() throws IOException {
		String html = parser.parseToHtml("# a list\n#* nested\n#* nested2\n# level1\n\npara");

		assertTrue(html.contains("<ol><li>a list<ul><li>nested</li><li>nested2</li></ul></li><li>level1</li></ol>"));
	}

	@Test
	public void testListWithStyle() throws IOException {
		String html = parser.parseToHtml("#{color: blue} a list with style");

		assertTrue(html.contains("<ol style=\"color: blue\">"));
		assertTrue(html.contains("<li>a list with style</li>"));
		assertTrue(html.contains("</ol>"));
	}

	@Test
	public void testListNotAList() throws IOException {
		// test for bug 263074
		String html = parser.parseToHtml("- first\n- second");

		assertTrue(html.contains("<body><p>- first<br/>- second</p></body>"));
	}

	@Test
	public void testTable() throws IOException {
		String html = parser.parseToHtml("table. \n|a|row with|three columns|");

		assertTrue(html.contains("<table><tr><td>a</td><td>row with</td><td>three columns</td></tr></table>"));
	}

	@Test
	public void testTable2() throws IOException {
		String html = parser
				.parseToHtml("foo bar\n|a|row with|three columns|\n|another|row|with three columns|\n\na para");

		assertTrue(html.contains(
				"<table><tr><td>a</td><td>row with</td><td>three columns</td></tr><tr><td>another</td><td>row</td><td>with three columns</td></tr></table>"));
	}

	@Test
	public void testTableHeader() throws IOException {
		String html = parser.parseToHtml("table.\n|_. a|row with|three columns|");

		assertTrue(html.contains("<table><tr><th>a</th><td>row with</td><td>three columns</td></tr></table>"));
	}

	@Test
	public void testTableCellAlignment() throws IOException {
		String html = parser.parseToHtml("table.\n|^a|<row with|>four|<>columns|");

		assertTrue(html.contains(
				"<table><tr><td style=\"vertical-align: top;\">a</td><td style=\"text-align: left;\">row with</td><td style=\"text-align: right;\">four</td><td style=\"text-align: center;\">columns</td></tr></table>"));
	}

	@Test
	public void testTableCellColspan() throws IOException {
		String html = parser.parseToHtml("table.\n|\\2a|\\3b|");

		assertTrue(html.contains("<table><tr><td colspan=\"2\">a</td><td colspan=\"3\">b</td></tr></table>"));
	}

	@Test
	public void testTableCellRowspan() throws IOException {
		String html = parser.parseToHtml("table.\n|/2a|/3b|");

		assertTrue(html.contains("<table><tr><td rowspan=\"2\">a</td><td rowspan=\"3\">b</td></tr></table>"));
	}

	@Test
	public void testTableCellColspanRowspan() throws IOException {
		String html = parser.parseToHtml("table.\n|\\4/2a|\\5/3b|");

		assertTrue(html.contains(
				"<table><tr><td rowspan=\"2\" colspan=\"4\">a</td><td rowspan=\"3\" colspan=\"5\">b</td></tr></table>"));
	}

	@Test
	public void testTableWithEmbeddedTextile() throws IOException {
		String html = parser.parseToHtml("table.\n|*a*|row _with_|stuff|");

		assertTrue(html.contains(
				"<table><tr><td><strong>a</strong></td><td>row <em>with</em></td><td>stuff</td></tr></table>"));
	}

	@Test
	public void testTableWithAttributes() throws IOException {
		String html = parser.parseToHtml("table.\n|{color: red;}a|(foo)row with|(#bar)three columns|");

		assertTrue(html.contains(
				"<table><tr><td style=\"color: red;\">a</td><td class=\"foo\">row with</td><td id=\"bar\">three columns</td></tr></table>"));
	}

	@Test
	public void testTableWithAttributes2() throws IOException {
		String html = parser.parseToHtml("""
				table{border:1px solid black;}.
				|This|is|a|row|
				|This|is|a|row|""");

		assertTrue(html.contains(
				"<table style=\"border:1px solid black;\"><tr><td>This</td><td>is</td><td>a</td><td>row</td></tr><tr><td>This</td><td>is</td><td>a</td><td>row</td></tr></table>"));
	}

	@Test
	public void testTableWithAttributes3() throws IOException {
		String html = parser.parseToHtml("|This|is|a|row|\n" + "{background:#ddd}. |This|is|grey|row|");

		assertTrue(html.contains(
				"<table><tr><td>This</td><td>is</td><td>a</td><td>row</td></tr><tr style=\"background:#ddd\"><td>This</td><td>is</td><td>grey</td><td>row</td></tr></table>"));
	}

	@Test
	public void testTableWithStyles() {
		String html = parser.parseToHtml("table{border: 1px solid black}.\n|a|table|row|");

		assertTrue(html.contains(
				"<body><table style=\"border: 1px solid black\"><tr><td>a</td><td>table</td><td>row</td></tr></table></body>"));
	}

	@Test
	public void testTableWithStylesAndTrailingWhitespace() {
		String html = parser.parseToHtml("table{border: 1px solid black}. \n|a|table|row|");

		assertTrue(html.contains(
				"<body><table style=\"border: 1px solid black\"><tr><td>a</td><td>table</td><td>row</td></tr></table></body>"));
	}

	@Test
	public void testPhraseModifierBold() throws IOException {
		String html = parser.parseToHtml("a paragraph with **bold content**");

		assertTrue(html.contains("<p>a paragraph with <b>bold content</b></p>"));
	}

	@Test
	public void testPhraseModifierBoldWithId() throws IOException {
		String html = parser.parseToHtml("a paragraph with **(#1)bold content**");

		assertTrue(html.contains("<p>a paragraph with <b id=\"1\">bold content</b></p>"));
	}

	@Test
	public void testSimplePhraseModifiers() throws IOException {
		String[][] pairs = { { "**", "b" }, { "??", "cite" }, { "__", "i" }, { "_", "em" }, { "*", "strong" },
				{ "-", "del" }, { "+", "ins" }, { "~", "sub" }, { "^", "sup" }, { "%", "span" }, { "@", "code" }, };
		for (String[] pair : pairs) {
			initParser();
			String html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0]);

			assertTrue(html.contains("<p>a paragraph with <" + pair[1] + ">content foo bar baz</" + pair[1] + "></p>"));

			html = parser.parseToHtml(
					String.format("a %s2%s + b%s2%s = c%s2%s", pair[0], pair[0], pair[0], pair[0], pair[0], pair[0]));

			assertTrue(html.contains(String.format("a <%s>2</%s> + b%s2%s = c%s2%s", pair[1], pair[1], pair[0], pair[0],
					pair[0], pair[0])));

			html = parser
					.parseToHtml("a paragraph with (" + pair[0] + "content foo bar baz" + pair[0] + ") punctuation");

			assertTrue(html.contains(
					"<p>a paragraph with (<" + pair[1] + ">content foo bar baz</" + pair[1] + ">) punctuation</p>"));

			html = parser
					.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0] + ". punctuation");

			assertTrue(html.contains(
					"<p>a paragraph with <" + pair[1] + ">content foo bar baz</" + pair[1] + ">. punctuation</p>"));

			initParser();
			html = parser.parseToHtml("a paragraph with " + pair[0] + "(#abc)content foo bar baz" + pair[0]);

			assertTrue(html.contains(
					"<p>a paragraph with <" + pair[1] + " id=\"abc\">content foo bar baz</" + pair[1] + "></p>"));

			// test for false-positive
			html = parser.parseToHtml("a paragraph with" + pair[0] + "content foo bar baz" + pair[0]);

			assertFalse(pair[1], html.contains("<" + pair[1] + ">"));
			assertFalse(pair[1], html.contains("</" + pair[1] + ">"));
			html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0] + "baz.");

			assertFalse(pair[1], html.contains("<" + pair[1] + ">"));
			assertFalse(pair[1], html.contains("</" + pair[1] + ">"));
		}
	}

	@Test
	public void testDeletedIssue22() {
		// test for a false-positive
		String html = parser.parseToHtml("Foo bar-baz one two three four-five.");

		assertTrue(html.contains("<p>Foo bar-baz one two three four-five.</p>"));
	}

	@Test
	public void testDeletedBug338284() {
		Map<String, String> markupAndExpected = new LinkedHashMap<>();
		markupAndExpected.put("Foo -one two-three four-", "<p>Foo <del>one two-three four</del></p>");
		markupAndExpected.put("Foo -one two- three four-", "<p>Foo <del>one two</del> three four-</p>");
		markupAndExpected.put("-one two-", "<p><del>one two</del></p>");
		markupAndExpected.put("-one-two-", "<p><del>one-two</del></p>");
		markupAndExpected.put("-one- two-", "<p><del>one</del> two-</p>");
		markupAndExpected.put("text -one two- and -three four-",
				"<p>text <del>one two</del> and <del>three four</del></p>");
		markupAndExpected.put("-a-", "<p><del>a</del></p>");
		markupAndExpected.put("--", "<p>--</p>");
		for (java.util.Map.Entry<String, String> entry : markupAndExpected.entrySet()) {
			String markup = entry.getKey();
			String expectedHtml = entry.getValue();

			String html = parser.parseToHtml(markup);

			assertTrue("Expecting " + expectedHtml + " in HTML: " + html, html.contains(expectedHtml));
		}
	}

	@Test
	public void testPhraseModifierDeletedWithHyphens_bug321538() {
		String html = parser.parseToHtml(
				"as I said, -hello oh-so-cruel world- again.\n\nThis works: -hell world-\n\nThis doesn't: -hello oh-so-cruel world-");

		assertTrue(html.contains("<p>as I said, <del>hello oh-so-cruel world</del> again.</p>"));
		assertTrue(html.contains("<p>This works: <del>hell world</del></p>"));
		assertTrue(html.contains("<p>This doesn&#8217;t: <del>hello oh-so-cruel world</del></p>"));
	}

	@Test
	public void testImage() throws IOException {
		String html = parser.parseToHtml("Here comes an !imageUrl! with more text");

		assertTrue(html.contains("<img border=\"0\" src=\"imageUrl\"/>"));
	}

	@Test
	public void testImageWithAltAndTitle() throws IOException {
		String html = parser.parseToHtml("Here comes an !imageUrl(alt text)! with more text");

		assertTrue(html.contains("<img alt=\"alt text\" title=\"alt text\" border=\"0\" src=\"imageUrl\"/>"));
	}

	@Test
	public void testImageAlignLeft() throws IOException {
		String html = parser.parseToHtml("Here comes an !<imageUrl! with more text");

		assertTrue(html.contains("<img align=\"left\" border=\"0\" src=\"imageUrl\"/>"));
	}

	@Test
	public void testImageAlignRight() throws IOException {
		String html = parser.parseToHtml("Here comes an !>imageUrl! with more text");

		assertTrue(html.contains("<img align=\"right\" border=\"0\" src=\"imageUrl\"/>"));
	}

	@Test
	public void testImageAlignCenter() throws IOException {
		String html = parser.parseToHtml("Here comes an !=imageUrl! with more text");

		assertTrue(html.contains("<img align=\"center\" border=\"0\" src=\"imageUrl\"/>"));
	}

	@Test
	public void testImageRelative() throws IOException {
		String html = parser.parseToHtml("Here comes an !foo/bar/baz.jpg! with more text");

		assertTrue(html.contains("<img border=\"0\" src=\"foo/bar/baz.jpg\"/>"));
	}

	@Test
	public void testImageHyperlink() throws IOException {
		String html = parser.parseToHtml("Here comes a !hyperlink!:http://www.google.com to something");

		assertTrue(html.contains("<a href=\"http://www.google.com\"><img border=\"0\" src=\"hyperlink\"/></a>"));
		assertFalse(html.contains("</a>:http://www.google.com"));
		assertTrue(html.contains("</a> to something"));
	}

	@Test
	public void testImageHyperlinkWithAttributes() throws IOException {
		String html = parser.parseToHtml("Here comes a !(foo-bar)hyperlink!:http://www.google.com to something");

		assertTrue(html.contains(
				"<a href=\"http://www.google.com\"><img class=\"foo-bar\" border=\"0\" src=\"hyperlink\"/></a>"));
		assertFalse(html.contains("</a>:http://www.google.com"));
		assertTrue(html.contains("</a> to something"));
	}

	@Test
	public void testImageFalsePositiveOnMultipleExclamationMarks() throws IOException {
		String html = parser.parseToHtml("Here comes a non-image!!! more text !!! and more");

		assertTrue(html.contains("<body><p>Here comes a non-image!!! more text !!! and more</p></body>"));
	}

	@Test
	public void testHtmlLiteral() throws IOException {
		String htmlFragment = "<a href=\"foo-bar\"><img src=\"some-image.jpg\"/></a>";
		String html = parser.parseToHtml("a paragraph " + htmlFragment + " with HTML literal");

		assertTrue(html.contains("<p>a paragraph " + htmlFragment + " with HTML literal</p>"));

	}

	@Test
	public void testHtmlLiteralSelfClosingTag() throws IOException {
		String html = parser.parseToHtml("a <br/> br tag");

		assertTrue(html.contains("a <br/> br tag"));
	}

	@Test
	public void testHtmlLiteralTwoLinesWithAnchors() throws IOException {
		String html = parser.parseToHtml("Link 1 <a href=\"x\">x</a>\nand line 2 <a href=\"y\">y</a>");

		assertTrue(html.contains("<p>Link 1 <a href=\"x\">x</a><br/>"));
		assertTrue(html.contains("and line 2 <a href=\"y\">y</a></p></body>"));
	}

	@Test
	public void testHtmlLiteralUnclosedTag() throws IOException {
		String html = parser.parseToHtml("<b>bold text with no terminating tag");

		assertTrue(html.contains("<b>bold text"));
	}

	@Test
	public void testHtmlLiteralAdjacentTags() throws IOException {
		String html = parser.parseToHtml("<span><a>some text</a></span>");

		assertTrue(html.contains("<span><a>some text</a></span>"));
	}

	@Test
	public void testHtmlLiteralAdjacentTags2() throws IOException {
		String html = parser.parseToHtml("<span>abc</span><a>some text</a>");

		assertTrue(html.contains("<span>abc</span><a>some text</a>"));
	}

	@Test
	public void testHtmlLiteralWithEmbeddedPhraseModifiers() throws IOException {
		Pattern pattern = Pattern
				.compile("(<[a-zA-Z][a-zA-Z0-9_-]*(?:\\s*[a-zA-Z][a-zA-Z0-9_:-]*=\"[^\"]*\")*\\s*/?>)");
		Matcher matcher = pattern.matcher(
				"This document was authored using Textile markup: <a href=\"https://textile-j.dev.java.net/source/browse/*checkout*/textile-j/trunk/java/org.eclipse.mylyn.wikitext.ui.doc/help/Textile-J%20User%20Guide.textile\">original Textile markup for this document</a> ");
		while (matcher.find()) {

		}
		String html = parser.parseToHtml(
				"This document was authored using Textile markup: <a href=\"https://textile-j.dev.java.net/source/browse/*checkout*/textile-j/trunk/java/org.eclipse.mylyn.wikitext.ui.doc/help/Textile-J%20User%20Guide.textile\">original Textile markup for this document</a>");

		assertTrue(html.contains(
				"markup: <a href=\"https://textile-j.dev.java.net/source/browse/*checkout*/textile-j/trunk/java/org.eclipse.mylyn.wikitext.ui.doc/help/Textile-J%20User%20Guide.textile\">original Textile markup for this document</a>"));
	}

	@Test
	public void testHtmlLiteralLoneCloseTag() throws IOException {
		String html = parser.parseToHtml("bold text with only a terminating</b> tag");

		assertTrue(html.contains("terminating</b> tag"));
	}

	@Test
	public void testHtmlLiteralTerminatingTagWithLegalWhitespace() throws IOException {
		String html = parser.parseToHtml("<b>bold text</b  >");

		assertTrue(html.contains("<b>bold text</b  >"));
	}

	@Test
	public void testHtmlLiteralFalsePositive() throws IOException {
		String html = parser.parseToHtml("a <br br tag that is not real");

		assertTrue(html.contains("a &lt;br br tag"));
	}

	@Test
	public void testHtmlLiteralFalsePositive2() throws IOException {
		String html = parser.parseToHtml("some no tag <!-- nt");

		assertTrue(html.contains("&lt;!&#8212;"));
	}

	@Test
	public void testHtmlLiteralFalsePositive3() throws IOException {
		String html = parser.parseToHtml("some no tag <0nt");

		assertTrue(html.contains("&lt;0nt"));
	}

	@Test
	public void testHtmlLiteralFalsePositive4() throws IOException {
		String html = parser.parseToHtml("some no tag <_nt");

		assertTrue(html.contains("&lt;_nt"));
	}

	@Test
	public void testEscaping() throws IOException {
		String html = parser.parseToHtml("==no <b>textile</b> *none* _at_ all==");

		assertTrue(html.contains("no <b>textile</b> *none* _at_ all"));
	}

	@Test
	public void testEscaping2() throws IOException {
		String html = parser.parseToHtml("==*none*==");

		assertTrue(html.contains("<p>*none*</p>"));
	}

	@Test
	public void testEscaping3() throws IOException {
		String html = parser.parseToHtml("Link 1 ==<a href=\"x\">x</a>==\nand line 2 ==<a href=\"y\">y</a>==");

		assertTrue(html.contains("<p>Link 1 <a href=\"x\">x</a><br/>"));
		assertTrue(html.contains("and line 2 <a href=\"y\">y</a></p>"));
	}

	@Test
	public void testEscaping4() throws IOException {
		String html = parser.parseToHtml(
				"=={toc}== Generates a table of contents.  Eg: =={toc}== or =={toc:style=disc|maxLevel=3}==");

		assertTrue(html.contains(
				"<body><p>{toc} Generates a table of contents.  Eg: {toc} or {toc:style=disc|maxLevel=3}</p></body>"));
	}

	@Test
	public void testEscaping_NoTextile() throws IOException {
		String html = parser.parseToHtml("notextile. foo <b>bar</b>\n<i>baz</i>\n\ntextile *here*");

		assertTrue(Pattern.compile("<body>foo <b>bar</b>\\s+<i>baz</i>\\s+<p>textile <strong>here</strong></p></body>")
				.matcher(html)
				.find());
	}

	@Test
	public void testEscaping_NoTextile_Extended() throws IOException {
		String html = parser
				.parseToHtml("notextile.. foo <b>bar</b>\n<i>baz</i>\n\nnotextile *here*\n\np. textile *here*");

		assertTrue(Pattern.compile("""
				<body>foo <b>bar</b>\\s+<i>baz</i>\
				\\s*notextile \\*here\\*\
				\\s+<p>textile <strong>here</strong></p></body>""").matcher(html).find());
	}

	@Test
	public void testReplacements() throws IOException {
		String html = parser.parseToHtml("some text with copyright(c), trademark(tm) and registered(r)");

		assertTrue(Pattern.compile(".*?<p>some text with copyright&#169;, trademark&#8482; and registered&#174;</p>.*",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).matches());
	}

	@Test
	public void testApostrophe() throws IOException {
		String html = parser.parseToHtml("it's");

		assertTrue(html.contains("it&#8217;s"));
	}

	@Test
	public void testQuotations() throws IOException {
		String html = parser.parseToHtml("some 'thing is' quoted");

		assertTrue(html.contains("some &#8216;thing is&#8217; quoted"));
	}

	@Test
	public void testDoubleQuotations() throws IOException {
		String html = parser.parseToHtml("some \"thing is\" quoted");

		assertTrue(html.contains("some &#8220;thing is&#8221; quoted"));
	}

	@Test
	public void testDoubleQuotationsGerman() throws IOException {
		MarkupLanguageConfiguration configuration = new MarkupLanguageConfiguration();
		configuration.setLocale(Locale.GERMAN);
		parser.getMarkupLanguage().configure(configuration);

		String html = parser.parseToHtml("some \"thing is\" quoted");

		assertTrue(html.contains("some &#8222;thing is&#8221; quoted"));
	}

	@Test
	public void testDoubleQuotationsInTable() throws IOException {
		assertMarkup("<table><tr><td>\"thing is\"</td></tr></table>", "| \"thing is\" |");
	}

	@Test
	public void testCopyright() throws IOException {
		String html = parser.parseToHtml("copy(c)");

		assertTrue(html.contains("copy&#169;"));
	}

	@Test
	public void testTrademark() throws IOException {
		String html = parser.parseToHtml("trade(tm)");

		assertTrue(html.contains("trade&#8482;"));
	}

	@Test
	public void testRegistered() throws IOException {
		String html = parser.parseToHtml("registered(r)");

		assertTrue(html.contains("registered&#174;"));
	}

	@Test
	public void testCopyright2() throws IOException {
		String html = parser.parseToHtml("Copyright (C)");

		assertTrue(html.contains("Copyright &#169;"));
	}

	@Test
	public void testRegistered2() throws IOException {
		String html = parser.parseToHtml("Registered (R)");

		assertTrue(html.contains("Registered &#174;"));
	}

	@Test
	public void testTrademark2() throws IOException {
		String html = parser.parseToHtml("Trademark (TM)");

		assertTrue(html.contains("Trademark &#8482;"));
	}

	@Test
	public void testCopyright3() throws IOException {
		String html = parser.parseToHtml("copy (c)");

		assertTrue(html.contains("copy &#169;"));
	}

	@Test
	public void testTrademark3() throws IOException {
		String html = parser.parseToHtml("trade (tm)");

		assertTrue(html.contains("trade &#8482;"));
	}

	@Test
	public void testRegistered3() throws IOException {
		String html = parser.parseToHtml("registered (r)");

		assertTrue(html.contains("registered &#174;"));
	}

	@Test
	public void testEmDash() throws IOException {
		String html = parser.parseToHtml("one -- two");

		assertTrue(html.contains("one &#8212; two"));
	}

	@Test
	public void testEmDashAtStartOfLine() throws IOException {
		String html = parser.parseToHtml("-- two");

		assertTrue(html.contains("&#8212; two"));
	}

	@Test
	public void testEmDashNegativeNoPrecedingSpace() throws IOException {
		String html = parser.parseToHtml("one-- two");

		assertTrue(html.contains("one&#8212; two"));
	}

	@Test
	public void testEmDashAfterImage() throws IOException {
		String html = parser.parseToHtml("!images/button.png(Button)! -- Button");

		assertTrue(html.contains("<p><img alt=\"Button\" title=\"Button\" border=\"0\" src=\"images/button.png\""
				+ "/> &#8212; Button</p>"));
	}

	@Test
	public void testEnDash() throws IOException {
		String html = parser.parseToHtml("one - two");

		assertTrue(html.contains("one &#8211; two"));
	}

	@Test
	public void testMul() throws IOException {
		String html = parser.parseToHtml("2 x 4");

		assertTrue(html.contains("2 &#215; 4"));
	}

	@Test
	public void testFalseMul() throws IOException {
		String html = parser.parseToHtml("a x 4");

		assertTrue(html.contains("a x 4"));
	}

	@Test
	public void testHyperlink() throws IOException {
		String html = parser.parseToHtml("Here comes a \"hyperlink\":http://www.google.com to something");

		assertTrue(html.contains("<a href=\"http://www.google.com\">hyperlink</a>"));
	}

	@Test
	public void testHyperlinkWithClass() throws IOException {
		String html = parser.parseToHtml("Here comes a \"(test)hyperlink\":http://www.google.com to something");

		assertTrue(html.contains("<a href=\"http://www.google.com\" class=\"test\">hyperlink</a>"));
	}

	@Test
	public void testHyperlinkWithEmphasis() throws IOException {
		String html = parser.parseToHtml("Here comes a \"_Click me_\":/stories/10146 to something");

		assertTrue(html.contains("<a href=\"/stories/10146\"><em>Click me</em></a>"));
	}

	@Test
	public void testHyperlinkWithEmphasis2() throws IOException {
		String html = parser.parseToHtml("\"_Eclipse_\":http://eclipse.org");

		assertTrue(html.contains("<a href=\"http://eclipse.org\"><em>Eclipse</em></a>"));
	}

	@Test
	public void testHyperlinkWithPunctuation() throws IOException {
		String html = parser.parseToHtml("Here comes a \"Click me!\":/stories/10146 to something");

		assertTrue(html.contains("<a href=\"/stories/10146\">Click me!</a>"));
	}

	@Test
	public void testHyperlinkWithBold() throws IOException {
		String html = parser.parseToHtml("Here comes a \"*Click me*\":/stories/10146 to something");

		assertTrue(html.contains("<a href=\"/stories/10146\"><strong>Click me</strong></a>"));
	}

	@Test
	public void testHyperlinkWithBoldWrapper() throws IOException {
		String html = parser.parseToHtml("Here comes a *\"Click me\":/stories/10146* to something");

		assertTrue(html.contains("<strong><a href=\"/stories/10146\">Click me</a></strong>"));
	}

	@Test
	public void testHyperlinkWithBoldWrapper2() throws IOException {
		String html = parser.parseToHtml("*\"text\":url*");

		assertTrue(html.contains("<strong><a href=\"url\">text</a></strong>"));
	}

	@Test
	public void testHyperlinkTailNegative() throws IOException {
		String[] tails = { ",", ".", ":", ";" };
		for (String tail : tails) {
			String html = parser
					.parseToHtml("Here comes a \"hyperlink\":http://www.google.com" + tail + " to something");

			assertTrue(html.contains("<a href=\"http://www.google.com\">hyperlink</a>" + tail + " to"));
		}
	}

	@Test
	public void testHyperlinkTailPositive() throws IOException {
		String[] tails = { ")" };
		for (String tail : tails) {
			String html = parser
					.parseToHtml("Here comes a \"hyperlink\":http://www.google.com" + tail + " to something");

			assertTrue(html.contains("<a href=\"http://www.google.com" + tail + "\">hyperlink</a> to"));
		}
	}

	@Test
	public void testHyperlinkRelative() throws IOException {
		String html = parser.parseToHtml("Here comes a \"hyperlink\":foo/bar/baz.jpg to something");

		assertTrue(html.contains("<a href=\"foo/bar/baz.jpg\">hyperlink</a>"));
	}

	@Test
	public void testHyperlinkWithWidthAndHeight() {
		assertMarkup("<p><img style=\"width:32px;height:64px\" border=\"0\" src=\"images/foo.png\"/></p>",
				"!{width:32px;height:64px}images/foo.png!");
	}

	@Test
	public void testAcronym() throws IOException {
		String html = parser.parseToHtml("ABC(A Better Comb)");

		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
	}

	@Test
	public void testAcronym2() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABC(A Better Comb)");

		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
	}

	@Test
	public void testAcronym3() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABCR(A Better Comb)");

		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABCR</acronym>"));
	}

	@Test
	public void testAcronymNegative() throws IOException {
		// must have 3 or more upper-case letters for an acronym
		String html = parser.parseToHtml("Some preceding text AB(A Better Comb)");

		assertTrue(html.contains("<p>Some preceding text AB(A Better Comb)</p>"));
	}

	/**
	 * test for bug# 240743
	 */
	@Test
	public void testAcronymBug240743() {
		String markup = "As a very minor improvement to Textile-J(as what I proposed here http://www.cs.ubc.ca/~jingweno/soc/SoC2008.pdf)";
		String html = parser.parseToHtml(markup);

		assertTrue(html.contains(
				"<p>As a very minor improvement to Textile-J(as what I proposed here http://www.cs.ubc.ca/~jingweno/soc/SoC2008.pdf)</p>"));
	}

	@Test
	public void testGlossary() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABC(A Better Comb)\n\n{glossary}");

		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
		assertTrue(html.contains("<dl><dt>ABC</dt><dd>A Better Comb</dd></dl>"));
	}

	@Test
	public void testGlossaryWithStyle() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABC(A Better Comb)\n\n{glossary:style=bullet}");

		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
		assertTrue(html.contains("<dl style=\"list-style: bullet\"><dt>ABC</dt><dd>A Better Comb</dd></dl>"));
	}

	@Test
	public void testTableOfContents() throws IOException {
		String html = parser.parseToHtml(
				"h1. Table Of Contents\n\n{toc}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		assertTrue(html.contains("<a href=\"#Subhead2\">"));
		assertTrue(html.contains("<h2 id=\"Subhead2\">"));
		assertTrue(html.contains("href=\"#Subhead4\""));
		assertTrue(html.contains("<h3 id=\"Subhead4\">"));
	}

	@Test
	public void testTableOfContentsWithNoClass() throws IOException {
		String html = parser.parseToHtml(
				"h1. Table Of Contents\n\n{toc}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		assertTrue(html.contains("<ol class=\"toc\""));
	}

	@Test
	public void testTableOfContentsWithClass() throws IOException {
		String html = parser.parseToHtml(
				"h1. Table Of Contents\n\n{toc:class=test}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		assertTrue(html.contains("<ol class=\"test\""));
	}

	@Test
	public void testTableOfContentsWithClassAtTopLevel_bug341019() throws IOException {
		String html = parser.parseToHtml(
				"h1. Table Of Contents\n\n{toc:class=test}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		assertTrue(html.contains("<ol class=\"test\" style=\"list-style: none;\">"));
		assertTrue(html.contains("<ol style=\"list-style: none;\">"));
	}

	@Test
	public void testTableOfContentsWithMaxLevel() throws IOException {
		String html = parser.parseToHtml(
				"h1. Table Of Contents\n\n{toc:maxLevel=2}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		assertTrue(html.contains("<a href=\"#Subhead2\">"));
		assertTrue(html.contains("<h2 id=\"Subhead2\">"));
		assertFalse(html.contains("href=\"#Subhead4\""));
		assertTrue(html.contains("<h3 id=\"Subhead4\">"));
	}

	@Test
	public void testExtendedBlockQuote() {
		String html = parser.parseToHtml("bq.. one\ntwo\n\nthree\np. some para");

		assertTrue(Pattern.compile("<blockquote><p>one<br/>\\s*two</p>\\s*<p>three</p></blockquote><p>some para</p>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	@Test
	public void testExtendedBlockCode() {
		String html = parser.parseToHtml("bc.. one\ntwo\n\nthree\n\n\nblah");

		assertTrue(Pattern.compile(
				"<pre><code>one" + REGEX_NEWLINE + "two" + REGEX_NEWLINE + REGEX_NEWLINE + "three" + REGEX_NEWLINE
				+ REGEX_NEWLINE + REGEX_NEWLINE + "blah" + REGEX_NEWLINE + "</code></pre>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	@Test
	public void testExtendedBlockCode2() {
		String html = parser.parseToHtml("bc.. \none\ntwo\n\nthree\n\n\nmore\n\np. some para");

		assertTrue(Pattern.compile(
				"<pre><code>one" + REGEX_NEWLINE + "two" + REGEX_NEWLINE + REGEX_NEWLINE + "three" + REGEX_NEWLINE
				+ REGEX_NEWLINE + REGEX_NEWLINE + "more" + REGEX_NEWLINE + "</code></pre><p>some para</p>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	@Test
	public void testExtendedPre() {
		String html = parser.parseToHtml("pre.. one\ntwo\n\nthree\n\n\nblah\np. para");

		assertTrue(Pattern.compile(
				"<pre>one" + REGEX_NEWLINE + "two" + REGEX_NEWLINE + REGEX_NEWLINE + "three" + REGEX_NEWLINE
				+ REGEX_NEWLINE + REGEX_NEWLINE + "blah" + REGEX_NEWLINE + "</pre><p>para</p>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	@Test
	public void testParagraphWithLeadingSpace() {
		String markup = """
				 <div>

				some text

				 </div>""";
		String html = parser.parseToHtml(markup);

		assertTrue(html.contains("<body><div><p>some text</p></div></body>"));
	}

	@Test
	public void testParagraphWithLeadingSpace2() {
		String markup = """
				 para text
				para line 2

				new para""";
		String html = parser.parseToHtml(markup);

		assertTrue(Pattern
				.compile("<body>para text\\s+para line 2<p>new para</p></body>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	@Test
	public void testParagraphsWithLineBreak() {
		String html = parser.parseToHtml("first\nsecond\n\np. third\nfourth\n\nfifth");

		assertTrue(html.contains("<p>first<br/>second</p>"));
		assertTrue(html.contains("<p>third<br/>fourth</p>"));
		assertTrue(html.contains("<p>fifth</p>"));
	}

	@Test
	public void testParagraphsWithLineThatHasWhitespaceInDelimitingLine() {
		// see issue 44
		String html = parser.parseToHtml("first\n \nsecond");

		assertTrue(html.contains("<body><p>first</p><p>second</p></body>"));
	}

	@Test
	public void testBug50XHTMLCompliance() throws Exception {
		StringWriter writer = new StringWriter();

		MarkupParser parser = new MarkupParser(new TextileLanguage());
		final HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setXhtmlStrict(true);
		builder.setEmitDtd(true);
		builder.setHtmlDtd(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		parser.setBuilder(builder);
		parser.parse("!<image.png!:http://foo.bar");

		String html = writer.toString();

		assertTrue(html.contains(
				"<a href=\"http://foo.bar\"><img style=\"border-width: 0px;text-align: left;\" alt=\"\" src=\"image.png\"/></a>"));
	}

	@Test
	public void testBug50NoXHTMLCompliance() throws Exception {
		StringWriter writer = new StringWriter();

		MarkupParser parser = new MarkupParser(new TextileLanguage());
		final HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setXhtmlStrict(false);
		builder.setEmitDtd(false);
		parser.setBuilder(builder);
		parser.parse("!<image.png!:http://foo.bar");

		String html = writer.toString();

		assertTrue(
				html.contains("<a href=\"http://foo.bar\"><img align=\"left\" border=\"0\" src=\"image.png\"/></a>"));
	}

	@Test
	public void testNamedLinks() {
		String markup = """
				I am crazy about "TextileJ":textilej
				and "it's":textilej "all":textilej I ever
				"link to":textilej!

				[textilej]https://textile-j.dev.java.net""";
		String html = parser.parseToHtml(markup);

		assertTrue(html.contains(
				"<p>I am crazy about <a href=\"https://textile-j.dev.java.net\">TextileJ</a><br/>and <a href=\"https://textile-j.dev.java.net\">it&#8217;s</a> <a href=\"https://textile-j.dev.java.net\">all</a> I ever<br/><a href=\"https://textile-j.dev.java.net\">link to</a>!</p><p>[textilej]https://textile-j.dev.java.net</p>"));
	}

	@Test
	public void testXmlEscaping() {
		String html = parser.parseToHtml("some <start>mark</start> up");

		assertTrue(html.contains("<p>some <start>mark</start> up</p>"));
	}

	@Test
	public void testHtmlEscaping() {
		String html = parser.parseToHtml("some <span class=\"s\">mark</span> up");

		assertTrue(html.contains("<p>some <span class=\"s\">mark</span> up</p>"));
	}

	@Test
	public void testFootnoteReferenceLexicalPosition() {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse("a footnote reference[1] more text");
		List<Event> events = builder.getEvents();
		for (Event event : events) {
			if (event.spanType == SpanType.SUPERSCRIPT) {
				assertEquals(20, event.locator.getLineCharacterOffset());
				assertEquals(23, event.locator.getLineSegmentEndOffset());
				return;
			}
		}
		fail("expected to find superscript span");
	}

	@Test
	public void testLinkWithItalicStyle() {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse("\"_text_\":http://example.com");
		List<Event> events = builder.getEvents();

		boolean emphasisFound = false;
		boolean textFound = false;
		for (Event event : events) {
			if (event.spanType == SpanType.EMPHASIS) {
				assertEquals(1, event.locator.getLineCharacterOffset());
				assertEquals(7, event.locator.getLineSegmentEndOffset());
				emphasisFound = true;
			} else if (event.text != null) {
				assertEquals(2, event.locator.getLineCharacterOffset());
				assertEquals(6, event.locator.getLineSegmentEndOffset());
				textFound = true;
			}
		}
		assertTrue("expected to find emphasis span", emphasisFound);
		assertTrue("expected to find text", textFound);
	}

	@Test
	public void testBoldItalicsBold() {
		String html = parser.parseToHtml("*bold _ital ics_ bold*");

		assertTrue(html.contains("<strong>bold <em>ital ics</em> bold</strong>"));
	}

	@Test
	public void testItalicsBold() {
		String html = parser.parseToHtml("_italics **bol d** italics_");

		assertTrue(html.contains("<em>italics <b>bol d</b> italics</em>"));
	}

	@Test
	public void testBoldItalics() {
		String html = parser.parseToHtml("*_bold and italic_ not just bold*");

		assertTrue(html.contains("<strong><em>bold and italic</em> not just bold</strong>"));
	}

	@Test
	public void testNestedPhraseModifiersLexicalPosition() {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse("a _sample *bold -sdf-*_");
		List<Event> events = builder.getEvents();
		int found = 0;
		for (Event event : events) {

			if (event.spanType == SpanType.EMPHASIS) {
				++found;
				assertEquals(2, event.locator.getLineCharacterOffset());
				assertEquals(23, event.locator.getLineSegmentEndOffset());
			} else if (event.spanType == SpanType.STRONG) {
				++found;
				assertEquals(10, event.locator.getLineCharacterOffset());
				assertEquals(22, event.locator.getLineSegmentEndOffset());
			} else if (event.spanType == SpanType.DELETED) {
				++found;
				assertEquals(16, event.locator.getLineCharacterOffset());
				assertEquals(21, event.locator.getLineSegmentEndOffset());
			}
		}
		assertEquals(3, found);
	}

	@Test
	public void testMarkupContainingCDATA() {
		// bug 302291 text containing CDATA produces invalid HTML

		for (String blockType : new String[] { "pre", "bc" }) {

			String html = parser.parseToHtml(blockType + ". <![CDATA[123 456]]>");

			assertTrue(html.contains("&lt;![CDATA[123 456]]&gt;"));

		}
	}

	@Test
	public void testEntityReferences() {
		String[] entities = { "copy", "amp", "foobar", "#28", "x3C", "x3E" };
		for (String entity : entities) {
			String markup = "text &" + entity + ";";
			String html = parser.parseToHtml(markup);

			assertTrue(html.contains("&" + entity + ";"));

			markup = "&" + entity + ";";
			html = parser.parseToHtml(markup);

			assertTrue(html.contains("&" + entity + ";"));

			markup = "&" + entity + "; text";
			html = parser.parseToHtml(markup);

			assertTrue(html.contains("&" + entity + ";"));
		}
	}

	@Test
	public void testEntityReferences_NegativeMatch() {
		String[] entities = { "copy", "amp", "foobar", "#28", "x3C", "x3E" };
		for (String entity : entities) {
			String markup = "text &" + entity + " ;";
			String html = parser.parseToHtml(markup);

			assertTrue(html.contains("&amp;" + entity + " ;"));

			markup = "text & " + entity + ";";
			html = parser.parseToHtml(markup);

			assertTrue(html.contains("&amp; " + entity + ";"));
		}
	}
}
