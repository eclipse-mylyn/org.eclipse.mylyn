/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.creole.core.token;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.creole.core.CreoleLanguage;

/**
 * Tokens that represent links, as follows: <code>[[link]]</code>
 * 
 * @author Igor Malinin
 */
public class LinkReplacementToken extends PatternBasedElement {

	private static final Pattern replacementPattern = Pattern.compile("\\W"); //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return "(~)?(\\[\\[([^\\]|]+)(?:[|]([^\\]]*))?\\]\\])"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 4;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new LinkProcessor();
	}

	private static class LinkProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String escaped = group(1);
			if (escaped != null) {
				String escapedText = group(2);
				builder.characters(escapedText);
			} else {
				String link = group(3);
				String text = group(4);
				if (text == null || text.trim().length() == 0) {
					text = link;
				}
				boolean looksLikeEmail = link.indexOf('@') != -1;
				if (link.indexOf('/') != -1 || link.indexOf('#') != -1 || looksLikeEmail) {
					if (looksLikeEmail) {
						text = text.replaceFirst("\\s*mailto:", ""); //$NON-NLS-1$ //$NON-NLS-2$
					}
					// url link
					builder.link(link, text);
				} else {
					// wiki link
					String target = replacementPattern.matcher(link).replaceAll("_"); //$NON-NLS-1$
					CreoleLanguage creoleLanguage = (CreoleLanguage) markupLanguage;
					boolean exists = creoleLanguage.computeInternalLinkExists(target);

					String internalHref = creoleLanguage.toInternalHref(target);
					if (!exists) {
						builder.characters(text);
						builder.link(internalHref, "?"); //$NON-NLS-1$
					} else {
						builder.link(internalHref, text);
					}
				}
			}
		}
	}

}
