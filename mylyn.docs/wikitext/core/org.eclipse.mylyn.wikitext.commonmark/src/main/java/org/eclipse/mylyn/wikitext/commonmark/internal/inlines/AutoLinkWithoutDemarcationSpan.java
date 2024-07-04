/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.util.UrlUtil;

public class AutoLinkWithoutDemarcationSpan extends SourceSpan {

	private final Pattern linkPattern = Pattern
			.compile("(https?://[a-zA-Z0-9%._~!$&?#'()*+,;:@/=-]*[a-zA-Z0-9_~!$&?#'(*+@/=-]).*", Pattern.DOTALL); //$NON-NLS-1$

	@Override
	public Optional<? extends Inline> createInline(Cursor cursor) {
		if (cursor.getChar() == 'h') {
			Matcher matcher = cursor.matcher(linkPattern);
			if (matcher.matches()) {
				String href = matcher.group(1);
				String link = href;

				int endOffset = cursor.getOffset(matcher.end(1));
				int linkLength = endOffset - cursor.getOffset();

				return Optional.of(new Link(cursor.getLineAtOffset(), cursor.getOffset(), linkLength, escapeUri(link),
						null, List.<Inline> of(
								new Characters(cursor.getLineAtOffset(), cursor.getOffset(), linkLength, href))));
			}
		}
		return Optional.empty();
	}

	private String escapeUri(String link) {
		return UrlUtil.escapeUrlFragment(link).replace("%23", "#").replace("%25", "%"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
}
