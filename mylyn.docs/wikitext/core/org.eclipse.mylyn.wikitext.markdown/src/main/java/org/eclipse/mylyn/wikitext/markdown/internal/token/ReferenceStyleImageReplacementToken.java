/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.token;

import org.eclipse.mylyn.wikitext.markdown.internal.LinkDefinition;
import org.eclipse.mylyn.wikitext.markdown.internal.MarkdownContentState;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

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
