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

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import java.util.regex.Pattern;

public class HtmlType1Block extends AbstractHtmlBlock {

	private final Pattern startPattern = Pattern.compile("\\s{0,3}(<(?:pre|script|style))(\\s|>|$).*",
			Pattern.CASE_INSENSITIVE);

	private final Pattern closePattern = Pattern.compile("\\s{0,3}</(?:pre|script|style)\\s*>",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Override
	protected Pattern closePattern() {
		return closePattern;
	}

	@Override
	protected Pattern startPattern() {
		return startPattern;
	}
}
