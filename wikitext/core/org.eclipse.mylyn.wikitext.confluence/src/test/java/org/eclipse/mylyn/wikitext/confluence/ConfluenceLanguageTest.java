/*******************************************************************************
 * Copyright (c) 2007, 2017 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Patrick Boisclair - tests for bug 354077
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Test;

import com.google.common.io.Resources;

/**
 * @author David Green
 * @author Patrick Boisclair
 */
public class ConfluenceLanguageTest extends AbstractMarkupGenerationTest<ConfluenceLanguage> {

	@Override
	protected ConfluenceLanguage createMarkupLanguage() {
		return new ConfluenceLanguage();
	}

	@Test
	public void testDiscoverable() {
		MarkupLanguage language = ServiceLocator.getInstance().getMarkupLanguage("Confluence");
		assertNotNull(language);
		assertTrue(language instanceof ConfluenceLanguage);
	}

	@Test
	public void testIsDetectingRawHyperlinks() {
		assertTrue(getMarkupLanguage().isDetectingRawHyperlinks());
	}

	@Test
	public void testParagraph() throws Exception {
		String html = parser.parseToHtml("a paragraph\n\nanother paragraph\nwith\n2 lines");

		assertTrue(
				Pattern.compile("<body><p>a paragraph</p><p>another paragraph<br/>\\s*with<br/>\\s*2 lines</p></body>",
						Pattern.MULTILINE).matcher(html).find());
	}

	@Test
	public void testHeadings() {
		for (int x = 1; x <= 6; ++x) {
			initParser();
			String html = parser.parseToHtml("h" + x + ". a heading\n\nwith a para");

			assertTrue(
					Pattern.compile("<body><h" + x + " id=\"aheading\">a heading</h" + x + "><p>with a para</p></body>",
							Pattern.MULTILINE).matcher(html).find());

			html = parser.parseToHtml("h" + x + ". a heading\nwith a para");

			assertTrue(
					Pattern.compile("<body><h" + x + " id=\"aheading\">a heading</h" + x + "><p>with a para</p></body>",
							Pattern.MULTILINE).matcher(html).find());

			html = parser.parseToHtml("  h" + x + ". a heading\n\nwith a para");

			assertTrue(
					Pattern.compile("<body><h" + x + " id=\"aheading\">a heading</h" + x + "><p>with a para</p></body>",
							Pattern.MULTILINE).matcher(html).find());
		}
	}

	@Test
	public void testBlockQuote() {
		String html = parser.parseToHtml("bq. a multiline\nblock quote\n\nwith a para");

		assertTrue(Pattern.compile(
				"<body><blockquote><p>a multiline<br/>\\s*block quote</p></blockquote><p>with a para</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	@Test
	public void testBlockQuoteExtended() {
		String html = parser.parseToHtml("{quote}\na multiline\nblock quote\n\nwith two paras\n{quote}\nanother para");

		assertTrue(Pattern.compile(
				"<body><blockquote><p>a multiline<br/>\\s*block quote</p><p>with two paras</p></blockquote><p>another para</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	@Test
	public void testBlockQuoteExtended2() {
		String html = parser.parseToHtml("{quote}this is a quote{quote}\nsome more text");

		assertTrue(html.contains("<body><blockquote><p>this is a quote</p></blockquote><p>some more text</p></body>"));
	}

	@Test
	public void testBlockQuoteExtendedUnclosed() {
		String html = parser.parseToHtml("{quote}\na multiline\nblock quote\n\nwith two paras\n");

		assertTrue(Pattern.compile(
				"<body><blockquote><p>a multiline<br/>\\s*block quote</p><p>with two paras</p></blockquote></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	@Test
	public void testBlockQuoteExtendedLeadingSpaces() {
		String html = parser.parseToHtml("     {quote}\na multiline\nblock quote\n    {quote}\nmore text");

		assertTrue(html
				.contains("<body><blockquote><p>a multiline<br/>block quote</p></blockquote><p>more text</p></body>"));
	}

	@Test
	public void testBlockQuoteExtendedBreaksPara() {
		String html = parser.parseToHtml("a para\n{quote}quoted{quote}new para");

		assertTrue(html.contains("<body><p>a para</p><blockquote><p>quoted</p></blockquote><p>new para</p></body>"));
	}

	@Test
	public void testBlockQuoteWithBulletedList() {
		String html = parser.parseToHtml("{quote}\ntext\n* a list\n* second item\n\nmore text\n{quote}\nanother para");

		assertTrue(html.contains(
				"<body><blockquote><p>text</p><ul><li>a list</li><li>second item</li></ul><p>more text</p></blockquote><p>another para</p></body>"));
	}

	@Test
	public void testBlockQuoteExtendedWithNestedTable() {
		assertMarkup(
				"<blockquote><table><tr><td>Names</td><td>Occupation</td></tr><tr><td><ul><li>John</li><li>Jane</li></ul></td><td>Programmer</td></tr></table></blockquote>",
				"{quote}|Names|Occupation|\n|* John\n* Jane|Programmer|{quote}\n");
	}

	@Test
	public void testBlockQuoteExtendedWithMultipleNestedBlocks() {
		assertMarkup(
				"<blockquote><table><tr><td>Names</td><td>Occupation</td></tr><tr><td><ul><li>John</li><li>Jane</li></ul></td><td>Programmer</td></tr></table><ul><li>another</li><li>list</li></ul><p>and a para</p></blockquote>",
				"{quote}\n|Names|Occupation|\n|* John\n* Jane|Programmer|\n\n* another\n* list\n\nand a para{quote}\n");
	}

	@Test
	public void testBlockQuoteExtendedWithNestedBlockEndingAtStartOfLine() {
		assertMarkup(
				"<blockquote><h1 id=\"Headline1\">Headline 1</h1><h2 id=\"Headline2\">Headline 2</h2></blockquote>",
				"{quote}\nh1. Headline 1\nh2. Headline 2\n{quote}");
	}

	@Test
	public void testSimplePhraseModifiers() throws IOException {
		Object[][] pairs = new Object[][] { { "*", "strong" }, { "_", "em" }, { "??", "cite" }, { "-", "del" },
				{ "+", "u" }, { "^", "sup" }, { "~", "sub" }, };
		for (Object[] pair : pairs) {
			initParser();
			String html = parser.parseToHtml("a paragraph with " + pair[0] + "content foo bar baz" + pair[0]);

			assertTrue(html.contains("<p>a paragraph with <" + pair[1] + ">content foo bar baz</" + pair[1] + "></p>"));
		}
	}

	@Test
	public void testDeleted() {
		String html = parser.parseToHtml("one -two three-four five- six");

		assertTrue(html.contains("<del>two three-four five</del>"));
	}

	@Test
	public void testMonospaced() {
		String html = parser.parseToHtml("a paragraph with {{content foo bar baz}}");

		assertTrue(html.contains("<p>a paragraph with <tt>content foo bar baz</tt></p>"));
	}

	@Test
	public void testMonospaced_NegativeTest() {
		String html = parser.parseToHtml("a paragraph with \\{{content foo bar baz}}");

		assertTrue(html.contains("<p>a paragraph with {{content foo bar baz}}</p>"));
	}

	@Test
	public void testCharacterEscapeSequence() {
		assertMarkup("<p>a {[] &amp;#160;\u00A0 b</p>", "a \\{\\[\\] &\\#160;&#160; b");
	}

	@Test
	public void testCharacterEntityReference() {
		String html = parser.parseToHtml("a &#92;&#122; b");
		assertTrue(html.contains("<p>a \\z b</p>"));
	}

	@Test
	public void testEndash() {
		String html = parser.parseToHtml("an endash -- foo");

		assertTrue(html.contains("endash &#8211; foo"));
	}

	@Test
	public void testEnDashAtStartOfLine() throws IOException {
		String html = parser.parseToHtml("-- two");

		assertTrue(html.contains("&#8211; two"));
	}

	@Test
	public void testEnDashAfterWordNoWhitespace() throws IOException {
		String html = parser.parseToHtml("one-- two");

		assertFalse(html.contains("&#8211;"));
		assertTrue(html.contains("one-- two"));
	}

	@Test
	public void testEmdash() {
		String html = parser.parseToHtml("an emdash --- foo");

		assertTrue(html.contains("emdash &#8212; foo"));
	}

	@Test
	public void testEmDashAtStartOfLine() throws IOException {
		String html = parser.parseToHtml("--- two");

		assertTrue(html.contains("&#8212; two"));
	}

	@Test
	public void testEmDashAfterWordNoWhitespace() throws IOException {
		String html = parser.parseToHtml("one--- two");

		assertFalse(html.contains("&#8212;"));
		assertTrue(html.contains("one--- two"));
	}

	@Test
	public void testHorizontalRule() {
		String html = parser.parseToHtml("an hr \n----\n foo");

		assertTrue(html.contains("hr </p><hr/><p> foo"));
	}

	/**
	 * line starts with a horizontal rule, which is important since it is very similar to a level-4 list case.
	 */
	@Test
	public void testHorizontalRule2() {
		String html = parser.parseToHtml("----\n an hr foo");

		assertTrue(html.contains("<hr/><p> an hr foo</p>"));
	}

	@Test
	public void testHorizontalRule3() {
		String html = parser.parseToHtml("an hr foo \n ---- ");

		assertTrue(html.contains("<p>an hr foo </p><hr/>"));
	}

	@Test
	public void testHorizontalRule4() {
		String html = parser.parseToHtml("text\n----\nmore text");

		assertTrue(html.contains("<hr/>"));
	}

	@Test
	public void testFourDashesIsNotHorizontalRule() {
		String html = parser.parseToHtml("text----more text");

		assertTrue(html.contains("text----more text"));
	}

	@Test
	public void testFourSpacedDashesIsNotHorizontalRule() {
		String html = parser.parseToHtml("text  -  -  -  -more text");

		assertTrue(html.contains("text  -  -  -  -more text"));
	}

	@Test
	public void testFiveDashesIsNotHorizontalRule() {
		String html = parser.parseToHtml("text\n-----\nmore text");

		assertTrue(html.contains("text<br/>-----<br/>more text"));
	}

	@Test
	public void testSixDashesIsNotHorizontalRule() {
		String html = parser.parseToHtml("text\n------\nmore text");

		assertTrue(html.contains("text<br/>------<br/>more text"));
	}

	@Test
	public void testEightDashesIsNotHorizontalRule() {
		String html = parser.parseToHtml("text\n--------\nmore text");

		assertTrue(html.contains("text<br/>--------<br/>more text"));
	}

	@Test
	public void testEightDashesWithSpaceInBetweenIsNotHorizontalRule() {
		String html = parser.parseToHtml("text\n---- ----\nmore text");

		assertTrue(html.contains("text<br/>---- ----<br/>more text"));
	}

	@Test
	public void testTabBeforeFourDashesIsHorizontalRule() {
		String html = parser.parseToHtml("text\n	----\nmore text");

		assertTrue(html.contains("text</p><hr/><p>more text"));
	}

	@Test
	public void testTabAfterFourDashesIsHorizontalRule() {
		String html = parser.parseToHtml("text\n----	\nmore text");

		assertTrue(html.contains("text</p><hr/><p>more text"));
	}

	@Test
	public void testMultipleTabsBeforeAndAfterFourDashesIsHorizontalRule() {
		String html = parser.parseToHtml("text\n			----		\nmore text");

		assertTrue(html.contains("text</p><hr/><p>more text"));
	}

	@Test
	public void testTabBetweenFourDashesIsNotHorizontalRule() {
		String html = parser.parseToHtml("text\n--	--\nmore text");

		assertTrue(html.contains("text<br/>--	--<br/>more text"));
	}

	@Test
	public void testFourDashesWithoutASpaceAtFourthLevelIsHorizontalRule() {
		String html = parser.parseToHtml("- text\n-- second line\n--- third line\n----");

		assertTrue(html.contains("<li>text<ul><li>second line<ul><li>third line</li></ul></li></ul></li></ul><hr/>"));
	}

	@Test
	public void testFourDashesWithASpaceAtFourthLevelIsNotHorizontalRule() {
		String html = parser.parseToHtml("- text\n-- second line\n--- third line\n---- ");

		assertFalse(html.contains("<hr/>"));
	}

	@Test
	public void testHyperlink() {
		String html = parser.parseToHtml("a [http://example.com] hyperlink");

		assertTrue(
				html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkNoText() {
		String html = parser.parseToHtml("a [] nothing");

		assertTrue(html.contains("<body><p>a [] nothing</p></body>"));
	}

	@Test
	public void testHyperlinkPartiallyExpressed() {
		// bug 290434
		String html = parser.parseToHtml("a [ |] hyperlink");

		assertTrue(html.contains("<body><p>a  hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkInternal() {
		String oldPattern = getMarkupLanguage().getInternalLinkPattern();
		getMarkupLanguage().setInternalLinkPattern("/display/{0}"); //$NON-NLS-1$
		String html = parser.parseToHtml("a [Page Example] hyperlink");

		getMarkupLanguage().setInternalLinkPattern(oldPattern);
		assertTrue(html.contains("<body><p>a <a href=\"/display/Page Example\">Page Example</a> hyperlink</p></body>"));
	}

	protected ConfluenceLanguage getMarkupLanguage() {
		return (ConfluenceLanguage) parser.getMarkupLanguage();
	}

	@Test
	public void testHyperlinkInternalWithAnchor() {
		String oldPattern = getMarkupLanguage().getInternalLinkPattern();
		getMarkupLanguage().setInternalLinkPattern("/display/{0}"); //$NON-NLS-1$
		String html = parser.parseToHtml("a [#Page Example] hyperlink");

		getMarkupLanguage().setInternalLinkPattern(oldPattern);
		assertTrue(html.contains("<body><p>a <a href=\"#Page Example\">Page Example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkInternalWithName() {
		String oldPattern = getMarkupLanguage().getInternalLinkPattern();
		getMarkupLanguage().setInternalLinkPattern("/display/{0}"); //$NON-NLS-1$
		String html = parser.parseToHtml("a [Another Page Example|Page Example] hyperlink");

		getMarkupLanguage().setInternalLinkPattern(oldPattern);
		assertTrue(html.contains(
				"<body><p>a <a href=\"/display/Page Example\">Another Page Example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHuyperlinkUnclosed() {
		assertMarkup("<p>[1. first <br/>2. second]</p>", "[1. first \n2. second]");
	}

	@Test
	public void testDisabledRelativeParsingHyperlinkExternal() {
		setupLanguageWithRelativeLinksDisabled();
		assertMarkup("<p><a href=\"http://abc\">http://abc</a></p>", "[http://abc]");
	}

	@Test
	public void testDisabledRelativeParsingLinksWithName() {
		setupLanguageWithRelativeLinksDisabled();
		assertMarkup("<p>[abc|Title]</p>", "[abc|Title]");
	}

	@Test
	public void testDisabledRelativeParsingLinksWithoutName() {
		setupLanguageWithRelativeLinksDisabled();
		assertMarkup("<p>[abc]</p>", "[abc]");
	}

	@Test
	public void testDisabledRelativeParsingLinksAnchor() {
		setupLanguageWithRelativeLinksDisabled();
		assertMarkup("<p><a href=\"#anchor\">anchor</a></p>", "[#anchor]");
	}

	@Test
	public void testHyperlinkInternalWithNameAndTip() {
		String oldPattern = getMarkupLanguage().getInternalLinkPattern();
		getMarkupLanguage().setInternalLinkPattern("/display/{0}"); //$NON-NLS-1$
		String html = parser.parseToHtml("a [Another Page Example|Page Example| Some tip] hyperlink");

		getMarkupLanguage().setInternalLinkPattern(oldPattern);
		assertTrue(html.contains(
				"<body><p>a <a href=\"/display/Page Example\" title=\"Some tip\">Another Page Example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkWithTitle() {
		String html = parser.parseToHtml("a [Example|http://example.com] hyperlink");

		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkWithTitle2() {
		String html = parser.parseToHtml("a [Example Two | http://example.com] hyperlink");

		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example Two</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkWithUrlTitle() {
		assertMarkup("<p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p>",
				"a [http://example.com| http://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"http://example.com\">http://example.com with more text</a> hyperlink</p>",
				"a [http://example.com with more text| http://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"http://example.com\">http://example.com <strong>bolded</strong></a> hyperlink</p>",
				"a [http://example.com *bolded*| http://example.com] hyperlink");
	}

	@Test
	public void testHyperlinkHash() {
		String html = parser.parseToHtml("a [Example|#example] hyperlink");

		assertTrue(html.contains("<body><p>a <a href=\"#example\">Example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkHash2() {
		String html = parser.parseToHtml("a [#example] hyperlink");

		assertTrue(html.contains("<body><p>a <a href=\"#example\">example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkWithTip() {
		String html = parser.parseToHtml("a [example | http://example.com | title is here] hyperlink");

		assertTrue(html.contains(
				"<body><p>a <a href=\"http://example.com\" title=\"title is here\">example</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkImplied() {
		String html = parser.parseToHtml("a http://example.com hyperlink");

		assertTrue(
				html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkImpliedNegativeMatch() {
		String html = parser.parseToHtml("a http://example.com. hyperlink");

		assertTrue(html
				.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a>. hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkImpliedNegativeMatch2() {
		String html = parser.parseToHtml("a http://example.com) hyperlink");

		assertTrue(html
				.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a>) hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkWithSpaces() {
		String html = parser.parseToHtml("a [ http://example.com ] hyperlink");

		assertTrue(
				html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkWithTitleAndSpace() {
		String html = parser.parseToHtml("a [Example Two | http://example.com ] hyperlink");

		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example Two</a> hyperlink</p></body>"));
	}

	@Test
	public void testHyperlinkMailtoNoBase() {

		StringWriter out = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setBase(null);
		parser.setBuilder(builder);

		parser.parse("[test|mailto:foo@bar.com]");

		assertTrue(out.toString().contains("<a href=\"mailto:foo@bar.com\">test</a>"));
	}

	@Test
	public void testHyperlinkMailtoWithBase() throws URISyntaxException {

		StringWriter out = new StringWriter();

		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setBase(new URI("/"));
		parser.setBuilder(builder);

		parser.parse("[test|mailto:foo@bar.com]");

		assertTrue(out.toString().contains("<a href=\"mailto:foo@bar.com\">test</a>"));
	}

	@Test
	public void testItalicsWithHyperlink() {
		// bug 298626: [Confluence] italic formatting with embedded links is not handled correctly
		String html = parser.parseToHtml("_This [This is a test|http://my_url.jpg] is a test_");
		assertTrue(html.contains(
				"<body><p><em>This <a href=\"http://my_url.jpg\">This is a test</a> is a test</em></p></body>"));
	}

	@Test
	public void testItalicsWithHyperlink2() {
		// bug 298626: [Confluence] italic formatting with embedded links is not handled correctly
		String html = parser.parseToHtml("_This [This is a test|http://myurl.jpg] is a test_");
		assertTrue(html.contains(
				"<body><p><em>This <a href=\"http://myurl.jpg\">This is a test</a> is a test</em></p></body>"));
	}

	@Test
	public void testItalicsWithHyperlink3() {
		// bug 298626: [Confluence] italic formatting with embedded links is not handled correctly
		String html = parser.parseToHtml("_This [This is a test|http://my%5Furl.jpg] is a test_");
		assertTrue(html.contains(
				"<body><p><em>This <a href=\"http://my%5Furl.jpg\">This is a test</a> is a test</em></p></body>"));
	}

	@Test
	public void testNamedAnchor() {
		String html = parser.parseToHtml("a {anchor:a23423} named anchor");

		assertTrue(html.contains("<body><p>a <span id=\"a23423\"></span> named anchor</p></body>"));
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

	/**
	 * test scenario where we have a level-4 nested list, which happens to start with the same pattern as a horizontal
	 * rule. We want to match as a list in this case.
	 */
	@Test
	public void testListWithHrPattern() throws IOException {
		String html = parser.parseToHtml("- first\n-- second\n--- third\n---- fourth");

		assertTrue(html.contains(
				"<ul style=\"list-style: square\"><li>first<ul><li>second<ul><li>third<ul><li>fourth</li></ul></li></ul></li></ul></li></ul>"));
	}

	@Test
	public void testImage() {
		String html = parser.parseToHtml("an !image.png! image");

		assertTrue(html.contains("<body><p>an <img border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithFullUrl() {
		String html = parser.parseToHtml("an !http://www.foo.com/bin/image.png! image");

		assertTrue(html
				.contains("<body><p>an <img border=\"0\" src=\"http://www.foo.com/bin/image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesAlignRight() {
		String html = parser.parseToHtml("an !image.png|align=right! image");

		assertTrue(html.contains("<body><p>an <img align=\"right\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesAlignLeft() {
		String html = parser.parseToHtml("an !image.png|align=left! image");

		assertTrue(html.contains("<body><p>an <img align=\"left\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesAlignMiddle() {
		String html = parser.parseToHtml("an !image.png|align=middle! image");

		assertTrue(
				html.contains("<body><p>an <img align=\"middle\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesAlignCenter() {
		String html = parser.parseToHtml("an !image.png|align=center! image");

		assertTrue(html.contains(
				"<body><p>an <div style=\"text-align: center;\"><img border=\"0\" src=\"image.png\"/></div> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesAlignCenterToDocbook() {
		StringWriter out = new StringWriter();
		DocBookDocumentBuilder builder = new DocBookDocumentBuilder(out);
		parser.setBuilder(builder);
		parser.parse("an !image.png|align=center! image");
		String result = out.toString();

		assertTrue(result.contains(
				"<para>an <mediaobject><imageobject><imagedata fileref=\"image.png\"/></imageobject></mediaobject> image</para>"));
	}

	@Test
	public void testImageWithAttributesAlt() {
		String html = parser.parseToHtml("an !image.png|alt= some alt text! image");

		assertTrue(html
				.contains("<body><p>an <img alt=\"some alt text\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesTitle() {
		assertMarkup(
				"<p>an <img title=\"some title text\" alt=\"some title text\" border=\"0\" src=\"image.png\"/> image</p>",
				"an !image.png|title= some title text! image");
	}

	@Test
	public void testImageWithAttributesAltAndTitle() {
		assertMarkup(
				"<p>an <img alt=\"alt text here\" title=\"some title text\" border=\"0\" src=\"image.png\"/> image</p>",
				"an !image.png|alt=\"alt text here\",title= some title text! image");
	}

	@Test
	public void testImageWithAttributesBorder() {
		String html = parser.parseToHtml("an !image.png|border=5! image");

		assertTrue(html.contains("<body><p>an <img border=\"5\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesWidth() {
		String html = parser.parseToHtml("an !image.png|width=5! image");

		assertTrue(html.contains("<body><p>an <img width=\"5\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesHeight() {
		String html = parser.parseToHtml("an !image.png|height=5! image");

		assertTrue(html.contains("<body><p>an <img height=\"5\" border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageWithAttributesHeightBadValue() {
		String html = parser.parseToHtml("an !image.png|height=5a! image");

		assertTrue(html.contains("<body><p>an <img border=\"0\" src=\"image.png\"/> image</p></body>"));
	}

	@Test
	public void testImageNegativeMatch() {
		// Issue 67: https://textile-j.dev.java.net/issues/show_bug.cgi?id=67
		String html = parser.parseToHtml("I really like ice cream! Yay!");

		assertTrue(html.contains("<body><p>I really like ice cream! Yay!</p></body>"));
	}

	@Test
	public void testTable() {
		assertMarkup("<table><tr><td>a</td><td>row</td><td>not header</td></tr></table>", "|a|row|not header|");
	}

	@Test
	public void testTableWithMultilineText() {
		assertMarkup("<table><tr><td>label</td><td>line1<br/>line2<br/>line3</td><td>text</td></tr></table>",
				"|label|line1\nline2\nline3|text |");
	}

	@Test
	public void testTableWithMultilineTextImplicitEnd() {
		assertMarkup("<table><tr><td>label</td><td>line1<br/>line2<br/>line3</td></tr></table><p>text</p>",
				"|label|line1\nline2\nline3\n\ntext");
	}

	@Test
	public void testTableWithMultilineTextAndNestedBlock() {
		assertMarkup(
				"<table><tr><td><ul><li>listitem</li></ul></td><td>line1<br/>line2<br/>line3</td><td><ul><li>listitem2</li></ul></td></tr></table>",
				"|* listitem|line1\nline2\nline3|* listitem2|");
	}

	@Test
	public void testTableWithHeader() {
		assertMarkup(
				"<table><tr><th>a</th><th>header</th><th>row</th></tr><tr><td>a</td><td>row</td><td>not header</td></tr></table>",
				"||a||header||row||\n|a|row|not header|");
	}

	@Test
	public void testTableNestedWithHeader() {
		assertMarkup(
				"<p>a para</p><table><tr><th>a</th><th>header</th><th>row</th></tr><tr><td>a</td><td>row</td><td>not header</td></tr></table><p>tail</p>",
				"a para\n||a||header||row||\n|a|row|not header|\ntail");
	}

	@Test
	public void testTableWithLinkAndPipes() {
		// test for bug# 244240
		assertMarkup("<table><tr><td><a href=\"https://textile-j.dev.java.net/\">Website</a></td></tr></table>",
				"| [Website|https://textile-j.dev.java.net/] |");
	}

	@Test
	public void testTableWithLinkAndPipes2() {
		// test for bug# 244240
		assertMarkup(
				"<table><tr><td><a href=\"https://textile-j.dev.java.net/\">Website</a></td><td>another cell</td><td><a href=\"http://www.eclipse.org\">Eclipse</a></td></tr></table>",
				"| [Website|https://textile-j.dev.java.net/] | another cell | [Eclipse|http://www.eclipse.org] |");
	}

	@Test
	public void testTableWithUnclosedHyperlinks() {
		assertMarkup("<table><tr><td>one</td><td>[two<br/> three</td></tr></table>", "| one | [two\n three |");
	}

	@Test
	public void testTableWithUnclosedHyperlinks2() {
		assertMarkup("<table><tr><td>one</td><td>[two</td></tr></table><p>more content</p>",
				"| one | [two\n\nmore content");
	}

	@Test
	public void testTableWithSingletonList() {
		// test for bug# 513661
		assertMarkup("<table><tr><td><ul><li>one thing</li></ul></td><td>another cell</td></tr></table>",
				"|* one thing| another cell |");
	}

	@Test
	public void testTableWithEmptyCells() {
		assertMarkup("<table><tr><td></td></tr></table>", "|   |");
		assertMarkup("<table><tr><td></td><td></td></tr></table>", "|   |   |");
		assertMarkup("<table><tr><td></td><td>text</td></tr></table>", "|   | text |");
	}

	@Test
	public void testTableWithSingletonListAndWhitespacePrefix() {
		// test for bug# 513661
		assertMarkup("<table><tr><td><ul><li>one thing</li></ul></td><td>another cell</td></tr></table>",
				"|    * one thing| another cell |");
	}

	@Test
	public void testTableWithBulletedLists() {
		// test for bug# 513661
		assertMarkup(
				"<table><tr><td><ul><li>one thing</li><li>two things</li></ul></td><td>another cell</td></tr></table>",
				"|* one thing\n* two things| another cell |");
	}

	@Test
	public void testTableWithNumberedLists() {
		// test for bug# 513661
		assertMarkup(
				"<table><tr><td>other cell</td><td><ol><li>one thing</li><li>two things</li><li>three things </li></ol></td></tr></table>",
				"|other cell| # one thing\n# two things\n# three things |");
	}

	@Test
	public void testTableWithLinksAndLists() {
		// test for bug# 513661
		assertMarkup(
				"<table><tr><td><a href=\"https://textile-j.dev.java.net/\">Website</a></td><td><ol><li>one thing</li><li>two things</li><li>three things </li></ol></td><td><a href=\"http://www.eclipse.org\">Eclipse</a></td></tr></table>",
				"| [Website|https://textile-j.dev.java.net/]| # one thing\n# two things\n# three things | [Eclipse|http://www.eclipse.org] |");
	}

	@Test
	public void parseTableWithSquareBrace() {
		String content = "|[B\n"//
				+ "C]|\n";
		assertMarkup("<table><tr><td>[B<br/>C]</td></tr></table>", content);
	}

	@Test
	public void parseTableWithLinkAndSquareBrace() {
		assertMarkup("<table><tr><td>a <a href=\"http://example.com\">link</a> to [B<br/>C]</td></tr></table>",
				"|a [link|http://example.com] to [B\n"//
						+ "C]|\n");
		assertMarkup("<table><tr><td>[a <a href=\"http://example.com\">link</a> to B</td></tr></table>",
				"|[a [link|http://example.com] to B|");
	}

	@Test
	public void testTableWithMultipleLists() {
		// test for bug# 513661
		assertMarkup("<table>" + //
				"<tr><th>Bulleted list</th><th><ul><li>one thing</li><li>two things </li></ul></th></tr>" + //
				"<tr><td>Numbered list</td><td><ol><li>one thing</li><li>two things </li></ol></td></tr>" + //
				"<tr><td>Bulleted list</td><td><ul style=\"list-style: square\"><li>one thing<ul><li>two things </li></ul></li></ul></td></tr>"
				+ //
				"</table>", "||Bulleted list||* one thing\n* two things |\n" + //
						"|Numbered list|# one thing\n# two things |\n" + //
						"|Bulleted list|- one thing\n-- two things |");
	}

	@Test
	public void testTableWithEscapedPipe() {
		assertMarkup("<table><tr><td>some cell | content with pipe escaped</td></tr></table>",
				"| some cell \\| content with pipe escaped |");
	}

	@Test
	public void testTableWithEscapedSquareBrackets() {
		assertMarkup("<table><tr><td>some cell [ content with pipe escaped</td></tr></table>",
				"| some cell \\[ content with pipe escaped |");
	}

	@Test
	public void testPreformattedExtended() {
		String html = parser
				.parseToHtml("{noformat}\na multiline\n\tpreformatted\n\nwith two paras\n{noformat}\nanother para");

		assertTrue(Pattern
				.compile("body><pre>a multiline\\s+preformatted\\s+with two paras\\s+</pre><p>another para</p></body>",
						Pattern.MULTILINE)
				.matcher(html)
				.find());
	}

	@Test
	public void testPreformattedExtended2() {
		String html = parser
				.parseToHtml("{noformat}\na multiline\n\tpreformatted\n\nwith two paras{noformat}another para");

		assertTrue(html.contains("<body><pre>a multiline"));
		assertTrue(html.contains("</pre><p>another para</p></body>"));
		assertTrue(Pattern.compile("with two paras\\s*</pre>", Pattern.MULTILINE).matcher(html).find());
	}

	@Test
	public void testNote() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{note:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{note}" + "\n"
				+ "More text...");

		assertTrue(html.contains("<p>Some text</p><div class=\"note\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{note"));
	}

	@Test
	public void testNote2() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		parser.setBuilder(builder);
		parser.parse("{note}this is a note {note}\n\n* one thing\n* two things");
		String html = out.toString();

		assertTrue(html.contains(
				"<body><div class=\"note\"><p>this is a note </p></div><ul><li>one thing</li><li>two things</li></ul></body>"));
	}

	@Test
	public void testNote3() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		parser.setBuilder(builder);
		parser.parse("{note}this is a note {note}*bold* text\nfoo\n\nbar");
		String html = out.toString();

		assertTrue(html.contains(
				"<body><div class=\"note\"><p>this is a note </p></div><p><strong>bold</strong> text<br/>foo</p><p>bar</p></body>"));
	}

	@Test
	public void testNote4() {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		parser.setBuilder(builder);
		parser.parse("abc{note}this is a note {note}*bold* text\nfoo\n\nbar");
		String html = out.toString();

		assertTrue(html.contains(
				"<body><p>abc</p><div class=\"note\"><p>this is a note </p></div><p><strong>bold</strong> text<br/>foo</p><p>bar</p></body>"));
	}

	@Test
	public void testInfo() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{info:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{info}" + "\n"
				+ "More text...");

		assertTrue(html.contains("<p>Some text</p><div class=\"info\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{info"));
	}

	@Test
	public void testWarning() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{warning:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{warning}" + "\n"
				+ "More text...");

		assertTrue(html.contains("<p>Some text</p><div class=\"warning\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{warning"));
	}

	@Test
	public void testTip() {
		String html = parser.parseToHtml("h1. a header\n" + "\n" + "Some text\n" + "{tip:title=A Title}\n"
				+ "the body of the note\n" + "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{tip}" + "\n"
				+ "More text...");

		assertTrue(html.contains("<p>Some text</p><div class=\"tip\""));
		assertTrue(html.contains("</p></div><p>More text...</p>"));
		assertTrue(html.contains("or <strong>other</strong> <em>textile</em> <cite>markup</cite>"));
		assertFalse(html.contains("{tip"));
	}

	@Test
	public void testTipToDocBook() {
		StringWriter out = new StringWriter();
		parser.setBuilder(new DocBookDocumentBuilder(out));
		parser.parse("h1. a header\n" + "\n" + "Some text\n" + "{tip:title=A Title}\n" + "the body of the note\n"
				+ "which may span multiple lines\n" + "\n"
				+ "And may even have multiple paragraphs or *other* _textile_ ??markup??\n" + "{tip}" + "\n"
				+ "More text...");
		String docbook = out.toString();

		assertTrue(docbook.contains("<tip><title>A Title</title><para>the body of"));
		assertTrue(docbook.contains(
				"paragraphs or <emphasis role=\"bold\">other</emphasis> <emphasis>textile</emphasis> <citation>markup</citation></para></tip>"));
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
	public void testTableOfContentsWithMaxLevel() throws IOException {
		String html = parser.parseToHtml(
				"h1. Table Of Contents\n\n{toc:maxLevel=2}\n\nh1. Top Header\n\nsome text\n\nh2. Subhead\n\nh2. Subhead2\n\nh1. Top Header 2\n\nh2. Subhead 3\n\nh3. Subhead 4");

		assertTrue(html.contains("<a href=\"#Subhead2\">"));
		assertTrue(html.contains("<h2 id=\"Subhead2\">"));
		assertFalse(html.contains("href=\"#Subhead4\""));
		assertTrue(html.contains("<h3 id=\"Subhead4\">"));
	}

	@Test
	public void testBoldItalicsBold() {
		String html = parser.parseToHtml("*bold _ital ics_ bold*");

		assertTrue(html.contains("<strong>bold <em>ital ics</em> bold</strong>"));
	}

	@Test
	public void testItalicsBold() {
		String html = parser.parseToHtml("_italics *bol d* italics_");

		assertTrue(html.contains("<em>italics <strong>bol d</strong> italics</em>"));
	}

	@Test
	public void testItalics() {
		assertMarkup("<p><em>italics</em></p>", "_italics_");
	}

	@Test
	public void testDoubleUnderscore() {
		assertMarkup("<p>__italics__</p>", "__italics__");
	}

	@Test
	public void testItalicsNegativeMatchTrailingWhitespace() {
		assertMarkup("<p>_italics _</p>", "_italics _");
	}

	@Test
	public void testItalicsNegativeMatchLeadingWhitespace() {
		assertMarkup("<p>_ italics_</p>", "_ italics_");
	}

	@Test
	public void testItalicsNegativeMatchNoContent() {
		assertMarkup("<p>__</p>", "__");
	}

	@Test
	public void testItalicsNegativeMatchNoTerminator() {
		assertMarkup("<p>_some text_here no end</p>", "_some text_here no end");
	}

	@Test
	public void testItalicsContainsUnderscore() {
		assertMarkup("<p><em>some text_here with</em> end</p>", "_some text_here with_ end");
	}

	@Test
	public void testItalicsNonGreedy() {
		assertMarkup("<p><em>italics</em> a_</p>", "_italics_ a_");
	}

	@Test
	public void testItalicsNonGreedy2() {
		assertMarkup("<p>some <em>italics</em> and <em>more italics</em> here</p>",
				"some _italics_ and _more italics_ here");
	}

	@Test
	public void testHyperlinkWithItalics() {
		assertMarkup("<p><a href=\"http://my_url.jpg\"><em>This is a test</em></a></p>",
				"[_This is a test_|http://my_url.jpg]");
	}

	@Test
	public void testHyperlinkWithBold() {
		assertMarkup("<p><a href=\"http://my_url.jpg\"><strong>This is a test</strong></a></p>",
				"[*This is a test*|http://my_url.jpg]");
	}

	@Test
	public void testHyperlinkWithBoldItalics() {
		assertMarkup("<p><a href=\"http://my_url.jpg\"><strong><em>This is a test</em></strong></a></p>",
				"[*_This is a test_*|http://my_url.jpg]");
	}

	@Test
	public void testBoldItalics() {
		assertMarkup("<p><strong><em>bold and italic</em> not just bold</strong></p>",
				"*_bold and italic_ not just bold*");
	}

	@Test
	public void testInlineQuote() {
		assertMarkup("<p>a paragraph <q>with inline</q> quote</p>", "a paragraph {quote}with inline{quote} quote");
	}

	@Test
	public void testInlineQuoteWithBullets() {
		assertMarkup("<ul><li>a bullet <q>with inline</q> quote</li></ul>",
				"* a bullet {quote}with inline{quote} quote");
	}

	@Test
	public void testInlineQuoteWithBullets2() {
		assertMarkup("<ul><li><q>a bullet with inline</q> quote</li></ul>",
				"* {quote}a bullet with inline{quote} quote");
	}

	@Test
	public void testInlineQuoteNegativeMatch() {
		assertMarkup("<p>a paragraph {quote}with inline quote</p>", "a paragraph {quote}with inline quote");
	}

	@Test
	public void testInlineQuoteNegativeMatch2() {
		assertMarkup("<blockquote><p>a paragraph with </p></blockquote><p>inline quote{quote}</p>",
				"{quote}a paragraph with {quote}inline quote{quote}");
	}

	@Test
	public void testColor() {
		assertMarkup("<div style=\"color: red;\"><p>a paragraph</p><p>another paragraph</p></div><p>text</p>",
				"{color:red}\na paragraph\n\nanother paragraph\n{color}\ntext");
	}

	@Test
	public void testColor2() {
		assertMarkup("<div style=\"color: red;\"><p>a paragraph</p><p>another paragraph</p></div><p>text</p>",
				"{color:red}a paragraph\n\nanother paragraph{color}text");
	}

	@Test
	public void testColor3() {
		assertMarkup("<p>text <span style=\"color: red;\">more text</span> text</p>",
				"text {color:red}more text{color} text");
	}

	@Test
	public void testColor4() {
		assertMarkup("<p>text</p><div style=\"color: red;\"><p>more text</p></div><p>text</p>",
				"text\n{color:red}more text{color}\ntext");
	}

	@Test
	public void testColorLexicalOffsets() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = "views to help SOA  development. These includes [SOA Services Explorer](in green) for the list of all available SOA services,\n[Types Explorer](in {color:#0000ff}blue{color}) for searching and browsing all available ";

		parser.parse(content);

		int previousDocumentOffset = -1;
		for (RecordingDocumentBuilder.Event event : builder.getEvents()) {
			assertTrue(previousDocumentOffset <= event.locator.getDocumentOffset());
			previousDocumentOffset = event.locator.getDocumentOffset();
			if (event.text != null) {
				int start = event.locator.getDocumentOffset();
				int end = event.locator.getLineSegmentEndOffset() + event.locator.getLineDocumentOffset();
				assertEquals(event.text.length(), end - start);
				assertTrue(end >= start);
				assertEquals(content.substring(start, end), event.text);
			}
		}

	}

	/**
	 * bug 318695
	 */
	@Test
	public void testHangOnBug318695() throws IOException {
		String content = Resources.toString(ConfluenceLanguageTest.class.getResource("resources/bug318695.confluence"),
				StandardCharsets.UTF_8);
		parser.setBuilder(new HtmlDocumentBuilder(new StringWriter()));
		parser.parse(new StringReader(content));
		// if we reach here we didn't hang.
	}

	/**
	 * bug 424387
	 */
	@Test
	public void stackOverflowWithLargeContentOnBug424387() throws IOException {
		String content = Resources.toString(ConfluenceLanguageTest.class.getResource("resources/bug424387.confluence"),
				StandardCharsets.UTF_8);
		parser.setBuilder(new HtmlDocumentBuilder(new StringWriter()));
		parser.parse(new StringReader(content));
		// if we reach here we didn't hang.
	}

	/**
	 * bug 533397
	 */
	@Test
	public void stackOverflowWithLargeContentInTable() throws IOException {
		String content = Resources.toString(ConfluenceLanguageTest.class.getResource("resources/bug533397.confluence"),
				StandardCharsets.UTF_8);
		parser.setBuilder(new HtmlDocumentBuilder(new StringWriter()));
		parser.parse(new StringReader(content));
		// if we reach here we didn't hang.
	}

	@Test
	public void testParagraphWithSingleNewline() {
		assertMarkup("<p>one<br/>two</p><p>three</p>", "one\ntwo\n\nthree");
	}

	@Test
	public void testParagraphWithMultipleNewlines() {
		assertMarkup("<p>one<br/><br/><br/>two</p><p>three</p>", "one\n\\\\\\\\two\n\nthree");
	}

	@Test
	public void testParagraphWithMultipleNewlines2() {
		assertMarkup("<p>one<br/><br/><br/>two</p><p>three</p>", "one\\\\\\\\\\\\two\n\nthree");
	}

	@Test
	public void testListItemWithNewline() {
		assertMarkup("<ul><li>one<br/>two</li><li>three</li></ul>", "* one\ntwo\n* three");
	}

	@Test
	public void testListItemWithIndent() {
		assertMarkup("<ul><li>one<br/>two</li><li>three</li></ul>", " \t* one\ntwo\n* three");
	}

	@Test
	public void testListItemWithIndentUsingSpaces() {
		assertMarkup("<ul><li>one</li><li>two</li><li>three</li></ul>", " * one\n * two\n * three");
	}

	@Test
	public void testListItemWithTwoNewlines() {
		assertMarkup("<ul><li>one</li></ul><p>two</p><ul><li>three</li></ul>", "* one\n\ntwo\n* three");
	}

	@Test
	public void testInlineCode() {
		assertMarkup("<p><code>someCamelCaseCodeHere()</code> and some text</p>",
				"@someCamelCaseCodeHere()@ and some text");
	}

	@Test
	public void testEmailInInlineCode() {
		assertMarkup("<p><code>sampleEmail@sample.com</code></p>", "@sampleEmail@sample.com@");
	}

	@Test
	public void testNestedEmailsInInlineCode() {
		assertMarkup("<p><code>snippet by sampleEmail@sample.com and another@another.com</code></p>",
				"@snippet by sampleEmail@sample.com and another@another.com@");
	}

	@Test
	public void testNoNestingInCode() {
		assertMarkup("<p><code>_underline_</code></p>", "@_underline_@");
	}

	@Test
	public void testEmailAddressOutsideOfCode() {
		assertMarkup("<p>sampleEmail@sample.com followed by <code>this</code></p>",
				"sampleEmail@sample.com followed by @this@");
	}

	@Test
	public void testEmailRightBeforeCode() {
		assertMarkup("<p>sampleEmail@sample.com <code>this</code></p>", "sampleEmail@sample.com @this@");
	}

	@Test
	public void testEmailBeforeAndAfterCode() {
		assertMarkup("<p>sampleEmail@sample.com <code>this</code> another@another.com</p>",
				"sampleEmail@sample.com @this@ another@another.com");
	}

	@Test
	public void testClone() {
		ConfluenceLanguage language = (ConfluenceLanguage) parser.getMarkupLanguage();
		ConfluenceLanguage newLanguage = language.clone();
		assertTrue(newLanguage.isParseRelativeLinks());
	}

	@Test
	public void clonePreservesRelativeLinksFlag() {
		setupLanguageWithRelativeLinksDisabled();
		ConfluenceLanguage language = (ConfluenceLanguage) parser.getMarkupLanguage();
		ConfluenceLanguage newLanguage = language.clone();
		assertFalse(newLanguage.isParseRelativeLinks());
	}

	private void setupLanguageWithRelativeLinksDisabled() {
		ConfluenceLanguage language = new ConfluenceLanguage();
		language.setParseRelativeLinks(false);
		parser.setMarkupLanguage(language);
	}
}
