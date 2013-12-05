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

import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

public class HtmlSubsetDocumentBuilder extends DocumentBuilder {

	private final DocumentBuilder delegate;

	public HtmlSubsetDocumentBuilder(Writer out, boolean formatting) {
		this(new HtmlDocumentBuilder(checkNotNull(out, "Must provide a writer"), formatting)); //$NON-NLS-1$
	}

	HtmlSubsetDocumentBuilder(DocumentBuilder delegate) {
		this.delegate = checkNotNull(delegate, "Must provide a delegate"); //$NON-NLS-1$
	}

	@Override
	public void beginDocument() {
		delegate.beginDocument();
	}

	@Override
	public void endDocument() {
		delegate.endDocument();
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		delegate.beginBlock(type, attributes);
	}

	@Override
	public void endBlock() {
		delegate.endBlock();
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		delegate.beginSpan(type, attributes);
	}

	@Override
	public void endSpan() {
		delegate.endSpan();
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		delegate.beginHeading(level, attributes);
	}

	@Override
	public void endHeading() {
		delegate.endHeading();
	}

	@Override
	public void characters(String text) {
		delegate.characters(text);
	}

	@Override
	public void entityReference(String entity) {
		delegate.entityReference(entity);
	}

	@Override
	public void image(Attributes attributes, String url) {
		delegate.image(attributes, url);
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		delegate.link(attributes, hrefOrHashName, text);
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		delegate.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}

	@Override
	public void acronym(String text, String definition) {
		delegate.acronym(text, definition);
	}

	@Override
	public void lineBreak() {
		delegate.lineBreak();
	}

	@Override
	public void charactersUnescaped(String literal) {
		delegate.charactersUnescaped(literal);
	}

}
