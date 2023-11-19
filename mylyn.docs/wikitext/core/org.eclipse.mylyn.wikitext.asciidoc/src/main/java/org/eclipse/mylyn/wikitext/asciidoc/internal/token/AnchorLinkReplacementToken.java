/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Anchor ID definition (macro form)
 */
public class AnchorLinkReplacementToken extends PatternBasedElement {

	final static String ANCHOR_PATTERN = "\\[\\[([^\\],]+)(,[^\\]]+)?\\]\\]"; //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return ANCHOR_PATTERN;
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new AnchorLinkReplacementTokenProcessor();
	}
}
