/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author Stefan Seelmann
 */
public class SimplePhraseModifier extends PatternBasedElement {

	private final String delimiter;

	private final SpanType spanType;

	private final boolean wordBoundary;

	public SimplePhraseModifier(String delimiter, SpanType spanType) {
		this(delimiter, spanType, false);
	}

	public SimplePhraseModifier(String delimiter, SpanType spanType, boolean wordBoundary) {
		this.delimiter = delimiter;
		this.spanType = spanType;
		this.wordBoundary = wordBoundary;
	}

	@Override
	protected String getPattern(int groupOffset) {
		final String quotedDelimiter = Pattern.quote(delimiter);
		String pattern;
		if (wordBoundary) {
			// word boundary implicitly assumes single occurrence
			// of the pattern (e.g. * and not **)
			pattern = quotedDelimiter + "(?!" + quotedDelimiter + ")" + " *" + "(.+?)" + " *" + quotedDelimiter + "(?!" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$ //$NON-NLS-6$
					+ quotedDelimiter + ")"; //$NON-NLS-1$
			pattern = "(^|\\W)" + pattern + "($|\\W)"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			pattern = quotedDelimiter + " *" + "(.+?)" + " *" + quotedDelimiter; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return pattern;
	}

	@Override
	protected int getPatternGroupCount() {
		return wordBoundary ? 3 : 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new CodePhraseModifierProcessor();
	}

	private class CodePhraseModifierProcessor extends PatternBasedElementProcessor {

		@Override
		public int getLineStartOffset() {
			if (wordBoundary) {
				final int value = end(1);
				if (value >= 0) {
					return value;
				}
			}
			return super.getLineStartOffset();
		}

		@Override
		public int getLineEndOffset() {
			if (wordBoundary) {
				final int value = start(3);
				if (value >= 0) {
					return value;
				}
			}
			return super.getLineEndOffset();
		}

		@Override
		public void emit() {
			String content = group(wordBoundary ? 2 : 1);
			getBuilder().beginSpan(spanType, new Attributes());
			getMarkupLanguage().emitMarkupText(parser, state, content);
			getBuilder().endSpan();
		}
	}
}
