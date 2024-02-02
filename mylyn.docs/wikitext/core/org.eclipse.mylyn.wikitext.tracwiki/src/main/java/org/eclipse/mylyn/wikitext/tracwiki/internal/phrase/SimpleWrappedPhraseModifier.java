/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.tracwiki.internal.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class SimpleWrappedPhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = 1;

	private static class SimplePhraseModifierProcessor extends PatternBasedElementProcessor {
		private final SpanType[] spanType;

		private final boolean nesting;

		public SimplePhraseModifierProcessor(SpanType[] spanType, boolean nesting) {
			this.spanType = spanType;
			this.nesting = nesting;
		}

		@Override
		public void emit() {
			for (SpanType type : spanType) {
				getBuilder().beginSpan(type, new Attributes());
			}
			if (nesting) {
				getMarkupLanguage().emitMarkupLine(parser, state, getStart(this), getContent(this), 0);
			} else {
				getMarkupLanguage().emitMarkupText(parser, state, getContent(this));
			}
			for (int x = 0; x < spanType.length; ++x) {
				getBuilder().endSpan();
			}
		}
	}

	private final String startDelimiter;

	private final String endDelimiter;

	private final SpanType[] spanType;

	private final boolean nesting;

	public SimpleWrappedPhraseModifier(String startDelimiter, String endDelimiter, SpanType[] spanType) {
		this(startDelimiter, endDelimiter, spanType, false);
	}

	public SimpleWrappedPhraseModifier(String startDelimiter, String endDelimiter, SpanType[] spanType,
			boolean nesting) {
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
		this.spanType = spanType;
		this.nesting = nesting;
	}

	@Override
	protected String getPattern(int groupOffset) {
		String pattern = Pattern.quote(startDelimiter) + "([^\\s-](?:.*?[^\\s-])?)(?:(?<=[^!])" + // content: note that we dont allow preceding '-' or trailing '-' to avoid conflict with strikethrough and emdash //$NON-NLS-1$
				Pattern.quote(endDelimiter) + ")"; //$NON-NLS-1$
		return pattern;
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	protected static String getContent(PatternBasedElementProcessor processor) {
		return processor.group(CONTENT_GROUP);
	}

	protected static int getStart(PatternBasedElementProcessor processor) {
		return processor.start(CONTENT_GROUP);
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new SimplePhraseModifierProcessor(spanType, nesting);
	}
}
