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

package org.eclipse.mylyn.wikitext.core.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlParserTest {

	@Test
	public void testCanParseSomething() throws Exception {
		HtmlParser parser = new HtmlParser();
		assertCanParseSomething(parser);
	}

	protected void assertCanParseSomething(HtmlParser parser) throws IOException, SAXException {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		parser.parse(new InputSource(new StringReader("<body><p>test</p></body>")), builder);

		String result = out.toString();

		Assert.assertEquals("<p>test</p>", result.trim());
	}

	@Test
	public void instance() {
		HtmlParser instance = HtmlParser.instance();
		assertNotNull(instance);
		assertNotNull(instance.getDelegate());
	}

	@Test
	public void instanceWithHtmlCleanupRules() {
		HtmlParser instance = HtmlParser.instanceWithHtmlCleanupRules();
		assertNotNull(instance);
		assertNotNull(instance.getDelegate());
		assertTrue(instance.getDelegate() instanceof org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser);
		org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser delegate = (org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser) instance.getDelegate();
		assertFalse(delegate.getProcessors().isEmpty());
	}

	@Test
	public void jsoupNotAvailable() throws Exception {
		HtmlParser parser = new HtmlParser() {
			@Override
			boolean isJsoupAvailable() {
				return false;
			}
		};
		assertCanParseSomething(parser);
	}
}
