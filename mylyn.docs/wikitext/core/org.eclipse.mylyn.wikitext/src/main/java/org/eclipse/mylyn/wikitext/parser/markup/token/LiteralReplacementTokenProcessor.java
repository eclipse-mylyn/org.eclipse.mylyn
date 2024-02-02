/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * A processor that emits a literal unescaped. Useful for emitting markup that is not directly supported by the {@link DocumentBuilder
 * builder}.
 *
 * @author David Green
 * @since 3.0
 */
public class LiteralReplacementTokenProcessor extends PatternBasedElementProcessor {

	private final String literal;

	public LiteralReplacementTokenProcessor(String literal) {
		this.literal = literal;
	}

	@Override
	public void emit() {
		getBuilder().charactersUnescaped(literal);
	}

}
