/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Jeremie Bresson - Bug 391850
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

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

	public SimpleWrappedPhraseModifier(String startDelimiter, String endDelimiter, SpanType[] spanType, boolean nesting) {
		this.startDelimiter = startDelimiter;
		this.endDelimiter = endDelimiter;
		this.spanType = spanType;
		this.nesting = nesting;
	}

	@Override
	protected String getPattern(int groupOffset) {
		String quotedStartDelimiter = quoteLite(startDelimiter);
		String quotedStartDelimiterLastChar = quoteLite(startDelimiter.substring(startDelimiter.length() - 1,
				startDelimiter.length()));
		String quotedEndDelimiter = quoteLite(endDelimiter);

		return quotedStartDelimiter + //
				"(?!" + quotedStartDelimiterLastChar + ")" + //  //$NON-NLS-1$//$NON-NLS-2$
				"([^" + quotedEndDelimiter + "]+|\\S(?:.*?\\S)?)" + // content //$NON-NLS-1$ //$NON-NLS-2$
				quotedEndDelimiter;
	}

	/**
	 * quote a literal for use in a regular expression
	 */
	private String quoteLite(String literal) {
		StringBuilder buf = new StringBuilder(literal.length() * 2);
		for (int x = 0; x < literal.length(); ++x) {
			char c = literal.charAt(x);
			switch (c) {
			case '^':
			case '*':
			case '?':
			case '+':
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
