/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

public class DefinitionListBlock extends Block {

	private static final Pattern START_PATTERN = Pattern.compile("(?:(;\\s+(.+))|(-\\s+(.+?)\\s*(:=)\\s*((.+?)\\s*(=:)?)?))"); //$NON-NLS-1$

	private static final Pattern END_ITEM_PATTERN = Pattern.compile("(.*?)\\s*(=:)\\s*"); //$NON-NLS-1$

	private static final Pattern COLON_START_PATTERN = Pattern.compile(":\\s+(.+)"); //$NON-NLS-1$

	private Matcher matcher;

	private int blockLineCount;

	private int levelsOpen = 0;

	private int linesThisItem = 0;

	@Override
	protected int processLineContent(String line, int offset) {
		++blockLineCount;
		if (blockLineCount == 1) {
			beginBlock(BlockType.DEFINITION_LIST, new ListAttributes());
			handleStartTerm();
			return -1;
		} else {
			matcher = START_PATTERN.matcher(line);
			if (matcher.matches()) {
				closeItems();
				handleStartTerm();
				return -1;
			} else {
				String content = line;
				int contentOffset = 0;
				boolean closeThisLine = false;
				if (levelsOpen < 2) {
					Matcher ddMatcher = COLON_START_PATTERN.matcher(line);
					if (ddMatcher.matches()) {
						content = ddMatcher.group(1);
						contentOffset = ddMatcher.start(1);

						closeItems();
						beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
						linesThisItem = 0;
						closeThisLine = true;
					} else {
						setClosed(true);
						return 0;
					}
				} else {
					// continuation of list item.  blank line is termination
					if (markupLanguage.isEmptyLine(line)) {
						setClosed(true);
						return 0;
					}
					Matcher endMatcher = END_ITEM_PATTERN.matcher(line);
					if (endMatcher.matches()) {
						content = endMatcher.group(1);
					}
				}
				if (markupLanguage.isEmptyLine(content)) {
					closeItems();
					return -1;
				}
				if (++linesThisItem > 1) {
					builder.lineBreak();
				}
				markupLanguage.emitMarkupLine(getParser(), state, contentOffset, content, offset);
				if (closeThisLine) {
					endBlock();
				}
			}
		}
		return -1;
	}

	protected void handleStartTerm() {
		linesThisItem = 0;
		int textLineOffset;
		String term = matcher.group(2);
		String definition = null;
		int definitionOffset = 0;
		int definitionSegmentOffset = 0;
		boolean definitionEnd = false;
		if (term == null) {
			term = matcher.group(4);
			textLineOffset = matcher.start(4);
			definition = matcher.group(7);
			definitionOffset = matcher.start(7);
			definitionEnd = matcher.group(8) != null;
			definitionSegmentOffset = matcher.start(5);
			state.setLineSegmentEndOffset(definitionSegmentOffset);
		} else {
			textLineOffset = matcher.start(2);
		}

		beginBlock(BlockType.DEFINITION_TERM, new Attributes());
		markupLanguage.emitMarkupLine(getParser(), state, textLineOffset, term, 0);
		endBlock();

		if (definition != null) {
			// since it's a mid-line block, we must set the offsets here 
			state.setLineCharacterOffset(definitionSegmentOffset);
			beginBlock(BlockType.DEFINITION_ITEM, new Attributes());

			markupLanguage.emitMarkupLine(getParser(), state, definitionOffset, definition, 0);
			if (definitionEnd) {
				endBlock();
			} else {
				linesThisItem = 1;
			}
		}
	}

	private void closeItems() {
		while (levelsOpen > 1) {
			endBlock();
		}
	}

	public void beginBlock(BlockType type, Attributes attributes) {
		++levelsOpen;
		builder.beginBlock(type, attributes);
	}

	public void endBlock() {
		--levelsOpen;
		builder.endBlock();
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			while (levelsOpen > 0) {
				endBlock();
			}
		}
		super.setClosed(closed);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		matcher = START_PATTERN.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		if (matcher.matches()) {
			return true;
		}
		matcher = null;
		return false;
	}

}
