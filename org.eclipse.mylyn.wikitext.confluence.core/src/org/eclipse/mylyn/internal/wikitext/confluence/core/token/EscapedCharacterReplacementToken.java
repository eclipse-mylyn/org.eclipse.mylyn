/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.confluence.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class EscapedCharacterReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		// currently we only escape curly braces, however
		// it's possible there may be others.  The documentation only shows escaping these.
		return "\\\\(\\{|\\})"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new EscapedCharacterReplacementTokenProcessor();
	}

	private static class EscapedCharacterReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String character = group(1);
			getBuilder().characters(character);
		}

	}
}
