/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.core.parser.markup.IdGenerationStrategy;

/**
 * 
 * @author David Green
 */
public class MediaWikiIdGenerationStrategy extends IdGenerationStrategy {

	private final Map<String, Integer> anchorReferenceCount = new HashMap<String, Integer>();

	@Override
	public String generateId(String headingText) {

		// from the MediaWiki source (Parser.php line 3090) the basic algorithm pseudocode is as follows:

		// anchor = escapeId( headingText )
		// ++refCount(anchor)
		// if (refCount(anchor) > 1) {
		//   anchor = anchor + '_' + refCount(anchor) 
		// }

		String anchor = escape(headingText);
		Integer previousRefCount = anchorReferenceCount.put(anchor, 1);
		if (previousRefCount != null) {
			int refCount = previousRefCount + 1;
			anchorReferenceCount.put(anchor, refCount);
			anchor = anchor + '_' + refCount;
		}

		return anchor;
	}

	private String escape(String headingText) {
		// implementation based on Sanitizer.php line 629
		String escaped = headingText.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		// TODO: decode entity and char references
		try {
			escaped = URLEncoder.encode(escaped, "utf-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		return escaped.replace("%3A", ":").replace('%', '.'); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
