/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.markup.phrase;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * A processor that emits the first group as characters, optionally unescaped.
 * 
 * @see DocumentBuilder#characters(String)
 * @see DocumentBuilder#charactersUnescaped(String)
 * 
 * @author David Green
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
