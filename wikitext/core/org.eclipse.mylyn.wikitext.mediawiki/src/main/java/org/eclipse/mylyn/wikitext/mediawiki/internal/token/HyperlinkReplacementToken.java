/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.mediawiki.internal.token;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class HyperlinkReplacementToken extends PatternBasedElement {

	@Override
	protected String getPattern(int groupOffset) {
		return "(?:(\"|\\!)([^\"]+)\\" + (1 + groupOffset) + ":([^\\s]+))"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected int getPatternGroupCount() {
		return 3;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkReplacementTokenProcessor();
	}

	private static class HyperlinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String hyperlinkBoundaryText = group(1);
			String hyperlinkSrc = group(2);
			String href = group(3);

			if (hyperlinkBoundaryText.equals("\"")) { //$NON-NLS-1$
				builder.link(href, hyperlinkSrc);
			} else {
				builder.imageLink(href, getMarkupLanguage().mapImageName(hyperlinkSrc));
			}
		}

		@Override
		public MediaWikiLanguage getMarkupLanguage() {
			return (MediaWikiLanguage) super.getMarkupLanguage();
		}
	}

}
