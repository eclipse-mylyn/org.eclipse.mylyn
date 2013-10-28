/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.junit.Test;

/**
 * @author David Green
 */
public class DocumentProcessorTest {
	// subclass to work around cross-Bundle package access limitations
	private static class TestDocumentProcessor extends DocumentProcessor {

		@Override
		public void process(Document document) {
			// ignore
		}

		public static void normalizeTextNodes(Element parentElement) {
			DocumentProcessor.normalizeTextNodes(parentElement);
		}
	}

	@Test
	public void testNormalizeTextNodes() {
		Document document = new Document("");
		Element element = document.appendElement("root");
		element.appendText("first ");
		element.appendText("second,");
		element.appendText(" third");
		element.appendElement("break");
		element.appendText("fourth");

		assertEquals(5, element.childNodes().size());

		TestDocumentProcessor.normalizeTextNodes(element);

		assertEquals(3, element.childNodes().size());
		assertTrue(element.childNode(0) instanceof TextNode);
		assertEquals("first second, third", ((TextNode) element.childNode(0)).text());
		assertTrue(element.childNode(2) instanceof TextNode);
		assertEquals("fourth", ((TextNode) element.childNode(2)).text());
	}
}
