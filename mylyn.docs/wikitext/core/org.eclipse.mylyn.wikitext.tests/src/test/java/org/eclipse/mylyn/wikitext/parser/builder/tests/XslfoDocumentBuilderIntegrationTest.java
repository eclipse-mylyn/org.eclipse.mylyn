/*******************************************************************************
 * Copyright (c) 2010, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Torkild U. Resheim - bugs 336592 and 336813
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.XslfoDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.util.DefaultXmlStreamWriter;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

/**
 * @author David Green
 * @author Torkild U. Resheim
 */
public class XslfoDocumentBuilderIntegrationTest {

	private StringWriter out;

	private XslfoDocumentBuilder documentBuilder;

	private MarkupParser parser;

	@Before
	public void setUp() throws Exception {
		out = new StringWriter();
		documentBuilder = new XslfoDocumentBuilder(new DefaultXmlStreamWriter(out));
		parser = new MarkupParser();
		parser.setBuilder(documentBuilder);
	}

	// test for bug 304013: [wikitext-to-xslfo] Missing </block> in <static-content>
	@Test
	public void testXslFoNoMissingBlock_bug304013() {
		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		parser.setMarkupLanguage(new MediaWikiLanguage());

		parser.parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n{{NonExistantTemplate}}\n" + "\n" + "= H1 =\n" + "\n"
				+ "== H2 ==\n" + "\n" + "some text");
		assertFalse(Pattern.compile("<static-content[^>]*></static-content>").matcher(out.toString()).find());
	}

	@Test
	public void testForXslFoBookmarks_bug336592() {
		final String markup = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n= Bookmark H1 =\n== Bookmark H2 ==\n";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();

		assertTrue(Pattern.compile(
				"<bookmark-tree>\\s*<bookmark internal-destination=\"Bookmark_H1\">\\s*<bookmark-title>Bookmark H1</bookmark-title>\\s*<bookmark internal-destination=\"Bookmark_H2\">\\s*<bookmark-title>Bookmark H2</bookmark-title>\\s*</bookmark>\\s*</bookmark>\\s*</bookmark-tree>")
				.matcher(xslfo).find());
	}

	@Test
	public void testforTableCSSAttributes_bug336813() {
		final String markup = "{| style=\"border-style: solid; border-color: #000; border-width: 1px;\"\n" + "|-\n"
				+ "! header 1\n" + "! header 2\n" + "! header 3\n" + "|-\n" + "| row 1, cell 1\n" + "| row 1, cell 2\n"
				+ "| row 1, cell 3\n" + "|- style=\"border-style: solid; border-color: #000; border-width: 1px;\" \n"
				+ "| row 2, cell 1\n" + "| row 2, cell 2\n"
				+ "| style=\"border-style: solid; border-color: #000; border-width: 1px;\" | row 2, cell 3\n" + "|}";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();

		// Test for border attributes in table
		assertTrue(Pattern.compile("<table-body border-color=\"#000\" border-style=\"solid\" border-width=\"1px\">")
				.matcher(xslfo).find());
		// Test for border attributes in row
		assertTrue(Pattern.compile("<table-row border-color=\"#000\" border-style=\"solid\" border-width=\"1px\">")
				.matcher(xslfo).find());
		// Test for border attributes in cell
		assertTrue(Pattern.compile(
				"<block font-size=\"10.0pt\" border-color=\"#000\" border-style=\"solid\" border-width=\"1px\">")
				.matcher(xslfo).find());
	}

	@Test
	public void testforTableSpan_bug336813() {
		final String markup = "{|\n|-\n" + "| Column 1 || Column 2 || Column 3\n" + "|-\n" + "| rowspan=\"2\"| A\n"
				+ "| colspan=\"2\" | B\n" + "|-\n" + "| C <!-- column 1 occupied by cell A -->\n" + "| D \n" + "|-\n"
				+ "| E\n" + "| rowspan=\"2\" colspan=\"2\" | F\n" + "|- \n"
				+ "| G <!-- column 2+3 occupied by cell F -->\n" + "|- \n" + "| colspan=\"3\" | H\n" + "|}";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();

		// Test for rowspan
		assertTrue(Pattern.compile(
				"<table-cell number-rows-spanned=\"2\" padding-left=\"2pt\" padding-right=\"2pt\" padding-top=\"2pt\" padding-bottom=\"2pt\">")
				.matcher(xslfo).find());

		// Test for colspan
		assertTrue(Pattern.compile(
				"<table-cell number-columns-spanned=\"2\" padding-left=\"2pt\" padding-right=\"2pt\" padding-top=\"2pt\" padding-bottom=\"2pt\">")
				.matcher(xslfo).find());

	}

	@Test
	public void testforTableRowAlign_bug336813() {
		final String markup = "{|\n" + "|- valign=\"top\"\n |'''Row heading'''\n"
				+ "| A longer piece of text. Lorem ipsum...\n |A shorter piece of text.\n"
				+ "|- style=\"vertical-align: bottom;\"\n |'''Row heading'''\n"
				+ "|A longer piece of text. Lorem ipsum... \n |A shorter piece of text.\n" + "|}";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();

		// From "valign" attribute
		assertTrue(Pattern.compile("<table-row display-align=\"before\">").matcher(xslfo).find());

		// From css styling
		assertTrue(Pattern.compile("<table-row display-align=\"after\">").matcher(xslfo).find());
	}

	@Test
	public void testforTableCellAlign_bug336813() {
		final String markup = "{|\n" + "|- \n |'''Row heading'''\n"
				+ "| valign=\"top\" | A longer piece of text. Lorem ipsum...\n |A shorter piece of text.\n"
				+ "|- \n |'''Row heading'''\n"
				+ "| style=\"vertical-align: bottom;\" | A longer piece of text. Lorem ipsum... \n |A shorter piece of text.\n"
				+ "|}";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();

		// From "valign" attribute
		assertTrue(Pattern.compile("<table-cell display-align=\"before\"").matcher(xslfo).find());

		// From css styling
		assertTrue(Pattern.compile("<block font-size=\"10.0pt\" display-align=\"after\">").matcher(xslfo).find());
	}

	@Test
	public void testforTableCellTextAlign_bug336813() {
		final String markup = "{|\n" + "|- \n |'''Row heading'''\n"
				+ "| align=\"left\" | A longer piece of text. Lorem ipsum...\n |A shorter piece of text.\n"
				+ "|- \n |'''Row heading'''\n"
				+ "| style=\"text-align: right;\" | A longer piece of text. Lorem ipsum... \n |A shorter piece of text.\n"
				+ "|}";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();

		// From "text-align" attribute
		assertTrue(Pattern.compile("<table-cell text-align=\"left\"").matcher(xslfo).find());

		// From css styling
		assertTrue(Pattern.compile("<block font-size=\"10.0pt\" text-align=\"right\">").matcher(xslfo).find());
	}

	@Test
	public void testforXslFoLinks() {
		final String markup = "\"INTERN-LABEL\":#intern_label\n" + "\"*INTERN-BOLD-LABEL*\":#intern_bold_label\n"
				+ "\"EXTERN-LABEL\":http://extern-label.com/\n";

		documentBuilder.getConfiguration().setPageNumbering(false);
		documentBuilder.getConfiguration().setTitle("Title");

		parser.setMarkupLanguage(new TextileLanguage());
		parser.parse(markup);

		final String xslfo = out.toString();

		assertTrue(xslfo.contains("<basic-link internal-destination=\"intern_label\">INTERN-LABEL</basic-link>"));
		assertTrue(xslfo.contains(
				"<basic-link internal-destination=\"intern_bold_label\"><inline font-weight=\"bold\">INTERN-BOLD-LABEL</inline></basic-link>"));
		assertTrue(xslfo.contains(
				"<basic-link external-destination=\"url(http://extern-label.com/)\">EXTERN-LABEL</basic-link>"));
	}

	@Test
	public void testCopyrightExtent() {
		documentBuilder.getConfiguration().setCopyright("Test Copyright");

		parser.setMarkupLanguage(new TextileLanguage());
		parser.parse("test");

		assertEquals(resource("testCopyrightExtent.xml"), out.toString());
	}

	private String resource(String resourceName) {
		URL resource = XslfoDocumentBuilderIntegrationTest.class.getResource(
				"resources/" + XslfoDocumentBuilderIntegrationTest.class.getSimpleName() + "_" + resourceName);
		try {
			return Resources.toString(resource, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
