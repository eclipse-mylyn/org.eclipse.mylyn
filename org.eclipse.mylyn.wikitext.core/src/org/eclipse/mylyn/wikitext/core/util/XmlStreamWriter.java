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

/**
 *
 *
 * @author David Green
 */
public abstract class XmlStreamWriter {

	public abstract void close();

	public abstract void flush();

	public abstract String getPrefix(String uri);

	public abstract void setDefaultNamespace(String uri);

	public abstract void setPrefix(String prefix, String uri);

	public abstract void writeAttribute(String localName, String value);

	public abstract void writeAttribute(String namespaceURI, String localName,
			String value);

	public abstract void writeAttribute(String prefix, String namespaceURI,
			String localName, String value);

	public abstract void writeCData(String data);

	public abstract void writeCharacters(String text);

	public abstract void writeCharacters(char[] text, int start, int len);

	public abstract void writeComment(String data);

	public abstract void writeDTD(String dtd);

	public abstract void writeDefaultNamespace(String namespaceURI);

	public abstract void writeEmptyElement(String localName);

	public abstract void writeEmptyElement(String namespaceURI, String localName);

	public abstract void writeEmptyElement(String prefix, String localName,
			String namespaceURI);

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

	public abstract void writeStartElement(String prefix, String localName,
			String namespaceURI);

	/**
	 * Write an XML fragment directly to the output.  The given text is not processed or XML-encoded,
	 * since it is assumed to be a legal XML fragment.
	 */
	public abstract void writeLiteral(String literal);

}