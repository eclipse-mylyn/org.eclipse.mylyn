/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.commonmark.CommonMarkLanguage;
import org.eclipse.mylyn.wikitext.commonmark.internal.spec.tests.SimplifiedHtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@SuppressWarnings("restriction")
public class CommonMarkAsserts {

	public static void assertContent(String expectedHtml, String input) {
		String html = parseToHtml(new CommonMarkLanguage(), input);
		assertHtmlEquals(expectedHtml, html);
	}

	public static void assertContent(MarkupLanguage language, String expectedHtml, String input) {
		String html = parseToHtml(language, input);
		assertHtmlEquals(expectedHtml, html);
	}

	private static void assertHtmlEquals(String expectedHtml, String html) {
		if (expectedHtml.trim().equals(html.trim())) {
			return;
		}
		assertEquals(toComparisonValue(expectedHtml), toComparisonValue(html));
	}

	private static String toComparisonValue(String html) {
		if (html == null) {
			return null;
		}
		try {
			StringWriter out = new StringWriter();
			DocumentBuilder builder = createDocumentBuilder(out);
			HtmlParser.instance().parse(new InputSource(new StringReader(html)), builder);
			return out.toString().trim();
		} catch (IOException | SAXException e) {
			throw new RuntimeException(html, e);
		}
	}

	private static String parseToHtml(MarkupLanguage markupLanguage, String input) {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = createDocumentBuilder(out);
		MarkupParser parser = new MarkupParser(markupLanguage, builder);
		try {
			parser.parse(new StringReader(input));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toString();
	}

	private static DocumentBuilder createDocumentBuilder(StringWriter out) {
		HtmlDocumentBuilder builder = new SimplifiedHtmlDocumentBuilder(out);
		builder.setDocumentHandler(new HtmlDocumentHandler() {

			@Override
			public void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
			}

			@Override
			public void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
			}
		});
		return builder;
	}
}
