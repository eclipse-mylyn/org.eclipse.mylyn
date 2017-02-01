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

import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects inline links: [Text](http://www.example.com "Optional title").
 * 
 * @author Stefan Seelmann
 */
public class InlineLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "\\[\\s*(.*?)\\s*\\]\\(\\s*(.+?)(?:\\s\"(.*?)\")?\\s*\\)"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 3;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineLinkReplacementTokenProcessor();
	}

	private static class InlineLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String href = group(2);
			String title = group(3);
			LinkAttributes attributes = new LinkAttributes();
			if (title != null) {
				attributes.setTitle(title);
			}
			builder.link(attributes, href, text);
		}
	}

}
