/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
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
 * @author David Green
 */
public class EmphasisPhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = 1;

	private static class EmphasisProcessor extends PatternBasedElementProcessor {

		public EmphasisProcessor() {
		}

		@Override
		public void emit() {
			Attributes attributes = new Attributes();
			getBuilder().beginSpan(SpanType.EMPHASIS, attributes);
			getMarkupLanguage().emitMarkupLine(parser, state, getStart(this), getContent(this), 0);
			getBuilder().endSpan();
		}
	}

	public EmphasisPhraseModifier() {
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "_(?!(?:_|\\s))" + //$NON-NLS-1$
				"((?:(?:\\[[^\\]]+\\])|[^_])+)" + //$NON-NLS-1$
				"(?<!(?:_|\\s))_"; //$NON-NLS-1$
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
		return new EmphasisProcessor();
	}
}