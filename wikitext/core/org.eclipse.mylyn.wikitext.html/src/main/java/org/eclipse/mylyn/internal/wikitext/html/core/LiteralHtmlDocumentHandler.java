/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

public class LiteralHtmlDocumentHandler implements HtmlDocumentHandler {

	private final String prefix;

	private final String suffix;

	public LiteralHtmlDocumentHandler(String prefix, String suffix) {
		this.prefix = checkNotNull(prefix);
		this.suffix = checkNotNull(suffix);
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
