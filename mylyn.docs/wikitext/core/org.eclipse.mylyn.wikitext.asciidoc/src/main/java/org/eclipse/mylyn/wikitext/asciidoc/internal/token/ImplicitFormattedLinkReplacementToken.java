/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Detects plain links with formatting: http://www.example.com[Title]
 *
 * @author Max Rydahl Andersen
 */
public class ImplicitFormattedLinkReplacementToken extends PatternBasedElement {

	final static String URL_PATTERN = "((" + ImplicitLinkReplacementToken.URL_PROTOCOLS_PATTERN //$NON-NLS-1$
			+ "://[^\\[\\s]+)\\[(.*?)\\])"; //$NON-NLS-1$

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
			LinkAttributes attributes = new LinkAttributes();
			if (text.endsWith("^")) {
				text = text.substring(0, text.length() - 1);
				attributes.setTarget("_blank");
			}
			builder.link(attributes, href, text);
		}
	}
}
