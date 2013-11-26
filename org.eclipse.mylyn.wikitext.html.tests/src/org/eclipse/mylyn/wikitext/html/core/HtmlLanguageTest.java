/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class HtmlLanguageTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void create() {
		HtmlLanguage language = new HtmlLanguage();
		assertEquals("HTML", language.getName());
	}

	@Test
	public void parse() {
		String sourceHtml = "<p>one <b>two</b> three</p>";
		String expectedHtml = "<p>one <b>two</b> three</p>";
		assertProcessContent(expectedHtml, sourceHtml, false);
	}

	@Test
	public void parseAsDocument() {
		String sourceHtml = "<p>one <b>two</b> three</p>";
		String expectedHtml = "<?xml version='1.0' encoding='utf-8' ?>" + //
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">" + //
				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>" + //
				"<body><p>one <b>two</b> three</p></body>" + //
				"</html>";
		assertProcessContent(expectedHtml, sourceHtml, true);
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
		DocumentBuilder builder = new HtmlLanguage().createDocumentBuilder(out);
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("test");
		builder.endBlock();
		builder.endDocument();
		assertEquals(loadResourceContent("newDocumentBuilderIsFormatting"), out.toString().trim());
	}

	private String loadResourceContent(String resourceName) {
		try {
			String fileName = HtmlLanguageTest.class.getSimpleName() + '_' + resourceName + ".xml";
			URL resource = HtmlLanguageTest.class.getResource(fileName);
			return Resources.toString(resource, Charsets.UTF_8).trim();
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	protected void assertProcessContent(String expectedHtml, String sourceHtml, boolean asDocument) {
		Writer out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);

		HtmlLanguage language = new HtmlLanguage();
		MarkupParser markupParser = new MarkupParser(language, builder);
		markupParser.parse(sourceHtml, asDocument);

		assertEquals(expectedHtml, out.toString());
	}
}
