/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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

	private final Mode mode;

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

	public SimplePhraseModifier(String delimiter, SpanType spanType, Mode mode) {
		this.delimiter = delimiter;
		this.spanType = spanType;
		this.mode = mode;
	}

	@Override
	protected String getPattern(int groupOffset) {
		final String quotedDelimiter = Pattern.quote(delimiter);
		String pattern;
		pattern = quotedDelimiter + " *" + "(.+?)" + " *" + quotedDelimiter; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return pattern;
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new CodePhraseModifierProcessor();
	}

	private class CodePhraseModifierProcessor extends PatternBasedElementProcessor {

		@Override
		public void emit() {
			String content = group(1);
			getBuilder().beginSpan(spanType, new Attributes());
			switch (mode) {
			case NORMAL:
				getMarkupLanguage().emitMarkupText(parser, state, content);
				break;
			case NESTING:
				int contentStart = start(1);
				getMarkupLanguage().emitMarkupLine(parser, state, contentStart, content, 0);
				break;
			case SPECIAL:
				getBuilder().charactersUnescaped(content);
				break;
			}
			getBuilder().endSpan();
		}
	}
}
