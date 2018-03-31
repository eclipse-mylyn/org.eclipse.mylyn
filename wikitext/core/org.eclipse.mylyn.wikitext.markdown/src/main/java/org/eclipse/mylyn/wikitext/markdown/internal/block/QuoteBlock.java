/*******************************************************************************
 * Copyright (c) 2012, 2015 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * Markdown blockquote.
 *
 * @author Stefan Seelmann
 */
public class QuoteBlock extends NestableBlock {

	private static final Pattern startPattern = Pattern.compile(">\\s?(.*)"); //$NON-NLS-1$

	private static final Pattern linePattern = Pattern.compile("(?:>\\s?)?(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Block nestedBlock = null;

	@Override
	public boolean canStart(String line, int lineOffset) {
		return startPattern.matcher(line.substring(lineOffset)).matches();
	}

	@Override
	protected int processLineContent(String line, int offset) {

		String text = line.substring(offset);
		// start of block
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.QUOTE, new Attributes());
		}

		// empty line: end of block
		if (markupLanguage.isEmptyLine(text)) {
			setClosed(true);
			return offset;
		}

		// extract the content
		Matcher matcher = linePattern.matcher(text);
		if (!matcher.matches()) {
			setClosed(true);
			return offset;
		}
		int contentStart = offset + matcher.start(1);

		if (nestedBlock != null) {
			int processed = nestedBlock.processLine(line, contentStart);
			if (nestedBlock.isClosed()) {
				nestedBlock = null;
				if (processed >= contentStart && processed < line.length()) {
					processNextBlock(line, contentStart);
				}
			}
		} else {
			processNextBlock(line, contentStart);
		}

		blockLineCount++;
		return -1;
	}

	private void processNextBlock(String line, int contentStart) {
		// determine nested block, at least the paragraph block must match
		for (Block block : getMarkupLanguage().getBlocks()) {
			if (block.canStart(line, contentStart)) {
				nestedBlock = clone(block);
				break;
			}
		}
		// delegate content processing to nested block
		nestedBlock.processLine(line, contentStart);
		if (nestedBlock.isClosed()) {
			nestedBlock = null;
		}
	}

	private Block clone(Block block) {
		Block clonedBlock = block.clone();
		clonedBlock.setParser(getParser());
		clonedBlock.setState(getState());
		return clonedBlock;
	}

	@Override
	public void setClosed(boolean closed) {
		if (nestedBlock != null) {
			nestedBlock.setClosed(true);
			nestedBlock = null;
		}
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}
}
