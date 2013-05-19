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

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Detects automatic links: &lt;http://www.example.com&gt;.
 * 
 * @author Stefan Seelmann
 */
public class AutomaticLinkReplacementToken extends PatternBasedElement {

	public static final String AUTOMATIC_LINK_REGEX = "<((?:https?|ftp):[^'\">\\s]+)>"; //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return AUTOMATIC_LINK_REGEX;
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
			builder.link(href, href);
		}
	}

}
