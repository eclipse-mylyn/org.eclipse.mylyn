/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Billy Huang - Bug 396332
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.junit.Test;

/**
 * @author David Green
 */
public class HtmlCleanerTest {
	@Test
	public void testFirstNode_MoveWhitespaceOutside() {
		String result = clean("<p>foo <span style=\"color:blue;\"> bar</span></p>");
		TestUtil.println(result);
		assertTrue(result, result.contains("<p>foo <span style=\"color: blue;\">bar</span></p>"));
	}

	@Test
	public void testFirstNode_MoveWhitespaceOutside2() {
		String result = clean("<p>foo <span style=\"color:blue;\"> <br/>bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <br /><span style=\"color: blue;\">bar</span></p>"));
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside() {
		String result = clean("<p>foo <span style=\"color:blue;\"><br/>bar<br/> </span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <br /><span style=\"color: blue;\">bar</span><br /></p>"));
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside2() {
		String result = clean("<p>foo <span style=\"color:blue;\"><br/>bar<br/>ab </span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <br /><span style=\"color: blue;\">bar<br />ab</span></p>"));
	}

	@Test
	public void testEmptyFontTag() {
		String result = clean("<p>foo <font color=\"red\"> </font>bar</p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo  bar</p>"));
	}

	@Test
	public void testFontTag_Black() {
		String result = clean("<p>foo <font color=\"black\"> bar</font></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <span style=\"color: black;\">bar</span></p>"));
	}

	@Test
	public void testFontTag_Nothing() {
		String result = clean("<p>foo <font color=\"  \"> bar</font></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRemoveExcessiveStyles() {
		String result = clean("<p>foo <span > bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRemoveExcessiveStyles_lots_of_styles() {
		String result = clean("<p>foo <span style=\"font-style: italic;font-weight: bold; color: blue; bogus: bad; ignoreThis: too\"> bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <span style=\"font-style: italic;font-weight: bold;color: blue;\">bar</span></p>"));
	}

	@Test
	public void testRemoveExcessiveStyles_lots_of_styles2() {
		String result = clean("<p>foo <span style=\"bogus: bad; ignoreThis: too\"> bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRemoveSpanContainingOnlyWhitespace() {
		String result = clean("<p>foo<span> </span>bar</p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle3CharactersNonHex() {
		String result = clean("<p><span style=\"color: 123\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #123;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersNonHex() {
		String result = clean("<p><span style=\"color: 123456\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #123456;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle3CharactersHex() {
		String result = clean("<p><span style=\"color: adc\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #adc;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersHex() {
		String result = clean("<p><span style=\"color: afcebd\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #afcebd;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersMixed() {
		String result = clean("<p><span style=\"color: A1B2C3\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #A1B2C3;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingValidCssColorStyleHexNotChanged() {
		String result = clean("<p><span style=\"color: #ABCDEF\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #ABCDEF;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingValidCssColorStyleNonHexNotChanged() {
		String result = clean("<p><span style=\"color: red\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: red;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingValidCssColorStyleNonHexLotsOfStylesNotChanged() {
		String result = clean("<p><span style=\"font-style: italic;font-weight: bold;color: red\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"font-style: italic;font-weight: bold;color: red;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersMixedWithImportantDeclaration() {
		String result = clean("<p><span style=\"color: A1B2C3 !important\">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #A1B2C3 !important;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersMixedWithImportantDeclarationLotsOfWhitespace() {
		String result = clean("<p><span style=\"  color: A1B2C3 !    important   \">foo bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p><span style=\"color: #A1B2C3 !    important;\">foo bar</span></p>"));
	}

	@Test
	public void testTrailingWhitespaceBodyNoBlock() {
		// bug 406943
		String result = cleanToBody("<html>\n<body>\ntext\n</body>\n</html>");
		TestUtil.println(result);
		assertEquals("<body>text</body>", result);
	}

	@Test
	public void testTrailingWhitespaceBodyNoBlock_WhitespaceOutsideBody() {
		// bug 406943
		String result = cleanToBody("<html>\n<body>\ntext\n</body>\n</html>");
		TestUtil.println(result);
		assertEquals("<body>text</body>", result);
	}

	@Test
	public void testTrailingWhitespaceBodyNoBlock_WhitespaceOutsideBody2() {
		// bug 406943
		Document document = Document.createShell("");
		document.body().appendChild(new TextNode("\n", ""));
		document.body().appendChild(new TextNode("text", ""));
		document.body().appendChild(new TextNode("\n", ""));
		document.body().appendChild(new TextNode("\n", ""));
		String result = cleanToBody(document);
		TestUtil.println(result);
		assertEquals("<body>text</body>", result);
	}

	private String cleanToBody(String originalHtml) {
		Document document = Jsoup.parse(originalHtml);
		return cleanToBody(document);
	}

	private String cleanToBody(Document document) {
		new HtmlCleaner().apply(document);
		document.outputSettings().prettyPrint(false);
		String result = document.body().outerHtml();
		return result;
	}

	private String clean(String originalHtml) {
		Document document = Jsoup.parse(originalHtml);
		new HtmlCleaner().apply(document);
		document.outputSettings().prettyPrint(false);
		String result = document.outerHtml();
		return result;
	}
}
