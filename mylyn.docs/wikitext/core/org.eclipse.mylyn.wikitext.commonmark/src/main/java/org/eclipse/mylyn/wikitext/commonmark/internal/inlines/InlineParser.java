/*******************************************************************************
 * Copyright (c) 2015, 2022 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EntityReferences;
import org.eclipse.mylyn.wikitext.parser.builder.NoOpDocumentBuilder;

public class InlineParser {

	private final List<SourceSpan> spans;

	InlineParser(SourceSpan... spans) {
		this(Arrays.asList(spans));
	}

	public InlineParser(List<SourceSpan> spans) {
		this.spans = List.copyOf(spans);
	}

	public void emit(ProcessingContext context, DocumentBuilder builder, TextSegment textSegment) {
		List<Inline> inlines = parse(context, textSegment);
		emit(builder, inlines);
	}

	public String toStringContent(ProcessingContext context, TextSegment textSegment) {
		List<Inline> inlines = parse(context, textSegment);
		return toStringContent(inlines);
	}

	public static void emit(DocumentBuilder builder, List<Inline> inlines) {
		for (Inline inline : inlines) {
			builder.setLocator(inline.getLocator());
			inline.emit(builder);
		}
	}

	public void createContext(ProcessingContextBuilder contextBuilder, TextSegment textSegment) {
		for (Inline inline : parse(contextBuilder.build(), textSegment)) {
			inline.createContext(contextBuilder);
		}
	}

	public List<Inline> parse(ProcessingContext context, TextSegment segment) {
		Cursor cursor = new Cursor(segment);

		List<Inline> inlines = new ArrayList<>();
		while (cursor.hasChar()) {
			consumeOne(context, inlines, cursor);
		}

		return secondPass(inlines);
	}

	static List<Inline> secondPass(List<Inline> inlines) {
		List<Inline> processedInlines = List.copyOf(inlines);
		Optional<InlinesSubstitution> substitution = Optional.empty();
		do {
			for (Inline inline : processedInlines) {
				substitution = inline.secondPass(processedInlines);
				if (substitution.isPresent()) {
					processedInlines = substitution.get().apply(processedInlines);
					break;
				}
			}
		} while (substitution.isPresent());
		return processedInlines;
	}

	static String toStringContent(List<Inline> contents) {
		final StringBuilder stringBuilder = new StringBuilder();
		DocumentBuilder altDocumentBuilder = new NoOpDocumentBuilder() {

			@Override
			public void characters(String text) {
				stringBuilder.append(text);
			}

			@Override
			public void entityReference(String entity) {
				stringBuilder.append(firstNonNull(EntityReferences.instance().equivalentString(entity), ""));
			}
		};
		for (Inline inline : contents) {
			inline.emit(altDocumentBuilder);
		}
		return stringBuilder.toString();
	}

	private void consumeOne(ProcessingContext context, List<Inline> inlines, Cursor cursor) {
		for (SourceSpan span : spans) {
			Optional<? extends Inline> inline = span.createInline(cursor);
			if (inline.isPresent()) {
				inline.get().apply(context, inlines, cursor);
				return;
			}
		}
		throw new IllegalStateException();
	}

}
