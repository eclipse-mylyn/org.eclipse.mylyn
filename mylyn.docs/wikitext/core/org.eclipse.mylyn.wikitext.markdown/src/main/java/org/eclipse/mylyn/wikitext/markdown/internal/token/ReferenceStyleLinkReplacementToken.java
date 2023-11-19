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
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Detects reference-style links: [Text][Reference ID].
 * 
 * @author Stefan Seelmann
 */
public class ReferenceStyleLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(\\[\\s*(.*?)\\s*\\]\\s*\\[\\s*(.*?)\\s*\\])"; //$NON-NLS-1$
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
			String text = group(2);
			String refid = group(3);
			if (refid.isEmpty()) {
				refid = text;
			}
			MarkdownContentState mdContentState = (MarkdownContentState) getState();
			LinkDefinition linkDefinition = mdContentState.getLinkDefinition(refid);
			if (linkDefinition != null) {
				String href = linkDefinition.getUrl();
				String title = linkDefinition.getTitle();
				LinkAttributes attributes = new LinkAttributes();
				if (title != null) {
					attributes.setTitle(title);
				}
				builder.link(attributes, href, text);
			} else {
				// link definition is missing, just print the raw content
				builder.characters(group(1));
			}
		}
	}

}
