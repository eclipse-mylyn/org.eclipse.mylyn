/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

/**
 * A document builder that does nothing. Generally used when parsing should have other side-effects but the output is
 * not of interest.
 * 
 * @author David Green
 */
public class NoOpDocumentBuilder extends DocumentBuilder {

	@Override
	public void acronym(String text, String definition) {
		// ignore

	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		// ignore

	}

	@Override
	public void beginDocument() {
		// ignore

	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		// ignore

	}

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		// ignore

	}

	@Override
	public void characters(String text) {
		// ignore

	}

	@Override
	public void charactersUnescaped(String literal) {
		// ignore

	}

	@Override
	public void endBlock() {
		// ignore

	}

	@Override
	public void endDocument() {
		// ignore

	}

	@Override
	public void endHeading() {
		// ignore

	}

	@Override
	public void endSpan() {
		// ignore

	}

	@Override
	public void entityReference(String entity) {
		// ignore

	}

	@Override
	public void image(Attributes attributes, String url) {
		// ignore

	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		// ignore

	}

	@Override
	public void lineBreak() {
		// ignore

	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		// ignore

	}

}
