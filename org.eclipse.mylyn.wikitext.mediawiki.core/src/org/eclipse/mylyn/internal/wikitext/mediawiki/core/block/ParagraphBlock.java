/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.block;

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

	private int blockLineCount = 0;

	private Block nestedBlock = null;

	private boolean newlinesCauseLineBreak = false;

	private final boolean testPreformattedBlocks;

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
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();

			builder.beginBlock(BlockType.PARAGRAPH, attributes);
		} else if (nestedBlock != null) {
			int returnOffset = nestedBlock.processLine(line, offset);
			if (nestedBlock.isClosed()) {
				nestedBlock = null;
			}
			if (returnOffset >= 0) {
				offset = returnOffset;
				if (nestedBlock != null) {
					throw new IllegalStateException();
				}
			} else {
				if (markupLanguage.isEmptyLine(line)) {
					setClosed(true);
					return 0;
				}
				return returnOffset;
			}
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

		++blockLineCount;

		if (nestedBlock != null) {
			if (blockLineCount > 1) {
				builder.lineBreak();
			}
			nestedBlock.processLine(line, offset);
		} else {
			if (testPreformattedBlocks && offset == 0 && line.length() > 0 && line.charAt(0) == ' ') {
				// a preformatted block.
				setClosed(true);
				return 0;
			}
			if (blockLineCount != 1) {
				// note: normally newlines don't automatically convert to line breaks
				if (newlinesCauseLineBreak) {
					builder.lineBreak();
				} else {
					builder.characters("\n"); //$NON-NLS-1$
				}
			}
			dialect.emitMarkupLine(getParser(), state, line, offset);
		}

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		return true;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			if (nestedBlock != null) {
				nestedBlock.setClosed(closed);
				nestedBlock = null;
			}
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
