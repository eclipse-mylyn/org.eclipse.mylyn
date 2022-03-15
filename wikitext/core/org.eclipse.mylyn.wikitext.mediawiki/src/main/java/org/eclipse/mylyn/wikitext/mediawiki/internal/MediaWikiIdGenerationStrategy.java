/*******************************************************************************
 * Copyright (c) 2007, 2022 David Green and others.
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

package org.eclipse.mylyn.wikitext.mediawiki.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;

/**
 * @author David Green
 */
public class MediaWikiIdGenerationStrategy extends IdGenerationStrategy {

	private final Map<String, Integer> anchorReferenceCount = new HashMap<>();

	@Override
	public String generateId(String headingText) {

		// from the MediaWiki source (Parser.php line 3090) the basic algorithm pseudocode is as follows:

		// anchor = escapeId( headingText )
		// ++refCount(anchor)
		// if (refCount(anchor) > 1) {
		//   anchor = anchor + '_' + refCount(anchor)
		// }

		String anchor = headingTextToId(headingText);
		Integer previousRefCount = anchorReferenceCount.put(anchor, 1);
		if (previousRefCount != null) {
			int refCount = previousRefCount + 1;
			anchorReferenceCount.put(anchor, refCount);
			anchor = anchor + '_' + refCount;
		}

		return anchor;
	}

	/**
	 * encode a page name or anchor following MediaWiki encoding behaviour
	 *
	 * @param headingText
	 *            the heading text, page name or anchor text
	 * @return an encoded id
	 */
	static String headingTextToId(String headingText) {
		// implementation based on Sanitizer.php line 629
		String escaped = headingText.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		// TODO: decode entity and char references
		escaped = URLEncoder.encode(escaped, StandardCharsets.UTF_8);
		return escaped.replace("%3A", ":").replace('%', '.'); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
