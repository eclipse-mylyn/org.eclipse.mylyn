/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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
package org.eclipse.mylyn.wikitext.confluence.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractConfluenceDelimitedBlock extends ParameterizedBlock {

	private final Pattern startPattern;

	private final Pattern endPattern;

	protected int blockLineCount = 0;

	private Matcher matcher;

	public AbstractConfluenceDelimitedBlock(String blockName) {
		startPattern = Pattern.compile("\\s*\\{" + blockName + "(?::([^\\}]*))?\\}(.*)"); //$NON-NLS-1$ //$NON-NLS-2$
		endPattern = Pattern.compile("\\s*(\\{" + blockName + "\\})(.*)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			setOptions(matcher.group(1));

			offset = matcher.start(2);

			beginBlock();
		}

		int endOfContent = line.length();
		int segmentEnd = endOfContent;
		boolean terminating = false;

		if (offset < endOfContent) {
			Matcher endMatcher = endPattern.matcher(line);
			if (blockLineCount == 0) {
				endMatcher.region(offset, endOfContent);
			}
			if (endMatcher.find()) {
				terminating = true;
				endOfContent = endMatcher.start(2);
				segmentEnd = endMatcher.start(1);
			}
		}

		if (endOfContent < line.length()) {
			state.setLineSegmentEndOffset(endOfContent);
		}

		++blockLineCount;

		final String content = line.substring(offset, segmentEnd);
		int contentOffset = handleBlockContent(content);

		if (terminating) {
			setClosed(true);
		}

		return finalOffset(line.length(), endOfContent, contentOffset, offset);
	}

	private int finalOffset(int lineLength, int endOfContent, int contentOffset, int initialOffset) {
		int finalOffset = contentOffset == -1 ? -1 : initialOffset + contentOffset;
		if (finalOffset == lineLength) {
			finalOffset = -1;
		} else if (endOfContent != lineLength) {
			finalOffset = endOfContent;
		}
		return finalOffset;
	}

	/**
	 * Process the given line of markup starting at the provided offset.
	 *
	 * @param line
	 *            the markup line to process
	 * @return a non-negative integer to indicate that processing of the block completed before the end of the line, or -1 if the entire
	 *         line was processed.
	 */
	protected abstract int handleBlockContent(String content);

	protected abstract void beginBlock();

	protected abstract void endBlock();

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			endBlock();
		}
		super.setClosed(closed);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		resetState();
		matcher = startPattern.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		return matcher.matches();
	}

	protected void resetState() {
		blockLineCount = 0;
	}

}
