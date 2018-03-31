/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

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
