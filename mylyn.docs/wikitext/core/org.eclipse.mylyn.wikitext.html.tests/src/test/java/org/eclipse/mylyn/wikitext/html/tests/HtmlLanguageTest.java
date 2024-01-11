/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.eclipse.mylyn.wikitext.html.HtmlLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.jsoup.Jsoup;
import org.junit.Test;

import com.google.common.io.Resources;

public class HtmlLanguageTest {

	@Test
	public void create() {
		HtmlLanguage language = new HtmlLanguage();
		assertEquals("HTML", language.getName());
	}

	@Test
	public void isDiscoverable() {
		MarkupLanguage language = ServiceLocator.getInstance(HtmlLanguageTest.class.getClassLoader())
				.getMarkupLanguage("HTML");
		assertNotNull(language);
		assertTrue(language instanceof HtmlLanguage);
	}

	@Test
	public void parse() {
		String sourceHtml = "<p>one <b>two</b> three</p>";
		String expectedHtml = "<p>one <b>two</b> three</p>";
		assertProcessContent(expectedHtml, sourceHtml, false, true);
	}

	@Test
	public void parseAsDocument() {
		String sourceHtml = "<p>one <b>two</b> three</p>";
		String expectedHtml = "<?xml version='1.0' encoding='utf-8' ?>" + //
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" + //
				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>" + //
				"<body><p>one <b>two</b> three</p></body>" + //
				"</html>";
		assertProcessContent(expectedHtml, sourceHtml, true, true);
	}

	@Test
	public void parseMalformed() {
		assertProcessContent(loadResourceContent("parseMalformed_expected.xml"),
				loadResourceContent("parseMalformed_input.html"), true, true);
	}

	@Test
	public void jsoupIsAvailableForMalformedHtmlParsing() {
		assertNotNull(Jsoup.parse("one<br>two"));
	}

	@Test
	public void newDocumentBuilder() {
		Writer out = new StringWriter();
		DocumentBuilder builder = new HtmlLanguage().createDocumentBuilder(out);
		assertNotNull(builder);
		assertTrue(builder instanceof HtmlDocumentBuilder);
	}

	@Test
	public void newDocumentBuilderIsFormatting() {
		Writer out = new StringWriter();
		DocumentBuilder builder = new HtmlLanguage().createDocumentBuilder(out, true);
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.endDocument();
		assertEquals(loadResourceContent("newDocumentBuilderIsFormatting.xml"), out.toString());
	}

	@Test
	public void newDocumentBuilderIsNotFormatting() {
		Writer out = new StringWriter();
		DocumentBuilder builder = new HtmlLanguage().createDocumentBuilder(out, false);
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.endDocument();
		assertEquals(loadResourceContent("newDocumentBuilderIsNotFormatting.xml"), out.toString());
	}

	@Test
	public void builder() {
		assertNotNull(HtmlLanguage.builder());
	}

	@Test
	public void cloneSupported() {
		HtmlLanguage language = new HtmlLanguage();
		HtmlLanguage cloned = language.clone();
		assertNotNull(cloned);
		assertEquals(language.getName(), cloned.getName());
	}

	@Test
	public void parseCleansHtmlDefaultsToTrue() {
		assertTrue(new HtmlLanguage().isParseCleansHtml());
	}

	@Test
	public void parseCleansHtml() {
		HtmlLanguage htmlLanguage = new HtmlLanguage();
		htmlLanguage.setParseCleansHtml(true);
		assertTrue(htmlLanguage.isParseCleansHtml());
		htmlLanguage.setParseCleansHtml(false);
		assertFalse(htmlLanguage.isParseCleansHtml());
	}

	@Test
	public void parseCleansHtmlSetOnClone() {
		HtmlLanguage htmlLanguage = new HtmlLanguage();
		htmlLanguage.setParseCleansHtml(true);
		assertEquals(htmlLanguage.isParseCleansHtml(), htmlLanguage.clone().isParseCleansHtml());
		htmlLanguage.setParseCleansHtml(false);
		assertEquals(htmlLanguage.isParseCleansHtml(), htmlLanguage.clone().isParseCleansHtml());
	}

	@Test
	public void parseCleansHtmlAffectsParsing() {
		assertProcessContent("test <span class=\"test\">one</span> two", "test<span class=\"test\"> one </span>two",
				false, true);
		assertProcessContent("test<span class=\"test\"> one </span>two", "test<span class=\"test\"> one </span>two",
				false, false);
	}

	private String loadResourceContent(String resourceName) {
		try {
			String fileName = HtmlLanguageTest.class.getSimpleName() + '_' + resourceName;
			URL resource = HtmlLanguageTest.class.getResource(fileName);
			return convertToUnixLineEndings(Resources.toString(resource, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String convertToUnixLineEndings(String resource) {
		return resource.replaceAll("\\r\\n?", "\n");
	}

	protected void assertProcessContent(String expectedHtml, String sourceHtml, boolean asDocument,
			boolean parseCleansHtml) {
		Writer out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);

		HtmlLanguage language = new HtmlLanguage();
		language.setParseCleansHtml(parseCleansHtml);
		MarkupParser markupParser = new MarkupParser(language, builder);
		markupParser.parse(sourceHtml, asDocument);

		assertEquals(expectedHtml, out.toString());
	}
}
