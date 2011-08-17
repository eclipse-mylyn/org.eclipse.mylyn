/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.phrase.EscapePhraseModifier;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

/**
 * Matches any markup text.
 * 
 * @author David Green
 */
public class ParagraphBlock extends Block {

	private static final Pattern ESCAPE_PHRASE_MODIFIER_PATTERN = Pattern.compile(new EscapePhraseModifier().getPattern(0));

	private static final Pattern NESTED_BLOCK_START_PATTERN = Pattern.compile("<!--", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private static final int NESTED_BLOCK_START_GROUP = 0;

	private int blockLineCount = 0;

	private boolean newlinesCauseLineBreak = false;

	private final boolean testPreformattedBlocks;

	private int nestedStartOffset = -1;

	private int nestedLineNumber = -1;

	private int lastLineNumber = -1;

	public ParagraphBlock(boolean testPreformattedBlocks) {
		this.testPreformattedBlocks = testPreformattedBlocks;
	}

	public boolean isNewlinesCauseLineBreak() {
		return newlinesCauseLineBreak;
	}

	public void setNewlinesCauseLineBreak(boolean newlinesCauseLineBreak) {
		this.newlinesCauseLineBreak = newlinesCauseLineBreak;
	}

	@Override
	public int processLineContent(String line, int offset) {
		nestedStartOffset = -1;
		nestedLineNumber = -1;
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();

			builder.beginBlock(BlockType.PARAGRAPH, attributes);
		}

		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		MediaWikiLanguage dialect = (MediaWikiLanguage) getMarkupLanguage();

		// paragraphs can have nested lists and other things
		for (Block block : dialect.getParagraphBreakingBlocks()) {
			if (block.canStart(line, offset)) {
				setClosed(true);
				return 0;
			}
		}

		Matcher nestedStartMatcher = NESTED_BLOCK_START_PATTERN.matcher(line);
		if (offset > 0) {
			nestedStartMatcher.region(offset, line.length());
		}
		if (nestedStartMatcher.find()) {
			nestedStartOffset = nestedStartMatcher.start(NESTED_BLOCK_START_GROUP);
			if (isEscaped(line, offset, nestedStartOffset)) {
				nestedStartOffset = -1;
			} else {
				nestedLineNumber = getState().getLineNumber();
			}
		}

		++blockLineCount;

		if (testPreformattedBlocks && offset == 0 && line.length() > 0 && line.charAt(0) == ' ') {
			// a preformatted block.
			setClosed(true);
			return 0;
		}
		if (blockLineCount != 1 && nestedStartOffset == -1 && lastLineNumber != getState().getLineNumber()) {
			// note: normally newlines don't automatically convert to line breaks
			if (newlinesCauseLineBreak) {
				builder.lineBreak();
			} else {
				builder.characters("\n"); //$NON-NLS-1$
			}
		}

		if (nestedStartOffset > 0) {
			line = line.substring(0, nestedStartOffset);
		}
		dialect.emitMarkupLine(getParser(), state, line, offset);

		lastLineNumber = getState().getLineNumber();
		return nestedStartOffset;
	}

	private boolean isEscaped(String line, int lineOffset, int offset) {
		Matcher matcher = ESCAPE_PHRASE_MODIFIER_PATTERN.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			if (start <= offset && end >= offset) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean beginNesting() {
		return nestedStartOffset != -1;
	}

	@Override
	public int findCloseOffset(String line, int lineOffset) {
		return -1;
	}

	@Override
	public boolean canResume(String line, int lineOffset) {
		if (getState().getLineNumber() == nestedLineNumber && lineOffset == nestedStartOffset) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		return true;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
