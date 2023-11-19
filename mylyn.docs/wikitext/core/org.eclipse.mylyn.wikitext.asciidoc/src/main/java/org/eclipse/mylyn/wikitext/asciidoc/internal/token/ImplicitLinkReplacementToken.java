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
 * Detects plain links: http://www.example.com
 *
 * @author Max Rydahl Andersen
 */
public class ImplicitLinkReplacementToken extends PatternBasedElement {

	//TODO: make this more generic instead of having special handling of protocols
	public static final String URL_PROTOCOLS_PATTERN = "(?:https?|mailto|callto|irc|ftp|github-mac)"; //$NON-NLS-1$

	final static String URL_PATTERN = "(" + URL_PROTOCOLS_PATTERN + "://\\S+)(?:$| )"; //$NON-NLS-1$ //$NON-NLS-2$

	@Override
	protected String getPattern(int groupOffset) {
		return URL_PATTERN;
	}

	@Override
	protected int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new AutomaticLinkReplacementTokenProcessor();
	}

	private static class AutomaticLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String href = group(1);
			builder.link(new LinkAttributes(), href, href);
		}
	}
}
