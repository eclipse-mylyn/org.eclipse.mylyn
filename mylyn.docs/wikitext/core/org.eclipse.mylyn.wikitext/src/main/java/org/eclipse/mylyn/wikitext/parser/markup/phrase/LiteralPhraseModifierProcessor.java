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
package org.eclipse.mylyn.wikitext.parser.markup.phrase;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * A processor that emits the first group as characters, optionally unescaped.
 *
 * @see DocumentBuilder#characters(String)
 * @see DocumentBuilder#charactersUnescaped(String)
 * @author David Green
 * @since 3.0
 */
public class LiteralPhraseModifierProcessor extends PatternBasedElementProcessor {

	private final boolean escaping;

	private final int group;

	/**
	 * Construct this with a group of 1.
	 *
	 * @param escaping
	 *            indicate if the processor should escape characters
	 */
	public LiteralPhraseModifierProcessor(boolean escaping) {
		this(escaping, 1);
	}

	/**
	 * @param escaping
	 *            indicate if the processor should escape characters
	 * @param group
	 *            the {@link PatternBasedElementProcessor#group(int) group} of characters to emit
	 */
	public LiteralPhraseModifierProcessor(boolean escaping, int group) {
		this.escaping = escaping;
		this.group = group;
	}

	@Override
	public void emit() {
		if (escaping) {
			getBuilder().characters(group(group));
		} else {
			getBuilder().charactersUnescaped(group(group));
		}
	}

}
