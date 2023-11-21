/*******************************************************************************
 * Copyright (c) 2017 Jeremie Bresson and others.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

public class InlineAttributeReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(?<!\\\\)\\{(.*?)\\}"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineAttributeTokenReplacementProcessor();
	}

	private static class InlineAttributeTokenReplacementProcessor extends PatternBasedElementProcessor {
		private static final Pattern SET_PATTERN = Pattern.compile("set:(.*?):(.*)"); //$NON-NLS-1$

		private static final Pattern UNSET_PATTERN = Pattern.compile("set:(.*?)!"); //$NON-NLS-1$

		@Override
		public void emit() {
			String key = group(1);
			Matcher setMatcher = SET_PATTERN.matcher(key);
			Matcher unsetMatcher = UNSET_PATTERN.matcher(key);

			AsciiDocContentState asciiDocState = (AsciiDocContentState) getState();
			if (setMatcher.matches()) {
				String newKey = setMatcher.group(1);
				String newValue = setMatcher.group(2);
				asciiDocState.putAttribute(newKey, newValue);
			} else if (unsetMatcher.matches()) {
				String newKey = unsetMatcher.group(1);
				asciiDocState.removeAttribute(newKey);
			} else if (asciiDocState.isAttributeDefined(key)) {
				markupLanguage.emitMarkupLine(getParser(), state, asciiDocState.getAttribute(key), 0);
			} else {
				builder.characters(group(0));
			}
		}
	}
}
