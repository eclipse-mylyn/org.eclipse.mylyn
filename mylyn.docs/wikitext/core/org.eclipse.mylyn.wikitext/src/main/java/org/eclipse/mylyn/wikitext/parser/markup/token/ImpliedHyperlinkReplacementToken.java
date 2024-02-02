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
package org.eclipse.mylyn.wikitext.parser.markup.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * A token that detects hyperlinks in the markup and emits them as a link. hyperlinks must use the HTTP or HTTPS protocols to be detected.
 * Url detection is based on RFC 3986.
 *
 * @author David Green
 * @since 3.0
 */
public class ImpliedHyperlinkReplacementToken extends PatternBasedElement {

	@Override
	public String getPattern(int groupOffset) {
		// based on RFC 3986
		// even though it's valid we don't want to detect URLs that end with '.', ',', ';', ':' or ')'
		return "(https?://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]*[a-zA-Z0-9_~!$&?#'(*+@/=-])"; //$NON-NLS-1$
	}

	@Override
	public int getPatternGroupCount() {
		return 1;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkReplacementTokenProcessor();
	}

	private static class HyperlinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String target = group(1);
			getBuilder().link(target, target);
		}
	}
}
