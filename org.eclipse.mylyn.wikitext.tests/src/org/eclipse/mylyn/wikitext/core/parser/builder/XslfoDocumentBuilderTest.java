/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Torkild U. Resheim - Generate bookmarks for headers, bug 336592
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.io.StringWriter;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

public class XslfoDocumentBuilderTest extends TestCase {
	private StringWriter out;

	private XslfoDocumentBuilder documentBuilder;

	private MarkupParser parser;

	@Override
	protected void setUp() throws Exception {
		out = new StringWriter();
		documentBuilder = new XslfoDocumentBuilder(new FormattingXMLStreamWriter(new DefaultXmlStreamWriter(out)));
		parser = new MarkupParser();
		parser.setBuilder(documentBuilder);
	}

	// test for bug 304013: [wikitext-to-xslfo] Missing </block> in <static-content>
	public void testXslFoNoMissingBlock_bug304013() {
		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		parser.setMarkupLanguage(new MediaWikiLanguage());

		parser.parse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n{{NonExistantTemplate}}\n" + "\n" + "= H1 =\n" + "\n"
				+ "== H2 ==\n" + "\n" + "some text");
//		System.out.println(out);
		assertFalse(Pattern.compile("<static-content[^>]*></static-content>").matcher(out.toString()).find());
	}

	public void testForXslFoBookmarks_bug336592() {
		final String markup = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n= Bookmark H1 =\n== Bookmark H2 ==\n";

		documentBuilder.getConfiguration().setPageNumbering(true);
		documentBuilder.getConfiguration().setTitle("Title");
		OutlineItem op = new OutlineParser(new MediaWikiLanguage()).parse(markup);
		documentBuilder.setOutline(op);
		parser.setMarkupLanguage(new MediaWikiLanguage());
		parser.parse(markup, true);

		final String xslfo = out.toString();
		TestUtil.println(xslfo);

		assertTrue(Pattern.compile(
				"<bookmark-tree>\\s*<bookmark internal-destination=\"Bookmark_H1\">\\s*<bookmark-title>Bookmark H1</bookmark-title>\\s*<bookmark internal-destination=\"Bookmark_H2\">\\s*<bookmark-title>Bookmark H2</bookmark-title>\\s*</bookmark>\\s*</bookmark>\\s*</bookmark-tree>")
				.matcher(xslfo)
				.find());
	}
}
