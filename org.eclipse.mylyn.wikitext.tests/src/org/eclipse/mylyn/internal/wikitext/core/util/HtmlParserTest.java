/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author David Green
 * @see HtmlParser
 */
public class HtmlParserTest extends TestCase {

	private HtmlParser parser;

	private StringWriter out;

	private TextileDocumentBuilder builder;

	@Override
	protected void setUp() throws Exception {
		parser = new HtmlParser();
		out = new StringWriter();
		builder = new TextileDocumentBuilder(out);
		super.setUp();
	}

	public void testBasicHtml() throws IOException, SAXException {
		performTest("<html><body>test 123</body></html>", "test 123\n\n");
	}

	public void testBasicHtmlWithPara() throws IOException, SAXException {
		performTest("<html><body>test 123<p>abc\n\n\ndef</p></body></html>", "test 123\n\nabc   def\n\n");
	}

	public void testNumericList() throws IOException, SAXException {
		performTest(
				"<html><body>test 123<ol>\n\n\n<li>first plus text</li>\n<li>second</li>ignore</ol>test</body></html>",
				"test 123\n\n# first plus text\n# second\n\ntest\n\n");
	}

	public void testNumericListWithStyles() throws IOException, SAXException {
		performTest("<html><body><ol><li>first <em>plus</em> <b>text</b></li></ol></body></html>",
				"# first _plus_ *text*\n\n");
	}

	public void testTable() throws IOException, SAXException {
		performTest(
				"<html><body><table><tbody><tr><th>heading1</th><td>cell2</td></tr><tr><td>cell3 and more</td><td>cell 4</td></tr></tbody></table></body></html>",
				"|_.heading1|cell2|\n|cell3 and more|cell 4|\n\n");
	}

	public void testImageLink() throws IOException, SAXException {
		performTest(
				"<html><body><p>some text <a href=\"http://example.com/foo+bar/baz.html\"><img src=\"images/image.png\"/></a> and more text</p></body></html>",
				"some text !images/image.png!:http://example.com/foo+bar/baz.html and more text\n\n");
	}

	public void testImage() throws IOException, SAXException {
		performTest("<html><body><p>some text <img src=\"images/image.png\"/> and more text</p></body></html>",
				"some text !images/image.png! and more text\n\n");
	}

	public void testPreExtended() throws IOException, SAXException {
		performTest("<html><body>test 123<pre>xfoo\nbar\n\nbaz</pre></body></html>",
				"test 123\n\npre.. xfoo\nbar\n\nbaz\n\n\n");
	}

	public void testPre() throws IOException, SAXException {
		performTest("<html><body>test 123<pre>xfoo\nbar\nbaz</pre></body></html>",
				"test 123\n\npre. xfoo\nbar\nbaz\n\n");
	}

	public void testBlockCode() throws IOException, SAXException {
		performTest("<html><body>test 123<pre><code>foo\nbar\nbaz</code></pre></body></html>",
				"test 123\n\nbc. foo\nbar\nbaz\n\n");
	}

	public void testSpanCode() throws IOException, SAXException {
		performTest("<html><body>test 123 <code>foo baz</code></body></html>", "test 123 @foo baz@\n\n");
	}

	public void testSpanCode2() throws IOException, SAXException {
		performTest("<html><body><ul><li>a <code>foo baz</code></li></ul></body></html>", "* a @foo baz@\n\n");
	}

	public void testNbsp160() throws IOException, SAXException {
		performTest("<html><body>test&nbsp;two&#160;three</body></html>", "test two three\n\n");
	}

	public void testLtGt() throws IOException, SAXException {
		performTest("<html><body>test&lt;two&gt;three</body></html>", "test<two>three\n\n");
	}

	public void testApos() throws IOException, SAXException {
		performTest("<html><body>test&apos;two</body></html>", "test'two\n\n");
	}

	public void testAmp() throws IOException, SAXException {
		performTest("<html><body>test&amp;two</body></html>", "test&two\n\n");
	}

	public void testEntityReferences() throws IOException, SAXException {
		performTest("<html><body>&copy;&reg;&euro;</body></html>", "(c)(r)\u20ac\n\n");
	}

	private void performTest(String html, String expectedResult) throws IOException, SAXException {
		TestUtil.println("HTML: " + html);

		parser.parse(sourceForHtml(html), builder);

		String generatedMarkup = out.toString();

		TestUtil.println("Markup: " + generatedMarkup);

		Assert.assertEquals(expectedResult, generatedMarkup);

		MarkupParser markupParser = new MarkupParser(new TextileLanguage());

		StringWriter out = new StringWriter();
		HtmlDocumentBuilder htmlBuilder = new HtmlDocumentBuilder(out);
		htmlBuilder.setEmitAsDocument(false);
		markupParser.setBuilder(htmlBuilder);
		markupParser.parse(generatedMarkup);

		String generatedHtml = out.toString();

		TestUtil.println("Generated HTML: " + generatedHtml);
	}

	private InputSource sourceForHtml(String string) {
		return new InputSource(new StringReader(string));
	}
}
