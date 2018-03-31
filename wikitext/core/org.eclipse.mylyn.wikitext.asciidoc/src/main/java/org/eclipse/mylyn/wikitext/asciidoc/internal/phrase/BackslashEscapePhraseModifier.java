/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
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

package org.eclipse.mylyn.wikitext.asciidoc.internal.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.token.PatternLiteralReplacementToken;

/**
 * @author Stefan Seelmann
 */
public class BackslashEscapePhraseModifier extends PatternLiteralReplacementToken {

	public BackslashEscapePhraseModifier(String token) {
		super(buildPattern(token), token);
	}

	private static String buildPattern(String token) {
		if ("**".equals(token) || "__".equals(token) || "++".equals(token)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// these require a double escape
			return "(" + Pattern.quote("\\\\" + token) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return "(" + Pattern.quote("\\" + token) + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
