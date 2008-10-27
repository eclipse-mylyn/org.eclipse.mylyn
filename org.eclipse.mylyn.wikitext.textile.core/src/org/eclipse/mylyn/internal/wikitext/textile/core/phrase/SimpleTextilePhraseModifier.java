/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.textile.core.phrase;

import org.eclipse.mylyn.internal.wikitext.textile.core.Textile;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A simple phrase modifier implementation that matches a pattern in text and emits a {@link SpanType span} containing
 * the content of the matched region.
 * 
 * @author David Green
 */
public class SimpleTextilePhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = Textile.ATTRIBUTES_GROUP_COUNT + 1;

	protected static final int ATTRIBUTES_OFFSET = 1;

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
			configureAttributes(this, attributes);
			getBuilder().beginSpan(spanType, attributes);
			if (nesting) {
				getMarkupLanguage().emitMarkupLine(parser, state, state.getLineCharacterOffset() + getStart(this),
						getContent(this), 0);
			} else {
				getMarkupLanguage().emitMarkupText(parser, state, getContent(this));
			}
			getBuilder().endSpan();
		}
	}

	private final String delimiter;

	private final SpanType spanType;

	private final boolean nesting;

	/**
	 * 
	 * @param delimiter
	 *            the text pattern to detect
	 * @param spanType
	 *            the type of span to be emitted for this phrase modifier
	 * @param nesting
	 *            indicate if this phrase modifier allows nested phrase modifiers
	 */
	public SimpleTextilePhraseModifier(String delimiter, SpanType spanType, boolean nesting) {
		this.delimiter = delimiter;
		this.spanType = spanType;
		this.nesting = nesting;
	}

	@Override
	protected String getPattern(int groupOffset) {
		String quotedDelimiter = quoteLite(getDelimiter());

		return quotedDelimiter + "(?!" + quotedDelimiter + ")" + Textile.REGEX_ATTRIBUTES + "([^\\s" + quotedDelimiter
				+ "]+|\\S[^" + quotedDelimiter + "]*[^\\s" + quotedDelimiter + "])" + // content
				quotedDelimiter;
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
		return Textile.ATTRIBUTES_GROUP_COUNT + 1;
	}

	protected String getDelimiter() {
		return delimiter;
	}

	protected static void configureAttributes(PatternBasedElementProcessor processor, Attributes attributes) {
		Textile.configureAttributes(processor, attributes, ATTRIBUTES_OFFSET, false);
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
