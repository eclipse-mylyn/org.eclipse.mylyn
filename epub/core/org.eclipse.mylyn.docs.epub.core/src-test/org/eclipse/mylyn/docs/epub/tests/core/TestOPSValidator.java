/*******************************************************************************
 * Copyright (c) 2011, 2014, 2017 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.tests.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.mylyn.docs.epub.internal.OPSValidator;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests for the OPS validator.
 *
 * @author Torkild U. Resheim
 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm
 */
@SuppressWarnings({ "nls" })
public class TestOPSValidator {

	private final String[] illegalAttributes = new String[] { "anything", "goes", "in", "here" };

	private final String[] illegalElements = new String[] { "anything", "goes", "in", "here" };

	private final String[] legalAttributes = new String[] { "accesskey", "charset", "class", "coords", "dir", "href",
			"hreflang", "id", "rel", "rev", "shape", "style", "tabindex", "target", "title", "type", "xml:lang" };

	/**
	 * A list of legal elements according to the EPUB 2.0.1 specification
	 *
	 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm#Section1.3.4
	 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm#Section2.2
	 */
	private final String[] legalElements = new String[] { "body", "head", "html", "title", "abbr", "acronym", "address",
			"blockquote", "br", "cite", "code", "dfn", "div", "em", "h1", "h2", "h3", "h4", "h5", "h6", "kbd", "p",
			"pre", "q", "samp", "span", "strong", "var", "a", "dl", "dt", "dd", "ol", "ul", "li", "object", "param",
			"b", "big", "hr", "i", "small", "sub", "sup", "tt", "del", "ins", "bdo", "caption", "col", "colgroup",
			"table", "tbody", "td", "tfoot", "th", "thead", "tr", "img", "area", "map", "style", "link", "base" };

	@Test
	public void testIllegalAttributes() throws ParserConfigurationException, SAXException, IOException {
		for (String attr : illegalAttributes) {
			String in = "<html><body><div " + attr + "=\"test\">content</div></body></html>";
			String expected = "<html><body><div>content</div></body></html>";
			String out = OPSValidator.clean(new InputSource(new StringReader(in)), "test.html");
			assertEquals(expected, out);
		}
	}

	@Test
	public void testIllegalElements() throws ParserConfigurationException, SAXException, IOException {
		for (String element : illegalElements) {
			String in = "<html><body><" + element + "></" + element + "></body></html>";
			String result = "<html><body></body></html>";
			String out = OPSValidator.clean(new InputSource(new StringReader(in)), "test.html");
			assertEquals(result, out);

		}
	}

	@Test
	public void testLegalAttributes() throws ParserConfigurationException, SAXException, IOException {
		for (String attr : legalAttributes) {
			String in = "<html><body><div " + attr + "=\"test\"></div></body></html>";
			String out = OPSValidator.clean(new InputSource(new StringReader(in)), "test.html");
			assertEquals(in, out);
		}
	}

	@Test
	public void testLegalElements() throws ParserConfigurationException, SAXException, IOException {
		for (String element : legalElements) {
			String in = "<html><body><" + element + ">content</" + element + "></body></html>";
			String out = OPSValidator.clean(new InputSource(new StringReader(in)), "test.html");
			assertEquals(in, out);

		}
	}

	@Test
	public void testNormal() throws ParserConfigurationException, SAXException, IOException {
		String in = "<body><h1 id=\"h1-1\">test</h1></body>";
		String out = OPSValidator.clean(new InputSource(new StringReader(in)), "test.html");
		assertEquals(in, out);
	}
}
