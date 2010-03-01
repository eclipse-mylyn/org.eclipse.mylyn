/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.io.StringWriter;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.util.DefaultXmlStreamWriter;
import org.eclipse.mylyn.wikitext.core.util.FormattingXMLStreamWriter;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

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
}
