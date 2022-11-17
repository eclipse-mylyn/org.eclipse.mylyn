/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.tests.wikitext.parser.builder;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
public class DocBookDocumentBuilderIntegrationTest {

	private final static String DOCBOOK_BEGIN = "<?xml version='1.0' ?><!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd\"><book><title></title><chapter><title></title>";

	private final static String DOCBOOK_END = "</chapter></book>";

	private MarkupParser parser;

	private StringWriter out;

	private DocBookDocumentBuilder builder;

	@Before
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new DocBookDocumentBuilder(out);
		parser.setBuilder(builder);

	}

	@Test
	public void testInlineImage() {
		parser.parse("some text !(inline)images/foo.png! some text");
		String docbook = out.toString();

		String expectedContent = "<para>some text <inlinemediaobject role=\"inline\"><imageobject><imagedata fileref=\"images/foo.png\"/></imageobject></inlinemediaobject> some text</para>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testInlineQuote() {
		parser.setMarkupLanguage(new ConfluenceLanguage());
		parser.parse("some text {quote}quoted text{quote} some text");
		String docbook = out.toString();

		String expectedContent = "<para>some text <quote>quoted text</quote> some text</para>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testImage() {
		parser.parse("some text !images/foo.png! some text");
		String docbook = out.toString();

		String expectedContent = "<para>some text <mediaobject><imageobject><imagedata fileref=\"images/foo.png\"/></imageobject></mediaobject> some text</para>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testImageWithScaling() {
		parser.parse("some text !{width:80%}images/foo.png! some text");
		String docbook = out.toString();

		String expectedContent = "<para>some text <mediaobject><imageobject><imagedata fileref=\"images/foo.png\" scale=\"80\"/></imageobject></mediaobject> some text</para>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testImageWithWidthAndHeight() {
		parser.parse("!{width:32px;height:64px}images/foo.png!");
		String docbook = out.toString();
		String expectedContent = "<para><mediaobject><imageobject><imagedata fileref=\"images/foo.png\" width=\"32px\" depth=\"64px\"/></imageobject></mediaobject></para>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}

	@Test
	public void testGlossaryUsesDefinitionList() {
		builder.setAutomaticGlossary(false);
		parser.parse("ABW(A Better Way) is not NIMBY(Not In My Back Yard)\n\n{glossary}");

		String docbook = out.toString();

		String expectedContent = "<para><glossterm>ABW</glossterm> is not <glossterm>NIMBY</glossterm></para><variablelist><varlistentry><term>ABW</term><listitem><para>A Better Way</para></listitem></varlistentry><varlistentry><term>NIMBY</term><listitem><para>Not In My Back Yard</para></listitem></varlistentry></variablelist>";
		assertEquals(DOCBOOK_BEGIN + expectedContent + DOCBOOK_END, docbook);
	}
}
