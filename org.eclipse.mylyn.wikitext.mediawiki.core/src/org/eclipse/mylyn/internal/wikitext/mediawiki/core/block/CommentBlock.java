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

/**
 * a block for <a href="http://www.mediawiki.org/wiki/Help:Formatting">comments</a> delmited XML-style
 * 
 * @author David Green
 */
public class CommentBlock extends Block {

	private static final Pattern COMMENT_START_PATTERN = Pattern.compile("\\s*(<!--).*"); //$NON-NLS-1$

	private static final Pattern COMMENT_END_PATTERN = Pattern.compile("(-->)"); //$NON-NLS-1$

	@Override
	protected int processLineContent(String line, int offset) {
		Matcher closeMatcher = COMMENT_END_PATTERN.matcher(line);
		if (offset > 0) {
			closeMatcher.region(offset, line.length());
		}
		if (closeMatcher.find()) {
			int newContentStart = closeMatcher.end(1);
			setClosed(true);
			if (newContentStart < line.length()) {
				return newContentStart;
			}
			return -1;
		}
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		final Matcher matcher = COMMENT_START_PATTERN.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

}
