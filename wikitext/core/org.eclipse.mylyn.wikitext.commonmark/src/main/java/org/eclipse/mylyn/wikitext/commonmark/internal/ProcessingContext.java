/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.parser.IdGenerator;

import com.google.common.collect.ImmutableMap;

public class ProcessingContext {

	public static ProcessingContextBuilder builder() {
		return new ProcessingContextBuilder();
	}

	public static class NamedUriWithTitle {

		private final String name;

		private final String uri;

		private final String title;

		public NamedUriWithTitle(String name, String uri, String title) {
			this.name = name;
			this.uri = uri;
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public String getUri() {
			return uri;
		}

		public String getTitle() {
			return title;
		}
	}

	private final InlineParser inlineParser;

	private final ImmutableMap<String, NamedUriWithTitle> links;

	private final IdGenerator idGenerator;

	ProcessingContext(InlineParser inlineParser, ImmutableMap<String, NamedUriWithTitle> links,
			IdGenerator idGenerator) {
		this.inlineParser = checkNotNull(inlineParser);
		this.links = checkNotNull(links);
		this.idGenerator = checkNotNull(idGenerator);
	}

	public boolean isEmpty() {
		return links.isEmpty();
	}

	public NamedUriWithTitle namedUriWithTitle(String name) {
		return links.get(name.toLowerCase());
	}

	public String generateHeadingId(int headingLevel, String headingText) {
		return idGenerator.newId("h" + headingLevel, headingText);
	}

	public InlineParser getInlineParser() {
		return inlineParser;
	}
}
