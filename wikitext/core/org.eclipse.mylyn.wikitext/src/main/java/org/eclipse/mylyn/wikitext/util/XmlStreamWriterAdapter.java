/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.common.collect.ImmutableSet;

class XmlStreamWriterAdapter implements XMLStreamWriter {
	private final XmlStreamWriter delegate;

	public XmlStreamWriterAdapter(XmlStreamWriter delegate) {
		this.delegate = Objects.requireNonNull(delegate, "Must provide a delegate"); //$NON-NLS-1$
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
	public void writeAttribute(String localName, String value) {
		delegate.writeAttribute(localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) {
		delegate.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
		delegate.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeCData(String data) {
		delegate.writeCData(data);
	}

	@Override
	public void writeCharacters(String text) {
		delegate.writeCharacters(text);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) {
		delegate.writeCharacters(text, start, len);
	}

	@Override
	public void writeComment(String data) {
		delegate.writeComment(data);
	}

	@Override
	public void writeDTD(String dtd) {
		delegate.writeDTD(dtd);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) {
		delegate.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeEmptyElement(String localName) {
		delegate.writeEmptyElement(localName);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) {
		delegate.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
		delegate.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEndDocument() {
		delegate.writeEndDocument();
	}

	@Override
	public void writeEndElement() {
		delegate.writeEndElement();
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
	public void writeProcessingInstruction(String target) {
		delegate.writeProcessingInstruction(target);
	}

	@Override
	public void writeProcessingInstruction(String target, String data) {
		delegate.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeStartDocument() {
		delegate.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String version) {
		delegate.writeStartDocument(version);
	}

	@Override
	public void writeStartDocument(String encoding, String version) {
		delegate.writeStartDocument(encoding, version);
	}

	@Override
	public void writeStartElement(String localName) {
		delegate.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) {
		delegate.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) {
		delegate.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return new NamespaceContext() {

			@Override
			public Iterator<String> getPrefixes(String namespaceUri) {
				String prefix = getPrefix(namespaceUri);
				if (prefix == null) {
					return ImmutableSet.<String> of().iterator();
				}
				return Collections.singletonList(prefix).iterator();
			}

			@Override
			public String getPrefix(String namespaceUri) {
				return delegate.getPrefix(namespaceUri);
			}

			@Override
			public String getNamespaceURI(String prefix) {
				return delegate.getNamespaceURI(prefix);
			}
		};
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		Objects.requireNonNull(name);
		throw new IllegalArgumentException(name);
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
		Objects.requireNonNull(context);
		// silently ignore
	}

}
