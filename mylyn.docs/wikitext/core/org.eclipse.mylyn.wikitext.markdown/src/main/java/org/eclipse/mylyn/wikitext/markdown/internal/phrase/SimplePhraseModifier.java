/*******************************************************************************
 * Copyright (c) 2012, 2015 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

public class SimplePhraseModifier extends PatternBasedElement {

	private final String delimiter;

	private final SpanType spanType;

	public SimplePhraseModifier(String delimiter, SpanType spanType) {
		this.delimiter = delimiter;
		this.spanType = spanType;
	}

	@Override
	protected String getPattern(int groupOffset) {
		String quotedDelimiter = Pattern.quote(delimiter);
		return quotedDelimiter + " *" + "(.+?)" + " *" + quotedDelimiter; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			getMarkupLanguage().emitMarkupText(parser, state, content);
			getBuilder().endSpan();
		}
	}
}
