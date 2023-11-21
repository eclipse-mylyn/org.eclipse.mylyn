/*******************************************************************************
 * Copyright (c) 2012-2014 Torkild U. Resheim
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.internal;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.mylyn.docs.epub.core.ValidationMessage;
import org.eclipse.mylyn.docs.epub.core.ValidationMessage.Severity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This type is a SAX parser that will read an <i>Open Publication Structure</i> file and optionally produce a new
 * version where elements and attributes not in the EPUB 2.0.1 <b>preferred</b> vocabulary are stripped. Alternatively
 * warnings can be issued when such elements and attributes are found and the contents left as is.
 *
 * @author Torkild U. Resheim
 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm
 */
public class OPSValidator extends DefaultHandler {

	public enum Mode {
		/** Remove non-preferred elements and attributes */
		REMOVE,
		/** Issue warnings when non-preferred elements or attributes are found */
		WARN
	}

	public static String clean(InputSource file, String href)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		SAXParser parser = factory.newSAXParser();
		OPSValidator tocGenerator = new OPSValidator(href, Mode.REMOVE);
		try {
			parser.parse(file, tocGenerator);
			return tocGenerator.getContents().toString();
		} catch (SAXException e) {
			System.err.println("Could not parse " + href); //$NON-NLS-1$
			e.printStackTrace();
		}
		return null;
	}

	public static List<ValidationMessage> validate(InputSource file, String href)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		SAXParser parser = factory.newSAXParser();
		OPSValidator tocGenerator = new OPSValidator(href, Mode.WARN);
		try {
			parser.parse(file, tocGenerator);
			return tocGenerator.getMessages();
		} catch (SAXException e) {
			System.err.println("Could not parse " + href); //$NON-NLS-1$
			e.printStackTrace();
		}
		return null;
	}

	private StringBuilder buffer = null;

	private StringBuilder contents = null;

	private final String href;

	@SuppressWarnings("nls")
	private final String[] legalAttributes = new String[] { "accesskey", "charset", "class", "coords", "dir", "href",
			"hreflang", "id", "rel", "rev", "shape", "style", "tabindex", "target", "title", "type", "xml:lang",
			/* Are these OK? */
			"xmlns", "src", "alt" };

	/**
	 * A list of legal elements according to the EPUB 2.0.1 specification
	 *
	 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm#Section1.3.4
	 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm#Section2.2
	 */
	@SuppressWarnings("nls")
	private final String[] legalElements = new String[] { "body", "head", "html", "title", "abbr", "acronym", "address",
			"blockquote", "br", "cite", "code", "dfn", "div", "em", "h1", "h2", "h3", "h4", "h5", "h6", "kbd", "p",
			"pre", "q", "samp", "span", "strong", "var", "a", "dl", "dt", "dd", "ol", "ul", "li", "object", "param",
			"b", "big", "hr", "i", "small", "sub", "sup", "tt", "del", "ins", "bdo", "caption", "col", "colgroup",
			"table", "tbody", "td", "tfoot", "th", "thead", "tr", "img", "area", "map", "style", "link", "base" };

	private final ArrayList<ValidationMessage> messages;

	private Mode mode = Mode.WARN;

	/**
	 * A list of elements that should be let through regardless of contents. Some publishers use this element to handle
	 * features present in certain reading systems.
	 */
	@SuppressWarnings("nls")
	private final String[] passthroughElements = new String[] { "meta" };

	private boolean recording = false;

	public OPSValidator(String href, Mode mode) {
		super();
		this.href = href;
		buffer = new StringBuilder();
		contents = new StringBuilder();
		messages = new ArrayList<ValidationMessage>();
		this.mode = mode;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (recording) {
			buffer.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (isLegalElement(qName)) {
			contents.append(buffer);
			contents.append("</" + qName + ">"); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.setLength(0);
		}
		recording = false;
	}

	public StringBuilder getContents() {
		return contents;
	}

	public ArrayList<ValidationMessage> getMessages() {
		return messages;
	}

	/**
	 * Returns <code>true</code> if the given attribute name is legal.
	 *
	 * @param name
	 * @return
	 */
	private boolean isLegalAttribute(String name) {
		for (String legal : legalAttributes) {
			if (name.equalsIgnoreCase(legal)) {
				return true;
			}
		}
		return false;
	}

	private boolean isLegalElement(String name) {
		for (String legal : legalElements) {
			if (name.equalsIgnoreCase(legal)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Use to determine whether or not elements with this name should be let trough the validator with any change or
	 * warning.
	 *
	 * @param name
	 *            the name of the element
	 * @return whether or not to pass through
	 */
	private boolean isPassthroughElement(String name) {
		for (String legal : passthroughElements) {
			if (name.equalsIgnoreCase(legal)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (isPassthroughElement(qName)) {
			// Record any text content
			contents.append('<');
			contents.append(qName);
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getQName(i);
				contents.append(' ');
				contents.append(name);
				contents.append("=\""); //$NON-NLS-1$
				contents.append(attributes.getValue(i));
				contents.append("\""); //$NON-NLS-1$
			}
			contents.append('>');
			recording = true;

		} else if (mode.equals(Mode.WARN) || isLegalElement(qName)) {
			// Record any text content
			contents.append('<');
			contents.append(qName);
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getQName(i);
				if (mode.equals(Mode.WARN) || isLegalAttribute(name)) {
					contents.append(' ');
					contents.append(name);
					contents.append("=\""); //$NON-NLS-1$
					contents.append(attributes.getValue(i));
					contents.append("\""); //$NON-NLS-1$
					if (!isLegalAttribute(name)) {
						messages.add(new ValidationMessage(Severity.WARNING, MessageFormat.format(
								"Attribute \"{0}\" in file \"{1}\" is not in OPS Preferred Vocabularies", name, href))); //$NON-NLS-1$
					}
				}
			}
			contents.append('>');
			recording = true;
			if (!isLegalElement(qName)) {
				messages.add(new ValidationMessage(Severity.WARNING, MessageFormat
						.format("Element \"{0}\" in file \"{1}\" is not in OPS Preferred Vocabularies", qName, href))); //$NON-NLS-1$

			}
		}
	}
}
