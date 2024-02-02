/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.parser.builder;

import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;

/**
 * An extension of {@link HtmlDocumentBuilder} which is responsible for emitting the HTML document tags and content which wrap the body's
 * content. Normally this would include the {@code <html>} and {@code <body>} tags, however implementations may emit any content.
 * <p>
 * Example implementation:
 * </p>
 *
 * <pre>
 * <code>
 * class MyHtmlDocumentHandler implements HtmlDocumentHandler {
 *
 * 	&#64;Override
 * 	public void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
 * 		writer.writeStartDocument("utf-8", "1.0");
 * 		writer.writeStartElement(builder.getHtmlNsUri(), "html");
 * 		writer.writeDefaultNamespace(builder.getHtmlNsUri());
 * 		writer.writeStartElement(builder.getHtmlNsUri(), "body");
 * 	}
 *
 * 	&#64;Override
 * 	public void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
 * 		writer.writeEndElement();
 * 		writer.writeEndElement();
 * 		writer.writeEndDocument();
 * 	}
 * }
 * </code>
 * </pre>
 *
 * @author David Green
 * @since 3.0
 */
public interface HtmlDocumentHandler {
	/**
	 * Writes the content that occurs at the start of the document up to and including the {@code <body>} tag (if any).
	 *
	 * @param builder
	 *            the builder the builder for which the handler is being invoked
	 * @param writer
	 *            the writer to which content is written
	 */
	void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer);

	/**
	 * Writes the content that occurs at the end of the document startign with the {@code </body>} closing tag (if any).
	 *
	 * @param builder
	 *            the builder the builder for which the handler is being invoked
	 * @param writer
	 *            the writer to which content is written
	 */
	void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer);
}
