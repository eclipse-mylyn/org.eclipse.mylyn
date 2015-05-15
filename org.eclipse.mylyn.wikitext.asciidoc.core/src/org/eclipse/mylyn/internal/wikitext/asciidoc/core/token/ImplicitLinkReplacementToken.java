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
 * Detects plain links: http://www.example.com 
 * 
 * @author Max Rydahl Andersen
 */
public class ImplicitLinkReplacementToken extends PatternBasedElement {

	//TODO: make this more generic instead of having special handling of protocols
	public static final String URL_PROTOCOLS_PATTERN = "(?:https?|mailto|callto|irc|ftp|github-mac)";
	
	final static String URL_PATTERN = "(" + URL_PROTOCOLS_PATTERN + "://\\S+)(?:$| )";
	
	@Override
	protected String getPattern(int groupOffset) {
		return URL_PATTERN; //$NON-NLS-1$
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
