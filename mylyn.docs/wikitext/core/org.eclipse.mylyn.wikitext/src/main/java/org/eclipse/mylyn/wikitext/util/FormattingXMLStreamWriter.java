/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
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

import java.util.Stack;

/**
 * An {@link XmlStreamWriter} that creates formatted output by emitting whitespace into the document output stream.
 *
 * @author David Green
 * @since 3.0
 */
public class FormattingXMLStreamWriter extends XmlStreamWriter {

	private final XmlStreamWriter delegate;

	private int indentLevel;

	private final Stack<Integer> childCounts = new Stack<>();

	private int childCount;

	private final Stack<String> elements = new Stack<>();

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
	public String getNamespaceURI(String prefix) {
		return delegate.getNamespaceURI(prefix);
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
			value = ""; //$NON-NLS-1$
		}
		delegate.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) {
		if (value == null) {
			value = ""; //$NON-NLS-1$
		}
		delegate.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String localName, String value) {
		if (value == null) {
			value = ""; //$NON-NLS-1$
		}
		delegate.writeAttribute(localName, value);
	}

	@Override
	public void writeCData(String data) {
		++lineOffset;
		delegate.writeCData(data);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) {
		int lineStart = start;
		int length = 0;
		for (int x = 0; x < len; ++x) {
			final int charOffset = start + x;
			++length;
			if (lineOffset == 0 && text[charOffset] != '\n') {
				maybeIndent(false, true);
			}
			++lineOffset;
			if (text[charOffset] == '\n') {
				delegate.writeCharacters(text, lineStart, length);
				length = 0;
				lineOffset = 0;
				lineStart = charOffset;
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
		char[] chars = text.toCharArray();
		writeCharacters(chars, 0, chars.length);
	}

	@Override
	public void writeLiteral(String literal) {
		++lineOffset;
		delegate.writeLiteral(literal);
	}

	@Override
	public void writeComment(String data) {
		if (data == null) {
			data = ""; //$NON-NLS-1$
		}
		++childCount;
		maybeIndent();
		++lineOffset;
		delegate.writeComment(data);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) {
		delegate.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeDTD(String dtd) {
		++lineOffset;
		delegate.writeDTD(dtd);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
		++childCount;
		maybeIndent();
		++lineOffset;
		delegate.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) {
		++childCount;
		maybeIndent();
		++lineOffset;
		delegate.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(String localName) {
		++childCount;
		maybeIndent();
		++lineOffset;
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
		++lineOffset;
		delegate.writeEntityRef(name);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) {
		delegate.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeProcessingInstruction(String target, String data) {
		++childCount;
		++lineOffset;
		delegate.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeProcessingInstruction(String target) {
		++childCount;
		++lineOffset;
		delegate.writeProcessingInstruction(target);
	}

	@Override
	public void writeStartDocument() {
		++childCount;
		++lineOffset;
		delegate.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String encoding, String version) {
		++childCount;
		++lineOffset;
		delegate.writeStartDocument(encoding, version);
	}

	@Override
	public void writeStartDocument(String version) {
		++childCount;
		++lineOffset;
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
		++lineOffset;
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
		++lineOffset;
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
		++lineOffset;
		delegate.writeStartElement(localName);
	}

	private void maybeIndent() {
		maybeIndent(true, false);
	}

	private void maybeIndent(boolean withNewline, boolean force) {
		if (childCount == 0 && !force || preserveWhitespace()) {
			return;
		}
		StringBuilder buf = new StringBuilder();
		if (withNewline && (childCount > 1 || !childCounts.isEmpty())) {
			buf.append('\n');
			lineOffset = 0;
		}
		for (int x = 0; x < indentLevel; ++x) {
			buf.append('\t');
		}
		lineOffset += indentLevel;
		if (buf.length() > 0) {
			delegate.writeCharacters(buf.toString().toCharArray(), 0, buf.length());
		}
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
