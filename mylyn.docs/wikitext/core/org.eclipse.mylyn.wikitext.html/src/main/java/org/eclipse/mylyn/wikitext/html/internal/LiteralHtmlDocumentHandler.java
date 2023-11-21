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

package org.eclipse.mylyn.wikitext.html.internal;

import static java.util.Objects.requireNonNull;

import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.util.XmlStreamWriter;

public class LiteralHtmlDocumentHandler implements HtmlDocumentHandler {

	private final String prefix;

	private final String suffix;

	public LiteralHtmlDocumentHandler(String prefix, String suffix) {
		this.prefix = requireNonNull(prefix);
		this.suffix = requireNonNull(suffix);
	}

	@Override
	public void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
		writer.writeLiteral(prefix);
	}

	@Override
	public void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
		writer.writeLiteral(suffix);
	}

}
