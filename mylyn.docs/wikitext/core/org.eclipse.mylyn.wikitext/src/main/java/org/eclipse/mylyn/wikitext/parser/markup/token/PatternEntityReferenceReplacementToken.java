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
package org.eclipse.mylyn.wikitext.parser.markup.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Replaces strings matching a pattern with a specified entity reference.
 *
 * @see EntityReplacementTokenProcessor
 * @author David Green
 * @since 3.0
 */
public class PatternEntityReferenceReplacementToken extends PatternBasedElement {

	private final String pattern;

	private final String replacement;

	public PatternEntityReferenceReplacementToken(String pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}

	@Override
	protected String getPattern(int groupOffset) {
		return pattern;
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EntityReplacementTokenProcessor(replacement);
	}

}
