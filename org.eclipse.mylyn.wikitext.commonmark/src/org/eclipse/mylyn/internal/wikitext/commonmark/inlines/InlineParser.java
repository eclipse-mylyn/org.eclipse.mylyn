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

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContextBuilder;
import org.eclipse.mylyn.internal.wikitext.commonmark.TextSegment;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class InlineParser {

	private final List<SourceSpan> spans;

	public InlineParser(SourceSpan... spans) {
		this(Arrays.asList(spans));
	}

	InlineParser(List<SourceSpan> spans) {
		this.spans = ImmutableList.copyOf(spans);
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
		List<Inline> processedInlines = ImmutableList.copyOf(inlines);
		Optional<InlinesSubstitution> substitution = Optional.absent();
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
