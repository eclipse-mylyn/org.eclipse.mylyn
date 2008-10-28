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
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.token;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

/**
 * match [[internal links]]
 * 
 * @author David Green
 * 
 */
public class HyperlinkInternalReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:\\[\\[([^\\]\\|]+?)\\s*(?:\\|\\s*([^\\]]*))?\\]\\])"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkReplacementTokenProcessor();
	}

	private static class HyperlinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String pageName = group(1);
			String altText = group(2);
			String href = ((MediaWikiLanguage) getMarkupLanguage()).toInternalHref(pageName);

			// category references start with ':' but are not referenced that way in the text
			if (pageName.startsWith(":")) { //$NON-NLS-1$
				pageName = pageName.substring(1);
			}
			if (altText == null || altText.trim().length() == 0) {
				altText = pageName;
				if (altText.startsWith("#")) { //$NON-NLS-1$
					altText = altText.substring(1);
				}
			}
			if (pageName.startsWith("#")) { //$NON-NLS-1$
				builder.link(href, altText);
			} else {
				Attributes attributes = new LinkAttributes();
				attributes.setTitle(pageName);
				builder.link(attributes, href, altText);
			}
		}
	}

}
