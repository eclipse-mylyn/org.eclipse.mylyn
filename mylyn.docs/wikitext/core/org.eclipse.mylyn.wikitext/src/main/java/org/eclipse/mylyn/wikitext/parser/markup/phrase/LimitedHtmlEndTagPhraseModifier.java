/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup.phrase;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * A phrase modifier that detects HTML and XML end-tags, but only those that are explicitly specified. The detected markup is passed through
 * to the builder unescaped.
 *
 * @see LimitedHtmlStartTagPhraseModifier
 * @author David Green
 * @since 3.0
 */
public class LimitedHtmlEndTagPhraseModifier extends PatternBasedElement {

	private final String pattern;

	/**
	 * @param elementNames
	 *            the element names to be detected.
	 */
	public LimitedHtmlEndTagPhraseModifier(String... elementNames) {
		StringBuilder buf = new StringBuilder();
		buf.append("(</"); //$NON-NLS-1$
		buf.append("(?:"); //$NON-NLS-1$
		int index = 0;
		for (String elementName : elementNames) {
			if (index++ > 0) {
				buf.append("|"); //$NON-NLS-1$
			}
			buf.append(elementName);
		}
		buf.append(")\\s*>)"); //$NON-NLS-1$
		pattern = buf.toString();
	}

	@Override
	protected String getPattern(int groupOffset) {
		return pattern;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new LiteralPhraseModifierProcessor(false);
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

}
