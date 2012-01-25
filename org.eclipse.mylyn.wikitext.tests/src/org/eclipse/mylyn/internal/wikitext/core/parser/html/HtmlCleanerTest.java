/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import static junit.framework.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * @author David Green
 */
public class HtmlCleanerTest {
	@Test
	public void testFirstNode_MoveWhitespaceOutside() {
		String result = clean("<p>foo <span> bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <span>bar</span></p>"));
	}

	@Test
	public void testFirstNode_MoveWhitespaceOutside2() {
		String result = clean("<p>foo <span> <br/>bar</span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <br /><span>bar</span></p>"));
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside() {
		String result = clean("<p>foo <span><br/>bar<br/> </span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <br /><span>bar</span><br /></p>"));
	}

	@Test
	public void testLastNode_MoveWhitespaceOutside2() {
		String result = clean("<p>foo <span><br/>bar<br/>ab </span></p>");
		TestUtil.println(result);
		assertTrue(result.contains("<p>foo <br /><span>bar<br />ab</span></p>"));
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

	private String clean(String originalHtml) {
		Document document = Jsoup.parse(originalHtml);
		new HtmlCleaner().apply(document);
		String result = document.outerHtml();
		return result;
	}
}
