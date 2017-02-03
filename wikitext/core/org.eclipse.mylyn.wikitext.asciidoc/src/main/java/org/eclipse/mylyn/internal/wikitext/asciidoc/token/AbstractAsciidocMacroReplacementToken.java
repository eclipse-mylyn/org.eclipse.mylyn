/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;

/**
 * Support for AsciiDoc Macros
 */
public abstract class AbstractAsciidocMacroReplacementToken extends PatternBasedElement {

	private static final String MACRO_PATTERN = ":([^\\[]+)\\[([^\\]]+)?\\]"; //$NON-NLS-1$

	private final String macroName;

	public AbstractAsciidocMacroReplacementToken(String macroName) {
		this.macroName = macroName;
	}

	@Override
	protected String getPattern(int groupOffset) {
		return macroName + MACRO_PATTERN;
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}
}
