/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.creole.core.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * @author Igor Malinin
 */
public class SimplePhraseModifier extends PatternBasedElement {

	private static final int CONTENT_GROUP = 1;

	private static final String NOT_A_LINK = "(?<!https?://)"; //$NON-NLS-1$

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

	private final String delimiter;

	private final SpanType[] spanType;

	private final boolean nesting;

	public SimplePhraseModifier(String delimiter, SpanType spanType) {
		this(delimiter, new SpanType[] { spanType });
	}

	public SimplePhraseModifier(String delimiter, SpanType spanType, boolean nesting) {
		this(delimiter, new SpanType[] { spanType }, nesting);
	}

	public SimplePhraseModifier(String delimiter, SpanType[] spanType) {
		this(delimiter, spanType, false);
	}

	public SimplePhraseModifier(String delimiter, SpanType[] spanType, boolean nesting) {
		this.delimiter = delimiter;
		this.spanType = spanType;
		this.nesting = nesting;
	}

	@Override
	protected String getPattern(int groupOffset) {
		// links can be italic
		return Pattern.quote(delimiter) + NOT_A_LINK + "(.*?)(?>" + //$NON-NLS-1$
				Pattern.quote(delimiter) + NOT_A_LINK + "|$)"; //$NON-NLS-1$
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
