/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.creole.internal.phrase;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.parser.markup.phrase.LiteralPhraseModifierProcessor;

/**
 * @author Igor Malinin
 */
public class NowikiPhraseModifier extends PatternBasedElement {

	private static class NowikiPhraseModifierProcessor extends LiteralPhraseModifierProcessor {

		public NowikiPhraseModifierProcessor() {
			super(true);
		}

		@Override
		public void emit() {
			getBuilder().beginSpan(SpanType.CODE, new Attributes());
			super.emit();
			getBuilder().endSpan();
		}
	}

	@Override
	public String getPattern(int groupOffset) {
		return "\\{\\{\\{((?:(?!\\}\\}\\}).)*)\\}\\}\\}"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new NowikiPhraseModifierProcessor();
	}

}
