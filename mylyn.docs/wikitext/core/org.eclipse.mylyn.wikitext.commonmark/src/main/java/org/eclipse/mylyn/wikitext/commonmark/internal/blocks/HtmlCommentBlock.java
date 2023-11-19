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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;

public class HtmlCommentBlock extends AbstractHtmlBlock {

	private final Pattern startPattern = Pattern.compile("\\s{0,3}(<!--)(?!>|->).*");

	private final Pattern doubleHyphen = Pattern.compile("--(?!>)");

	private final Pattern closePattern = Pattern.compile("(?<!-)-->");

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		if (line != null) {
			String text = line.getText();
			Matcher matcher = startPattern().matcher(text);
			if (matcher.matches()) {
				int end = matcher.end(1);
				if (end < text.length() - 1) {
					Matcher doubleHyphenMatcher = doubleHyphen.matcher(text);
					doubleHyphenMatcher.region(end, text.length());
					if (doubleHyphenMatcher.find()) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected Pattern closePattern() {
		return closePattern;
	}

	@Override
	protected Pattern startPattern() {
		return startPattern;
	}
}
