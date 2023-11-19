/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
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
package org.eclipse.mylyn.wikitext.util;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.google.common.escape.Escaper;
import com.google.common.xml.XmlEscapers;

/**
 * A default implementation of {@link XmlStreamWriter} that creates XML character output.
 *
 * @author David Green
 * @since 3.0
 */
public class DefaultXmlStreamWriter extends XmlStreamWriter {

	private final Escaper attributeEscaper = XmlEscapers.xmlAttributeEscaper();

	private final Escaper contentEscaper = XmlEscapers.xmlContentEscaper();

	private PrintWriter out;

	private final Map<String, String> prefixToUri = new HashMap<>();

	private final Map<String, String> uriToPrefix = new HashMap<>();

	private boolean inEmptyElement = false;

	private boolean inStartElement = false;

	private final Stack<String> elements = new Stack<>();

	private char xmlHeaderQuoteChar = '\'';

	public DefaultXmlStreamWriter(OutputStream out) {
		this.out = createUtf8PrintWriter(out);
	}

	public DefaultXmlStreamWriter(Writer out) {
		this.out = new PrintWriter(out);
	}

	public DefaultXmlStreamWriter(Writer out, char xmlHeaderQuoteChar) {
		this.out = new PrintWriter(out);
		this.xmlHeaderQuoteChar = xmlHeaderQuoteChar;
	}

	protected PrintWriter createUtf8PrintWriter(java.io.OutputStream out) {
		return new java.io.PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
	}

	@Override
	public void close() {
		if (out != null) {
			closeElement();
			flush();
		}
		out = null;
	}

	@Override
	public void flush() {
		if (out != null) {
			out.flush();
		}
	}

	@Override
	public String getPrefix(String uri) {
		return uriToPrefix.get(uri);
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return prefixToUri.get(prefix);
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		return null;
	}

	@Override
	public void setDefaultNamespace(String uri) {
		setPrefix("", uri); //$NON-NLS-1$
	}

	@Override
	public void setPrefix(String prefix, String uri) {
		prefixToUri.put(prefix, uri);
		uriToPrefix.put(uri, prefix);
	}

	@Override
	public void writeAttribute(String localName, String value) {
		out.write(' ');
		out.write(localName);
		out.write("=\""); //$NON-NLS-1$
		if (value != null) {
			attrEncode(value);
		}
		out.write("\""); //$NON-NLS-1$
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) {
		String prefix = uriToPrefix.get(namespaceURI);
		writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
		out.write(' ');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
		out.write("=\""); //$NON-NLS-1$
		if (value != null) {
			attrEncode(value);
		}
		out.write("\""); //$NON-NLS-1$
	}

	private void attrEncode(String value) {
		if (value == null) {
			return;
		}
		printEscaped(out, value, true);
	}

	private void encode(String text) {
		if (text == null) {
			return;
		}
		printEscaped(out, text, false);
	}

	@Override
	public void writeCData(String data) {
		closeElement();
		out.write("<![CDATA["); //$NON-NLS-1$
		out.write(data);
		out.write("]]>"); //$NON-NLS-1$
	}

	@Override
	public void writeCharacters(String text) {
		closeElement();
		encode(text);
	}

	public void writeCharactersUnescaped(String text) {
		closeElement();
		out.print(text);
	}

	@Override
	public void writeLiteral(String literal) {
		writeCharactersUnescaped(literal);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) {
		closeElement();
		encode(new String(text, start, len));
	}

	@Override
	public void writeComment(String data) {
		closeElement();
		out.write("<!-- "); //$NON-NLS-1$
		out.write(data);
		out.write(" -->"); //$NON-NLS-1$
	}

	@Override
	public void writeDTD(String dtd) {
		out.write(dtd);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) {
		writeAttribute("xmlns", namespaceURI); //$NON-NLS-1$
	}

	private void closeElement() {
		if (inEmptyElement) {
			out.write("/>"); //$NON-NLS-1$
			inEmptyElement = false;
		} else if (inStartElement) {
			out.write(">"); //$NON-NLS-1$
			inStartElement = false;
		}
	}

	@Override
	public void writeEmptyElement(String localName) {
		closeElement();
		inEmptyElement = true;
		out.write('<');
		out.write(localName);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) {
		closeElement();
		inEmptyElement = true;
		String prefix = uriToPrefix.get(namespaceURI);
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
		closeElement();
		inEmptyElement = true;
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
		}
		out.write(localName);
	}

	@Override
	public void writeEndDocument() {
		if (!elements.isEmpty()) {
			throw new IllegalStateException(elements.size() + " elements not closed"); //$NON-NLS-1$
		}
	}

	@Override
	public void writeEndElement() {
		closeElement();
		if (elements.isEmpty()) {
			throw new IllegalStateException();
		}
		String name = elements.pop();
		out.write('<');
		out.write('/');
		out.write(name);
		out.write('>');
	}

	@Override
	public void writeEntityRef(String name) {
		closeElement();
		out.write('&');
		out.write(name);
		out.write(';');
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) {
		if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) { //$NON-NLS-1$
			writeDefaultNamespace(namespaceURI);
		} else {
			writeAttribute("xmlns:" + prefix, namespaceURI); //$NON-NLS-1$
		}
	}

	@Override
	public void writeProcessingInstruction(String target) {
		closeElement();
	}

	@Override
	public void writeProcessingInstruction(String target, String data) {
		closeElement();

	}

	@Override
	public void writeStartDocument() {
		out.write(processXmlHeader("<?xml version='1.0' ?>")); //$NON-NLS-1$
	}

	@Override
	public void writeStartDocument(String version) {
		out.write(processXmlHeader("<?xml version='" + version + "' ?>")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void writeStartDocument(String encoding, String version) {
		out.write(processXmlHeader("<?xml version='" + version + "' encoding='" + encoding + "' ?>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public void writeStartElement(String localName) {
		closeElement();
		inStartElement = true;
		elements.push(localName);
		out.write('<');
		out.write(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) {
		String prefix = uriToPrefix.get(namespaceURI);
		writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) {
		closeElement();
		inStartElement = true;
		out.write('<');
		if (prefix != null && prefix.length() > 0) {
			out.write(prefix);
			out.write(':');
			elements.push(prefix + ':' + localName);
		} else {
			elements.push(localName);
		}
		out.write(localName);
	}

	/**
	 *
	 */
	public char getXmlHeaderQuoteChar() {
		return xmlHeaderQuoteChar;
	}

	/**
	 *
	 */
	public void setXmlHeaderQuoteChar(char xmlHederQuoteChar) {
		this.xmlHeaderQuoteChar = xmlHederQuoteChar;
	}

	/**
	 * @deprecated use {@link #getXmlHeaderQuoteChar()}
	 */
	@Deprecated
	public char getXmlHederQuoteChar() {
		return getXmlHeaderQuoteChar();
	}

	/**
	 * @deprecated use {@link #setXmlHeaderQuoteChar(char)}
	 */
	@Deprecated
	public void setXmlHederQuoteChar(char xmlHederQuoteChar) {
		setXmlHeaderQuoteChar(xmlHederQuoteChar);
	}

	private String processXmlHeader(String header) {
		return xmlHeaderQuoteChar == '\'' ? header : header.replace('\'', xmlHeaderQuoteChar);
	}

	private void printEscaped(PrintWriter writer, CharSequence s, boolean attribute) {
		Escaper escaper = attribute ? attributeEscaper : contentEscaper;
		writer.write(escaper.escape(s.toString()));
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	protected static String getEntityRef(int ch, boolean attribute) {
		// Encode special XML characters into the equivalent character
		// references.
		// These five are defined by default for all XML documents.
		switch (ch) {
		case '<':
			return "lt"; //$NON-NLS-1$
		case '>':
			if (!attribute) {
				// bug 302291: text containing CDATA produces invalid HTML
				return "gt"; //$NON-NLS-1$
			}
		case '"':
			if (attribute) {
				return "quot"; //$NON-NLS-1$
			}
			break;
		case '&':
			return "amp"; //$NON-NLS-1$

		// WARN: there is no need to encode apostrophe, and doing so has an
		// adverse
		// effect on XHTML documents containing javascript with some browsers.
		// case '\'':
		// return "apos";
		}
		return null;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	protected static boolean isUtf8Printable(char ch) {
		// fall-back method here.
		if ((ch >= ' ' && ch <= 0x10FFFF && ch != 0xF7) || ch == '\n' || ch == '\r' || ch == '\t') {
			// If the character is not printable, print as character reference.
			// Non printables are below ASCII space but not tab or line
			// terminator, ASCII delete, or above a certain Unicode threshold.
			return true;
		}

		return false;
	}

}
