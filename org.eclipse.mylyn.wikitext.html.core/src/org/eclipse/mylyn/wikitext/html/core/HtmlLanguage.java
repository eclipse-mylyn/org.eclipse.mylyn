/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.core;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Throwables;

/**
 * @author david.green
 * @since 2.0
 */
public class HtmlLanguage extends MarkupLanguage {

	static final String NAME_HTML = "HTML"; //$NON-NLS-1$

	public HtmlLanguage() {
		setName(NAME_HTML);
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		HtmlParser htmlParser = new HtmlParser();
		InputSource source = new InputSource(new StringReader(markupContent));
		try {
			htmlParser.parse(source, parser.getBuilder(), asDocument);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		} catch (SAXException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		return new HtmlDocumentBuilder(out, formatting);
	}

	/**
	 * Creates a {@link HtmlLanguageBuilder}.
	 * 
	 * @return the new {@link HtmlLanguageBuilder}
	 */
	public static HtmlLanguageBuilder builder() {
		return new HtmlLanguageBuilder();
	}

}
