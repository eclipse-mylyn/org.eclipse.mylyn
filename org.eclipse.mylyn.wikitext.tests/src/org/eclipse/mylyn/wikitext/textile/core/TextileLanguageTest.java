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
package org.eclipse.mylyn.wikitext.textile.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.RecordingDocumentBuilder.Event;

/**
 * NOTE: most textile test cases can be found in {@link MarkupParserTest}
 * 
 * @author David Green
 * 
 */
public class TextileLanguageTest extends TestCase {

	private static final String REGEX_NEWLINE = "(?:\\s*?^)";

	private MarkupParser parser;

	private TextileLanguage markupLanaguage;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initParser();
	}

	private void initParser() throws IOException {
		parser = new MarkupParser();
		markupLanaguage = new TextileLanguage();
		parser.setMarkupLanaguage(markupLanaguage);
	}

	public void testSimpleHeaders() {
		String html = parser.parseToHtml("h1. a header\n\nh2. another header");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<h1 id=\"aheader\">a header</h1>"));
		assertTrue(html.contains("<h2 id=\"anotherheader\">another header</h2>"));
	}

	public void testMultilineBlockCode() {
		String html = parser.parseToHtml("bc. one\ntwo\n\nthree");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<pre><code>one\\s+^two\\s+^</code>\\s*</pre>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	public void testPhraseModifierWorksAtStartOfLine() {
		String html = parser.parseToHtml("-a phrase modifier- at the start of a line");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<del>a phrase modifier</del> at"));
	}

	public void testPhraseModifierWorksAtEndOfLine() {
		String html = parser.parseToHtml("at the start of a line: -a phrase modifier-");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("line: <del>a phrase modifier</del>"));
	}

	public void testPhraseModifierSingleChar() {
		String html = parser.parseToHtml("a single character phrase modifier -b- is there");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("modifier <del>b</del>"));
	}

	public void testPhraseModifierFalsePositives() {
		String html = parser.parseToHtml("this is - not a phrase modifier- and -neither is this - so there");
		System.out.println("HTML: \n" + html);
		assertTrue(!html.contains("<del>"));
	}

	public void testPhraseModifierFalsePositives2() {
		String html = parser.parseToHtml("this is - not a phrase modifier - ");
		System.out.println("HTML: \n" + html);
		assertTrue(!html.contains("<del>"));
	}

	public void testBlockCodeAtEndOfDocument() {
		String html = parser.parseToHtml("bc. one\ntwo\n");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<pre><code>one\\s*^two\\s*^</code></pre>", Pattern.MULTILINE | Pattern.UNIX_LINES)
				.matcher(html)
				.find());
	}

	public void testBlockCodeAtEndOfDocument2() {
		String html = parser.parseToHtml("bc. one\ntwo");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<pre><code>one\\s*^two\\s*^</code></pre>", Pattern.MULTILINE | Pattern.UNIX_LINES)
				.matcher(html)
				.find());
	}

	public void testPhraseModifierStrong() {
		String html = parser.parseToHtml("*strong text*");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<strong>strong text</strong>"));
	}

	public void testPhraseModifiers() {
		String html = parser.parseToHtml("_emphasis_ *strong text* __italic__ **bold** ??citation?? -deleted text- +inserted text+ ^superscript^ ~subscript~ %spanned text% @code text@");
		System.out.println("HTML: \n" + html);
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

	public void testPhraseModifiersWithNonWordCharacters() {
		String html = parser.parseToHtml("_emphasis:_ *strong text:* __italic:__ **bold:** ??citation:?? -deleted text:- +inserted text:+ ^superscript:^ ~subscript:~ %spanned text:% @code text:@");
		System.out.println("HTML: \n" + html);
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

	public void testAdjacentPhraseModifiers() {
		String html = parser.parseToHtml("_emphasis_ *strong text*");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<em>emphasis</em>"));
		assertTrue(html.contains("<strong>strong text</strong>"));
	}

	public void testPhraseModifiersEnclosingText() {
		String html = parser.parseToHtml("_emphasis_ some text *strong text*");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(".*?<em>emphasis</em>\\s*some text\\s*<strong>strong text</strong>.*",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).matches());
	}

	public void testPhraseModifierCode() {
		String html = parser.parseToHtml("@code1@:");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(".*?<code>code1</code>:.*", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.matches());
	}

	public void testRelativeUrlNoBase() {
		String html = parser.parseToHtml("\"An URL\":foo/bar.html");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"foo/bar.html\">An URL</a>"));
	}

	public void testGlossaryValidHtml() {
		String html = parser.parseToHtml("h1. Foo\n\none TWO(Two Ways Out) and MDD(Model-Driven Development)\n\nh1. Glossary\n\n{glossary}");
		System.out.println("HTML: \n" + html);
		assertFalse(html.contains("<p><dl"));
		assertTrue(html.contains("</h1><dl"));
	}

	public void testLineStartingWithDeletedPhraseModifier() {
		String html = parser.parseToHtml("-this is deleted text-");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p><del>this is deleted text</del></p>"));
	}

	public void testListItemWithDeletedText() {
		String html = parser.parseToHtml("- this is a list item with -deleted text-");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ul><li>this is a list item with <del>deleted text</del></li></ul>"));
	}

	public void testHtmlEntityEncoding() {
		String html = parser.parseToHtml("Some A&BC Thing");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("A&amp;BC"));
	}

	public void testParagraphs() throws IOException {
		String html = parser.parseToHtml("first para\nnew line\n\nsecond para\n\n\n\n");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>first para<br/>\\s*new line</p><p>second para</p></body>",
				Pattern.MULTILINE).matcher(html.toString()).find());
	}

	public void testParagraphWithId() throws IOException {
		String html = parser.parseToHtml("p(#ab). first para");
		System.out.println(html);
		assertTrue(html.contains("<p id=\"ab\">first para</p>"));
	}

	public void testParagraphWithClass() throws IOException {
		String html = parser.parseToHtml("p(foo). first para");
		System.out.println(html);
		assertTrue(html.contains("<p class=\"foo\">first para</p>"));
	}

	public void testParagraphWithClassAndId() throws IOException {
		String html = parser.parseToHtml("p(foo#ab). first para");
		System.out.println(html);
		assertTrue(html.contains("<p id=\"ab\" class=\"foo\">first para</p>"));
	}

	public void testParagraphWithClassAndIdAndStyle() throws IOException {
		String html = parser.parseToHtml("p(foo#ab){color:black;}. first para");
		System.out.println(html);
		assertTrue(html.contains("<p id=\"ab\" class=\"foo\" style=\"color:black;\">first para</p>"));
	}

	public void testParagraphLeftAligned() throws IOException {
		String html = parser.parseToHtml("p<. first para");
		System.out.println(html);
		assertTrue(html.contains("<p style=\"text-align: left;\">first para</p>"));
	}

	public void testParagraphWithNestedList() throws IOException {
		String html = parser.parseToHtml("first para\n# numeric list\nfirst para second line");
		System.out.println(html);
		// NOTE: textile dialect doesn't nest lists because that is invalid XHTML.  Instead
		// the paragraph is terminated for the list.
		assertTrue(html.contains("<body><p>first para</p><ol><li>numeric list</li></ol><p>first para second line</p></body>"));
	}

	public void testPreformattedDoesntMarkupContent() throws IOException {
		String html = parser.parseToHtml("pre. \n|_. a|_. table|_. header|\n|a|table|row|\n|a|table|row|\n");
		System.out.println(html);

		assertTrue(Pattern.compile(
				"<body><pre>\\s+\\|_. a\\|_. table\\|_. header\\|\\s+\\|a\\|table\\|row\\|\\s+\\|a\\|table\\|row\\|\\s+</pre></body>",
				Pattern.MULTILINE)
				.matcher(html.toString())
				.find());
	}

	public void testHeading1() throws IOException {
		String html = parser.parseToHtml("h1(#ab). heading1\n\nnew para\n\na para");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><h1 id=\"ab\">heading1</h1><p>new para</p><p>a para</p></body>",
				Pattern.MULTILINE).matcher(html.toString()).find());
	}

	public void testHeadingMultiline() throws IOException {
		String html = parser.parseToHtml("h1. heading1\nsecondline\n\na para");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><h1 id=\"heading1\">heading1\\s+secondline</h1><p>a para</p></body>",
				Pattern.MULTILINE).matcher(html.toString()).find());
	}

	public void testHeading0NoHeading() throws IOException {
		String html = parser.parseToHtml("h0. heading0\n\nnew para\n\na para");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>h0. heading0</p><p>new para</p><p>a para</p></body>", Pattern.MULTILINE)
				.matcher(html.toString())
				.find());
	}

	public void testMultilinePreformatted() throws IOException {
		String html = parser.parseToHtml("pre. one\ntwo\n\nthree");
		System.out.println(html);
		assertTrue(Pattern.compile("<pre>one\\s+^two\\s+^</pre><p>three</p>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html.toString())
				.find());
	}

	public void testBlockQuote() throws IOException {
		String html = parser.parseToHtml("bq. one\ntwo\n\nthree");

		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<blockquote>\\s*<p>one<br/>\\s*two</p>\\s*</blockquote>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testBlockQuoteWithCitation() throws IOException {
		String html = parser.parseToHtml("bq.:http://www.example.com some text");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<blockquote cite=\"http://www.example.com\">\\s*<p>some text</p>\\s*</blockquote>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testBlockCode() throws IOException {
		String html = parser.parseToHtml("bc. one\ntwo\n\nthree");

		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<pre><code>one\\s+two\\s+</code></pre>", Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(html)
				.find());
	}

	public void testBlockCodeWithEmbeddedHtmlTags() throws IOException {
		String html = parser.parseToHtml("bc. \nhere is <a href=\"#\">a working example</a>\n\n");

		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(
				"<body><pre><code>\\s+here is &lt;a href=\"#\">a working example&lt;/a>\\s+</code></pre></body>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testFootnote() throws IOException {
		String html = parser.parseToHtml("See foo[1].\n\nfn1. Foo.");

		System.out.println("HTML: \n" + html);
		// assert on footnote target
		assertTrue(Pattern.compile("<p id=\"___fn[^\"]+\" class=\"footnote\"><sup>1</sup> Foo.</p>")
				.matcher(html)
				.find());
		// assert on footnote reference
		assertTrue(Pattern.compile("<sup class=\"footnote\"><a href=\"#___fn[^\"]+\">1</a></sup>").matcher(html).find());
	}

	public void testFootnoteRefNoFootnote() throws IOException {
		markupLanaguage.setPreprocessFootnotes(true);
		String html = parser.parseToHtml("See foo[1].\n\nNo such footnote!");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>See foo[1].</p><p>No such footnote!</p></body>"));
	}

	public void testListUnordered() throws IOException {
		String html = parser.parseToHtml("* a list\n* with two lines");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ul>"));
		assertTrue(html.contains("<li>a list</li>"));
		assertTrue(html.contains("<li>with two lines</li>"));
		assertTrue(html.contains("</ul>"));
	}

	public void testListUnordered2() throws IOException {
		String html = parser.parseToHtml("* a list\n** with several lines\n*** foo\n**  ??foo?? intentional two spaces leading content");

		System.out.println("HTML: \n" + html);
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

	public void testListWithStyle() throws IOException {
		String html = parser.parseToHtml("#{color: blue} a list with style");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<ol style=\"color: blue\">"));
		assertTrue(html.contains("<li>a list with style</li>"));
		assertTrue(html.contains("</ol>"));
	}

	public void testTable() throws IOException {
		String html = parser.parseToHtml("table. \n|a|row with|three columns|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td>a</td><td>row with</td><td>three columns</td></tr></table>"));
	}

	public void testTable2() throws IOException {
		String html = parser.parseToHtml("foo bar\n|a|row with|three columns|\n|another|row|with three columns|\n\na para");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td>a</td><td>row with</td><td>three columns</td></tr><tr><td>another</td><td>row</td><td>with three columns</td></tr></table>"));
	}

	public void testTableHeader() throws IOException {
		String html = parser.parseToHtml("table.\n|_. a|row with|three columns|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><th>a</th><td>row with</td><td>three columns</td></tr></table>"));
	}

	public void testTableCellAlignment() throws IOException {
		String html = parser.parseToHtml("table.\n|^a|<row with|>four|<>columns|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td style=\"text-align: top;\">a</td><td style=\"text-align: left;\">row with</td><td style=\"text-align: right;\">four</td><td style=\"text-align: center;\">columns</td></tr></table>"));
	}

	public void testTableCellColspan() throws IOException {
		String html = parser.parseToHtml("table.\n|\\2a|\\3b|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td colspan=\"2\">a</td><td colspan=\"3\">b</td></tr></table>"));
	}

	public void testTableCellRowspan() throws IOException {
		String html = parser.parseToHtml("table.\n|/2a|/3b|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td rowspan=\"2\">a</td><td rowspan=\"3\">b</td></tr></table>"));
	}

	public void testTableCellColspanRowspan() throws IOException {
		String html = parser.parseToHtml("table.\n|\\4/2a|\\5/3b|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td rowspan=\"2\" colspan=\"4\">a</td><td rowspan=\"3\" colspan=\"5\">b</td></tr></table>"));
	}

	public void testTableWithEmbeddedTextile() throws IOException {
		String html = parser.parseToHtml("table.\n|*a*|row _with_|stuff|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td><strong>a</strong></td><td>row <em>with</em></td><td>stuff</td></tr></table>"));
	}

	public void testTableWithAttributes() throws IOException {
		String html = parser.parseToHtml("table.\n|{color: red;}a|(foo)row with|(#bar)three columns|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td style=\"color: red;\">a</td><td class=\"foo\">row with</td><td id=\"bar\">three columns</td></tr></table>"));
	}

	public void testTableWithAttributes2() throws IOException {
		String html = parser.parseToHtml("table{border:1px solid black;}.\n" + "|This|is|a|row|\n" + "|This|is|a|row|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table style=\"border:1px solid black;\"><tr><td>This</td><td>is</td><td>a</td><td>row</td></tr><tr><td>This</td><td>is</td><td>a</td><td>row</td></tr></table>"));
	}

	public void testTableWithAttributes3() throws IOException {
		String html = parser.parseToHtml("|This|is|a|row|\n" + "{background:#ddd}. |This|is|grey|row|");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table><tr><td>This</td><td>is</td><td>a</td><td>row</td></tr><tr style=\"background:#ddd\"><td>This</td><td>is</td><td>grey</td><td>row</td></tr></table>"));
	}

	public void testPhraseModifierBold() throws IOException {
		String html = parser.parseToHtml("a paragraph with **bold content**");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>a paragraph with <b>bold content</b></p>"));
	}

	public void testPhraseModifierBoldWithId() throws IOException {
		String html = parser.parseToHtml("a paragraph with **(#1)bold content**");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>a paragraph with <b id=\"1\">bold content</b></p>"));
	}

	public void testSimplePhraseModifiers() throws IOException {
		String[][] pairs = new String[][] { { "**", "b" }, { "??", "cite" }, { "__", "i" }, { "_", "em" },
				{ "*", "strong" }, { "-", "del" }, { "+", "ins" }, { "~", "sub" }, { "^", "sup" }, { "%", "span" },
				{ "@", "code" }, };
		for (String[] pair : pairs) {
			initParser();
			String html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0]);
			System.out.println("HTML: \n" + html);
			assertTrue(html.contains("<p>a paragraph with <" + pair[1] + ">content foo bar baz</" + pair[1] + "></p>"));

			html = parser.parseToHtml(String.format("a %s2%s + b%s2%s = c%s2%s", pair[0], pair[0], pair[0], pair[0],
					pair[0], pair[0]));
			System.out.println("HTML: \n" + html);
			assertTrue(html.contains(String.format("a <%s>2</%s> + b%s2%s = c%s2%s", pair[1], pair[1], pair[0],
					pair[0], pair[0], pair[0])));

			html = parser.parseToHtml("a paragraph with (" + pair[0] + "content foo bar baz" + pair[0]
					+ ") punctuation");
			System.out.println("HTML: \n" + html);
			assertTrue(html.contains("<p>a paragraph with (<" + pair[1] + ">content foo bar baz</" + pair[1]
					+ ">) punctuation</p>"));

			html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0] + ". punctuation");
			System.out.println("HTML: \n" + html);
			assertTrue(html.contains("<p>a paragraph with <" + pair[1] + ">content foo bar baz</" + pair[1]
					+ ">. punctuation</p>"));

			initParser();
			html = parser.parseToHtml("a paragraph with " + pair[0] + "(#abc)content foo bar baz" + pair[0]);

			System.out.println("HTML: \n" + html);
			assertTrue(html.contains("<p>a paragraph with <" + pair[1] + " id=\"abc\">content foo bar baz</" + pair[1]
					+ "></p>"));

			// test for false-positive
			html = parser.parseToHtml("a paragraph with" + pair[0] + "content foo bar baz" + pair[0]);
			System.out.println("HTML: \n" + html);
			assertFalse(pair[1], html.contains("<" + pair[1] + ">"));
			assertFalse(pair[1], html.contains("</" + pair[1] + ">"));
			html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0] + "baz.");
			System.out.println("HTML: \n" + html);
			assertFalse(pair[1], html.contains("<" + pair[1] + ">"));
			assertFalse(pair[1], html.contains("</" + pair[1] + ">"));
		}
	}

	public void testDeletedIssue22() {
		// test for a false-positive
		String html = parser.parseToHtml("Foo bar-baz one two three four-five.");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>Foo bar-baz one two three four-five.</p>"));
	}

	public void testImage() throws IOException {
		String html = parser.parseToHtml("Here comes an !imageUrl! with more text");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<img border=\"0\" src=\"imageUrl\"/>"));
	}

	public void testImageWithAltAndTitle() throws IOException {
		String html = parser.parseToHtml("Here comes an !imageUrl(alt text)! with more text");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<img alt=\"(alt text)\" title=\"(alt text)\" border=\"0\" src=\"imageUrl\"/>"));
	}

	public void testImageAlignLeft() throws IOException {
		String html = parser.parseToHtml("Here comes an !<imageUrl! with more text");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<img align=\"left\" border=\"0\" src=\"imageUrl\"/>"));
	}

	public void testImageAlignRight() throws IOException {
		String html = parser.parseToHtml("Here comes an !>imageUrl! with more text");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<img align=\"right\" border=\"0\" src=\"imageUrl\"/>"));
	}

	public void testImageAlignCenter() throws IOException {
		String html = parser.parseToHtml("Here comes an !=imageUrl! with more text");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<img align=\"center\" border=\"0\" src=\"imageUrl\"/>"));
	}

	public void testImageRelative() throws IOException {
		String html = parser.parseToHtml("Here comes an !foo/bar/baz.jpg! with more text");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<img border=\"0\" src=\"foo/bar/baz.jpg\"/>"));
	}

	public void testImageHyperlink() throws IOException {
		String html = parser.parseToHtml("Here comes a !hyperlink!:http://www.google.com to something");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.google.com\"><img border=\"0\" src=\"hyperlink\"/></a>"));
		assertFalse(html.contains("</a>:http://www.google.com"));
		assertTrue(html.contains("</a> to something"));
	}

	public void testImageHyperlinkWithAttributes() throws IOException {
		String html = parser.parseToHtml("Here comes a !(foo-bar)hyperlink!:http://www.google.com to something");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.google.com\"><img class=\"foo-bar\" border=\"0\" src=\"hyperlink\"/></a>"));
		assertFalse(html.contains("</a>:http://www.google.com"));
		assertTrue(html.contains("</a> to something"));
	}

	public void testImageFalsePositiveOnMultipleExclamationMarks() throws IOException {
		String html = parser.parseToHtml("Here comes a non-image!!! more text !!! and more");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>Here comes a non-image!!! more text !!! and more</p></body>"));
	}

	public void testHtmlLiteral() throws IOException {
		String htmlFragment = "<a href=\"foo-bar\"><img src=\"some-image.jpg\"/></a>";
		String html = parser.parseToHtml("a paragraph " + htmlFragment + " with HTML literal");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>a paragraph " + htmlFragment + " with HTML literal</p>"));

	}

	public void testHtmlLiteralSelfClosingTag() throws IOException {
		String html = parser.parseToHtml("a <br/> br tag");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("a <br/> br tag"));
	}

	public void testHtmlLiteralTwoLinesWithAnchors() throws IOException {
		String html = parser.parseToHtml("Link 1 <a href=\"x\">x</a>\nand line 2 <a href=\"y\">y</a>");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>Link 1 <a href=\"x\">x</a><br/>"));
		assertTrue(html.contains("and line 2 <a href=\"y\">y</a></p></body>"));
	}

	public void testHtmlLiteralUnclosedTag() throws IOException {
		String html = parser.parseToHtml("<b>bold text with no terminating tag");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<b>bold text"));
	}

	public void testHtmlLiteralAdjacentTags() throws IOException {
		String html = parser.parseToHtml("<span><a>some text</a></span>");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<span><a>some text</a></span>"));
	}

	public void testHtmlLiteralAdjacentTags2() throws IOException {
		String html = parser.parseToHtml("<span>abc</span><a>some text</a>");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<span>abc</span><a>some text</a>"));
	}

	public void testHtmlLiteralWithEmbeddedPhraseModifiers() throws IOException {
		Pattern pattern = Pattern.compile("(<[a-zA-Z][a-zA-Z0-9_-]*(?:\\s*[a-zA-Z][a-zA-Z0-9_:-]*=\"[^\"]*\")*\\s*/?>)");
		Matcher matcher = pattern.matcher("This document was authored using Textile markup: <a href=\"https://textile-j.dev.java.net/source/browse/*checkout*/textile-j/trunk/java/org.eclipse.mylyn.wikitext.ui.doc/help/Textile-J%20User%20Guide.textile\">original Textile markup for this document</a> ");
		while (matcher.find()) {
			System.out.println(String.format("Found '%s'", matcher.group(1)));
		}
		String html = parser.parseToHtml("This document was authored using Textile markup: <a href=\"https://textile-j.dev.java.net/source/browse/*checkout*/textile-j/trunk/java/org.eclipse.mylyn.wikitext.ui.doc/help/Textile-J%20User%20Guide.textile\">original Textile markup for this document</a>");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("markup: <a href=\"https://textile-j.dev.java.net/source/browse/*checkout*/textile-j/trunk/java/org.eclipse.mylyn.wikitext.ui.doc/help/Textile-J%20User%20Guide.textile\">original Textile markup for this document</a>"));
	}

	public void testHtmlLiteralLoneCloseTag() throws IOException {
		String html = parser.parseToHtml("bold text with only a terminating</b> tag");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("terminating</b> tag"));
	}

	public void testHtmlLiteralTerminatingTagWithLegalWhitespace() throws IOException {
		String html = parser.parseToHtml("<b>bold text</b  >");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<b>bold text</b  >"));
	}

	public void testHtmlLiteralFalsePositive() throws IOException {
		String html = parser.parseToHtml("a <br br tag that is not real");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("a &lt;br br tag"));
	}

	public void testHtmlLiteralFalsePositive2() throws IOException {
		String html = parser.parseToHtml("some no tag <!-- nt");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("&lt;!--"));
	}

	public void testHtmlLiteralFalsePositive3() throws IOException {
		String html = parser.parseToHtml("some no tag <0nt");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("&lt;0nt"));
	}

	public void testHtmlLiteralFalsePositive4() throws IOException {
		String html = parser.parseToHtml("some no tag <_nt");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("&lt;_nt"));
	}

	public void testEscaping() throws IOException {
		String html = parser.parseToHtml("==no <b>textile</b> *none* _at_ all==");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("no <b>textile</b> *none* _at_ all"));
	}

	public void testEscaping2() throws IOException {
		String html = parser.parseToHtml("==*none*==");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>*none*</p>"));
	}

	public void testEscaping3() throws IOException {
		String html = parser.parseToHtml("Link 1 ==<a href=\"x\">x</a>==\nand line 2 ==<a href=\"y\">y</a>==");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>Link 1 <a href=\"x\">x</a><br/>"));
		assertTrue(html.contains("and line 2 <a href=\"y\">y</a></p>"));
	}

	public void testEscaping4() throws IOException {
		String html = parser.parseToHtml("=={toc}== Generates a table of contents.  Eg: =={toc}== or =={toc:style=disc|maxLevel=3}==");

		System.out.println(html);

		assertTrue(html.contains("<body><p>{toc} Generates a table of contents.  Eg: {toc} or {toc:style=disc|maxLevel=3}</p></body>"));
	}

	public void testReplacements() throws IOException {
		String html = parser.parseToHtml("some text with copyright(c), trademark(tm) and registered(r)");

		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(".*?<p>some text with copyright&#169;, trademark&#8482; and registered&#174;</p>.*",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).matches());
	}

	public void testApostrophe() throws IOException {
		String html = parser.parseToHtml("it's");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("it&#8217;s"));
	}

	public void testQuotations() throws IOException {
		String html = parser.parseToHtml("some 'thing is' quoted");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("some &#8216;thing is&#8217; quoted"));
	}

	public void testDoubleQuotations() throws IOException {
		String html = parser.parseToHtml("some \"thing is\" quoted");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("some &#8220;thing is&#8221; quoted"));
	}

	public void testDoubleQuotationsInTable() throws IOException {
		String html = parser.parseToHtml("| \"thing is\" |");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("&#8220;thing is&#8221;"));
	}

	public void testCopyright() throws IOException {
		String html = parser.parseToHtml("copy(c)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("copy&#169;"));
	}

	public void testTrademark() throws IOException {
		String html = parser.parseToHtml("trade(tm)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("trade&#8482;"));
	}

	public void testRegistered() throws IOException {
		String html = parser.parseToHtml("registered(r)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("registered&#174;"));
	}

	public void testCopyright2() throws IOException {
		String html = parser.parseToHtml("Copyright (C)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("Copyright &#169;"));
	}

	public void testRegistered2() throws IOException {
		String html = parser.parseToHtml("Registered (R)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("Registered &#174;"));
	}

	public void testTrademark2() throws IOException {
		String html = parser.parseToHtml("Trademark (TM)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("Trademark &#8482;"));
	}

	public void testCopyright3() throws IOException {
		String html = parser.parseToHtml("copy (c)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("copy &#169;"));
	}

	public void testTrademark3() throws IOException {
		String html = parser.parseToHtml("trade (tm)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("trade &#8482;"));
	}

	public void testRegistered3() throws IOException {
		String html = parser.parseToHtml("registered (r)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("registered &#174;"));
	}

	public void testEmDash() throws IOException {
		String html = parser.parseToHtml("one -- two");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("one &#8212; two"));
	}

	public void testEnDash() throws IOException {
		String html = parser.parseToHtml("one - two");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("one &#8211; two"));
	}

	public void testMul() throws IOException {
		String html = parser.parseToHtml("2 x 4");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("2 &#215; 4"));
	}

	public void testFalseMul() throws IOException {
		String html = parser.parseToHtml("a x 4");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("a x 4"));
	}

	public void testHyperlink() throws IOException {
		String html = parser.parseToHtml("Here comes a \"hyperlink\":http://www.google.com to something");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.google.com\">hyperlink</a>"));
	}

	public void testHyperlinkTail() throws IOException {
		String[] tails = new String[] { ",", ".", ")", ":", ";" };
		for (String tail : tails) {
			String html = parser.parseToHtml("Here comes a \"hyperlink\":http://www.google.com" + tail
					+ " to something");

			System.out.println("HTML: \n" + html);
			assertTrue(html.contains("<a href=\"http://www.google.com\">hyperlink</a>" + tail + " to"));
		}
	}

	public void testHyperlinkRelative() throws IOException {
		String html = parser.parseToHtml("Here comes a \"hyperlink\":foo/bar/baz.jpg to something");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"foo/bar/baz.jpg\">hyperlink</a>"));
	}

	public void testAcronym() throws IOException {
		String html = parser.parseToHtml("ABC(A Better Comb)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
	}

	public void testAcronym2() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABC(A Better Comb)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
	}

	public void testAcronym3() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABCR(A Better Comb)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABCR</acronym>"));
	}

	public void testAcronymNegative() throws IOException {
		// must have 3 or more upper-case letters for an acronym
		String html = parser.parseToHtml("Some preceding text AB(A Better Comb)");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>Some preceding text AB(A Better Comb)</p>"));
	}

	/**
	 * test for bug# 240743
	 */
	public void testAcronymBug240743() {
		String markup = "As a very minor improvement to Textile-J(as what I proposed here http://www.cs.ubc.ca/~jingweno/soc/SoC2008.pdf)";
		String html = parser.parseToHtml(markup);

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>As a very minor improvement to Textile-J(as what I proposed here http://www.cs.ubc.ca/~jingweno/soc/SoC2008.pdf)</p>"));
	}

	public void testGlossary() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABC(A Better Comb)\n\n{glossary}");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
		assertTrue(html.contains("<dl><dt>ABC</dt><dd>A Better Comb</dd></dl>"));
	}

	public void testGlossaryWithStyle() throws IOException {
		String html = parser.parseToHtml("Some preceding text ABC(A Better Comb)\n\n{glossary:style=bullet}");

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<acronym title=\"A Better Comb\">ABC</acronym>"));
		assertTrue(html.contains("<dl style=\"list-style: bullet\"><dt>ABC</dt><dd>A Better Comb</dd></dl>"));
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

	public void testExtendedBlockQuote() {
		String html = parser.parseToHtml("bq.. one\ntwo\n\nthree\np. some para");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<blockquote><p>one<br/>\\s*two</p>\\s*<p>three</p></blockquote><p>some para</p>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testExtendedBlockCode() {
		String html = parser.parseToHtml("bc.. one\ntwo\n\nthree\n\n\nblah");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(
				"<pre><code>one" + REGEX_NEWLINE + "two" + REGEX_NEWLINE + REGEX_NEWLINE + "three" + REGEX_NEWLINE
						+ REGEX_NEWLINE + REGEX_NEWLINE + "blah" + REGEX_NEWLINE + "</code></pre>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testExtendedBlockCode2() {
		String html = parser.parseToHtml("bc.. \none\ntwo\n\nthree\n\n\nmore\n\np. some para");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(
				"<pre><code>" + REGEX_NEWLINE + "one" + REGEX_NEWLINE + "two" + REGEX_NEWLINE + REGEX_NEWLINE + "three"
						+ REGEX_NEWLINE + REGEX_NEWLINE + REGEX_NEWLINE + "more" + REGEX_NEWLINE
						+ "</code></pre><p>some para</p>", Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testExtendedPre() {
		String html = parser.parseToHtml("pre.. one\ntwo\n\nthree\n\n\nblah\np. para");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(
				"<pre>one" + REGEX_NEWLINE + "two" + REGEX_NEWLINE + REGEX_NEWLINE + "three" + REGEX_NEWLINE
						+ REGEX_NEWLINE + REGEX_NEWLINE + "blah" + REGEX_NEWLINE + "</pre><p>para</p>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testParagraphWithLeadingSpace() {
		String markup = " <div>\n\n" + "some text\n\n" + " </div>";
		String html = parser.parseToHtml(markup);
		System.out.println("MARKUP: \n" + markup);
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><div><p>some text</p></div></body>"));
	}

	public void testParagraphWithLeadingSpace2() {
		String markup = " para text\n" + "para line 2\n" + "\n" + "new para";
		String html = parser.parseToHtml(markup);
		System.out.println("MARKUP: \n" + markup);
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile("<body>para text\\s+para line 2<p>new para</p></body>",
				Pattern.MULTILINE | Pattern.DOTALL).matcher(html).find());
	}

	public void testParagraphsWithLineThatHasWhitespaceInDelimitingLine() {
		// see issue 44
		String html = parser.parseToHtml("first\n \nsecond");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>first</p><p>second</p></body>"));
	}

	public void testBug50XHTMLCompliance() throws Exception {
		StringWriter writer = new StringWriter();

		MarkupParser parser = new MarkupParser(new TextileLanguage());
		final HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setXhtmlStrict(true);
		builder.setEmitDtd(true);
		builder.setHtmlDtd("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		parser.setBuilder(builder);
		parser.parse("!<image.png!:http://foo.bar");

		String html = writer.toString();
		System.out.println(html);

		assertTrue(html.contains("<a href=\"http://foo.bar\"><img style=\"border-width: 0px;text-align: left;\" alt=\"\" src=\"image.png\"/></a>"));
	}

	public void testBug50NoXHTMLCompliance() throws Exception {
		StringWriter writer = new StringWriter();

		MarkupParser parser = new MarkupParser(new TextileLanguage());
		final HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setXhtmlStrict(false);
		builder.setEmitDtd(false);
		parser.setBuilder(builder);
		parser.parse("!<image.png!:http://foo.bar");

		String html = writer.toString();
		System.out.println(html);

		assertTrue(html.contains("<a href=\"http://foo.bar\"><img align=\"left\" border=\"0\" src=\"image.png\"/></a>"));
	}

	public void testNamedLinks() {
		String markup = "I am crazy about \"TextileJ\":textilej\n" + "and \"it's\":textilej \"all\":textilej I ever\n"
				+ "\"link to\":textilej!\n\n" + "[textilej]https://textile-j.dev.java.net";
		String html = parser.parseToHtml(markup);

		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>I am crazy about <a href=\"https://textile-j.dev.java.net\">TextileJ</a><br/>and <a href=\"https://textile-j.dev.java.net\">it's</a> <a href=\"https://textile-j.dev.java.net\">all</a> I ever<br/><a href=\"https://textile-j.dev.java.net\">link to</a>!</p><p>[textilej]https://textile-j.dev.java.net</p>"));
	}

	public void testXmlEscaping() {
		String html = parser.parseToHtml("some <start>mark</start> up");
		System.out.println(html);
		assertTrue(html.contains("<p>some <start>mark</start> up</p>"));
	}

	public void testHtmlEscaping() {
		String html = parser.parseToHtml("some <span class=\"s\">mark</span> up");
		System.out.println(html);
		assertTrue(html.contains("<p>some <span class=\"s\">mark</span> up</p>"));
	}

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

	public void testBoldItalicsBold() {
		String html = parser.parseToHtml("*bold _ital ics_ bold*");
		System.out.println(html);
		assertTrue(html.contains("<strong>bold <em>ital ics</em> bold</strong>"));
	}

	public void testItalicsBold() {
		String html = parser.parseToHtml("_italics **bol d** italics_");
		System.out.println(html);
		assertTrue(html.contains("<em>italics <b>bol d</b> italics</em>"));
	}

	public void testBoldItalics() {
		String html = parser.parseToHtml("*_bold and italic_ not just bold*");
		System.out.println(html);
		assertTrue(html.contains("<strong><em>bold and italic</em> not just bold</strong>"));
	}

	public void testNestedPhraseModifiersLexicalPosition() {
		RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse("a _sample *bold -sdf-*_");
		List<Event> events = builder.getEvents();
		int found = 0;
		for (Event event : events) {
			System.out.println(event);
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
}
