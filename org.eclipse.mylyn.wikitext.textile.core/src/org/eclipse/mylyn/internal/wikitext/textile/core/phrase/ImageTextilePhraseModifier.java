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
package org.eclipse.mylyn.internal.wikitext.textile.core.phrase;

import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.textile.core.Textile;
import org.eclipse.mylyn.internal.wikitext.textile.core.TextileContentState;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes.Align;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author David Green
 */
public class ImageTextilePhraseModifier extends PatternBasedElement {

	protected static final int ALIGNMENT_GROUP = Textile.ATTRIBUTES_GROUP_COUNT + 1;

	protected static final int CONTENT_GROUP = Textile.ATTRIBUTES_GROUP_COUNT + 2;

	protected static final int ATTRIBUTES_OFFSET = 1;

	@Override
	protected String getPattern(int groupOffset) {
		String quotedDelimiter = Pattern.quote("!"); //$NON-NLS-1$

		return quotedDelimiter + Textile.REGEX_ATTRIBUTES + "(<|>|=)?([^\\s!](?:.*?\\S)?)(\\([^\\)]+\\))?" + // content //$NON-NLS-1$
				quotedDelimiter + "(:([^\\s]*[^\\s!.)(,]))?"; // optional hyperlink suffix //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return Textile.ATTRIBUTES_GROUP_COUNT + 5;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ImagePhraseModifierProcessor();
	}

	private static class ImagePhraseModifierProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String alignment = group(ALIGNMENT_GROUP);
			String imageUrl = group(CONTENT_GROUP);
			String altAndTitle = group(CONTENT_GROUP + 1);
			String href = group(CONTENT_GROUP + 3);
			String namedLinkUrl = href == null ? null : ((TextileContentState) getState()).getNamedLinkUrl(href);
			if (namedLinkUrl != null) {
				href = namedLinkUrl;
			}

			ImageAttributes attributes = new ImageAttributes();
			attributes.setTitle(altAndTitle);
			attributes.setAlt(altAndTitle);
			if (alignment != null) {
				if ("<".equals(alignment)) { //$NON-NLS-1$
					attributes.setAlign(Align.Left);
				} else if (">".equals(alignment)) { //$NON-NLS-1$
					attributes.setAlign(Align.Right);
				} else if ("=".equals(alignment)) { //$NON-NLS-1$
					attributes.setAlign(Align.Center);
				}
			}
			Textile.configureAttributes(this, attributes, ATTRIBUTES_OFFSET, false);
			if (href != null) {
				builder.imageLink(attributes, href, imageUrl);
			} else {
				builder.image(attributes, imageUrl);
			}
		}
	}

}
