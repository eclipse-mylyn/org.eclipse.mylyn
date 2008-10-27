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
package org.eclipse.mylyn.wikitext.mediawiki.core;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.RecordingDocumentBuilder;

/**
 * 
 * 
 * @author David Green
 */
public class MediaWikiLanguageTest extends TestCase {

	private MarkupParser parser;

	private MediaWikiLanguage markupLanaguage;

	@Override
	public void setUp() {
		markupLanaguage = new MediaWikiLanguage();
		parser = new MarkupParser(markupLanaguage);
	}

	public void testParagraph() {
		String html = parser.parseToHtml("first para<br/>\nfirst para line2\n\nsecond para\n\nthird para");
		System.out.println(html);
		assertTrue(Pattern.compile(
				"<body><p>first para<br/>\\s*first para line2</p><p>second para</p><p>third para</p></body>").matcher(
				html).find());
	}

	public void testNowiki() {
		String html = parser.parseToHtml("'''<nowiki>no <!-- markup here</nowiki>'''");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p><b>no &lt;!-- markup here</b></p></body>").matcher(html).find());
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

	public void testItalic() {
		String html = parser.parseToHtml("normal ''italic text'' normal");
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>normal <i>italic text</i> normal</p></body>").matcher(html).find());
	}

	public void testHeadings() {
		for (int x = 1; x <= 6; ++x) {
			String delimiter = repeat(x, "=");
			String html = parser.parseToHtml(delimiter + "heading text" + delimiter
					+ "\nfirst para<br/>\nfirst para line2\n\nsecond para\n\nthird para");
			System.out.println(html);
			assertTrue(Pattern.compile(
					"<body><h" + x + " id=\"[^\"]+\">heading text</h" + x
							+ "><p>first para<br/>\\s*first para line2</p><p>second para</p><p>third para</p></body>",
					Pattern.MULTILINE).matcher(html).find());
		}
	}

	public void testHeadingsWithPara() {
		String html = parser.parseToHtml("\n== H1 ==\n\npa\n\n=== H3 ===\n\nabc");
		System.out.println(html);
		assertTrue(html.contains("<body><h2 id=\"H1\">H1</h2><p>pa</p><h3 id=\"H3\">H3</h3><p>abc</p></body>"));
	}

	// FIXME: can paragraphs be interrupted by headings?
	//	public void testHeadingsWithPara2() {
	//		String html = parser.parseToHtml("== H1 ==\npa\n=== H3 ===\n\nabc");
	//		System.out.println(html);
	//		assertTrue(html.contains("<body><h2>H1</h2><p>pa</p><h3>H3</h3><p>abc</p></body>"));
	//	}

	private String repeat(int i, String string) {
		StringBuilder buf = new StringBuilder(string.length() * i);
		for (int x = 0; x < i; ++x) {
			buf.append(string);
		}
		return buf.toString();
	}

	public void testHorizontalRule() {
		String html = parser.parseToHtml("an hr ---- foo");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("hr <hr/> foo"));
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

	public void testListOrderedWithContinuation() throws IOException {
		String html = parser.parseToHtml("# a list\n" + "## a nested item\n" + "### another nested item\n"
				+ "#: continued\n" + "# another item");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><ol><li>a list<ol><li>a nested item<ol><li>another nested item</li></ol>continued</li></ol></li><li>another item</li></ol></body>"));

		// TODO: continuations on first level?
	}

	public void testListOrderedWithContinuationToDocBook() throws IOException {
		StringWriter out = new StringWriter();
		parser.setBuilder(new DocBookDocumentBuilder(out));

		parser.parse("# a list\n" + "## a nested item\n" + "### another nested item\n" + "#: continued\n"
				+ "# another item");

		String docbook = out.toString();

		System.out.println("DocBook: \n" + docbook);

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

		assertTrue(docbook.contains("<orderedlist><listitem><para>a list</para><orderedlist><listitem><para>a nested item</para><orderedlist><listitem><para>another nested item</para></listitem></orderedlist><para>continued</para></listitem></orderedlist></listitem><listitem><para>another item</para></listitem></orderedlist>"));

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

	public void testDefinitionList() {
		String html = parser.parseToHtml(";Definition\n:item1\n:item2\na para");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><dl><dt>Definition</dt><dd>item1</dd><dd>item2</dd></dl><p>a para</p></body>"));
	}

	public void testDefinitionList2() {
		String html = parser.parseToHtml(";Definition : item1\n:item2\na para");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><dl><dt>Definition</dt><dd>item1</dd><dd>item2</dd></dl><p>a para</p></body>"));
	}

	public void testDefinitionList3() {
		String html = parser.parseToHtml(";Definition [http://www.foobar.com Foo Bar] : Foo Bar test 123\n;Definition 2 [http://www.foobarbaz.com Foo Bar Baz] : another definition\na para");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("</head><body><dl><dt>Definition <a href=\"http://www.foobar.com\">Foo Bar</a></dt><dd>Foo Bar test 123</dd><dt>Definition 2 <a href=\"http://www.foobarbaz.com\">Foo Bar Baz</a></dt><dd>another definition</dd></dl><p>a para</p></body>"));
	}

	public void testIndented() {
		String html = parser.parseToHtml("::Indented\na para");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><dl><dd><dl><dd>Indented</dd></dl></dd></dl><p>a para</p></body>"));
	}

	public void testPreformatted() {
		String html = parser.parseToHtml("normal para\n preformatted\n more pre\nnormal para");
		System.out.println("HTML: \n" + html);
		assertTrue(Pattern.compile(
				"<body><p>normal para</p><pre>preformatted\\s+more pre\\s+</pre><p>normal para</p></body>").matcher(
				html).find());
	}

	public void testHtmlTags() {
		String html = parser.parseToHtml("normal para <b id=\"foo\">test heading</b>");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>normal para <b id=\"foo\">test heading</b></p>"));
	}

	public void testHtmlComment() {
		String html = parser.parseToHtml("normal para <!-- test comment --> normal *foo*");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>normal para <!-- test comment --> normal *foo*</p></body>"));
	}

	public void testLinkInternalPageReference() {
		String html = parser.parseToHtml("a [[Main Page]] reference to the Main Page");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"/wiki/Main_Page\" title=\"Main Page\">Main Page</a> reference to the Main Page</p></body>"));
	}

	public void testLinkInternalPageAnchorReference() {
		String html = parser.parseToHtml("a [[#Some link|alt text]] reference to an internal anchor");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"#Some_link\">alt text</a> reference to an internal anchor</p></body>"));
	}

	public void testLinkInternalPageReferenceWithAltText() {
		String html = parser.parseToHtml("a [[Main Page|alternative text]] reference to the Main Page");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"/wiki/Main_Page\" title=\"Main Page\">alternative text</a> reference to the Main Page</p></body>"));
	}

	public void testLinkInternalCategoryReference() {
		String html = parser.parseToHtml("a [[:Category:Help]] reference to the Main Page");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"Category:Help\" title=\"Category:Help\">Category:Help</a> reference to the Main Page</p></body>"));
	}

	public void testHyperlinkImplied() {
		String html = parser.parseToHtml("a http://example.com hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	public void testHyperlinkInternal() {
		String html = parser.parseToHtml("Also see the [[Mylyn_FAQ#Installation_Troubleshooting | Installation FAQ]].");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>Also see the <a href=\"/wiki/Mylyn_FAQ#Installation_Troubleshooting\" title=\"Mylyn_FAQ#Installation_Troubleshooting\">Installation FAQ</a>.</p>"));
	}

	public void testHyperlinkQualifiedInternal() {
		markupLanaguage.setInternalLinkPattern("http://wiki.eclipse.org/Mylyn/{0}");
		String html = parser.parseToHtml("Also see the [[Mylyn/FAQ#Installation_Troubleshooting | Installation FAQ]].");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<p>Also see the <a href=\"http://wiki.eclipse.org/Mylyn/FAQ#Installation_Troubleshooting\" title=\"Mylyn/FAQ#Installation_Troubleshooting\">Installation FAQ</a>.</p>"));
	}

	public void testHyperlinkExternal() {
		String html = parser.parseToHtml("a [http://example.com] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">http://example.com</a> hyperlink</p></body>"));
	}

	public void testHyperlinkExternalWithAltText() {
		String html = parser.parseToHtml("a [http://example.com|Example] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example</a> hyperlink</p></body>"));
	}

	public void testHyperlinkExternalWithAltText2() {
		String html = parser.parseToHtml("a [http://example.com Example Title] hyperlink");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <a href=\"http://example.com\">Example Title</a> hyperlink</p></body>"));
	}

	public void testImage() {
		String html = parser.parseToHtml("a [[Image:foo.png]] image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <img border=\"0\" src=\"foo.png\"/> image</p></body>"));
	}

	public void testImageWithAltText() {
		String html = parser.parseToHtml("a [[Image:foo.png|Example]] image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <img title=\"Example\" alt=\"Example\" border=\"0\" src=\"foo.png\"/> image</p></body>"));
	}

	public void testImageWithAltTextAndOptions() {
		String html = parser.parseToHtml("a [[Image:foo.png|100px|center|Example]] image");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><p>a <img height=\"100\" width=\"100\" align=\"middle\" title=\"Example\" alt=\"Example\" border=\"0\" src=\"foo.png\"/> image</p></body>"));
	}

	public void testTable() {
		String html = parser.parseToHtml("{|\n" + "|Orange\n" + "|Apple\n" + "|-\n" + "|Bread\n" + "|Pie\n" + "|-\n"
				+ "|Butter\n" + "|Ice cream \n" + "|}");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><td>Orange</td><td>Apple</td></tr><tr><td>Bread</td><td>Pie</td></tr><tr><td>Butter</td><td>Ice cream </td></tr></table></body>"));
	}

	public void testTable2() {
		String html = parser.parseToHtml("{|\n" + "|  Orange    ||   Apple   ||   more\n" + "|-\n"
				+ "|   Bread    ||   Pie     ||   more\n" + "|-\n" + "|   Butter   || Ice cream ||  and more\n"
				+ "|}\n");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><td>Orange</td><td>Apple</td><td>more</td></tr><tr><td>Bread</td><td>Pie</td><td>more</td></tr><tr><td>Butter</td><td>Ice cream</td><td>and more</td></tr></table></body>"));
	}

	public void testTableHeadings() {
		String html = parser.parseToHtml("{|\n" + "!  Fruit    !!   Quantity   !!  Price\n" + "|-\n"
				+ "|   Apple    ||   lb     ||   0.99\n" + "|}\n");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<body><table><tr><th>Fruit</th><th>Quantity</th><th>Price</th></tr><tr><td>Apple</td><td>lb</td><td>0.99</td></tr></table></body>"));
	}

	public void testTableLexicalOffsets() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = "{|\n" + "|  Orange    ||   Apple   ||   more\n" + "|-\n"
				+ "|   Bread    ||   Pie     ||   more\n" + "|-\n" + "|   Butter   || Ice cream ||  and more\n"
				+ "|}\n";
		System.out.println(content);
		parser.parse(content);
		System.out.println("Events: \n" + builder);

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

	public void testTableIncomplete() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = "{|\n" + "|  Orange    ||   Apple   ||   more\n" + "|-\n"
				+ "|   Bread    ||   Pie     ||   more\n" + "|-\n" + "|   Butter   || Ice cream ||  and more\n"
				+ "| \n";
		System.out.println(content);
		parser.parse(content);
		System.out.println("Events: \n" + builder);

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

	public void testTableIncomplete2() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		final String content = "{|\n" + "| foo |\n" + "|}";
		System.out.println(content);
		parser.parse(content);
		System.out.println("Events: \n" + builder);

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

	public void testTableWithSyntax() {
		final RecordingDocumentBuilder builder = new RecordingDocumentBuilder();
		parser.setBuilder(builder);
		String content = "{|\n" + "| <nowiki>'''''bold italic'''''</nowiki> || '''''bold italic''''' ||\n" + "|}";
		System.out.println(content);
		parser.parse(content);
		System.out.println("Events: \n" + builder);

		for (RecordingDocumentBuilder.Event event : builder.getEvents()) {
			if (event.text != null) {
				int start = event.locator.getDocumentOffset();
				int end = event.locator.getLineSegmentEndOffset() + event.locator.getLineDocumentOffset();

				assertTrue(end >= start);

			}
		}
	}

	public void testTableOptions() {
		String html = parser.parseToHtml("{| border=\"1\"\n" + "|- style=\"font-style:italic;color:green;\"\n"
				+ "| colspan=\"2\" | Orange || valign=\"top\" | Apple\n" + "|}");
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<table border=\"1\"><tr style=\"font-style:italic;color:green;\">"));
		assertTrue(html.contains("<td colspan=\"2\">Orange</td>"));
		assertTrue(html.contains("<td valign=\"top\">Apple</td>"));
	}

	public void testEntityReference() {
		String tests = "&Agrave; &Aacute; &Acirc; &Atilde; &Auml; &Aring; &AElig; &Ccedil; &Egrave; &Eacute; &Ecirc; &Euml; &Igrave; &Iacute; &Icirc; &Iuml; &Ntilde; &Ograve; &Oacute; &Ocirc; &Otilde; &Ouml; &Oslash; &Ugrave; &Uacute; &Ucirc; &Uuml; &szlig; &agrave; &aacute; &acirc; &atilde; &auml; &aring; &aelig; &ccedil; &egrave; &eacute; &ecirc; &euml; &igrave; &iacute; &icirc; &iuml; &ntilde; &ograve; &oacute; &ocirc; &oelig; &otilde; &ouml; &oslash; &ugrave; &uacute; &ucirc; &uuml; &yuml; &iquest; &iexcl; &sect; &para; &dagger; &Dagger; &bull; &ndash; &mdash; &lsaquo; &rsaquo; &laquo; &raquo; &lsquo; &rsquo; &ldquo; &rdquo; &trade; &copy; &reg; &cent; &euro; &yen; &pound; &curren; &#8304; &sup1; &sup2; &sup3; &#8308; &int; &sum; &prod; &radic; &minus; &plusmn; &infin; &asymp; &prop; &equiv; &ne; &le; &ge; &times; &middot; &divide; &part; &prime; &Prime; &nabla; &permil; &deg; &there4; &alefsym; &oslash; &isin; &notin; &cap; &cup; &sub; &sup; &sube; &supe; &not; &and; &or; &exist; &forall;  &rArr; &lArr; &dArr; &uArr; &hArr; &rarr; &darr; &uarr; &larr; &harr;";
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

	public void testTemplateEnDash() {
		// note: spacing is very specific
		String html = parser.parseToHtml("A{{ndash}}B");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A&nbsp;&ndash; B</p></body>"));
		html = parser.parseToHtml("A{{endash}}B");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A&nbsp;&ndash; B</p></body>"));
	}

	public void testTemplateEmDash() {
		// note: spacing is very specific
		String html = parser.parseToHtml("A{{mdash}}B");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A&nbsp;&mdash; B</p></body>"));
		html = parser.parseToHtml("A{{emdash}}B");
		System.out.println(html);
		assertTrue(html.contains("<body><p>A&nbsp;&mdash; B</p></body>"));
	}

	public void testDefinitionListIndenting() {
		String markup = ": one\n: two\n\n: three\nfour\n:five";
		String html = parser.parseToHtml(markup);
		System.out.println(html);
		assertTrue(html.contains("<body><dl><dd>one</dd><dd>two</dd></dl><dl><dd>three</dd></dl><p>four</p><dl><dd>five</dd></dl></body>"));
	}

	public void testParagraphBreaksOnPreformatted() {
		String markup = "a normal para\n preformatted\n p\nnormal\n";
		String html = parser.parseToHtml(markup);
		System.out.println(html);
		assertTrue(Pattern.compile("<body><p>a normal para</p><pre>preformatted\\s+p\\s+</pre><p>normal</p></body>",
				Pattern.MULTILINE).matcher(html).find());
	}

	public void testParagraphBreaksOnHeading() {
		String markup = "a normal para\n= h1 =\nnormal\n";
		String html = parser.parseToHtml(markup);
		System.out.println(html);
		assertTrue(html.contains("<body><p>a normal para</p><h1 id=\"h1\">h1</h1><p>normal</p></body>"));
	}
}
