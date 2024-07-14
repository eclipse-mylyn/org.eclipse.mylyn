/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext.NamedUriWithTitle;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.util.Strings;

public class ProcessingContextBuilder {

	private final Map<String, NamedUriWithTitle> linkByName = new HashMap<>();

	private IdGenerationStrategy idGenerationStrategy = new CommonMarkIdGenerationStrategy();

	private InlineParser inlineParser;

	public ProcessingContextBuilder referenceDefinition(String name, String href, String title) {
		if (!Strings.isNullOrEmpty(name)) {
			String key = name.toLowerCase(Locale.ROOT);
			if (!linkByName.containsKey(key)) {
				linkByName.put(key, new NamedUriWithTitle(name, href, title));
			}
		}
		return this;
	}

	public ProcessingContextBuilder idGenerationStrategy(IdGenerationStrategy idGenerationStrategy) {
		this.idGenerationStrategy = idGenerationStrategy;
		return this;
	}

	public ProcessingContext build() {
		return new ProcessingContext(getInlineParser(), Map.copyOf(linkByName), idGenerator());
	}

	public ProcessingContextBuilder inlineParser(InlineParser inlineParser) {
		this.inlineParser = inlineParser;
		return this;
	}

	public InlineParser getInlineParser() {
		return inlineParser == null ? InlineContent.commonMarkStrict() : inlineParser;
	}

	private IdGenerator idGenerator() {
		if (idGenerationStrategy == null) {
			return new IdGenerator() {

				@Override
				public String newId(String type, String text) {
					return null;
				}
			};
		}
		IdGenerator generator = new IdGenerator();
		generator.setGenerationStrategy(idGenerationStrategy);
		return generator;
	}

	ProcessingContextBuilder() {
		// prevent instantiation
	}
}
