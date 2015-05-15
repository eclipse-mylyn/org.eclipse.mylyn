/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.token;

import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects explicit links like: link:http://www.example.com[link text], link:index.html[]
 *
 * @author Max Rydahl Andersen
 */
public class ExplicitLinkReplacementToken extends PatternBasedElement {

	static String EXPLICT_LINK_PATTERN = "(((?:link:\\+\\+)(.*)\\+\\+(\\[(.*?)\\]))|((?:link:)(.*)(\\[(.*?)\\])))"; //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return EXPLICT_LINK_PATTERN;
	}

	@Override
	protected int getPatternGroupCount() {
		return 9;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineLinkReplacementTokenProcessor();
	}

	private static class InlineLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String href, text;

			if (group(7) != null) { // without ++
				href = group(7);
				text = group(9);
			} else { // with ++
				href = group(3);
				text = group(5);
			}

			LinkAttributes attributes = new LinkAttributes();
			if (text == null) {
				text = href;
			}

			builder.link(attributes, href, text);

		}
	}
}
