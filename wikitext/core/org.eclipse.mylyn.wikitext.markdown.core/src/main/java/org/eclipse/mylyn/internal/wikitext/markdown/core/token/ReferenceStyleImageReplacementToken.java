/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.token;

import org.eclipse.mylyn.internal.wikitext.markdown.core.LinkDefinition;
import org.eclipse.mylyn.internal.wikitext.markdown.core.MarkdownContentState;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects reference-style images: ![Alt text][Reference ID].
 * 
 * @author Stefan Seelmann
 */
public class ReferenceStyleImageReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "!(\\[\\s*(.*?)\\s*\\]\\s*\\[\\s*(.*?)\\s*\\])"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 3;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new ReferenceStyleLinkReplacementTokenProcessor();
	}

	private static class ReferenceStyleLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String altText = group(2);
			String refid = group(3);
			if (refid.isEmpty()) {
				refid = altText;
			}
			MarkdownContentState mdContentState = (MarkdownContentState) getState();
			LinkDefinition linkDefinition = mdContentState.getLinkDefinition(refid);
			if (linkDefinition != null) {
				String href = linkDefinition.getUrl();
				String title = linkDefinition.getTitle();
				ImageAttributes attributes = new ImageAttributes();
				attributes.setTitle(title);
				attributes.setAlt(altText);
				builder.image(attributes, href);
			} else {
				// image definition is missing, just print the raw content
				builder.characters(group(1));
			}
		}
	}

}
