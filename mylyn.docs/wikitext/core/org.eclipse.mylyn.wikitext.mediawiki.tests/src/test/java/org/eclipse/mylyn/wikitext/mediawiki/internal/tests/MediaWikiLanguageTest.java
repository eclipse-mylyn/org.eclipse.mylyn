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
 *     Jeremie Bresson - Bug 381506, 381912, 391850, 304495, 396545
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.mediawiki.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.Template;
import org.eclipse.mylyn.wikitext.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings({ "nls", "restriction" })
public class MediaWikiLanguageTest extends AbstractMarkupGenerationTest<MediaWikiLanguage> {

	private Locale locale;

	@Override
	protected MediaWikiLanguage createMarkupLanguage() {
		return new MediaWikiLanguage();
	}

	@Before
	public void setUp() {
		locale = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
	}

	@After
	public void tearDown() throws Exception {
		Locale.setDefault(locale);
	}

	@Test
	public void testDiscoverable() {
		MarkupLanguage language = ServiceLocator.getInstance(MediaWikiLanguageTest.class.getClassLoader())
				.getMarkupLanguage("MediaWiki");
		assertNotNull(language);
		assertTrue(language instanceof MediaWikiLanguage);
	}

	@Test
	public void testIsDetectingRawHyperlinks() {
		assertTrue(getMarkupLanguage().isDetectingRawHyperlinks());
	}

	protected MediaWikiLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	@Test
	public void testParagraph() {
		assertMarkup("<p>first para<br/>\nfirst para line2</p><p>second para</p><p>third para</p>",
				"first para<br/>\nfirst para line2\n\nsecond para\n\nthird para");
	}

	@Test
	public void testNowiki() {
		assertMarkup("<p><b>no &lt;!-- markup here</b></p>", "'''<nowiki>no <!-- markup here</nowiki>'''");
	}

	@Test
	public void testNoWiki_325022() {
		// bug 325022: nowiki is not correctly detected
		assertMarkup("<p>//[username[:password]@]host[:port]</p>",
				"//<nowiki>[</nowiki>username<nowiki>[</nowiki>:password]@]host<nowiki>[</nowiki>:port]");
	}

	@Test
	public void testBoldItalic() {
		assertMarkup("<p><b><i>foo bar</i></b></p>", "'''''foo bar'''''");
		assertMarkup("<p>normal <b><i>bold italic text</i></b> normal</p>", "normal '''''bold italic text''''' normal");

		assertMarkup("<p><b><i>bold italic text</i></b> normal</p>", "'''''bold italic text''''' normal");
		assertMarkup("<p>normal <b><i>bold italic text</i></b></p>", "normal '''''bold italic text'''''");

		assertMarkup("<p>before <b><i>foo bar</i></b> after a <b><i>second time</i></b> normal</p>",
				"before '''''foo bar''''' after a '''''second time''''' normal");
	}

	@Test
	public void testBoldItalicImmediatelyFollowingTag() {
		assertMarkup("<p>normal<br/><b><i>bold italic text</i></b> normal</p>",
				"normal<br>'''''bold italic text''''' normal");
	}

	@Test
	public void testBoldItalic_single_character_bug369921() {
		assertMarkup("<p><b><i>aa</i></b> bb <b><i>cc</i></b></p>", "'''''aa''''' bb '''''cc'''''");
		assertMarkup("<p><b><i>a</i></b> b <b><i>c</i></b></p>", "'''''a''''' b '''''c'''''");
	}

	@Test
	public void testBoldItalic_adjacentText_bug369921() {
		assertMarkup("<p><b><i>aa</i></b>bb</p>", "'''''aa'''''bb");
	}

	@Test
	public void testBoldItalicWithWhitespace() {
		//Bug 391850
		assertMarkup("<p>normal <b><i> bi text</i></b> normal</p>", "normal ''''' bi text''''' normal");
		assertMarkup("<p>normal <b><i>bi text </i></b> normal</p>", "normal '''''bi text ''''' normal");
		assertMarkup("<p>normal <b><i> bi text </i></b> normal</p>", "normal ''''' bi text ''''' normal");

		assertMarkup("<p><b><i> bi text</i></b> normal</p>", "''''' bi text''''' normal");
		assertMarkup("<p><b><i>bi text </i></b> normal</p>", "'''''bi text ''''' normal");
		assertMarkup("<p><b><i> bi text </i></b> normal</p>", "''''' bi text ''''' normal");

		assertMarkup("<p>normal <b><i> bi text</i></b></p>", "normal ''''' bi text'''''");
		assertMarkup("<p>normal <b><i>bi text </i></b></p>", "normal '''''bi text '''''");
		assertMarkup("<p>normal <b><i> bi text </i></b></p>", "normal ''''' bi text '''''");
	}

	@Test
	public void testBoldItalicWithSingleQuote() {
		//Bug 509775
		assertMarkup("<p>n <b><i>bi 'a' and 'e' text</i></b> n</p>", "n '''''bi 'a' and 'e' text''''' n");
		assertMarkup("<p><b><i>bi 'a' and 'e' text</i></b> n</p>", "'''''bi 'a' and 'e' text''''' n");
		assertMarkup("<p>n <b><i>bi 'a' and 'e' text</i></b></p>", "n '''''bi 'a' and 'e' text'''''");

	}

	@Test
	public void testBold() {
		assertMarkup("<p><b>foo bar</b></p>", "'''foo bar'''");
		assertMarkup("<p>normal <b>bold text</b> normal</p>", "normal '''bold text''' normal");

		assertMarkup("<p><b>bold text</b> normal</p>", "'''bold text''' normal");
		assertMarkup("<p>normal <b>bold text</b></p>", "normal '''bold text'''");

		assertMarkup("<p>before <b>foo bar</b> after a <b>second time</b> normal</p>",
				"before '''foo bar''' after a '''second time''' normal");
	}

	@Test
	public void testBoldImmediatelyFollowingTag() {
		assertMarkup("<p>normal<br/><b>bold text</b> normal</p>", "normal<br>'''bold text''' normal");
	}

	@Test
	public void testBold_single_character_bug369921() {
		assertMarkup("<p><b>aa</b> bb <b>cc</b></p>", "'''aa''' bb '''cc'''");
		assertMarkup("<p><b>a</b> b <b>c</b></p>", "'''a''' b '''c'''");
	}

	@Test
	public void testBold_adjacentText_bug369921() {
		assertMarkup("<p><b>aa</b>bb</p>", "'''aa'''bb");
	}

	@Test
	public void testBoldWithWhitespace() {
		//Bug 391850
		assertMarkup("<p>normal <b> bold text</b> normal</p>", "normal ''' bold text''' normal");
		assertMarkup("<p>normal <b>bold text </b> normal</p>", "normal '''bold text ''' normal");
		assertMarkup("<p>normal <b> bold text </b> normal</p>", "normal ''' bold text ''' normal");

		assertMarkup("<p><b> bold text</b> normal</p>", "''' bold text''' normal");
		assertMarkup("<p><b>bold text </b> normal</p>", "'''bold text ''' normal");
		assertMarkup("<p><b> bold text </b> normal</p>", "''' bold text ''' normal");

		assertMarkup("<p>normal <b> bold text</b></p>", "normal ''' bold text'''");
		assertMarkup("<p>normal <b>bold text </b></p>", "normal '''bold text '''");
		assertMarkup("<p>normal <b> bold text </b></p>", "normal ''' bold text '''");
	}

	@Test
	public void testBoldWithSingleQuote() {
		//Bug 509775
		assertMarkup("<p>normal <b>bold 'a' and 'e' text</b> normal</p>", "normal '''bold 'a' and 'e' text''' normal");
		assertMarkup("<p>normal <b> a' bold</b> normal</p>", "normal ''' a' bold''' normal");
		assertMarkup("<p>normal <b>bold 'a bold </b> normal</p>", "normal '''bold 'a bold ''' normal");
	}

	@Test
	public void testItalic() {
		assertMarkup("<p><i>foo bar</i></p>", "''foo bar''");
		assertMarkup("<p>normal <i>italic text</i> normal</p>", "normal ''italic text'' normal");

		assertMarkup("<p><i>italic text</i> normal</p>", "''italic text'' normal");
		assertMarkup("<p>normal <i>italic text</i></p>", "normal ''italic text''");

		assertMarkup("<p>before <i>foo bar</i> after a <i>second time</i> normal</p>",
				"before ''foo bar'' after a ''second time'' normal");
	}

	@Test
	public void testItalic_single_character_bug369921() {
		assertMarkup("<p><i>aa</i> bb <i>cc</i></p>", "''aa'' bb ''cc''");
		assertMarkup("<p><i>a</i> b <i>c</i></p>", "''a'' b ''c''");
	}

	@Test
	public void testItalic_adjacentText_bug369921() {
		assertMarkup("<p><i>aa</i>bb</p>", "''aa''bb");
	}

	@Test
	public void testItalicWithWhitespace() {
		//Bug 391850
		assertMarkup("<p>normal <i> italic text</i> normal</p>", "normal '' italic text'' normal");
		assertMarkup("<p>normal <i>italic text </i> normal</p>", "normal ''italic text '' normal");
		assertMarkup("<p>normal <i> italic text </i> normal</p>", "normal '' italic text '' normal");

		assertMarkup("<p><i> italic text</i> normal</p>", "'' italic text'' normal");
		assertMarkup("<p><i>italic text </i> normal</p>", "''italic text '' normal");
		assertMarkup("<p><i> italic text </i> normal</p>", "'' italic text '' normal");

		assertMarkup("<p>normal <i> italic text</i></p>", "normal '' italic text''");
		assertMarkup("<p>normal <i>italic text </i></p>", "normal ''italic text ''");
		assertMarkup("<p>normal <i> italic text </i></p>", "normal '' italic text ''");
	}

	@Test
	public void testItalicWithSingleQuote() {
		//Bug 509775
		assertMarkup("<p>norm <i>italic 'a' and 'e' text</i> norm</p>", "norm ''italic 'a' and 'e' text'' norm");
		assertMarkup("<p>normal <i>italic 'a text </i> normal</p>", "normal ''italic 'a text '' normal");
		assertMarkup("<p>normal <i> some a' italic</i> normal</p>", "normal '' some a' italic'' normal");
	}

	@Test
	public void testHeadings() {
		for (int x = 1; x <= 6; ++x) {
			String delimiter = repeat(x, "=");
			String[] headingMarkupSamples = { delimiter + "heading text" + delimiter,
					delimiter + "heading text" + delimiter + "  ", delimiter + "heading text" + delimiter + " \t " };
			for (String headingMarkup : headingMarkupSamples) {
				String html = parser.parseToHtml(
						headingMarkup + "\nfirst para<br/>\nfirst para line2\n\nsecond para\n\nthird para");

				assertTrue(Pattern.compile("<body><h" + x + " id=\"[^\"]+\">heading text</h" + x
						+ "><p>first para<br/>\\s*first para line2</p><p>second para</p><p>third para</p></body>",
						Pattern.MULTILINE).matcher(html).find());
			}
		}
	}

	@Test
	public void testHeadingWithStyles_bug355713() {
		assertMarkup(
				"<h2 id=\".27.27.27bold.27.27.27_.27.27italic.27.27_underlined_strikethrough\"><b>bold</b> <i>italic</i> <u>underlined</u> <s>strikethrough</s></h2>",
				"== '''bold''' ''italic'' <u>underlined</u> <s>strikethrough</s> ==");
	}

	@Test
	public void testHeadingsWithPara() {
		assertMarkup("<h2 id=\"H1\">H1</h2><p>pa</p><h3 id=\"H3\">H3</h3><p>abc</p>",
				"\n== H1 ==\n\npa\n\n=== H3 ===\n\nabc");
	}

	private String repeat(int i, String string) {
		StringBuilder buf = new StringBuilder(string.length() * i);
		for (int x = 0; x < i; ++x) {
			buf.append(string);
		}
		return buf.toString();
	}

	@Test
	public void testHorizontalRule() {
		assertMarkup("<p>an hr <hr/> foo</p>", "an hr ---- foo");
	}

	@Test
	public void testHorizontalRule2() {
		assertMarkup("<p>Mediawiki should render:\n<hr/>\nAs a \"horizontal rule\".</p>",
				"Mediawiki should render:\n----\nAs a \"horizontal rule\".");
	}

	@Test
	public void testListUnordered() throws IOException {
		assertMarkup("<ul><li>a list</li><li>with two lines</li></ul>", "* a list\n* with two lines");
	}

	@Test
	public void testListOrdered() throws IOException {
		assertMarkup("<ol><li>a list</li><li>with two lines</li></ol>", "# a list\n# with two lines");
	}

	@Test
	public void testListOrderedWithContinuation() throws IOException {
		assertMarkup(
				"<ol><li>a list<ol><li>a nested item<ol><li>another nested item</li></ol>continued</li></ol></li><li>another item</li></ol>",
				"""
				# a list
				## a nested item
				### another nested item
				#: continued
				# another item""");
	}

	@Test
	public void testListOrderedWithContinuationToDocBook() throws IOException {
		StringWriter out = new StringWriter();
		parser.setBuilder(new DocBookDocumentBuilder(out));

		parser.parse("""
				# a list
				## a nested item
				### another nested item
				#: continued
				# another item""");

		String docbook = out.toString();

		// should look like this:
		//
		//		<orderedlist>
		//			<listitem>
		//				<para>a list</para>
		//				<orderedlist>
		//					<listitem>
		//						<para>a nested item</para>
		//						<orderedlist>
		//							<listitem>
		//								<para>another nested item</para>
		//							</listitem>
		//						</orderedlist>
		//						<para>continued</para>
		//					</listitem>
		//				</orderedlist>
		//			</listitem>
		//			<listitem>
		//				<para>another item</para>
		//			</listitem>
		//		</orderedlist>

		assertTrue(docbook.contains(
				"<orderedlist><listitem><para>a list</para><orderedlist><listitem><para>a nested item</para><orderedlist><listitem><para>another nested item</para></listitem></orderedlist><para>continued</para></listitem></orderedlist></listitem><listitem><para>another item</para></listitem></orderedlist>"));

	}

	@Test
	public void testListNested() throws IOException {
		assertMarkup("<ol><li>a list<ol><li>nested</li><li>nested2</li></ol></li><li>level1</li></ol><p>para</p>",
				"# a list\n## nested\n## nested2\n# level1\n\npara");
	}

	@Test
	public void testListMixed() throws IOException {
		// test for bug# 47
		assertMarkup("<ol><li>first</li></ol><ul><li>second</li></ul>", "# first\n* second");
	}

	@Test
	public void testListNestedMixed() throws IOException {
		assertMarkup("<ol><li>a list<ul><li>nested</li><li>nested2</li></ul></li><li>level1</li></ol><p>para</p>",
				"# a list\n#* nested\n#* nested2\n# level1\n\npara");
	}

	@Test
	public void testDefinitionList() {
		assertMarkup("<dl><dt>Definition</dt><dd>item1</dd><dd>item2</dd></dl><p>a para</p>",
				";Definition\n:item1\n:item2\na para");
	}

	@Test
	public void testDefinitionList2() {
		assertMarkup("<dl><dt>Definition</dt><dd>item1</dd><dd>item2</dd></dl><p>a para</p>",
				";Definition : item1\n:item2\na para");
	}

	@Test
	public void testDefinitionList3() {
		assertMarkup(
				"<dl><dt>Definition <a href=\"http://www.foobar.com\">Foo Bar</a></dt><dd>Foo Bar test 123</dd><dt>Definition 2 <a href=\"http://www.foobarbaz.com\">Foo Bar Baz</a></dt><dd>another definition</dd></dl><p>a para</p>",
				";Definition [http://www.foobar.com Foo Bar] : Foo Bar test 123\n;Definition 2 [http://www.foobarbaz.com Foo Bar Baz] : another definition\na para");
	}

	@Test
	public void testIndented() {
		assertMarkup("<dl><dd><dl><dd>Indented</dd></dl></dd></dl><p>a para</p>", "::Indented\na para");
	}

	@Test
	public void testPreformatted() {
		assertMarkup("<p>normal para</p><pre>preformatted\nmore pre\n</pre><p>normal para</p>",
				"normal para\n preformatted\n more pre\nnormal para");
	}

	@Test
	public void testPreformattedWithTag() {
		assertMarkup(
				"<p>normal para</p><pre class=\"TEST\" style=\"overflow:scroll\">preformatted\n more pre\n</pre><p>normal para</p>",
				"normal para\n<pre style=\"overflow:scroll\" class=\"TEST\">preformatted\n more pre\n</pre>normal para");
	}

	@Test
	public void testPreformattedWithTagStartEndOnSameLine() {
		assertMarkup("<p>normal para</p><pre>preformatted\n</pre><p>normal para</p>",
				"normal para\n<pre>preformatted</pre>normal para");
	}

	@Test
	public void testPreformattedWithTagStartEndOnSameLine3() {
		assertMarkup("<p>normal para</p><pre>preformatted\n</pre><p>normal para</p>",
				"normal para\n<pre>preformatted</pre>\nnormal para");
	}

	@Test
	public void testPreformattedWithTagStartEndOnSameLine2() {
		//see also BUG 381506 for the usage of tags:
		assertMarkup(
				"<p>example:</p><pre>&lt;a href=\"show_bug.cgi\\?id\\=(.+?)\"&gt;.+?&lt;span class=\"summary\"&gt;(.+?)&lt;/span&gt;\n</pre><p>If</p>",
				"example:\n\n<pre><a href=\"show_bug.cgi\\?id\\=(.+?)\">.+?<span class=\"summary\">(.+?)</span></pre>\n\nIf");
	}

	@Test
	public void testPreformattedSource_bug349724() {
		assertMarkup(
				"<p>normal para</p><pre class=\"source-javascript\">preformatted\n more pre\n\n</pre><p>normal para</p>",
				"normal para\n<source lang=\"javascript\">preformatted\n more pre\n</source>normal para");
	}

	@Test
	public void testPreformattedWithTagAndMarkup() {
		//BUG 381506:
		assertMarkup(
				"<p>example:</p><pre>a block\nWith '''Bold text''' or ''Italic text'' style\nIs not converted\n</pre><p>If</p>",
				"example:\n\n<pre>a block\nWith '''Bold text''' or ''Italic text'' style\nIs not converted</pre>\n\nIf");
	}

	@Test
	public void testPreformattedWithMarkup() {
		//BUG 381506:
		assertMarkup(
				"<p>normal para</p><pre>preformatted\nwith <b>Bold text</b> or <i>Italic text</i> style\nmore pre\n</pre><p>normal para</p>",
				"normal para\n preformatted\n with '''Bold text''' or ''Italic text'' style\n more pre\nnormal para");
	}

	@Test
	public void testPreformattedWithFont() {
		//BUG 381506:
		assertMarkup(
				"<p>normal para</p><pre>preformatted\nwith <font color=\"red\">some red color</font>\nmore pre\n</pre><p>normal para</p>",
				"normal para\n preformatted\n with <font color=\"red\">some red color</font>\n more pre\nnormal para");
	}

	@Test
	public void testHtmlTags() {
		assertMarkup("<p>normal para <b id=\"foo\">test heading</b></p>", "normal para <b id=\"foo\">test heading</b>");
	}

	@Test
	public void testHtmlComment() {
		assertMarkup("<p>normal para  normal *foo*</p>", "normal para <!-- test comment --> normal *foo*");
	}

	@Test
	public void testHtmlCommentEmpty() {
		assertMarkup("<p>normal para  normal *foo*</p>", "normal para <!-- --> normal *foo*");
	}

	@Test
	public void testHtmlCommentOnSameLineAsAnotherCommentWhitespaceSeparator() {
		assertMarkup("<p>normal para   normal</p>", "normal para <!-- --> <!-- --> normal");
	}

	@Test
	public void testHtmlCommentOnSameLineAsAnotherComment() {
		assertMarkup("<p>normal para b normal</p>", "normal para <!-- -->b<!-- --> normal");
	}

	@Test
	public void testHtmlCodeWithNestedFormatting() {
		// bug 325023
		assertMarkup("<p><code>NonItalic=<i>Italic</i></code></p>", "<code>NonItalic=''Italic''</code>");
	}

	@Test
	public void testLinkInternalPageReference() {
		assertMarkup(
				"<p>a <a href=\"/wiki/Main_Page\" title=\"Main Page\">Main Page</a> reference to the Main Page</p>",
				"a [[Main Page]] reference to the Main Page");
	}

	@Test
	public void testLinkInternalPageAnchorReference() {
		assertMarkup("<p>a <a href=\"#Some_link\">alt text</a> reference to an internal anchor</p>",
				"a [[#Some link|alt text]] reference to an internal anchor");
	}

	@Test
	public void testLinkInternalPageReferenceWithAltText() {
		assertMarkup(
				"<p>a <a href=\"/wiki/Main_Page\" title=\"Main Page\">alternative text</a> reference to the Main Page</p>",
				"a [[Main Page|alternative text]] reference to the Main Page");
	}

	@Test
	public void testLinkInternalPageReferenceWithAltText2() {
		assertMarkup(
				"<p><a href=\"/wiki/Orion/Server_API/Preference_API\" title=\"Orion/Server_API/Preference API\">Preference API</a></p>",
				"[[Orion/Server_API/Preference API| Preference API]]");
	}

	@Test
	public void testLinkInternalPageReferenceWithAltTextAndAnchor() {
		//Bug 388657
		assertMarkup(
				"<p>a <a href=\"/wiki/Main_Page#Anchor_Text.3F\" title=\"Main Page#Anchor Text?\">text of the link</a> reference to the Main Page</p>",
				"a [[Main Page#Anchor Text?|text of the link]] reference to the Main Page");
	}

	@Test
	public void testLinkInternalPageReferenceWithAltTextAndAnchor2() {
		//Bug 388657
		assertMarkup(
				"<p>Go to <a href=\"/wiki/This!page#with_anchor.21\" title=\"This!page#with anchor!\">this page</a> to have an example</p>",
				"Go to [[This!page#with anchor!|this page]] to have an example");
	}

	@Test
	public void testLinkInternalPageReferenceWithAltTextInTables() {
		assertMarkup(
				"<table><tr><td><a href=\"/wiki/Orion/Server_API/Preference_API\" title=\"Orion/Server_API/Preference API\">Preference API</a></td></tr></table>",
				"""
				{|
				| [[Orion/Server_API/Preference API| Preference API]]
				|}""");
	}

	@Test
	public void testLinkInternalCategoryReference() {
		assertMarkup(
				"<p>a <a href=\"Category:Help\" title=\"Category:Help\">Category:Help</a> reference to the Main Page</p>",
				"a [[:Category:Help]] reference to the Main Page");
	}

	@Test
	public void testHyperlinkImplied() {
		assertMarkup("<p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p>",
				"a http://example.com hyperlink");
	}

	@Test
	public void testHyperlinkInternal() {
		assertMarkup(
				"<p>Also see the <a href=\"/wiki/Mylyn_FAQ#Installation_Troubleshooting\" title=\"Mylyn_FAQ#Installation_Troubleshooting\">Installation FAQ</a>.</p>",
				"Also see the [[Mylyn_FAQ#Installation_Troubleshooting | Installation FAQ]].");
	}

	@Test
	public void testHyperlinkQualifiedInternal() {
		markupLanguage.setInternalLinkPattern("http://wiki.eclipse.org/Mylyn/{0}");
		assertMarkup(
				"<p>Also see the <a href=\"http://wiki.eclipse.org/Mylyn/FAQ#Installation_Troubleshooting\" title=\"Mylyn/FAQ#Installation_Troubleshooting\">Installation FAQ</a>.</p>",
				"Also see the [[Mylyn/FAQ#Installation_Troubleshooting | Installation FAQ]].");
	}

	@Test
	public void testHyperlinkInternalPiped() {
		assertMarkup(
				"<p><a href=\"/wiki/MoDisco/QueryManager\" title=\"MoDisco/QueryManager\">create a query set</a></p>",
				"[[MoDisco/QueryManager|create a query set]]");
	}

	@Test
	public void testHyperlinkInternalWithSpaces() {
		markupLanguage.setInternalLinkPattern("http://wiki.eclipse.org/{0}");
		assertMarkup(
				"<p>Also see the <a href=\"http://wiki.eclipse.org/Mylyn/User_Guide\" title=\"Mylyn/User Guide\">Mylyn/User Guide</a>.</p>",
				"Also see the [[Mylyn/User Guide]].");
	}

	@Test
	public void testNotHyperlinkExternal() {
		//Bug 473109
		assertMarkup("<p>this [x] is not a link</p>", "this [x] is not a link");
		assertMarkup("<p>Use another style [Human]</p>", "Use another style [Human]");
		assertMarkup("<p>[test1://eclipse.org/] is not a link</p>", "[test1://eclipse.org/] is not a link");
		assertMarkup("<p>No link [test:me]</p>", "No link [test:me]");
	}

	@Test
	public void testHyperlinkExternal() {
		assertMarkup("<p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p>",
				"a [http://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"https://example.com\">https://example.com</a> hyperlink</p>",
				"a [https://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"mailto:noreply@eclipse.org\">mailto:noreply@eclipse.org</a> hyperlink</p>",
				"a [mailto:noreply@eclipse.org] hyperlink");
		assertMarkup("<p>a <a href=\"gopher://example.com\">gopher://example.com</a> hyperlink</p>",
				"a [gopher://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"news://example.com\">news://example.com</a> hyperlink</p>",
				"a [news://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"ftp://example.com\">ftp://example.com</a> hyperlink</p>",
				"a [ftp://example.com] hyperlink");
		assertMarkup("<p>a <a href=\"irc://example.com\">irc://example.com</a> hyperlink</p>",
				"a [irc://example.com] hyperlink");
		assertMarkup("<p><a href=\"shh://eclipse.org/\">shh://eclipse.org/</a></p>", "[shh://eclipse.org/]");
	}

	@Test
	public void testHyperlinkExternalWithAltText() {
		assertMarkup("<p>a <a href=\"http://example.com\">Example</a> hyperlink</p>",
				"a [http://example.com|Example] hyperlink");
	}

	@Test
	public void testHyperlinkExternalWithAltText2() {
		assertMarkup("<p>a <a href=\"http://example.com\">Example Title</a> hyperlink</p>",
				"a [http://example.com Example Title] hyperlink");
		assertMarkup("<p>a <a href=\"https://example.com\">Example Title</a> hyperlink</p>",
				"a [https://example.com Example Title] hyperlink");
		assertMarkup("<p>a <a href=\"mailto:noreply@eclipse.org\">Email</a> hyperlink</p>",
				"a [mailto:noreply@eclipse.org Email] hyperlink");
		assertMarkup("<p>a <a href=\"gopher://example.com\">Example Title</a> hyperlink</p>",
				"a [gopher://example.com Example Title] hyperlink");
		assertMarkup("<p>a <a href=\"news://example.com\">Example Title</a> hyperlink</p>",
				"a [news://example.com Example Title] hyperlink");
		assertMarkup("<p>a <a href=\"ftp://example.com\">Example Title</a> hyperlink</p>",
				"a [ftp://example.com Example Title] hyperlink");
		assertMarkup("<p>a <a href=\"irc://example.com\">Example Title</a> hyperlink</p>",
				"a [irc://example.com Example Title] hyperlink");
		assertMarkup(
				"<p>git clone on <a href=\"https://git.eclipse.org/r/mylyn/org.eclipse.mylyn.docs.git\">repo</a></p>",
				"git clone on [https://git.eclipse.org/r/mylyn/org.eclipse.mylyn.docs.git repo]");
	}

	@Test
	public void testImage() {
		assertMarkup("<p>a <img border=\"0\" src=\"foo.png\"/> image</p>", "a [[Image:foo.png]] image");
	}

	@Test
	public void testImageWithAltText() {
		assertMarkup("<p>a <img title=\"Example\" alt=\"Example\" border=\"0\" src=\"foo.png\"/> image</p>",
				"a [[Image:foo.png|Example]] image");
	}

	@Test
	public void testImageWithAltText2() {
		assertMarkup("<p>a <img alt=\"Alt Text\" title=\"Caption\" border=\"0\" src=\"foo.png\"/> image</p>",
				"a [[Image:foo.png|Alt Text|Caption]] image");
	}

	@Test
	public void testImageWithAltTextAndOptions() {
		assertMarkup(
				"<p>a <img width=\"100\" align=\"middle\" title=\"Example\" alt=\"Example\" border=\"0\" src=\"foo.png\"/> image</p>",
				"a [[Image:foo.png|100px|center|Example]] image");
	}

	@Test
	public void testImageWithAltTextAndHeightWidth() {
		assertMarkup("<p>a <img height=\"220\" width=\"100\" border=\"0\" src=\"foo.png\"/> image</p>",
				"a [[Image:foo.png|100x220px]] image");
	}

	@Test
	public void testImageWithAltTextAndWidth() {
		assertMarkup("<p>a <img width=\"100\" border=\"0\" src=\"foo.png\"/> image</p>",
				"a [[Image:foo.png|100px]] image");
	}

	@Test
	public void testImageWithLinkInCaption() {
		// example from http://en.wikipedia.org/wiki/International_Floorball_Federation
		assertMarkup(
				"<p><img align=\"left\" alt=\"the logo\" title=\"Official logo of the [[International Floorball Federation]], floorball&apos;s governing body.\" border=\"0\" src=\"IFF_Logo.JPG\"/></p>",
				"[[Image:IFF Logo.JPG|left|the logo|Official logo of the [[International Floorball Federation]], floorball's governing body.]]");
	}

	@Test
	public void testImageWithLinkInCaptionThumbnail() {
		// example from http://en.wikipedia.org/wiki/International_Floorball_Federation
		assertMarkup(
				"<p><div class=\"thumb left\"><div class=\"thumbinner\"><a href=\"IFF_Logo.JPG\" class=\"image\"><img class=\"thumbimage\" align=\"left\" alt=\"the logo\" border=\"0\" src=\"IFF_Logo.JPG\"/></a><div class=\"thumbcaption\">Official logo of the <a href=\"/wiki/International_Floorball_Federation\" title=\"International Floorball Federation\">International Floorball Federation</a>, floorball's governing body.</div></div></div></p>",
				"[[Image:IFF Logo.JPG|thumb|left|the logo|Official logo of the [[International Floorball Federation]], floorball's governing body.]]");
	}

	@Test
	public void testImageWithTitle() {
		assertMarkup(
				"<p>text text text text text text\n<img width=\"150\" alt=\"A large clock tower and other buildings line a great river.\" title=\"The Palace of Westminster\" border=\"0\" src=\"Westminstpalace.jpg\"/></p>",
				"text text text text text text\n[[Image:Westminstpalace.jpg|150px|alt=A large clock tower and other buildings line a great river.|The Palace of Westminster]]");
	}

	@Test
	public void testImageSimple() {
		assertMarkup("<p><img border=\"0\" src=\"ImportFedoraGit.png\"/></p>", "[[Image:ImportFedoraGit.png]]");
	}

	@Test
	public void testImageWithLeadingWhitespace() {
		assertMarkup("<p><img border=\"0\" src=\"SomeImage.png\"/></p>", "[[Image: SomeImage.png]]");
	}

	@Test
	public void testImageFile() {
		assertMarkup("<p>a <img border=\"0\" src=\"foo.png\"/> image</p>", "a [[File:foo.png]] image");
	}

	@Test
	public void testImageFile_Negative() {
		assertMarkup(
				"<p>a <a href=\"/wiki/FilImage:foo.png\" title=\"FilImage:foo.png\">FilImage:foo.png</a> image</p>",
				"a [[FilImage:foo.png]] image");
	}

	@Test
	public void testImage_Lower() {
		assertMarkup("<p>a <img border=\"0\" src=\"foo.png\"/> image</p>", "a [[image:foo.png]] image");
	}

	@Test
	public void testImageFile_Lower() {
		assertMarkup("<p>a <img border=\"0\" src=\"foo.png\"/> image</p>", "a [[file:foo.png]] image");
	}

	@Test
	public void testTable() {
		assertMarkup(
				"<table><tr><td>Orange</td><td>Apple</td></tr><tr><td>Bread</td><td>Pie</td></tr><tr><td>Butter</td><td>Ice cream </td></tr></table>",
				"""
				{|
				|Orange
				|Apple
				|-
				|Bread
				|Pie
				|-
				|Butter
				|Ice cream\s
				|}""");
	}

	@Test
	public void testTable2() {
		assertMarkup(
				"<table><tr><td>Orange</td><td>Apple</td><td>more</td></tr><tr><td>Bread</td><td>Pie</td><td>more</td></tr><tr><td>Butter</td><td>Ice cream</td><td>and more</td></tr></table>",
				"""
				{|
				|  Orange    ||   Apple   ||   more
				|-
				|   Bread    ||   Pie     ||   more
				|-
				|   Butter   || Ice cream ||  and more
				|}
				""");
	}

	@Test
	public void testTableWithBlankLine() {
		assertMarkup(
				"<table><tr><td>Orange</td><td>Apple</td></tr><tr><td>Bread<p>More bread</p></td><td>Pie</td></tr><tr><td>Butter</td><td>Ice cream </td></tr></table>",
				"""
				{|
				|Orange
				|Apple
				|-
				|Bread

				More bread
				|Pie
				|-
				|Butter
				|Ice cream\s
				|}""");
	}

	@Test
	public void testTableHeadings() {
		assertMarkup(
				"<table><tr><th>Fruit</th><th>Quantity</th><th>Price</th></tr><tr><td>Apple</td><td>lb</td><td>0.99</td></tr></table>",
				"""
				{|
				!  Fruit    !!   Quantity   !!  Price
				|-
				|   Apple    ||   lb     ||   0.99
				|}
				""");
	}

	@Test
	public void testTableHeadingsMixed() {
		assertMarkup(
				"<table><tr><th>headerCell</th><td>normalCell</td></tr><tr><td>normalCell2</td><th>headerCell2</th></tr></table>",
				"{|\n! headerCell || normalCell\n|-\n| normalCell2 !! headerCell2\n|}");
	}

	@Test
	public void testTableLexicalOffsets() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = """
				{|
				|  Orange    ||   Apple   ||   more
				|-
				|   Bread    ||   Pie     ||   more
				|-
				|   Butter   || Ice cream ||  and more
				|}
				""";

		parser.parse(content);

		for (RecordingDocumentBuilder.Event event : builder.getEvents()) {
			if (event.text != null) {
				int start = event.locator.getDocumentOffset();
				int end = event.locator.getLineSegmentEndOffset() + event.locator.getLineDocumentOffset();
				assertEquals(event.text.length(), end - start);
				assertTrue(end >= start);
				assertEquals(content.substring(start, end), event.text);
			}
		}

	}

	@Test
	public void testTableIncomplete() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = """
				{|
				|  Orange    ||   Apple   ||   more
				|-
				|   Bread    ||   Pie     ||   more
				|-
				|   Butter   || Ice cream ||  and more
				|\s
				""";

		parser.parse(content);

		for (RecordingDocumentBuilder.Event event : builder.getEvents()) {
			if (event.text != null) {
				int start = event.locator.getDocumentOffset();
				int end = event.locator.getLineSegmentEndOffset() + event.locator.getLineDocumentOffset();
				assertEquals(event.text.length(), end - start);
				assertTrue(end >= start);
				assertEquals(content.substring(start, end), event.text);
			}
		}

	}

	@Test
	public void testTableIncomplete2() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = """
				{|
				| foo |
				|}""";

		parser.parse(content);

		for (RecordingDocumentBuilder.Event event : builder.getEvents()) {
			if (event.text != null) {
				int start = event.locator.getDocumentOffset();
				int end = event.locator.getLineSegmentEndOffset() + event.locator.getLineDocumentOffset();
				assertEquals(event.text.length(), end - start);
				assertTrue(end >= start);
				assertEquals(content.substring(start, end), event.text);
			}
		}
	}

	@Test
	public void testTableWithSyntax() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		String content = """
				{|
				| <nowiki>'''''bold italic'''''</nowiki> || '''''bold italic''''' ||
				|}""";

		parser.parse(content);

		for (RecordingDocumentBuilder.Event event : builder.getEvents()) {
			if (event.text != null) {
				int start = event.locator.getDocumentOffset();
				int end = event.locator.getLineSegmentEndOffset() + event.locator.getLineDocumentOffset();

				assertTrue(end >= start);

			}
		}
	}

	@Test
	public void testTableOptions() {
		String html = parser.parseToHtml("""
				{| border="1"
				|- style="font-style:italic;color:green;"
				| colspan="2" | Orange || valign="top" | Apple
				|}""");

		assertTrue(html.contains("<table border=\"1\"><tr style=\"font-style:italic;color:green;\">"));
		assertTrue(html.contains("<td colspan=\"2\">Orange</td>"));
		assertTrue(html.contains("<td valign=\"top\">Apple</td>"));
	}

	@Test
	public void testTableOptions_CssClass() {
		assertMarkup("<table class=\"foo\"><tr><td>Some text</td></tr></table>", "{|class=\"foo\"\n|Some text\n|}");
	}

	@Test
	public void testTableWithParagraphs() {
		//BUG 381912:
		StringBuilder sb = new StringBuilder();
		sb.append("{|border=\"1\"\n");
		sb.append("|\n");
		sb.append("A paragraph with '''Bold text''' in a cell.\n");
		sb.append("|\n");
		sb.append("A cell ''containing'' more...\n");
		sb.append("\n");
		sb.append("Than one paragraph.\n");
		sb.append("|}\n");

		String html = parser.parseToHtml(sb.toString());

		Pattern pattern = Pattern.compile(
				"<table border=\"1\">\\s*<tr>\\s*<td>\\s*<p>\\s*A paragraph with \\s*<b>\\s*Bold text\\s*</b>\\s* in a cell.\\s*</p>\\s*</td>\\s*<td>\\s*<p>\\s*A cell \\s*<i>\\s*containing\\s*</i>\\s* more...\\s*</p>\\s*<p>\\s*Than one paragraph.\\s*</p>\\s*</td>\\s*</tr>\\s*</table>");
		assertContainsPattern(html, pattern);
	}

	@Test
	public void testTableWithLongerText() {
		//BUG 381912:
		//See: http://www.mediawiki.org/wiki/Help:Tables "longer text and more complex wiki syntax inside table cells".
		StringBuilder sb = new StringBuilder();
		sb.append("{|border=\"1\"\n");
		sb.append("|Sxto mesto kusoks ti sam, \n");
		sb.append("Da skandalis studentis bezopasostif tut, \n");
		sb.append("dost takai vcxera na mne\n");
		sb.append("Mai na zxen problem zembulbas, \n");
		sb.append("dost vozduh dusxijm kai te. \n");
		sb.append("\n");
		sb.append("Oliv slozxju informacias bi bez\n");
		sb.append("om gde detes komnat,\n");
		sb.append("To divaj neskolk pridijt ili\n");
		sb.append("Ktor zapalka bezopasostif es tot. \n");
		sb.append("|\n");
		sb.append("* Sxto mesto kusoks ti sam\n");
		sb.append("* Vi edat zaspatit zapomnitlubovijm sol\n");
		sb.append("* dost takai vcxera na mne\n");
		sb.append("|}\n");

		String html = parser.parseToHtml(sb.toString());

		Pattern pattern = Pattern.compile(
				"<table border=\"1\">\\s*<tr>\\s*<td>\\s*Sxto mesto kusoks ti sam,\\s*<p>\\s*Da skandalis studentis bezopasostif tut,\\s+dost takai vcxera na mne\\s+Mai na zxen problem zembulbas,\\s+dost vozduh dusxijm kai te.\\s*</p>\\s*<p>\\s*Oliv slozxju informacias bi bez\\s*om gde detes komnat,\\s*To divaj neskolk pridijt ili\\s*Ktor zapalka bezopasostif es tot.\\s*</p>\\s*</td>\\s*<td>\\s*<ul>\\s*<li>\\s*Sxto mesto kusoks ti sam\\s*</li>\\s*<li>\\s*Vi edat zaspatit zapomnitlubovijm sol\\s*</li>\\s*<li>\\s*dost takai vcxera na mne\\s*</li>\\s*</ul>\\s*</td>\\s*</tr>\\s*</table>");
		assertContainsPattern(html, pattern);
	}

	@Test
	public void testTableWithCodeInCellAndOptions() {
		//BUG 381912:
		StringBuilder sb = new StringBuilder();
		sb.append("{|border=\"1\"\n");
		sb.append("|\n");
		sb.append("  some\n");
		sb.append("|\n");
		sb.append("  code\n");
		sb.append("  multiline\n");
		sb.append("|style=\"background-color:#FFFF00;\"|\n");
		sb.append("  this is code in an highlighted cell\n");
		sb.append("|}\n");

		String html = parser.parseToHtml(sb.toString());

		Pattern pattern = Pattern.compile(
				"<table border=\"1\">\\s*<tr>\\s*<td>\\s*<pre>\\s*some\n</pre>\\s*</td>\\s*<td>\\s*<pre>\\s*code\n multiline\n</pre>\\s*</td>\\s*<td style=\"background-color:#FFFF00;\">\\s*<pre>\\s*this is code in an highlighted cell\n</pre>\\s*</td>\\s*</tr>\\s*</table>");
		assertContainsPattern(html, pattern);
	}

	@Test
	public void testTableWithExplicitFirstRowAndRowSpan() {
		//BUG 381912:
		StringBuilder sb = new StringBuilder();
		sb.append("{|border=\"1\"\n");
		sb.append("|-\n");
		sb.append("!colspan=\"6\"|XYZ uv\n");
		sb.append("|-\n");
		sb.append("|rowspan=\"2\"|X1 & X2\n");
		sb.append("|y1\n");
		sb.append("|y2\n");
		sb.append("|y3\n");
		sb.append("|colspan=\"2\"|Z9\n");
		sb.append("|-\n");
		sb.append("|z8\n");
		sb.append("|colspan=\"2\"|T6\n");
		sb.append("|u4\n");
		sb.append("|U6\n");
		sb.append("|}\n");

		String html = parser.parseToHtml(sb.toString());

		Pattern pattern = Pattern.compile(
				"<table border=\"1\">\\s*<tr>\\s*<th colspan=\"6\">\\s*XYZ uv\\s*</th>\\s*</tr>\\s*<tr>\\s*<td rowspan=\"2\">\\s*X1 &amp; X2\\s*</td>\\s*<td>\\s*y1\\s*</td>\\s*<td>\\s*y2\\s*</td>\\s*<td>\\s*y3\\s*</td>\\s*<td colspan=\"2\">\\s*Z9\\s*</td>\\s*</tr>\\s*<tr>\\s*<td>\\s*z8\\s*</td>\\s*<td colspan=\"2\">\\s*T6\\s*</td>\\s*<td>\\s*u4\\s*</td>\\s*<td>\\s*U6\\s*</td>\\s*</tr>\\s*</table>");
		assertContainsPattern(html, pattern);
	}

	@Test
	public void testTableNested() {
		//BUG 304495:
		StringBuilder sb = new StringBuilder();
		sb.append("{|\n");
		sb.append("| f ||\n");
		sb.append("{| border=\"1\"\n");
		sb.append("| a\n");
		sb.append("| b\n");
		sb.append("|}\n");
		sb.append("| ,\n");
		sb.append("|\n");
		sb.append("{| border=\"1\"\n");
		sb.append("| c\n");
		sb.append("| d\n");
		sb.append("|}\n");
		sb.append("|}\n");

		String html = parser.parseToHtml(sb.toString());

		Pattern pattern = Pattern.compile(
				"<table>\\s*<tr>\\s*<td>\\s*f\\s*<table border=\"1\">\\s*<tr>\\s*<td>\\s*a\\s*</td>\\s*<td>\\s*b\\s*</td>\\s*</tr>\\s*</table>\\s*</td>\\s*<td>\\s*,\\s*</td>\\s*<td>\\s*<table border=\"1\">\\s*<tr>\\s*<td>\\s*c\\s*</td>\\s*<td>\\s*d\\s*</td>\\s*</tr>\\s*</table>\\s*</td>\\s*</tr>\\s*</table>");
		assertContainsPattern(html, pattern);
	}

	@Test
	public void testTableNestedMalformed() {
		//BUG 304495:
		StringBuilder sb = new StringBuilder();
		sb.append("{| \n");
		sb.append("| first table first cell\n");
		sb.append("{| \n");
		sb.append("| second table first cell\n");
		sb.append("|}\n");
		sb.append("| first table first cell\n");

		String html = parser.parseToHtml(sb.toString());

		String expected = "<table><tr><td>first table first cell<table><tr><td>second table first cell</td></tr></table></td><td>first table first cell</td></tr></table>";
		assertTrue(html.contains(expected));
	}

	@Test
	public void testTableNestedMultiple() {
		//BUG 304495:
		StringBuilder sb;
		sb = new StringBuilder();
		sb.append("{| style=\"background-color:red;\"\n");
		sb.append("! AAA !! AAAAAAAA !! AA\n");
		sb.append("|-\n");
		sb.append("| a\n");
		sb.append("| aaaaa\n");
		sb.append("| aaa\n");
		sb.append("{| style=\"background-color:green;\"\n");
		sb.append("! B \n");
		sb.append("| bbbb\n");
		sb.append("|-\n");
		sb.append("! BBB\n");
		sb.append("| bb\n");
		sb.append("|-\n");
		sb.append("! BBBBB\n");
		sb.append("| bb\n");
		sb.append("|}\n");
		sb.append("|-\n");
		sb.append("| aaaa\n");
		sb.append("{| style=\"background-color:yellow;\"\n");
		sb.append("! BBBBB !! BBB\n");
		sb.append("|-\n");
		sb.append("| bbbbb\n");
		sb.append("| bbb\n");
		sb.append("|-\n");
		sb.append("| bb\n");
		sb.append("{| style=\"background-color:blue;\"\n");
		sb.append("! CCC !! CCCCC !! CCCCC\n");
		sb.append("|-\n");
		sb.append("| cc\n");
		sb.append("| ccccc\n");
		sb.append("| ccc\n");
		sb.append("|-\n");
		sb.append("| c\n");
		sb.append("| cccc\n");
		sb.append("| ccc\n");
		sb.append("|}\n");
		sb.append("| bbbbb\n");
		sb.append("|-\n");
		sb.append("| bbbb\n");
		sb.append("| bb\n");
		sb.append("|}\n");
		sb.append("| aaaaaaa\n");
		sb.append("| aa\n");
		sb.append("|-\n");
		sb.append("| aaa\n");
		sb.append("| aaaaaaaa\n");
		sb.append("| aaaa\n");
		sb.append("|}\n");

		String html = parser.parseToHtml(sb.toString());

		String expected = "<table style=\"background-color:red;\"><tr><th>AAA</th><th>AAAAAAAA</th><th>AA</th></tr><tr><td>a</td><td>aaaaa</td><td>aaa<table style=\"background-color:green;\"><tr><th>B </th><td>bbbb</td></tr><tr><th>BBB</th><td>bb</td></tr><tr><th>BBBBB</th><td>bb</td></tr></table></td></tr><tr><td>aaaa<table style=\"background-color:yellow;\"><tr><th>BBBBB</th><th>BBB</th></tr><tr><td>bbbbb</td><td>bbb</td></tr><tr><td>bb<table style=\"background-color:blue;\"><tr><th>CCC</th><th>CCCCC</th><th>CCCCC</th></tr><tr><td>cc</td><td>ccccc</td><td>ccc</td></tr><tr><td>c</td><td>cccc</td><td>ccc</td></tr></table></td><td>bbbbb</td></tr><tr><td>bbbb</td><td>bb</td></tr></table></td><td>aaaaaaa</td><td>aa</td></tr><tr><td>aaa</td><td>aaaaaaaa</td><td>aaaa</td></tr></table>";
		assertTrue(html.contains(expected));
	}

	@Test
	public void testTableLeadingSpaces() {
		//BUG 396545:
		StringBuilder sb = new StringBuilder();
		sb.append("{| \n");
		sb.append(" ! lorem\n");
		sb.append(" ! ipsum\n");
		sb.append(" |-\n");
		sb.append(" | dolor\n");
		sb.append(" | amtis\n");
		sb.append(" |}\n");

		String html = parser.parseToHtml(sb.toString());

		String expected = "<table><tr><th>lorem</th><th>ipsum</th></tr><tr><td>dolor</td><td>amtis</td></tr></table>";
		assertTrue(html.contains(expected));
	}

	@Test
	public void testTableLeadingSpacesInContext() {
		//BUG 396545:
		StringBuilder sb = new StringBuilder();
		sb.append("aaa\n");
		sb.append("  {| border=\"1\" \n");
		sb.append(" ! other !! test !! table\n");
		sb.append("   |-\n");
		sb.append("     | with\n");
		sb.append("       | some\n");
		sb.append("         | cells\n");
		sb.append("|-\n");
		sb.append("  | and || a || line\n");
		sb.append(" |}\n");
		sb.append(" bbb");

		String html = parser.parseToHtml(sb.toString());

		String expected = "<p>aaa</p><table border=\"1\"><tr><th>other</th><th>test</th><th>table</th></tr><tr><td>with</td><td>some</td><td>cells</td></tr><tr><td>and</td><td>a</td><td>line</td></tr></table><pre>bbb\n</pre>";
		assertTrue(html.contains(expected));
	}

	@Test
	public void testTableLeadingSpacesNestedMalformed() {
		//BUG 396545:
		StringBuilder sb = new StringBuilder();
		sb.append("{| \n");
		sb.append(" | first table first cell\n");
		sb.append("{| \n");
		sb.append(" | second table first cell\n");
		sb.append(" |}\n");
		sb.append(" | first table first cell\n");

		String html = parser.parseToHtml(sb.toString());

		String expected = "<table><tr><td>first table first cell<table><tr><td>second table first cell</td></tr></table></td><td>first table first cell</td></tr></table>";
		assertTrue(html.contains(expected));
	}

	@Test
	public void testEntityReference() {
		String tests = "&Agrave; &Aacute; &Acirc; &Atilde; &Auml; &Aring; &AElig; &Ccedil; &Egrave; &Eacute; &Ecirc; &Euml; &Igrave; &Iacute; &Icirc; &Iuml; &Ntilde; &Ograve; &Oacute; &Ocirc; &Otilde; &Ouml; &Oslash; &Ugrave; &Uacute; &Ucirc; &Uuml; &szlig; &agrave; &aacute; &acirc; &atilde; &auml; &aring; &aelig; &ccedil; &egrave; &eacute; &ecirc; &euml; &igrave; &iacute; &icirc; &iuml; &ntilde; &ograve; &oacute; &ocirc; &oelig; &otilde; &ouml; &oslash; &ugrave; &uacute; &ucirc; &uuml; &yuml; &iquest; &iexcl; &sect; &para; &dagger; &Dagger; &bull; &ndash; &mdash; &lsaquo; &rsaquo; &laquo; &raquo; &lsquo; &rsquo; &ldquo; &rdquo; &trade; &copy; &reg; &cent; &euro; &yen; &pound; &curren; &#8304; &sup1; &sup2; &sup3; &#8308; &int; &sum; &prod; &radic; &minus; &plusmn; &infin; &asymp; &prop; &equiv; &ne; &le; &ge; &times; &middot; &divide; &part; &prime; &Prime; &nabla; &permil; &deg; &there4; &alefsym; &oslash; &isin; &notin; &cap; &cup; &sub; &sup; &sube; &supe; &not; &and; &or; &exist; &forall;  &rArr; &lArr; &dArr; &uArr; &hArr; &rarr; &darr; &uarr; &larr; &harr; &mdash; &ndash;";
		final String[] allEntities = tests.split("\\s+");
		assertTrue(allEntities.length > 100);
		for (String testEntity : allEntities) {
			// sanity check
			assertTrue(testEntity.startsWith("&"));
			assertTrue(testEntity.endsWith(";"));

			String html = parser.parseToHtml(testEntity);
			assertTrue(testEntity + " in " + html, html.contains(testEntity));
			html = parser.parseToHtml(testEntity + " trailing text");
			assertTrue(testEntity + " in " + html, html.contains(testEntity));
			html = parser.parseToHtml(testEntity + "trailing text");
			assertTrue(testEntity + " in " + html, html.contains(testEntity));
			html = parser.parseToHtml("leading text " + testEntity);
			assertTrue(testEntity + " in " + html, html.contains(testEntity));
			html = parser.parseToHtml("leading text" + testEntity);
			assertTrue(testEntity + " in " + html, html.contains(testEntity));
		}
	}

	@Test
	public void testTemplateEnDash() {
		// note: spacing is very specific
		assertMarkup("<p>A&nbsp;&ndash; B</p>", "A{{ndash}}B");
		assertMarkup("<p>A&nbsp;&ndash; B</p>", "A{{endash}}B");
	}

	@Test
	public void testTemplateEmDash() {
		// note: spacing is very specific
		assertMarkup("<p>A&nbsp;&mdash; B</p>", "A{{mdash}}B");
		assertMarkup("<p>A&nbsp;&mdash; B</p>", "A{{emdash}}B");
	}

	@Test
	public void testTemplateCurrentMonth() {
		String html = parser.parseToHtml("{{CURRENTMONTH}}");
		assertContainsPattern(html, Pattern.compile("<p>[01]\\d</p>"));
	}

	@Test
	public void testTemplateCurrentMonthName() {
		String html = parser.parseToHtml("{{CURRENTMONTHNAME}}");
		assertContainsPattern(html, Pattern.compile(
				"<p>(January|February|March|April|May|June|July|August|September|October|November|December)</p>"));
	}

	@Test
	public void testTemplateCurrentMonthNameAbbrev() {
		String html = parser.parseToHtml("{{CURRENTMONTHABBREV}}");
		assertContainsPattern(html, Pattern.compile("<p>(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)</p>"));
	}

	@Test
	public void testTemplateCurrentDay() {
		String html = parser.parseToHtml("{{CURRENTDAY}}");
		assertContainsPattern(html, Pattern.compile("<p>[0123]\\d</p>"));
	}

	@Test
	public void testTemplateCurrentDOW() {
		String html = parser.parseToHtml("{{CURRENTDOW}}");
		assertContainsPattern(html, Pattern.compile("<p>\\d</p>"));
	}

	@Test
	public void testTemplateCurrentDayName() {
		String html = parser.parseToHtml("{{CURRENTDAYNAME}}");
		assertContainsPattern(html,
				Pattern.compile("<p>(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)</p>"));
	}

	@Test
	public void testTemplateCurrentTime() {
		String html = parser.parseToHtml("{{CURRENTTIME}}");
		assertContainsPattern(html, Pattern.compile("<p>[012]\\d:[0-5]\\d</p>"));
	}

	@Test
	public void testTemplateCurrentHour() {
		String html = parser.parseToHtml("{{CURRENTHOUR}}");
		assertContainsPattern(html, Pattern.compile("<p>[012]\\d</p>"));
	}

	@Test
	public void testTemplateCurrentWeek() {
		String html = parser.parseToHtml("{{CURRENTWEEK}}");
		assertContainsPattern(html, Pattern.compile("<p>[0-5]\\d</p>"));
	}

	@Test
	public void testTemplateUnmatched() {
		assertMarkup("<p>a</p>", "a{{ABogusTemplateName}}");
		assertMarkup("<p>a</p>", "a{{#foo}}");
	}

	@Test
	public void testTemplateCurrentTimestamp() {
		String html = parser.parseToHtml("{{CURRENTTIMESTAMP}}");
		assertContainsPattern(html, Pattern.compile("<p>\\d{14}</p>"));
	}

	@Test
	public void testDefinitionListIndenting() {
		assertMarkup("<dl><dd>one</dd><dd>two</dd></dl><dl><dd>three</dd></dl><p>four</p><dl><dd>five</dd></dl>",
				": one\n: two\n\n: three\nfour\n:five");
	}

	@Test
	public void testParagraphBreaksOnPreformatted() {
		assertMarkup("<p>a normal para</p><pre>preformatted\np\n</pre><p>normal</p>",
				"a normal para\n preformatted\n p\nnormal\n");
	}

	@Test
	public void testParagraphBreaksOnHeading() {
		assertMarkup("<p>a normal para</p><h1 id=\"h1\">h1</h1><p>normal</p>", "a normal para\n= h1 =\nnormal\n");
	}

	@Test
	public void testComputeOutline() throws IOException {
		OutlineParser outlineParser = new OutlineParser();
		outlineParser.setMarkupLanguage(new MediaWikiLanguage());

		OutlineItem outline = outlineParser.parse(readFully("sample.mediawiki"));

		Set<String> topLevelLabels = new LinkedHashSet<>();
		Set<String> topLevelIds = new LinkedHashSet<>();
		List<OutlineItem> children = outline.getChildren();
		for (OutlineItem item : children) {
			topLevelLabels.add(item.getLabel());
			topLevelIds.add(item.getId());
		}
		assertEquals(children.size(), topLevelIds.size());
		assertEquals(children.size(), topLevelLabels.size());
		assertTrue("Top-level labels: " + topLevelLabels, topLevelLabels.contains("Task-Focused UI"));
	}

	@Test
	public void testCloneTemplateExcludes() {
		markupLanguage.setTemplateExcludes("*foo");
		MediaWikiLanguage copy = (MediaWikiLanguage) markupLanguage.clone();
		assertEquals(markupLanguage.getTemplateExcludes(), copy.getTemplateExcludes());
	}

	@Test
	public void testTemplateExcludes() {
		// bug 367525
		markupLanguage.setTemplateExcludes("one, two, four_five");
		markupLanguage.setTemplates(Arrays.asList(new Template("one", "1"), new Template("two", "2"),
				new Template("three", "3"), new Template("four_five", "45")));
		assertMarkup("<p>a and  and 3 and </p>", "a{{one}} and {{two}} and {{three}} and {{four_five}}");
	}

	@Test
	public void testTemplateExcludesComplexNames() {
		//Bug 367525
		markupLanguage.setTemplateExcludes("#eclipseproject:technology.linux-distros");
		markupLanguage.setTemplates(
				Arrays.asList(new Template("#eclipseproject:technology.linux-distros", "! Not excluded - !")));
		assertMarkup("<p>foo  bar</p>", "foo {{#eclipseproject:technology.linux-distros}} bar");
	}

	@Test
	public void testTemplateExcludesRegEx() {
		//Bug 367525
		markupLanguage.setTemplateExcludes("*eclipseproject*, Linux_Tools");
		markupLanguage.setTemplates(Arrays.asList(new Template("Linux_Tools", "!Not excluded - Linux_Tools!"),
				new Template("#eclipseproject:technology.linux-distros", "!Not excluded - eclipseproject!")));
		assertMarkup("<p>foo  bar  baz</p>",
				"foo {{#eclipseproject:technology.linux-distros}} bar {{Linux_Tools}} baz");
	}

	@Test
	public void testTableOfContents() throws IOException {
		assertMarkup(
				"<h1 id=\"Table_Of_Contents\">Table Of Contents</h1><ol style=\"list-style: none;\"><li><a href=\"#Table_Of_Contents\">Table Of Contents</a></li><li><a href=\"#Top_Header\">Top Header</a><ol style=\"list-style: none;\"><li><a href=\"#Subhead\">Subhead</a></li><li><a href=\"#Subhead2\">Subhead2</a></li></ol></li><li><a href=\"#Top_Header_2\">Top Header 2</a><ol style=\"list-style: none;\"><li><a href=\"#Subhead_3\">Subhead 3</a><ol style=\"list-style: none;\"><li><a href=\"#Subhead_4\">Subhead 4</a></li></ol></li></ol></li></ol><h1 id=\"Top_Header\">Top Header</h1><p>some text</p><h2 id=\"Subhead\">Subhead</h2><h2 id=\"Subhead2\">Subhead2</h2><h1 id=\"Top_Header_2\">Top Header 2</h1><h2 id=\"Subhead_3\">Subhead 3</h2><h3 id=\"Subhead_4\">Subhead 4</h3>",
				"= Table Of Contents =\n\n__TOC__\n\n= Top Header =\n\nsome text\n\n== Subhead ==\n\n== Subhead2 ==\n\n= Top Header 2 =\n\n== Subhead 3 ==\n\n=== Subhead 4 ===");
	}

	@Test
	public void testTableOfContents_WithTextFollowingTOC() throws IOException {
		assertMarkup(
				"<h1 id=\"Table_Of_Contents\">Table Of Contents</h1><p>foo</p><ol style=\"list-style: none;\"><li><a href=\"#Table_Of_Contents\">Table Of Contents</a></li><li><a href=\"#Top_Header\">Top Header</a><ol style=\"list-style: none;\"><li><a href=\"#Subhead\">Subhead</a></li><li><a href=\"#Subhead2\">Subhead2</a></li></ol></li><li><a href=\"#Top_Header_2\">Top Header 2</a><ol style=\"list-style: none;\"><li><a href=\"#Subhead_3\">Subhead 3</a><ol style=\"list-style: none;\"><li><a href=\"#Subhead_4\">Subhead 4</a></li></ol></li></ol></li></ol><p>bar</p><h1 id=\"Top_Header\">Top Header</h1><p>some text</p><h2 id=\"Subhead\">Subhead</h2><h2 id=\"Subhead2\">Subhead2</h2><h1 id=\"Top_Header_2\">Top Header 2</h1><h2 id=\"Subhead_3\">Subhead 3</h2><h3 id=\"Subhead_4\">Subhead 4</h3>",
				"= Table Of Contents =\n\nfoo\n__TOC__ bar\n\n= Top Header =\n\nsome text\n\n== Subhead ==\n\n== Subhead2 ==\n\n= Top Header 2 =\n\n== Subhead 3 ==\n\n=== Subhead 4 ===");
	}

	@Test
	public void testComment_SingleLine() throws IOException {
		assertMarkup("", "<!-- comment -->");
	}

	@Test
	public void testComment_SingleLine_TrailingText() throws IOException {
		assertMarkup("<p> not a comment</p>", "<!-- comment --> not a comment");
	}

	@Test
	public void testComment_SingleLine_LeadingText() throws IOException {
		assertMarkup("<p>not a comment </p>", "not a comment <!-- comment -->");
	}

	@Test
	public void testComment_SingleLine_LeadingTrailingText() throws IOException {
		assertMarkup("<p>not a comment  more text</p>", "not a comment <!-- comment --> more text");
	}

	@Test
	public void testComment_SingleLine_MultipleBlocks() throws IOException {
		assertMarkup("<p>LoremIpsum</p>", "<!-- X -->Lorem<!-- Y -->Ipsum<!-- Z -->");
	}

	@Test
	public void testComment_SingleLine_MultipleBlocks_OnMultipleLines() throws IOException {
		assertMarkup("<p>LoremIpsum</p>", "<!-- X -->Lorem<!-- Y -->Ipsum\n<!-- Z -->");
	}

	@Test
	public void testComment_MultiLine() throws IOException {
		assertMarkup("", "<!-- comment\nwith\nMultiple lines of text -->\n");
	}

	@Test
	public void testComment_MultiLine_Multiple() throws IOException {
		assertMarkup("", "<!-- comment\nwith\nMultiple lines of text -->\n<!-- another comment -->");
	}

	@Test
	public void testComment_MultiLine_Multiple2() throws IOException {
		assertMarkup("<p>abc</p>", "<!-- comment\nwith\nMultiple lines of text -->abc<!-- another\ncomment -->");
	}

	@Test
	public void testComment_MultiLine_TrailingText() throws IOException {
		assertMarkup("<p> not a comment</p>", "<!-- comment\nwith\nMultiple lines of text --> not a comment");
	}

	@Test
	public void testComment_MultiLine_LeadingText() throws IOException {
		assertMarkup("<p>not a comment </p>", "not a comment <!-- comment\nwith\nMultiple lines of text -->");
	}

	@Test
	public void testComment_MultiLine_LeadingTrailingText() throws IOException {
		assertMarkup("<p>not a comment \n more text</p>",
				"not a comment <!-- comment\nwith\nMultiple lines of text --> more text");
	}

	@Test
	public void testImageFilenameCaseInsensitivity() {
		assertMarkup("<p><img border=\"0\" src=\"foo.gif\"/></p>", "[[Image:foo.gif]]");

		Set<String> imageNames = new HashSet<>();
		imageNames.add("Foo.gif");
		markupLanguage.setImageNames(imageNames);

		assertMarkup("<p><img border=\"0\" src=\"Foo.gif\"/></p>", "[[Image:foo.gif]]");
	}

	@Test
	public void testHeadingWithHtmlTags() {
		assertMarkup(
				"<h1 id=\"Heading_Text\"><span style=\"font-family:monospace\">Heading Text</span></h1><pre>text\n</pre>",
				"= <span style=\"font-family:monospace\">Heading Text</span> =\n\n text");
	}

	@Test
	public void testBehaviorSwitches() {
		//Bug 468609
		assertMarkup("", "__NOTOC__");
		assertMarkup("", "__FORCETOC__");
		assertMarkup("", "__NOEDITSECTION__");
		assertMarkup("", "__NEWSECTIONLINK__");
		assertMarkup("", "__NONEWSECTIONLINK__");
		assertMarkup("", "__NOGALLERY__");
		assertMarkup("", "__HIDDENCAT__");
		assertMarkup("", "__NOCONTENTCONVERT__");
		assertMarkup("", "__NOCC__");
		assertMarkup("", "__NOTITLECONVERT__");
		assertMarkup("", "__NOTC__");
		assertMarkup("", "__START__");
		assertMarkup("", "__END__");
		assertMarkup("", "__INDEX__");
		assertMarkup("", "__NOINDEX__");
		assertMarkup("", "__STATICREDIRECT__");
		assertMarkup("", "__DISAMBIG__");
	}

	@Test
	public void testBehaviorSwitchesWithText() {
		//Bug 468609
		assertMarkup("<p>Lorem ipsum</p><p>Ipsum Lorem</p>", "Lorem ipsum\n\n__NOTOC__\n\nIpsum Lorem");
	}

	private String readFully(String resource) throws IOException {
		return IOUtils.toString(MediaWikiLanguageTest.class.getResource(resource), StandardCharsets.UTF_8);
	}

	private void assertContainsPattern(String html, Pattern pattern) {
		if (!pattern.matcher(html).find()) {
			fail("Expected " + pattern + " but got " + html);
		}
	}
}
