/*******************************************************************************
 * Copyright (c) 2012 Torkild U. Resheim
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.docs.epub.core;

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
 * This type is a SAX parser that will read a XHTML file and produce a new version where elements and attributes not in
 * the EPUB 2.0.1 <b>preferred</b> vocabulary are stripped. Alternatively warnings can be issued when such elements and
 * attributes are found.
 * 
 * @author Torkild U. Resheim
 * @see http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm
 */
public class OPS2Validator extends DefaultHandler {

	public enum Mode {
		/** Remove non-preferred elements and attributes */
		REMOVE,
		/** Issue warnings when non-preferred elements or attributes are found */
		WARN
	}

	private StringBuilder contents = null;

	private final ArrayList<ValidationMessage> messages;

	public StringBuilder getContents() {
		return contents;
	}

	public ArrayList<ValidationMessage> getMessages() {
		return messages;
	}

	public static List<ValidationMessage> validate(InputSource file, String href) throws ParserConfigurationException,
			SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		SAXParser parser = factory.newSAXParser();
		OPS2Validator tocGenerator = new OPS2Validator(href, Mode.WARN);
		try {
			parser.parse(file, tocGenerator);
			return tocGenerator.getMessages();
		} catch (SAXException e) {
			System.err.println("Could not parse " + href); //$NON-NLS-1$
			e.printStackTrace();
		}
		return null;
	}

	public static String clean(InputSource file, String href) throws ParserConfigurationException, SAXException,
			IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		SAXParser parser = factory.newSAXParser();
		OPS2Validator tocGenerator = new OPS2Validator(href, Mode.REMOVE);
		try {
			parser.parse(file, tocGenerator);
			return tocGenerator.getContents().toString();
		} catch (SAXException e) {
			System.err.println("Could not parse " + href); //$NON-NLS-1$
			e.printStackTrace();
		}
		return null;
	}

	private StringBuilder buffer = null;

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
	private final String[] legalElements = new String[] { "body", "head", "html", "title", "abbr", "acronym",
			"address", "blockquote", "br", "cite", "code", "dfn", "div", "em", "h1", "h2", "h3", "h4", "h5", "h6",
			"kbd", "p", "pre", "q", "samp", "span", "strong", "var", "a", "dl", "dt", "dd", "ol", "ul", "li", "object",
			"param", "b", "big", "hr", "i", "small", "sub", "sup", "tt", "del", "ins", "bdo", "caption", "col",
			"colgroup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "img", "area", "map", "style", "link",
			"base" };

	private Mode mode = Mode.WARN;

	/**
	 * A list of elements that should be let through regardless of contents.
	 */
	@SuppressWarnings("nls")
	private final String[] passthroughElements = new String[] { "meta" };

	private boolean recording = false;

	private final String href;

	public OPS2Validator(String href, Mode mode) {
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
								"Attribute \"{0}\" in {1} is not in OPS Preferred Vocabularies", name, href))); //$NON-NLS-1$
					}
				}
			}
			contents.append('>');
			recording = true;
			if (!isLegalElement(qName)) {
				messages.add(new ValidationMessage(Severity.WARNING, MessageFormat.format(
						"Element \"{0}\" in {1} is not in OPS Preferred Vocabularies", qName, href))); //$NON-NLS-1$

			}
		}
	}
}