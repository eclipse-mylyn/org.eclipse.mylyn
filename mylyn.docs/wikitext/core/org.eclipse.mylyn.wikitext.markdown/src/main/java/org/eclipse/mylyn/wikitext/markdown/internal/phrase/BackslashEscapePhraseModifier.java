/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.token.PatternLiteralReplacementToken;

public class BackslashEscapePhraseModifier extends PatternLiteralReplacementToken {

	public BackslashEscapePhraseModifier(String token) {
		super("(" + Pattern.quote("\\" + token) + ")", token); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
