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
package org.eclipse.mylyn.wikitext.core.parser.markup.phrase;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A phrase modifier that detects HTML and XML tags, but only those that are explicitly specified. The detected markup
 * is passed through to the builder unescaped.
 * 
 * @see LimitedHtmlStartTagPhraseModifier
 * 
 * @author David Green
 */
public class LimitedHtmlStartTagPhraseModifier extends PatternBasedElement {

	private final String pattern;

	/**
	 * @param elementNames
	 *            the element names to be detected.
	 */
	public LimitedHtmlStartTagPhraseModifier(String... elementNames) {
		StringBuilder buf = new StringBuilder();
		buf.append("(<"); //$NON-NLS-1$
		buf.append("(?:"); //$NON-NLS-1$
		int index = 0;
		for (String elementName : elementNames) {
			if (index++ > 0) {
				buf.append("|"); //$NON-NLS-1$
			}
			buf.append(elementName);
		}
		buf.append(")(?:\\s*[a-zA-Z][a-zA-Z0-9_:-]*=\"[^\"]*\")*\\s*/?>)"); //$NON-NLS-1$
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
