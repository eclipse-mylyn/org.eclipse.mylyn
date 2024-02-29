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

import java.io.IOException;

import org.eclipse.mylyn.wikitext.internal.parser.html.AbstractSaxHtmlParser;
import org.eclipse.mylyn.wikitext.internal.parser.html.HtmlCleaner;
import org.eclipse.mylyn.wikitext.internal.parser.html.HtmlParser;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author David Green
 */
@SuppressWarnings({ "nls", "restriction" })
public class HtmlParserTest extends AbstractSaxParserTest {

	@Override
	protected AbstractSaxHtmlParser createParser() {
		return new HtmlParser();
	}

	@Test
	public void testBasicMalformed() throws IOException, SAXException {
		performTest("<p>foo<br>bar</p>", "foo\nbar\n\n");
	}

	@Test
	public void testBasicMalformed2() throws IOException, SAXException {
		performTest("<p>foo<p>bar", "foo\n\nbar\n\n");

	}

	@Test
	public void testSignificantWhitespaceNotLost() throws IOException, SAXException {
		String input = "<html><body><p>one <b>two</b> three</p></body></html>";
		performTest(input, "one **two** three\n\n");
	}

	@Test
	public void testSignificantWhitespaceNotLost_Clean() throws IOException, SAXException {
		String input = "<html><body><p>one <b>two</b> three</p></body></html>";
		new HtmlCleaner().configure((HtmlParser) parser);

		performTest(input, "one **two** three\n\n");
	}

	@Test
	public void testParseInvalidHtml() throws IOException, SAXException {
		String input = "</font>one <b>two";
		performTest(input, "one **two**\n\n");
	}

	@Test
	public void testParseInvalidHtml_Clean() throws IOException, SAXException {
		String input = "</font>one <b>two";
		new HtmlCleaner().configure((HtmlParser) parser);

		performTest(input, "one **two**\n\n");
	}

	@Test
	public void testParseWhitespaceCleanup() throws IOException, SAXException {
		String input = "one <b>two </b>three";
		new HtmlCleaner().configure((HtmlParser) parser);

		performTest(input, "one **two** three\n\n");
	}

}
