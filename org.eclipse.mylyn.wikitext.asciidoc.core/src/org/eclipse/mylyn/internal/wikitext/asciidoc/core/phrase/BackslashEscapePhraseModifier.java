/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.token.PatternLiteralReplacementToken;

/**
 * 
 * 
 * @author Stefan Seelmann
 */
public class BackslashEscapePhraseModifier extends PatternLiteralReplacementToken {

	public BackslashEscapePhraseModifier(String token) {
		super("(" + Pattern.quote("\\" + token) + ")", token); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
