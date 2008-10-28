/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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
import java.io.Reader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * A markup processor that can process lightweight markup formats such as Textile.
 * 
 * @author David Green
 * 
 */
public class MarkupParser {

	private MarkupLanguage markupLanguage;

	private DocumentBuilder builder;

	public MarkupParser() {
	}

	public MarkupParser(MarkupLanguage markupLanaguage, DocumentBuilder builder) {
		this.markupLanguage = markupLanaguage;
		this.builder = builder;
	}

	public MarkupParser(MarkupLanguage markupLanaguage) {
		this.markupLanguage = markupLanaguage;
	}

	/**
	 * the markup language of the markup to process
	 */
	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	/**
	 * set the markup language of the markup to process
	 */
	public void setMarkupLanaguage(MarkupLanguage markupLanaguage) {
		this.markupLanguage = markupLanaguage;
	}

	/**
	 * the builder to which parse results are propagated
	 */
	public DocumentBuilder getBuilder() {
		return builder;
	}

	/**
	 * set the builder to which parse results are propagated
	 */
	public void setBuilder(DocumentBuilder builder) {
		this.builder = builder;
	}

	public void parse(Reader markupContent) throws IOException {
		parse(markupContent, true);
	}

	public void parse(Reader markupContent, boolean asDocument) throws IOException {
		parse(readFully(markupContent), asDocument);
	}

	public void parse(String markupContent) {
		parse(markupContent, true);
	}

	public void parse(String markupContent, boolean asDocument) {
		if (markupLanguage == null) {
			throw new IllegalStateException("markup language is not set"); //$NON-NLS-1$
		}
		if (builder == null) {
			throw new IllegalStateException("builder is not set"); //$NON-NLS-1$
		}
		markupLanguage.processContent(this, markupContent, asDocument);
	}

	private String readFully(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();
		int c;
		while ((c = reader.read()) != -1) {
			writer.write(c);
		}
		return writer.toString();
	}

	/**
	 * parse the given markup content and produce the result as an HTML document.
	 * 
	 * @param markupContent
	 *            the content to parse
	 * 
	 * @return the HTML document text.
	 */
	public String parseToHtml(String markupContent) {
		if (builder != null) {
			throw new IllegalStateException("Builder must not be set"); //$NON-NLS-1$
		}

		StringWriter out = new StringWriter();

		setBuilder(new HtmlDocumentBuilder(out));

		parse(markupContent);

		setBuilder(null);

		return out.toString();
	}
}
