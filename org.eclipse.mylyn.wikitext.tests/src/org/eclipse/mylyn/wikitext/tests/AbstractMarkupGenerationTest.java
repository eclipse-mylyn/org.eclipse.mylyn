/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.tests;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.junit.Before;

/**
 * @since 2.0
 */
public abstract class AbstractMarkupGenerationTest<L extends MarkupLanguage> {

	protected MarkupParser parser;

	protected MarkupLanguage markupLanguage;

	@Before
	public void initParser() {
		parser = new MarkupParser();
		markupLanguage = createMarkupLanguage();
		parser.setMarkupLanguage(markupLanguage);
	}

	protected abstract L createMarkupLanguage();

	protected void assertMarkup(String expectedHtml, String markup) {
		String html = toHtml(markup);
		assertEquals(expectedHtml, html);
	}

	protected String toHtml(String markup) {
		Writer writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(markup);

		String html = writer.toString();
		TestUtil.println("HTML: \n" + html);
		return html;
	}
}
