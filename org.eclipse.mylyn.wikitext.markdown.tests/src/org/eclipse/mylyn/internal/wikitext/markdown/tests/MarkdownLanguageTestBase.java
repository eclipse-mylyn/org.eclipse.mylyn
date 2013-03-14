/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;

/**
 * Test base for markdown language tests.
 * 
 * @author Stefan Seelmann
 */
public abstract class MarkdownLanguageTestBase extends TestCase {

	private MarkupParser parser;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		parser = new MarkupParser(new MarkdownLanguage());
	}

	public String parseToHtml(String markup) {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		parser.setBuilder(builder);
		parser.parse(markup);
		return out.toString();
	}
}
