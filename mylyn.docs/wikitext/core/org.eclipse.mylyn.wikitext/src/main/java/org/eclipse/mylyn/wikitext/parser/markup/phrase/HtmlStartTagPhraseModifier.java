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
 * A phrase modifier that detects HTML and XML tags in the source, passing them through to the builder as either escaped or unescaped text.
 *
 * @see HtmlEndTagPhraseModifier
 * @author David Green
 * @since 3.0
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
