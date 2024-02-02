/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
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

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.parser.markup.phrase.LiteralPhraseModifierProcessor;

/**
 * A phrase modifier that detects HTML and XML entities in the source.
 * 
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class PreserverHtmlEntityToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(&[A-Za-z]{1,32}+;)"; //$NON-NLS-1$
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
