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
 * Detects plain links with formatting: http://www.example.com[Title]
 * 
 * @author Max Rydahl Andersen
 */
public class ImplicitFormattedLinkReplacementToken extends PatternBasedElement {

	final static String URL_PATTERN = "((" + ImplicitLinkReplacementToken.URL_PROTOCOLS_PATTERN + "://[^\\[\\s]+)\\[(.*?)\\])";

	@Override
	protected String getPattern(int groupOffset) {
		return URL_PATTERN; // $NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 3;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new AutomaticLinkReplacementTokenProcessor();
	}

	private static class AutomaticLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String href = group(2);
			String text = group(3);
			builder.link(new LinkAttributes(), href, text);
		}
	}
}
