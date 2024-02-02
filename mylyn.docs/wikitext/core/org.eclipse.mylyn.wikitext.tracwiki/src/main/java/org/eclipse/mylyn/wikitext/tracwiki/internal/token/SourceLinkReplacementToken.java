/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.tracwiki.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;
import org.eclipse.mylyn.wikitext.tracwiki.TracWikiLanguage;

/**
 * recognizes links to the Trac source browser, eg: <code>source:/trunk/COPYING</code> or <code>source:/trunk/COPYING@200</code> or
 * <code>source:/trunk/COPYING@200#L26</code>
 * 
 * @author David Green
 */
public class SourceLinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(source:([^\\s@#]+)(?:@(\\d+))?(?:#L(\\d+))?)"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 4;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new LinkReplacementTokenProcessor();
	}

	private static class LinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String source = group(2);
			String revision = group(3);
			String line = group(4);
			String href = ((TracWikiLanguage) markupLanguage).toSourceBrowserHref(source, revision, line);
			builder.link(href, text);
		}
	}

}
