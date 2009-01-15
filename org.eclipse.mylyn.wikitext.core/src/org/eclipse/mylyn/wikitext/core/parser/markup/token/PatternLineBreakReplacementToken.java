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
package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Replaces text that matches a pattern with a line break.
 * 
 * @see LineBreakReplacementTokenProcessor
 * 
 * @author David Green
 * @since 1.0
 */
public class PatternLineBreakReplacementToken extends PatternBasedElement {

	private final String pattern;

	public PatternLineBreakReplacementToken(String pattern) {
		this.pattern = pattern;
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
		return new LineBreakReplacementTokenProcessor();
	}

}
