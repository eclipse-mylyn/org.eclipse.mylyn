/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.token;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;

/**
 * @author David Green
 */
public class WikiWordReplacementToken extends PatternBasedElement {

	private static final Pattern replacementPattern = Pattern.compile("\\W"); //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return "(!)?([A-Z]\\w+(?:[A-Z]\\w*)+)"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new WikiWordProcessor();
	}

	private class WikiWordProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String escaped = group(1);
			String word = group(2);
			TWikiLanguage twikiLanguage = (TWikiLanguage) markupLanguage;
			if (escaped != null || !twikiLanguage.isAutoLinking()) {
				builder.characters(word);
			} else {
				String target = replacementPattern.matcher(word).replaceAll(""); //$NON-NLS-1$
				boolean exists = twikiLanguage.computeInternalLinkExists(target);

				String internalHref = twikiLanguage.toInternalHref(target);
				if (!exists) {
					builder.characters(word);
					builder.link(internalHref, "?"); //$NON-NLS-1$
				} else {
					builder.link(internalHref, word);
				}
			}
		}
	}

}
