/*******************************************************************************
 * Copyright (c) 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class EscapeBlock extends Block {

	private static final Pattern START_PATTERN = Pattern.compile("\\s*(<nowiki>).*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private static final Pattern END_PATTERN = Pattern.compile("(</nowiki>)", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount++ == 0) {
			Matcher matcher = START_PATTERN.matcher(line);
			if (offset > 0) {
				matcher.region(offset, line.length());
			}
			if (matcher.matches()) {
				offset = matcher.end(1);
			}
		}
		Matcher closeMatcher = END_PATTERN.matcher(line);
		if (offset > 0) {
			closeMatcher.region(offset, line.length());
		}
		int contentEnd = line.length();
		int end = -1;
		if (closeMatcher.find()) {
			int newContentStart = closeMatcher.end(1);
			setClosed(true);
			if (newContentStart < line.length()) {
				end = newContentStart;
				contentEnd = closeMatcher.start(1);
			}
		}
		getBuilder().characters(line.substring(offset, contentEnd));

		return end;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			Matcher matcher = START_PATTERN.matcher(line);
			if (lineOffset > 0) {
				matcher.region(lineOffset, line.length());
			}
			if (matcher.matches()) {
				return true;
			}
		}
		return false;
	}
}
