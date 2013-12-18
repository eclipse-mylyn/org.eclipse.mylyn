/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import org.eclipse.mylyn.internal.wikitext.core.parser.html.AbstractSaxHtmlParser;
import org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlCleaner;
import org.eclipse.mylyn.internal.wikitext.core.parser.html.XHtmlParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A parser for HTML, driving {@link DocumentBuilder}. Depending on parsers available at runtime, input may need to be
 * well-formed XHTML.
 * 
 * @see DocumentBuilder
 * @author David Green
 * @since 1.6
 * @see #instanceWithHtmlCleanupRules()
 */
public class HtmlParser {

	private final AbstractSaxHtmlParser delegate;

	private HtmlParser(AbstractSaxHtmlParser parser) {
		this.delegate = checkNotNull(parser);

	}

	public HtmlParser() {
		AbstractSaxHtmlParser parser;
		if (isJsoupAvailable()) {
			parser = new org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser();
		} else {
			parser = new XHtmlParser();
		}
		this.delegate = parser;
	}

	/**
	 * Provides a parser instance with cleanup rules that make the result more suitable for generating wiki markup.
	 * 
	 * @since 2.0
	 */
	public static HtmlParser instanceWithHtmlCleanupRules() {
		org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser parser = new org.eclipse.mylyn.internal.wikitext.core.parser.html.HtmlParser();
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		htmlCleaner.configure(parser);
		return new HtmlParser(parser);
	}

	/**
	 * Creates a new parser instance.
	 * 
	 * @since 2.0
	 */
	public static HtmlParser instance() {
		return new HtmlParser();
	}

	AbstractSaxHtmlParser getDelegate() {
		return delegate;
	}

	/**
	 * Parses well-formed XHTML from the given input, and emit an approximation of the source document to the given
	 * document builder. Equivalent to {@code parse(input,builder,true)}
	 * 
	 * @param input
	 *            the source input
	 * @param builder
	 *            the builder to which output is provided
	 * @throws IOException
	 * @throws SAXException
	 */
	public void parse(InputSource input, DocumentBuilder builder) throws IOException, SAXException {
		parse(input, builder, true);
	}

	/**
	 * Parses well-formed XHTML or HTML from the given input, and emit an approximation of the source document to the
	 * given document builder.
	 * 
	 * @param input
	 *            the source input
	 * @param builder
	 *            the builder to which output is provided
	 * @param asDocument
	 *            indicates if the builder should be driven as a {@link DocumentBuilder#beginDocument() document}.
	 * @since 2.0
	 */
	public void parse(InputSource input, DocumentBuilder builder, boolean asDocument) throws IOException, SAXException {
		checkNotNull(input);
		checkNotNull(builder);

		delegate.parse(input, builder, asDocument);
	}

	boolean isJsoupAvailable() {
		try {
			Class.forName("org.jsoup.Jsoup", true, HtmlParser.class.getClassLoader()); //$NON-NLS-1$
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

}
