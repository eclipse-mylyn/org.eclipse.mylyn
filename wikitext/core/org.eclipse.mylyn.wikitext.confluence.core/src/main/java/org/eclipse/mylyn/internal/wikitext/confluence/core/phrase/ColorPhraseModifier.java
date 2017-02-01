/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
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

public class ColorPhraseModifier extends PatternBasedElement {

	protected static final int CONTENT_GROUP = 2;

	private static class SimplePhraseModifierProcessor extends PatternBasedElementProcessor {
		private final SpanType spanType;

		public SimplePhraseModifierProcessor(SpanType spanType) {
			this.spanType = spanType;
		}

		@Override
		public void emit() {
			Attributes attributes = new Attributes();
			attributes.appendCssStyle("color: " + group(1) + ";"); //$NON-NLS-1$//$NON-NLS-2$
			getBuilder().beginSpan(spanType, attributes);

			getMarkupLanguage().emitMarkupLine(parser, state, getStart(this), getContent(this), 0);

			getBuilder().endSpan();
		}
	}

	public ColorPhraseModifier() {
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "\\{color:([^\\}]+)\\}(.*?)\\{color\\}"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	protected static String getContent(PatternBasedElementProcessor processor) {
		return processor.group(CONTENT_GROUP);
	}

	protected static int getStart(PatternBasedElementProcessor processor) {
		return processor.start(CONTENT_GROUP);
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new SimplePhraseModifierProcessor(SpanType.SPAN);
	}
}