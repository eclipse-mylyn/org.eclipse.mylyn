/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tests.internal.wikitext.parser.html.tests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.internal.parser.html.AbstractSaxHtmlParser;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.textile.internal.TextileDocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author David Green
 */
@SuppressWarnings({ "nls", "restriction" })
public abstract class AbstractSaxParserTest {

	protected AbstractSaxHtmlParser parser;

	private StringWriter out;

	private TextileDocumentBuilder builder;

	@Before
	public void setUp() throws Exception {
		parser = createParser();
		out = new StringWriter();
		builder = new TextileDocumentBuilder(out);
	}

	protected abstract AbstractSaxHtmlParser createParser();

	@Test
	public void testBasicHtml() throws IOException, SAXException {
		performTest("<html><body>test 123</body></html>", "test 123\n\n");
	}

	@Test
	public void testBasicHtmlWithPara() throws IOException, SAXException {
		performTest("<html><body>test 123<p>abc\n\n\ndef</p></body></html>", "test 123\n\nabc def\n\n");
	}

	@Test
	public void testBasicHtmlWithBoldEmphasis() throws IOException, SAXException {
		performTest("<html><body><p>text <b>bold</b> and <em>emphasis</em></p></body></html>",
				"text **bold** and _emphasis_\n\n");
	}

	@Test
	public void testBasicHtmlWithBoldEmphasis_NoPara() throws IOException, SAXException {
		performTest("<html><body>text <b>bold</b> and <em>emphasis</em></body></html>",
				"text **bold** and _emphasis_\n\n");
	}

	@Test
	public void testBasicHtmlWithHeadingPara() throws IOException, SAXException {
		performTest("<html><body><h1>Heading 1</h1>test 123<p>abc\n\n\ndef</p></body></html>",
				"h1. Heading 1\n\ntest 123\n\nabc def\n\n");
	}

	@Test
	public void testBasicHtmlWithNewlines() throws IOException, SAXException {
		performTest(
				"<html><body>\n<h1>First Heading</h1>\n\n<p>some content</p>\n<h1>Second Heading</h1>\n<p>some more content</p></body></html>",
				"h1. First Heading\n\nsome content\n\nh1. Second Heading\n\nsome more content\n\n");
	}

	@Test
	public void testNumericList() throws IOException, SAXException {
		performTest(
				"<html><body>test 123<ol>\n\n\n<li>first plus text</li>\n<li>second</li>ignore</ol>test</body></html>",
				"test 123\n\n# first plus text\n# second\n\ntest\n\n");
	}

	@Test
	public void testNumericListWithStyles() throws IOException, SAXException {
		performTest("<html><body><ol><li>first <em>plus</em> <b>text</b></li></ol></body></html>",
				"# first _plus_ **text**\n");
	}

	@Test
	public void testTable() throws IOException, SAXException {
		performTest(
				"<html><body><table><tbody><tr><th>heading1</th><td>cell2</td></tr><tr><td>cell3 and more</td><td>cell 4</td></tr></tbody></table></body></html>",
				"|_.heading1|cell2|\n|cell3 and more|cell 4|\n\n");
	}

	@Test
	public void testImageLink() throws IOException, SAXException {
		performTest(
				"<html><body><p>some text <a href=\"http://example.com/foo+bar/baz.html\"><img src=\"images/image.png\"/></a> and more text</p></body></html>",
				"some text !images/image.png!:http://example.com/foo+bar/baz.html and more text\n\n");
	}

	@Test
	public void testImage() throws IOException, SAXException {
		performTest("<html><body><p>some text <img src=\"images/image.png\"/> and more text</p></body></html>",
				"some text !images/image.png! and more text\n\n");
	}

	@Test
	public void testPreExtended() throws IOException, SAXException {
		performTest("<html><body>test 123<pre>xfoo\nbar\n\nbaz</pre></body></html>",
				"test 123\n\npre.. xfoo\nbar\n\nbaz\n\n");
	}

	@Test
	public void testPre() throws IOException, SAXException {
		performTest("<html><body>test 123<pre>xfoo\nbar\nbaz</pre></body></html>",
				"test 123\n\npre. xfoo\nbar\nbaz\n\n");
	}

	@Test
	public void testBlockCode() throws IOException, SAXException {
		performTest("<html><body>test 123<pre><code>foo\nbar\nbaz</code></pre></body></html>",
				"test 123\n\nbc. foo\nbar\nbaz\n\n");
	}

	@Test
	public void testSpanCode() throws IOException, SAXException {
		performTest("<html><body>test 123 <code>foo baz</code></body></html>", "test 123 @foo baz@\n\n");
	}

	@Test
	public void testSpanCode2() throws IOException, SAXException {
		performTest("<html><body><ul><li>a <code>foo baz</code></li></ul></body></html>", "* a @foo baz@\n");
	}

	@Test
	public void testSpanDel() throws IOException, SAXException {
		performTest("<html><body>test 123 <del>foo baz</del></body></html>", "test 123 -foo baz-\n\n");
	}

	@Test
	public void testSpanDelFromStrike() throws IOException, SAXException {
		performTest("<html><body>test 123 <strike>foo baz</strike></body></html>", "test 123 -foo baz-\n\n");
	}

	@Test
	public void testNbsp160() throws IOException, SAXException {
		performTest("<html><body>test&nbsp;two&#160;three</body></html>", "test two three\n\n");
	}

	@Test
	public void testLtGt() throws IOException, SAXException {
		performTest("<html><body>test&lt;two&gt;three</body></html>", "test<two>three\n\n");
	}

	@Test
	public void testApos() throws IOException, SAXException {
		performTest("<html><body>test&apos;two</body></html>", "test'two\n\n");
	}

	@Test
	public void testAmp() throws IOException, SAXException {
		performTest("<html><body>test&amp;two</body></html>", "test&two\n\n");
	}

	@Test
	public void testEntityReferences() throws IOException, SAXException {
		performTest("<html><body>&copy;&reg;&euro;</body></html>", "(c)(r)\u20ac\n\n");
	}

	@Test
	public void testListWithWhitespace() {
		assertParseHtml("<ul><li>one</li><li>two</li></ul>",
				"<html><body><ul> <li>one</li> \n <li>two</li>  </ul></body></html>");
	}

	@Test
	public void testOrderedListWithWhitespace() {
		assertParseHtml("<ol><li>one</li><li>two</li></ol>",
				"<html><body><ol> <li>one</li> \n <li>two</li>  </ol></body></html>");
	}

	@Test
	public void testTableWithRowCharacterContent() {
		assertParseHtml("<table><tr>ab<td>one</td></tr></table>",
				"<html><body><table> <tr>\t\r\nab <td>one</td>\n</tr>\n</table></body></html>");
	}

	@Test
	public void testAdjacentParagraphBlocksInListItem() {
		assertParseHtml("<ul><li><p>one</p><p>two</p></li></ul>",
				"<html><body><ul><li>\n<p>one</p>\n<p>two</p>\n</li></ul></body></html>");
	}

	@Test
	public void testAdjacentParagraphBlocks() {
		assertParseHtml("<p>one</p><p>two</p>", "<html><body>\n<p>one</p>\n<p>two</p>\n</body></html>");
	}

	@Test
	public void testInsignificantWhitespace() {
		assertParseHtml("<p>one</p><p>two three</p><pre> <pre><code>one\n\ntwo </code></pre> </pre>",
				"<html><body>\n<p>one </p>\n<p>two\n  \tthree</p>\n<pre> <code>one\n\ntwo </code> </pre></body></html>");
	}

	@Test
	public void testInsignificantWhitespaceNestedBlocks() {
		assertParseHtml("<ol><li>one<ul><li>point two</li><li>point three</li></ul>more</li></ol>",
				"<html><body><ol><li>one\n <ul><li>point two</li><li>point three</li></ul> more</li></ol></body></html>");
	}

	@Test
	public void testBlockQuote() {
		assertParseHtml("<blockquote></blockquote><div>and</div><blockquote>one<p>two</p></blockquote>",
				"<html><body><blockquote> </blockquote> <div>and</div> <blockquote> one <p>two</p> </blockquote></body></html>");
	}

	@Test
	public void testOrderedListWithStart() {
		assertParseHtml("<ol start=\"3\"><li>first</li><li>second</li></ol>",
				"<html><body><ol start=\"3\"><li>first</li><li>second</li></ol></body></html>");
	}

	private void assertParseHtml(String expectedResult, String html) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder htmlBuilder = new HtmlDocumentBuilder(out);
		htmlBuilder.setEmitAsDocument(false);
		try {
			parser.parse(sourceForHtml(html), htmlBuilder, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		assertEquals(expectedResult, out.toString());
	}

	protected void performTest(String html, String expectedResult) throws IOException, SAXException {
		parser.parse(sourceForHtml(html), builder, true);

		String generatedMarkup = out.toString();

		assertEquals(expectedResult, generatedMarkup);

		MarkupParser markupParser = new MarkupParser(new TextileLanguage());

		StringWriter out = new StringWriter();
		HtmlDocumentBuilder htmlBuilder = new HtmlDocumentBuilder(out);
		htmlBuilder.setEmitAsDocument(false);
		markupParser.setBuilder(htmlBuilder);
		markupParser.parse(generatedMarkup);
	}

	private InputSource sourceForHtml(String string) {
		return new InputSource(new StringReader(string));
	}
}
