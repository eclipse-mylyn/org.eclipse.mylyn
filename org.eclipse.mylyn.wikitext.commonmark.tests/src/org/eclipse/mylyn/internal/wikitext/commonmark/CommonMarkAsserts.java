/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.internal.wikitext.commonmark.spec.SimplifiedHtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.commonmark.CommonMarkLanguage;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Throwables;

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
			return out.toString();
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
			throw Throwables.propagate(e);
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
