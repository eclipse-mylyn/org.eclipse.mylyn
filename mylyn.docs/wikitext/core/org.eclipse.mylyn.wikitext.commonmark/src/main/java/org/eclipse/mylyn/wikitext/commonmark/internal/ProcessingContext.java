/*******************************************************************************
 * Copyright (c) 2015, 2022 David Green and others.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.parser.IdGenerator;

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

	private final Map<String, NamedUriWithTitle> links;

	private final IdGenerator idGenerator;

	ProcessingContext(InlineParser inlineParser, Map<String, NamedUriWithTitle> links, IdGenerator idGenerator) {
		this.inlineParser = requireNonNull(inlineParser);
		this.links = requireNonNull(links);
		this.idGenerator = requireNonNull(idGenerator);
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
