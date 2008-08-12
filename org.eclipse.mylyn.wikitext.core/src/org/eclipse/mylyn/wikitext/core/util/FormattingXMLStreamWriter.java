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
package org.eclipse.mylyn.wikitext.core.util;

import java.util.Stack;

/**
 * 
 * 
 * @author David Green
 */
public class FormattingXMLStreamWriter extends XmlStreamWriter {

	private XmlStreamWriter delegate;

	private int indentLevel;

	private Stack<Integer> childCounts = new Stack<Integer>();

	private int childCount;

	private Stack<String> elements = new Stack<String>();

	private int lineOffset = 0;

	public FormattingXMLStreamWriter(XmlStreamWriter delegate) {
		this.delegate = delegate;
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public void flush() {
		delegate.flush();
	}

	@Override
	public String getPrefix(String uri) {
		return delegate.getPrefix(uri);
	}

	@Override
	public void setDefaultNamespace(String uri) {
		delegate.setDefaultNamespace(uri);
	}

	@Override
	public void setPrefix(String prefix, String uri) {
		delegate.setPrefix(prefix, uri);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
		if (value == null) {
			value = "";
		}
		delegate.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) {
		if (value == null) {
			value = "";
		}
		delegate.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String localName, String value) {
		if (value == null) {
			value = "";
		}
		delegate.writeAttribute(localName, value);
	}

	@Override
	public void writeCData(String data) {
		delegate.writeCData(data);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) {
		int lineStart = start;
		int length = 0;
		for (int x = 0; x < len; ++x) {
			int charOffset = lineStart + x;
			++length;
			if (lineOffset == 0 && text[charOffset] != '\n') {
				maybeIndent(false, true);
			}
			++lineOffset;
			if (text[charOffset] == '\n') {
				delegate.writeCharacters(text, lineStart, length);
				length = 0;
				lineOffset = 0;
				lineStart = start + x;
			}
		}
		if (length > 0) {
			delegate.writeCharacters(text, lineStart, length);
			lineOffset += length;
		}
	}

	@Override
	public void writeCharacters(String text) {
		if (text == null) {
			return;
		}
		writeCharacters(text.toCharArray(), 0, text.length());
	}

	@Override
	public void writeLiteral(String literal) {
		delegate.writeLiteral(literal);
	}

	@Override
	public void writeComment(String data) {
		if (data == null) {
			data = "";
		}
		delegate.writeComment(data);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) {
		delegate.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeDTD(String dtd) {
		delegate.writeDTD(dtd);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
		++childCount;
		maybeIndent();
		delegate.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) {
		++childCount;
		maybeIndent();
		delegate.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(String localName) {
		++childCount;
		maybeIndent();
		delegate.writeEmptyElement(localName);
	}

	@Override
	public void writeEndDocument() {
		delegate.writeEndDocument();
	}

	@Override
	public void writeEndElement() {
		--indentLevel;
		maybeIndent();
		elements.pop();
		delegate.writeEndElement();
		childCount = childCounts.pop();
	}

	@Override
	public void writeEntityRef(String name) {
		delegate.writeEntityRef(name);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) {
		delegate.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeProcessingInstruction(String target, String data) {
		delegate.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeProcessingInstruction(String target) {
		delegate.writeProcessingInstruction(target);
	}

	@Override
	public void writeStartDocument() {
		delegate.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String encoding, String version) {
		delegate.writeStartDocument(encoding, version);
	}

	@Override
	public void writeStartDocument(String version) {
		delegate.writeStartDocument(version);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) {
		++childCount;
		maybeIndent();
		elements.push(localName);
		childCounts.push(childCount);
		childCount = 0;
		++indentLevel;
		delegate.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) {
		++childCount;
		maybeIndent();
		elements.push(localName);
		childCounts.push(childCount);
		childCount = 0;
		++indentLevel;
		delegate.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String localName) {
		++childCount;
		maybeIndent();
		elements.push(localName);
		childCounts.push(childCount);
		childCount = 0;
		++indentLevel;
		delegate.writeStartElement(localName);
	}

	private void maybeIndent() {
		maybeIndent(true, false);
	}

	private void maybeIndent(boolean withNewline, boolean force) {
		if ((childCount == 0 && !force) || preserveWhitespace()) {
			return;
		}
		StringBuilder buf = new StringBuilder();
		if (withNewline) {
			buf.append('\n');
		}
		for (int x = 0; x < indentLevel; ++x) {
			buf.append('\t');
		}
		lineOffset = indentLevel;
		delegate.writeCharacters(buf.toString().toCharArray(), 0, buf.length());
	}

	private boolean preserveWhitespace() {
		for (int x = elements.size() - 1; x >= 0; --x) {
			if (preserveWhitespace(elements.get(x))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Override this method to indicate which elements must have whitespace preserved.
	 * 
	 * @param elementName
	 *            the local name of the element
	 */
	protected boolean preserveWhitespace(String elementName) {
		return false;
	}

}