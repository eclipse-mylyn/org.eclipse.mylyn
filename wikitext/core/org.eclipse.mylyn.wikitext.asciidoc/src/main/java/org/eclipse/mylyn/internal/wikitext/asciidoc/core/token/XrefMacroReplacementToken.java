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

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Cross-Reference links (macro form)
 */
public class XrefMacroReplacementToken extends AbstractAsciidocMacroReplacementToken {

	public XrefMacroReplacementToken() {
		super("xref"); //$NON-NLS-1$
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new XrefReplacementTokenProcessor(0);
	}
}