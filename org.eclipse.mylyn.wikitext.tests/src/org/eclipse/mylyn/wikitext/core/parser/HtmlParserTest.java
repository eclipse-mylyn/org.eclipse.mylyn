/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.internal.wikitext.textile.core.TextileDocumentBuilder;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author David Green
 */
public class HtmlParserTest {

	@Test
	public void testCanParseSomething() throws IOException, SAXException {
		HtmlParser parser = new HtmlParser();
		StringWriter out = new StringWriter();
		parser.parse(new InputSource(new StringReader("<body>test</body>")), new TextileDocumentBuilder(out));

		String result = out.toString();
		TestUtil.println(result);

		Assert.assertEquals("test", result.trim());
	}
}
