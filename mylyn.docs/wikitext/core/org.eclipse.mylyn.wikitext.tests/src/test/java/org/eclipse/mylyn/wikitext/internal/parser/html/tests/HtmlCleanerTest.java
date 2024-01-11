/*******************************************************************************
 * Copyright (c) 2012, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Billy Huang - Bug 396332
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.internal.parser.html.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.internal.parser.html.HtmlCleaner;
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
		assertClean("<p>foo <span style=\"color: blue;\">bar</span></p>",
				"<p>foo <span style=\"color:blue;\"> bar</span></p>");
	}

	@Test
	public void testFirstNode_MoveWhitespaceOutside2() {
		assertClean("<p>foo <br><span style=\"color: blue;\">bar</span></p>",
				"<p>foo <span style=\"color:blue;\"> <br/>bar</span></p>");
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside() {
		assertClean("<p>foo <br><span style=\"color: blue;\">bar</span><br></p>",
				"<p>foo <span style=\"color:blue;\"><br/>bar<br/> </span></p>");
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside2() {
		assertClean("<p>foo <br><span style=\"color: blue;\">bar<br>ab</span></p>",
				"<p>foo <span style=\"color:blue;\"><br/>bar<br/>ab </span></p>");
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside3() {
		assertClean("<p>foo <br><span style=\"color: blue;\">bar<br>ab</span></p>",
				"<p>foo <span style=\"color:blue;\"><br/>bar<br/>ab&nbsp;</span></p>");
	}

	@Test
	public void testEmptyFontTag() {
		String result = clean("<p>foo <font color=\"red\"> </font>bar</p>");

		assertTrue(result.contains("<p>foo  bar</p>"));
	}

	@Test
	public void testFontTag_Black() {
		String result = clean("<p>foo <font color=\"black\"> bar</font></p>");

		assertTrue(result.contains("<p>foo <span style=\"color: black;\">bar</span></p>"));
	}

	@Test
	public void testFontTag_Nothing() {
		String result = clean("<p>foo <font color=\"  \"> bar</font></p>");

		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRemoveExcessiveStyles() {
		String result = clean("<p>foo <span > bar</span></p>");

		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void preserveSpanWithClass() {
		String result = clean("<p>foo <span class=\"test\">bar</span></p>");

		assertTrue(result.contains("<p>foo <span class=\"test\">bar</span></p>"));
	}

	@Test
	public void preserveSpanWithUnderlineTextDecoration() {
		String result = clean("<p>foo <span style=\"text-decoration: underline;\">bar</span></p>");

		assertTrue(result.contains("<p>foo <span style=\"text-decoration: underline;\">bar</span></p>"));
	}

	@Test
	public void preserveSpanWithUnderlineTextDecorationRemoveOthers() {
		String result = clean(
				"<p>foo <span style=\"ignoreThis: too; text-decoration: underline; bogus: bad\">bar</span></p>");

		assertTrue(result.contains("<p>foo <span style=\"text-decoration: underline;\">bar</span></p>"));
	}

	@Test
	public void preserveSpanWithLinethroughTextDecoration() {
		String result = clean("<p>foo <span style=\"text-decoration: line-through;\">bar</span></p>");

		assertTrue(result.contains("<p>foo <span style=\"text-decoration: line-through;\">bar</span></p>"));
	}

	@Test
	public void preserveSpanWithLinethroughTextDecorationRemoveOthers() {
		String result = clean(
				"<p>foo <span style=\"text-decoration: line-through; bogus: bad; ignoreThis: too\">bar</span></p>");

		assertTrue(result.contains("<p>foo <span style=\"text-decoration: line-through;\">bar</span></p>"));
	}

	@Test
	public void preserveSpanWithHyperlink() {
		String result = cleanToBody(
				"<p><span> </span><span class=\"foobar\"><a class=\"embedded\" href=\"https://foobar.com:9443/rm/resources/12345\" id=\"_1528753187524\">a link to somewhere  </a></span> <span>  </span></p>");

		assertEquals(
				"<body><p> <span class=\"foobar\"><a class=\"embedded\" href=\"https://foobar.com:9443/rm/resources/12345\" id=\"_1528753187524\">a link to somewhere</a></span> </p></body>",
				result);
	}

	@Test
	public void preserveSpanWithHyperlinkWithNoText() {
		String result = cleanToBody(
				"<p><span> </span><span class=\"foobar\"><a class=\"embedded\" href=\"https://foobar.com:9443/rm/resources/12345\" id=\"_1528753187524\">  </a></span> <span>  </span></p>");

		assertEquals(
				"<body><p> <span class=\"foobar\"><a class=\"embedded\" href=\"https://foobar.com:9443/rm/resources/12345\" id=\"_1528753187524\"></a></span> </p></body>",
				result);
	}

	@Test
	public void testRemoveExcessiveStyles_lots_of_styles() {
		String result = clean(
				"<p>foo <span style=\"font-style: italic;font-weight: bold; color: blue; bogus: bad; ignoreThis: too\"> bar</span></p>");

		assertTrue(result
				.contains("<p>foo <span style=\"font-style: italic;font-weight: bold;color: blue;\">bar</span></p>"));
	}

	@Test
	public void testRemoveExcessiveStyles_lots_of_styles2() {
		String result = clean("<p>foo <span style=\"bogus: bad; ignoreThis: too\"> bar</span></p>");

		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRemoveSpanContainingOnlyWhitespace() {
		String result = clean("<p>foo<span> </span>bar</p>");

		assertTrue(result.contains("<p>foo bar</p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle3CharactersNonHex() {
		String result = clean("<p><span style=\"color: 123\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #123;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersNonHex() {
		String result = clean("<p><span style=\"color: 123456\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #123456;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle3CharactersHex() {
		String result = clean("<p><span style=\"color: adc\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #adc;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersHex() {
		String result = clean("<p><span style=\"color: afcebd\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #afcebd;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersMixed() {
		String result = clean("<p><span style=\"color: A1B2C3\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #A1B2C3;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingValidCssColorStyleHexNotChanged() {
		String result = clean("<p><span style=\"color: #ABCDEF\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #ABCDEF;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingValidCssColorStyleNonHexNotChanged() {
		String result = clean("<p><span style=\"color: red\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: red;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingValidCssColorStyleNonHexLotsOfStylesNotChanged() {
		String result = clean("<p><span style=\"font-style: italic;font-weight: bold;color: red\">foo bar</span></p>");

		assertTrue(result
				.contains("<p><span style=\"font-style: italic;font-weight: bold;color: red;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersMixedWithImportantDeclaration() {
		String result = clean("<p><span style=\"color: A1B2C3 !important\">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #A1B2C3 !important;\">foo bar</span></p>"));
	}

	@Test
	public void testRepairSpanContainingMalformedCssColorStyle6CharactersMixedWithImportantDeclarationLotsOfWhitespace() {
		String result = clean("<p><span style=\"  color: A1B2C3 !    important   \">foo bar</span></p>");

		assertTrue(result.contains("<p><span style=\"color: #A1B2C3 !    important;\">foo bar</span></p>"));
	}

	@Test
	public void testTrailingWhitespaceBodyNoBlock() {
		// bug 406943
		String result = cleanToBody("<html>\n<body>\ntext\n</body>\n</html>");

		assertEquals("<body>text</body>", result);
	}

	@Test
	public void testTrailingWhitespaceBodyNoBlock_WhitespaceOutsideBody() {
		// bug 406943
		String result = cleanToBody("<html>\n<body>\ntext\n</body>\n</html>");

		assertEquals("<body>text</body>", result);
	}

	@Test
	public void testWhitespacesBetweenListItems() {
		String result = cleanToBody("<body><ol><li>item 0</li> <li>item 1</li>\t\n   <li>item 2</li></ol></body>");

		assertEquals("<body><ol><li>item 0</li><li>item 1</li><li>item 2</li></ol></body>", result);
	}

	@Test
	public void testWhitespacesBeforeBrTag() {
		// bug 433872
		assertClean("<br> foo", "<body>\n	<br/> foo</body>");
	}

	@Test
	public void testWhitespacesBeforeBrTag2() {
		// bug 433872
		assertClean("<hr><br> foo", "<body><hr>\n	<br/> foo</body>");
	}

	@Test
	public void testTextAndWhitespacesBeforeBrTag() {
		// bug 433872
		assertClean("foo<br> bar", "<body>foo \n	<br/> bar</body>");
	}

	@Test
	public void testTextBeforeBrTag() {
		// bug 433872
		assertClean("foo<br> bar", "<body>foo<br/> bar</body>");
	}

	@Test
	public void testTagBeforeBrTag() {
		// bug 433872
		assertClean("<hr><br> bar", "<body><hr/><br/> bar</body>");
	}

	@Test
	public void testWhitespacesPreservedAroundHrTag() {
		assertClean("foo <hr> foo", "<body>foo\n <hr/> foo</body>");
	}

	@Test
	public void testWhitespacesPreservedAroundDivTag() {
		String result = cleanToBody("<body>foo\n <div>bar</div> foo</body>");

		assertEquals("<body>foo <div>bar</div> foo</body>", result);
	}

	@Test
	public void testWhitespacesBetweenOrderedListAndItems() {
		String result = cleanToBody("<body><ol>  <li>item 0</li> \n</ol></body>");

		assertEquals("<body><ol><li>item 0</li></ol></body>", result);
	}

	@Test
	public void testWhitespacesBetweenUnorderedListAndItems() {
		String result = cleanToBody("<body><ul>  <li>item 0</li> \n</ul></body>");

		assertEquals("<body><ul><li>item 0</li></ul></body>", result);
	}

	@Test
	public void testWhitespacesBetweenTableCells() {
		String result = cleanToBody(
				"<body><table><tbody><tr><th>cell 0.0</th> <th>cell 0.1</th>\t\n  <th>cell 0.2</th></tr> <tr><td>cell 1.0</td> <td>cell 1.1</td> \t\r\n<td>cell 1.2</td></tr> <tr><td>cell 2.0</td> <td>cell 2.1</td> <td>cell 2.2</td></th></tbody></table></body>");

		assertEquals(
				"<body><table><tbody><tr><th>cell 0.0</th><th>cell 0.1</th><th>cell 0.2</th></tr><tr><td>cell 1.0</td><td>cell 1.1</td><td>cell 1.2</td></tr><tr><td>cell 2.0</td><td>cell 2.1</td><td>cell 2.2</td></tr></tbody></table></body>",
				result);
	}

	@Test
	public void testTrailingWhitespaceBodyNoBlock_WhitespaceOutsideBody2() {
		// bug 406943
		Document document = Document.createShell("");
		document.body().appendChild(new TextNode("\n"));
		document.body().appendChild(new TextNode("text"));
		document.body().appendChild(new TextNode("\n"));
		document.body().appendChild(new TextNode("\n"));
		String result = cleanToBody(document);

		assertEquals("<body>text</body>", result);
	}

	@Test
	public void preformattedWhitespacePreserve() {
		assertEquals("<body><pre> \none\r\ntwo\n</pre></body>", cleanToBody("<pre> \none\r\ntwo\n</pre>"));
	}

	@Test
	public void preformattedCodeWhitespacePreserve() {
		assertEquals("<body><pre><code> \none\r\ntwo\n</code></pre></body>",
				cleanToBody("<pre><code> \none\r\ntwo\n</code></pre>"));
	}

	private void assertClean(String expected, String input) {
		String result = cleanToBody(input);
		assertEquals("<body>" + expected + "</body>", result);
	}

	private String cleanToBody(String originalHtml) {
		Document document = Jsoup.parse(originalHtml);
		return cleanToBody(document);
	}

	private String cleanToBody(Document document) {
		new HtmlCleaner().apply(document);
		document.outputSettings().prettyPrint(false);
		return document.body().outerHtml();
	}

	private String clean(String originalHtml) {
		Document document = Jsoup.parse(originalHtml);
		new HtmlCleaner().apply(document);
		document.outputSettings().prettyPrint(false);
		return document.outerHtml();
	}
}
