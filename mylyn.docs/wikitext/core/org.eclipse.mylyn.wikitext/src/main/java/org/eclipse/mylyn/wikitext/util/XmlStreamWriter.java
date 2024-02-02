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

import javax.xml.stream.XMLStreamWriter;

/**
 * A means of writing XML content to a stream. Modeled after StAX with some small differences, notably:
 * <ul>
 * <li>StAX is only available in Java 6</li>
 * <li>The methods of this API only throw unchecked exceptions</li>
 * <li>This API provides a means for writing {@link #writeLiteral(String) unescaped XML}</li>
 * </ul>
 *
 * @author David Green
 * @since 3.0
 */
public abstract class XmlStreamWriter {

	public abstract void close();

	public abstract void flush();

	public abstract String getPrefix(String uri);

	/**
	 *
	 */
	public String getNamespaceURI(String prefix) {
		return null;
	}

	public abstract void setDefaultNamespace(String uri);

	public abstract void setPrefix(String prefix, String uri);

	public abstract void writeAttribute(String localName, String value);

	public abstract void writeAttribute(String namespaceURI, String localName, String value);

	public abstract void writeAttribute(String prefix, String namespaceURI, String localName, String value);

	public abstract void writeCData(String data);

	public abstract void writeCharacters(String text);

	public abstract void writeCharacters(char[] text, int start, int len);

	public abstract void writeComment(String data);

	public abstract void writeDTD(String dtd);

	public abstract void writeDefaultNamespace(String namespaceURI);

	public abstract void writeEmptyElement(String localName);

	public abstract void writeEmptyElement(String namespaceURI, String localName);

	public abstract void writeEmptyElement(String prefix, String localName, String namespaceURI);

	public abstract void writeEndDocument();

	public abstract void writeEndElement();

	public abstract void writeEntityRef(String name);

	public abstract void writeNamespace(String prefix, String namespaceURI);

	public abstract void writeProcessingInstruction(String target);

	public abstract void writeProcessingInstruction(String target, String data);

	public abstract void writeStartDocument();

	public abstract void writeStartDocument(String version);

	public abstract void writeStartDocument(String encoding, String version);

	public abstract void writeStartElement(String localName);

	public abstract void writeStartElement(String namespaceURI, String localName);

	public abstract void writeStartElement(String prefix, String localName, String namespaceURI);

	/**
	 * Write an XML fragment directly to the output. The given text is not processed or XML-encoded, since it is assumed to be a legal XML
	 * fragment.
	 */
	public abstract void writeLiteral(String literal);

	/**
	 * Creates an {@link XMLStreamWriter} for this {@link XmlStreamWriter}.
	 */
	public XMLStreamWriter toXMLStreamWriter() {
		return new XmlStreamWriterAdapter(this);
	}
}
