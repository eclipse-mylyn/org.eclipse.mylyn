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
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.token;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;

/**
 * Matches WikiWord internal hyperlinks.
 * 
 * @author David Green
 * 
 * @see TracWikiLanguage#isAutoLinking()
 */
public class WikiWordReplacementToken extends PatternBasedElement {

	private static final Pattern replacementPattern = Pattern.compile("\\W");

	@Override
	protected String getPattern(int groupOffset) {
		return "(!)?([A-Z]\\w+(?:[A-Z]\\w*)+)";
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
			TracWikiLanguage twikiLanguage = (TracWikiLanguage) markupLanguage;
			if (escaped != null || !twikiLanguage.isAutoLinking()) {
				builder.characters(word);
			} else {
				String target = replacementPattern.matcher(word).replaceAll("");
				boolean exists = twikiLanguage.computeInternalLinkExists(target);

				String internalHref = twikiLanguage.toInternalHref(target);
				if (!exists) {
					builder.characters(word);
					builder.link(internalHref, "?");
				} else {
					builder.link(internalHref, word);
				}
			}
		}
	}

}
