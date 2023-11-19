/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

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
		builder.link(linkTarget(), linkText());
	}

	private String linkTarget() {
		String target = linkTargetCaptureGroup();
		if (!target.contains("#")) { //$NON-NLS-1$
			target = "#" + target; //$NON-NLS-1$
		} else if (target.charAt(target.length() - 1) == '#') {
			target = target.substring(0, target.length() - 1);
		}
		return target;
	}

	private String linkText() {
		String linkText = linkTextCaptureGroup();

		StringBuilder buffer = new StringBuilder();
		if (linkText != null && linkText.length() > linkTextOffset) {
			buffer.append(linkText.substring(linkTextOffset).trim());
		} else {
			buffer.append("["); //$NON-NLS-1$
			String target = linkTargetCaptureGroup();
			int hashPosition = target.indexOf("#"); //$NON-NLS-1$
			if (hashPosition > 0) {
				int extStart = target.substring(0, hashPosition).lastIndexOf("."); //$NON-NLS-1$
				if (extStart > 0) {
					buffer.append(target.substring(0, extStart));
				} else {
					buffer.append(target.substring(0, hashPosition));
				}
				if (hashPosition != target.length() - 1) {
					buffer.append(target.substring(hashPosition));
				}
			} else {
				buffer.append(target);
			}
			buffer.append("]"); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	private String linkTargetCaptureGroup() {
		return group(1);
	}

	private String linkTextCaptureGroup() {
		return group(2);
	}
}
