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

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

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
