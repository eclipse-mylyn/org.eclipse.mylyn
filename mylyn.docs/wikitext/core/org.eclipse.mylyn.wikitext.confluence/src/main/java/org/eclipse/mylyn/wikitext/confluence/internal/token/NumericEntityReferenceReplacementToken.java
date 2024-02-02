/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

package org.eclipse.mylyn.wikitext.confluence.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

public class NumericEntityReferenceReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "&#(\\d+);"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new NumericEntityReferenceReplacementTokenProcessor();
	}

	private static class NumericEntityReferenceReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String character = group(1);
			int characterValue = Integer.parseInt(character);
			if (characterValue >= 32 && Character.isValidCodePoint(characterValue)) {
				getBuilder().characters(Character.toString((char) characterValue));
			}
		}
	}
}
