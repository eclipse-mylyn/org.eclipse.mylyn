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
package org.eclipse.mylyn.wikitext.parser.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.Locator;

/**
 * A document builder that dispatches events to one or more delegates.
 *
 * @author David Green
 * @since 3.0
 */
public class MultiplexingDocumentBuilder extends DocumentBuilder {

	private final List<DocumentBuilder> builders = new ArrayList<>();

	public MultiplexingDocumentBuilder(DocumentBuilder... delegates) {
		builders.addAll(Arrays.asList(delegates));
	}

	public void addDocumentBuilder(DocumentBuilder delegate) {
		builders.add(delegate);
	}

	@Override
	public void acronym(String text, String definition) {
		for (DocumentBuilder builder : builders) {
			builder.acronym(text, definition);
		}
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		for (DocumentBuilder builder : builders) {
			builder.beginBlock(type, attributes);
		}
	}

	@Override
	public void beginDocument() {
		for (DocumentBuilder builder : builders) {
			builder.beginDocument();
		}
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		for (DocumentBuilder builder : builders) {
			builder.beginHeading(level, attributes);
		}
	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		for (DocumentBuilder builder : builders) {
			builder.beginSpan(type, attributes);
		}
	}

	@Override
	public void characters(String text) {
		for (DocumentBuilder builder : builders) {
			builder.characters(text);
		}
	}

	@Override
	public void charactersUnescaped(String literal) {
		for (DocumentBuilder builder : builders) {
			builder.charactersUnescaped(literal);
		}
	}

	@Override
	public void endBlock() {
		for (DocumentBuilder builder : builders) {
			builder.endBlock();
		}
	}

	@Override
	public void endDocument() {
		for (DocumentBuilder builder : builders) {
			builder.endDocument();
		}
	}

	@Override
	public void flush() {
		for (DocumentBuilder builder : builders) {
			builder.flush();
		}
	}

	@Override
	public void endHeading() {
		for (DocumentBuilder builder : builders) {
			builder.endHeading();
		}
	}

	@Override
	public void endSpan() {
		for (DocumentBuilder builder : builders) {
			builder.endSpan();
		}
	}

	@Override
	public void entityReference(String entity) {
		for (DocumentBuilder builder : builders) {
			builder.entityReference(entity);
		}
	}

	@Override
	public void image(Attributes attributes, String url) {
		for (DocumentBuilder builder : builders) {
			builder.image(attributes, url);
		}
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		for (DocumentBuilder builder : builders) {
			builder.imageLink(linkAttributes, imageAttributes, href, imageUrl);
		}
	}

	@Override
	public void lineBreak() {
		for (DocumentBuilder builder : builders) {
			builder.lineBreak();
		}
	}

	@Override
	public void horizontalRule() {
		for (DocumentBuilder builder : builders) {
			builder.horizontalRule();
		}
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		for (DocumentBuilder builder : builders) {
			builder.link(attributes, hrefOrHashName, text);
		}
	}

	@Override
	public void setLocator(Locator locator) {
		super.setLocator(locator);
		for (DocumentBuilder builder : builders) {
			builder.setLocator(locator);
		}
	}

}
