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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * This abstract type should be used to form the basis for all types that are
 * used to scan EPUB XHTML content files.
 * 
 * @author Torkild U. Resheim
 */
public abstract class AbstractXHTMLScanner extends DefaultHandler2 {

	/** Buffer holding element text */
	protected StringBuilder buffer = null;

	/** Whether or not we are capturing element text */
	protected boolean recording = false;

	protected String currentHref = null;

	/**
	 * <code>true</code> indicates that the parser is within a &lt;head&gt; HTML
	 * element
	 */
	protected boolean insideHead;

	public AbstractXHTMLScanner() {
		super();
		buffer = new StringBuilder();
	}

	/**
	 * Determines whether or not the given element name represents a HTML
	 * header.
	 * 
	 * @param qName
	 *            the element name
	 * @return <code>true</code> if the element is a header
	 */
	protected int isHeader(String qName) {
		if (qName.startsWith("h") || qName.startsWith("H")) {
			if (qName.length() == 2 && !qName.equalsIgnoreCase("hr")) {
				String n = qName.substring(1);
				try {
					int i = Integer.parseInt(n);
					// Levels must be between 1 and 6
					if (i > 0 && i < 7) {
						return i;
					}
				} catch (NumberFormatException e) {
					System.err.println("Bad header in " + currentHref);
				}
			}
		}
		return 0;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("head")) {
			insideHead = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("head")) {
			insideHead = false;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// Some titles actually contain newlines – so we need to remove them.
		if (recording) {
			String s = new String(ch, start, length);
			buffer.append(s.replace("\n", ""));
		}
	}

}
