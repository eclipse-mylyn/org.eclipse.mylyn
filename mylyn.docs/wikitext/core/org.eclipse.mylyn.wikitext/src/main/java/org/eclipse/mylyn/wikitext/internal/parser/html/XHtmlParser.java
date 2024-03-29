/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies.
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

package org.eclipse.mylyn.wikitext.internal.parser.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.mylyn.wikitext.internal.util.ConcatenatingReader;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * a parser for well-formed XHTML, driving {@link DocumentBuilder}.
 * 
 * @see DocumentBuilder
 * @author David Green
 */
public class XHtmlParser extends AbstractSaxHtmlParser {

	@Override
	protected void parse(InputSource input, DocumentBuilder builder, ContentHandler contentHandler)
			throws IOException, SAXException {
		if ((input == null) || (builder == null)) {
			throw new IllegalArgumentException();
		}
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(contentHandler);

		Reader reader = input.getCharacterStream();
		if (reader == null) {
			final InputStream in = input.getByteStream();
			if (in == null) {
				throw new IllegalArgumentException("input must provide a byte stream or a character stream"); //$NON-NLS-1$
			}
			reader = new InputStreamReader(in, input.getEncoding() == null ? "utf-8" : input.getEncoding()); //$NON-NLS-1$
		}
		reader = new ConcatenatingReader(
				new StringReader(
						"<?xml version='1.0'?><!DOCTYPE html [ <!ENTITY nbsp \"&#160;\"> <!ENTITY copy \"&#169;\"> <!ENTITY reg \"&#174;\"> <!ENTITY euro \"&#8364;\"> ]>"), //$NON-NLS-1$
				reader);

		input = new InputSource(reader);
		xmlReader.parse(input);
	}

}
