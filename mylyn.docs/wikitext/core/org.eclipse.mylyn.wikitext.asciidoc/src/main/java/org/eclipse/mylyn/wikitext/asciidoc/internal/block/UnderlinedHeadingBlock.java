/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *      Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LookAheadReader;
import org.eclipse.mylyn.wikitext.asciidoc.internal.util.ReadAheadBlock;
import org.eclipse.mylyn.wikitext.parser.HeadingAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * AsciiDoc setext underlined headings.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class UnderlinedHeadingBlock extends Block implements ReadAheadBlock {

	private static final Pattern h1pattern = Pattern.compile("(=+)\\s*"); //$NON-NLS-1$

	private static final Pattern h2pattern = Pattern.compile("(-+)\\s*"); //$NON-NLS-1$

	private static final Pattern h3pattern = Pattern.compile("(~+)\\s*"); //$NON-NLS-1$

	private static final Pattern h4pattern = Pattern.compile("(\\^+)\\s*"); //$NON-NLS-1$

	private static final Pattern h5pattern = Pattern.compile("(\\++)\\s*"); //$NON-NLS-1$

	private int blockLineCount;

	private int lineLevel;

	@Override
	public boolean canStart(String line, int lineOffset, LookAheadReader lookAheadReader) {
		blockLineCount = 0;
		lineLevel = 0;
		String nextLine = lookAheadReader.lookAhead();
		if (nextLine == null) {
			return false;
		} else {
			int expectedLength = line.trim().length() - lineOffset;
			if (checkNextLine(expectedLength, nextLine, h1pattern, 1)) {
				return true;
			} else if (checkNextLine(expectedLength, nextLine, h2pattern, 2)) {
				return true;
			} else if (checkNextLine(expectedLength, nextLine, h3pattern, 3)) {
				return true;
			} else if (checkNextLine(expectedLength, nextLine, h4pattern, 4)) {
				return true;
			} else if (checkNextLine(expectedLength, nextLine, h5pattern, 5)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * The next line should be a sequence of same chars. The nextLine length (nextineLength) should: nextlineLength - 1 <= titleLength <=
	 * nextlineLength + 1
	 *
	 * @param length
	 *            length of the current line (title)
	 * @param nextLine
	 *            next line in the document
	 * @param pattern
	 *            regular expression in a Pattern to match a line of chars in the next line
	 * @param level
	 *            level that is set if the next line matches
	 * @return
	 */
	private boolean checkNextLine(int length, String nextLine, Pattern pattern, int level) {
		Matcher matcher = pattern.matcher(nextLine);
		if (matcher.matches()) {
			int lineLength = matcher.group(1).length();
			if (lineLength > length - 2 && lineLength < length + 2) {
				lineLevel = level;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		String message = "Read-ahead required, call canStart(String, int, LookAheadReader) instead."; //$NON-NLS-1$
		throw new UnsupportedOperationException(message);
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			HeadingAttributes attributes = new HeadingAttributes();
			attributes.setId(state.getIdGenerator().newId(null, line));

			int level = LanguageSupport.computeHeadingLevel(lineLevel, getAsciiDocState());
			builder.beginHeading(level, attributes);
			builder.characters(line.trim());
		} else {
			builder.endHeading();
			setClosed(true);
		}

		blockLineCount++;
		return -1;
	}

	protected AsciiDocContentState getAsciiDocState() {
		return (AsciiDocContentState) state;
	}
}
