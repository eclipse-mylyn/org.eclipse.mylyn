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

import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * quoted text block, matches blocks that start with <code>{quote}</code>. Creates an extended block type of {@link ParagraphBlock
 * paragraph}.
 *
 * @author David Green
 */
public class ExtendedQuoteBlock extends AbstractConfluenceDelimitedBlock {

	private int paraLine = 0;

	private boolean paraOpen = false;

	private Block nestedBlock = null;

	public ExtendedQuoteBlock() {
		super("quote"); //$NON-NLS-1$
	}

	@Override
	protected void resetState() {
		super.resetState();
		paraOpen = false;
		paraLine = 0;
		nestedBlock = null;
	}

	@Override
	protected void beginBlock() {
		Attributes attributes = new Attributes();
		builder.beginBlock(BlockType.QUOTE, attributes);
	}

	@Override
	protected void endBlock() {
		if (nestedBlock != null) {
			nestedBlock.setClosed(true);
			nestedBlock = null;
		}
		if (paraOpen) {
			closeParagraph();
		}
		builder.endBlock(); // quote
	}

	private void closeParagraph() {
		builder.endBlock();
		paraOpen = false;
		paraLine = 0;
	}

	@Override
	protected int handleBlockContent(String content) {
		if (nestedBlock == null) {
			checkForStartOfNestedBlock(content);
		}
		if (nestedBlock != null) {
			return delegateProcessingToNestedBlock(content);
		}
		if (blockLineCount == 1 && content.length() == 0) {
			return -1;
		}
		if (blockLineCount > 1 && paraOpen && getMarkupLanguage().isEmptyLine(content)) {
			closeParagraph();
			return -1;
		}
		if (!paraOpen) {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			paraOpen = true;
		}
		if (paraLine != 0) {
			builder.lineBreak();
		}
		++paraLine;
		getMarkupLanguage().emitMarkupLine(getParser(), state, content, 0);
		return -1;
	}

	private void checkForStartOfNestedBlock(String content) {
		ConfluenceLanguage markupLanguage = (ConfluenceLanguage) getMarkupLanguage();
		for (Block block : markupLanguage.getNestedBlocks()) {
			if (block.canStart(content, 0)) {
				nestedBlock = block.clone();
				nestedBlock.setParser(getParser());
				nestedBlock.setState(getState());
				if (paraOpen) {
					closeParagraph();
				}
			}
		}
	}

	private int delegateProcessingToNestedBlock(String content) {
		int lineOffset = nestedBlock.processLine(content, 0);
		if (nestedBlock.isClosed()) {
			nestedBlock = null;
		}

		if (lineOffset < content.length() && lineOffset >= 0) {
			if (nestedBlock == null) {
				//Return handling of the rest of the line to the main quote block
				return handleBlockContent(content.substring(lineOffset));
			}
			return lineOffset;
		}

		return -1;
	}

	@Override
	public int findCloseOffset(String line, int lineOffset) {
		if (nestedBlock == null) {
			return super.findCloseOffset(line, lineOffset);
		}
		return nestedBlock.findCloseOffset(line, lineOffset);
	}

	@Override
	public boolean beginNesting() {
		if (nestedBlock == null) {
			return super.beginNesting();
		}
		return nestedBlock.beginNesting();
	}

	@Override
	public boolean canResume(String line, int lineOffset) {
		if (nestedBlock == null) {
			return super.canResume(line, lineOffset);
		}
		return nestedBlock.canResume(line, lineOffset);
	}

	@Override
	protected void setOption(String key, String value) {
		// no options
	}
}
