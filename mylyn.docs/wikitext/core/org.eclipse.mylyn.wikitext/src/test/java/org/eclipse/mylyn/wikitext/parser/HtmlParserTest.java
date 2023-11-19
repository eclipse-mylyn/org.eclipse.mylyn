/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlParserTest {

	@Test
	public void testCanParseSomething() throws Exception {
		HtmlParser parser = new HtmlParser();
		assertCanParseSomething(parser);
	}

	protected void assertCanParseSomething(HtmlParser parser) throws IOException, SAXException {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		parser.parse(new InputSource(new StringReader("<body><p>test</p></body>")), builder);

		String result = out.toString();

		Assert.assertEquals("<p>test</p>", result.trim());
	}

	@Test
	public void instance() {
		HtmlParser instance = HtmlParser.instance();
		assertNotNull(instance);
		assertNotNull(instance.getDelegate());
	}

	@Test
	public void instanceWithHtmlCleanupRules() {
		HtmlParser instance = HtmlParser.instanceWithHtmlCleanupRules();
		assertNotNull(instance);
		assertNotNull(instance.getDelegate());
		assertTrue(instance.getDelegate() instanceof org.eclipse.mylyn.wikitext.internal.parser.html.HtmlParser);
		org.eclipse.mylyn.wikitext.internal.parser.html.HtmlParser delegate = (org.eclipse.mylyn.wikitext.internal.parser.html.HtmlParser) instance
				.getDelegate();
		assertFalse(delegate.getProcessors().isEmpty());
	}

	@Test
	public void jsoupNotAvailable() throws Exception {
		HtmlParser parser = new HtmlParser() {
			@Override
			boolean isJsoupAvailable() {
				return false;
			}
		};
		assertCanParseSomething(parser);
	}

	@Test
	public void parseAsDocumentTrue() throws IOException, SAXException {
		String result = parseHtmlToHtml("some <b>text</b> and more", true);
		assertEquals(
				"<?xml version='1.0' encoding='utf-8' ?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head><body>some <b>text</b> and more</body></html>",
				result);
	}

	@Test
	public void parseAsDocumentFalse() throws IOException, SAXException {
		String result = parseHtmlToHtml("some <b>text</b> and more", false);
		assertEquals("some <b>text</b> and more", result);
	}

	@Test
	public void parsePreservesAsDocumentSetting() throws IOException, SAXException {
		HtmlParser parser = new HtmlParser();
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);

		parser.parse(new InputSource(new StringReader("before <em>emphasis</em> after")), builder);

		assertEquals("before <em>emphasis</em> after", out.toString());
	}

	private String parseHtmlToHtml(String input, boolean asDocument) throws IOException, SAXException {
		HtmlParser parser = new HtmlParser();
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);

		parser.parse(new InputSource(new StringReader(input)), builder, asDocument);

		return out.toString();
	}
}
