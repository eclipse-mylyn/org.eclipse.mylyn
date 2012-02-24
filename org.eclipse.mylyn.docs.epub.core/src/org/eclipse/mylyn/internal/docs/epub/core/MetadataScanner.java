/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.core;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.mylyn.docs.epub.opf.Metadata;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This type is used to scan XHTML files for information that may be used in
 * EPUB metadata. This can be the publication title, copyright and author. Some
 * of this information can be found inside Dublin Core elements.
 * 
 * XXX: Not in use yet
 * 
 * @author Torkild U. Resheim
 * @see http://dublincore.org/documents/dc-html/
 * @see http://dublincore.org/documents/dcq-html/ (obsolete)
 */
public final class MetadataScanner extends AbstractXHTMLScanner {

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	}

	private final Metadata metadata;

	public MetadataScanner(Metadata metadata) {
		super();
		this.metadata = metadata;
	}

	public static void parse(InputSource file, Metadata metadata) throws ParserConfigurationException, SAXException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser parser = factory.newSAXParser();
		MetadataScanner tocGenerator = new MetadataScanner(metadata);
		try {
			parser.parse(file, tocGenerator);
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public Metadata getMetadata() {
		return metadata;
	}

}