/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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
package org.eclipse.mylyn.wikitext.textile.internal.phrase;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.textile.internal.Textile;

/**
 * A simple phrase modifier implementation that matches a pattern in text and emits a {@link SpanType span} containing the content of the
 * matched region.
 * 
 * @author David Green
 */
public class SimpleTextilePhraseModifier extends PatternBasedElement {

	public enum Mode {
		/**
		 * normal phrase content is processed
		 */
		NORMAL,
		/**
		 * special phrase content, ie: no token replacement
		 */
		SPECIAL,
		/**
		 * phrase may contain other nested phrases
		 */
		NESTING,
	}

	protected static final int CONTENT_GROUP = Textile.ATTRIBUTES_GROUP_COUNT + 1;

	protected static final int ATTRIBUTES_OFFSET = 1;

	private static class SimplePhraseModifierProcessor extends PatternBasedElementProcessor {
		private final SpanType spanType;

		private final Mode mode;

		public SimplePhraseModifierProcessor(SpanType spanType, Mode mode) {
			this.spanType = spanType;
			this.mode = mode;
		}

		@Override
		public void emit() {
			Attributes attributes = new Attributes();
			configureAttributes(this, attributes);
			getBuilder().beginSpan(spanType, attributes);
			switch (mode) {
				case NESTING:
					getMarkupLanguage().emitMarkupLine(parser, state, getStart(this), getContent(this), 0);
					break;
				case NORMAL:
					getMarkupLanguage().emitMarkupText(parser, state, getContent(this));
					break;
				case SPECIAL:
					getBuilder().characters(getContent(this));
					break;
			}
			getBuilder().endSpan();
		}
	}

	private final String delimiter;

	private final SpanType spanType;

	private final Mode mode;

	/**
	 * @param delimiter
	 *            the text pattern to detect
	 * @param spanType
	 *            the type of span to be emitted for this phrase modifier
	 * @param nesting
	 *            indicate if this phrase modifier allows nested phrase modifiers
	 */
	public SimpleTextilePhraseModifier(String delimiter, SpanType spanType, Mode mode) {
		this.delimiter = delimiter;
		this.spanType = spanType;
		this.mode = mode;
	}

	@Override
	protected String getPattern(int groupOffset) {
		String quotedDelimiter = quoteLite(getDelimiter());
		String firstCharacterOfDelimiter = quoteLite(getDelimiter().substring(0, 1));

		return quotedDelimiter + "(?!" + firstCharacterOfDelimiter + ")" + Textile.REGEX_ATTRIBUTES + "([^\\s" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
				+ quotedDelimiter + "]+|\\S(?:.*?\\S)?)" + // content //$NON-NLS-1$
				"(?<!" + firstCharacterOfDelimiter + ")" + quotedDelimiter; //$NON-NLS-1$ //$NON-NLS-2$
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
		return new SimplePhraseModifierProcessor(spanType, mode);
	}
}
