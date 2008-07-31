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
package org.eclipse.mylyn.wikitext.core.parser.util;

import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.DocBookDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

/**
 *
 *
 * @author David Green
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
			throw new IllegalStateException("must set markupLanguage");
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
		markupParser.setMarkupLanaguage(markupLanguage);

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
