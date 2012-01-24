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

import org.eclipse.mylyn.internal.wikitext.core.parser.html.AbstractSaxHtmlParser;
import org.eclipse.mylyn.internal.wikitext.core.parser.html.XHtmlParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * a parser for HTML, driving {@link DocumentBuilder}. Depending on parsers available at runtime, input may need to be
 * well-formed XHTML.
 * 
 * @see DocumentBuilder
 * @author David Green
 * @since 1.6
 */
public class HtmlParser {

	/**
	 * parse well-formed XHTML from the given input, and emit an approximation of the source document to the given
	 * document builder
	 * 
	 * @param input
	 *            the source input
	 * @param builder
	 *            the builder to which output is provided
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parse(InputSource input, DocumentBuilder builder) throws IOException, SAXException {
		if (input == null) {
			throw new IllegalArgumentException();
		}
		if (builder == null) {
			throw new IllegalArgumentException();
		}
		AbstractSaxHtmlParser parser;
		if (isJsoupAvailable()) {
			parser = new org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser();
		} else {
			parser = new XHtmlParser();
		}

		parser.parse(input, builder);
	}

	private boolean isJsoupAvailable() {
		try {
			Class.forName("org.jsoup.Jsoup", true, HtmlParser.class.getClassLoader()); //$NON-NLS-1$
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

}
