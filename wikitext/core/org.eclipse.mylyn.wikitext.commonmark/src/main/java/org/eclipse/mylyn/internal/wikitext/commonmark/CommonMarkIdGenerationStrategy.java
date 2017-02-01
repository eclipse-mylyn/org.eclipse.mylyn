/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark;

import java.util.Locale;

import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;

import com.google.common.base.CharMatcher;

public class CommonMarkIdGenerationStrategy extends IdGenerationStrategy {

	private final CharMatcher hyphenMatcher = CharMatcher.is('-');

	@Override
	public String generateId(String headingText) {
		String id = headingText.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9_]", "-");
		id = hyphenMatcher.trimAndCollapseFrom(id, '-');
		return id;
	}

}
