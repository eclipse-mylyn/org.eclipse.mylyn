/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * Element processor for {@link XrefReplacementToken} and {@link XrefMacroReplacementToken}
 */
class XrefReplacementTokenProcessor extends PatternBasedElementProcessor {

	private final int linkTextOffset;

	public XrefReplacementTokenProcessor(int linkTextOffset) {
		this.linkTextOffset = linkTextOffset;
	}

	@Override
	public void emit() {
		final String linkTarget = group(1);
		final String linkText = group(2);

		//Href or Hash Name:
		String hrefOrHashName = linkTarget;
		if (!hrefOrHashName.contains("#")) { //$NON-NLS-1$
			hrefOrHashName = "#" + hrefOrHashName; //$NON-NLS-1$
		} else if (hrefOrHashName.charAt(hrefOrHashName.length() - 1) == '#') {
			hrefOrHashName = hrefOrHashName.substring(0, hrefOrHashName.length() - 1);
		}

		//Text:
		StringBuilder sb = new StringBuilder();
		if (linkText != null && linkText.length() > linkTextOffset) {
			sb.append(linkText.substring(linkTextOffset).trim());
		} else {
			sb.append("["); //$NON-NLS-1$
			String text = linkTarget;
			int hashPosition = text.indexOf("#"); //$NON-NLS-1$
			if (hashPosition > 0) {
				int extStart = text.substring(0, hashPosition).lastIndexOf("."); //$NON-NLS-1$
				if (extStart > 0) {
					sb.append(text.substring(0, extStart));
				} else {
					sb.append(text.substring(0, hashPosition));
				}
				if (hashPosition != text.length() - 1) {
					sb.append(text.substring(hashPosition));
				}
			} else {
				sb.append(text);
			}
			sb.append("]"); //$NON-NLS-1$
		}
		builder.link(hrefOrHashName, sb.toString());
	}
}