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
package org.eclipse.mylyn.internal.wikitext.confluence.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A phrase modifier that is wrapped with confluence-style words, for example: {quote}
 * 
 * @author David Green
 */
public class ConfluenceWrappedPhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = 1;

	private static class SimplePhraseModifierProcessor extends PatternBasedElementProcessor {
		private final SpanType spanType;

		private final boolean nesting;

		public SimplePhraseModifierProcessor(SpanType spanType, boolean nesting) {
			this.spanType = spanType;
			this.nesting = nesting;
		}

		@Override
		public void emit() {
			Attributes attributes = new Attributes();
			getBuilder().beginSpan(spanType, attributes);
			if (nesting) {
				getMarkupLanguage().emitMarkupLine(parser, state, getStart(this), getContent(this), 0);
			} else {
				getMarkupLanguage().emitMarkupText(parser, state, getContent(this));
			}
			getBuilder().endSpan();
		}
	}

	private final String startDelimiter;

	private final String endDelimiter;

	private final SpanType spanType;

	private final boolean nesting;

	public ConfluenceWrappedPhraseModifier(String delimiter, SpanType spanType, boolean nesting) {
		this.startDelimiter = delimiter;
		this.endDelimiter = delimiter;
		this.spanType = spanType;
		this.nesting = nesting;
	}

	@Override
	protected String getPattern(int groupOffset) {
		String quotedStartDelimiter = quoteLite(startDelimiter);
		String quotedDelimiter = quoteLite(endDelimiter);

		return quotedStartDelimiter + "(.*?)" + quotedDelimiter; //$NON-NLS-1$
	}

	private String quoteLite(String literal) {
		StringBuilder buf = new StringBuilder(literal.length() * 2);
		for (int x = 0; x < literal.length(); ++x) {
			char c = literal.charAt(x);
			switch (c) {
			case '^':
			case '*':
			case '?':
			case '+':
			case '{':
			case '}':
			case '-':
				buf.append('\\');
			}
			buf.append(c);
		}
		return buf.toString();
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
