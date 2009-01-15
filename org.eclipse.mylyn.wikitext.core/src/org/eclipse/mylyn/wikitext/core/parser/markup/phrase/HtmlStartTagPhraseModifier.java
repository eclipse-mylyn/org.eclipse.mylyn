/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
 * A phrase modifier that detects HTML and XML tags in the source, passing them through to the builder as either escaped
 * or unescaped text.
 * 
 * @see HtmlEndTagPhraseModifier
 * 
 * @author David Green
 * @since 1.0
 */
public class HtmlStartTagPhraseModifier extends PatternBasedElement {

	private final boolean escaping;

	/**
	 * construct this as unescaping
	 */
	public HtmlStartTagPhraseModifier() {
		this(false);
	}

	/**
	 * @param escaping
	 *            indicate if the markup should be escaped
	 */
	public HtmlStartTagPhraseModifier(boolean escaping) {
		this.escaping = escaping;
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "(<[a-zA-Z][a-zA-Z0-9_:-]*(?:\\s*[a-zA-Z][a-zA-Z0-9_:-]*=\"[^\"]*\")*\\s*/?>)"; //$NON-NLS-1$
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new LiteralPhraseModifierProcessor(escaping);
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

}
