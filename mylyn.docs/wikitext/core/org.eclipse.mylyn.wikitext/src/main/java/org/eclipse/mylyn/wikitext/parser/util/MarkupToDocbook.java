/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.parser.util;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;

/**
 * A utility for creating a Docbook document from markup.
 *
 * @see DocBookDocumentBuilder
 * @author David Green
 * @since 3.0
 */
public class MarkupToDocbook {

	private String bookTitle;

	private MarkupLanguage markupLanguage;

	public MarkupLanguage getMarkupLanguage() {
		return markupLanguage;
	}

	public void setMarkupLanguage(MarkupLanguage markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	public String parse(String markupContent) throws Exception {
		if (markupLanguage == null) {
			throw new IllegalStateException("must set markupLanguage"); //$NON-NLS-1$
		}
		StringWriter out = new StringWriter();

		DocBookDocumentBuilder builder = new DocBookDocumentBuilder(out) {
			@Override
			protected XmlStreamWriter createXmlStreamWriter(Writer out) {
				return super.createFormattingXmlStreamWriter(out);
			}
		};
		builder.setBookTitle(bookTitle);

		MarkupParser markupParser = new MarkupParser();

		markupParser.setBuilder(builder);
		markupParser.setMarkupLanguage(markupLanguage);

		markupParser.parse(markupContent);

		return out.toString();
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

}
