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
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.block;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * quoted text block, matches blocks that start with <code>&gt;</code> or two spaces. These are what they call
 * discussion citations and block quotes, respectively. Creates an extended block type of {@link ParagraphBlock
 * paragraph}.
 * 
 * @author David Green
 */
public class QuoteBlock extends Block {

	static final Pattern startPattern = Pattern.compile("(?:(?:(>+)\\s+)|(  ))(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private Stack<BlockState> quoteBlockState;

	public QuoteBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			quoteBlockState = new Stack<BlockState>();
		} else {
			matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
		}
		String quoteStartGroup = matcher.group(1);
		int level = quoteStartGroup == null ? 1 : quoteStartGroup.length();
		offset = matcher.start(3);

		while (quoteBlockState.size() > level) {
			closeOne();
		}
		while (quoteBlockState.size() < level) {
			openOne(new Attributes());
		}

		BlockState blockState = quoteBlockState.peek();
		if (!blockState.paraOpen) {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			blockState.paraOpen = true;
		} else if (blockLineCount != 0) {
			builder.lineBreak();
		}
		++blockLineCount;

		getMarkupLanguage().emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	private void openOne(Attributes attributes) {
		if (!quoteBlockState.isEmpty()) {
			BlockState blockState = quoteBlockState.peek();
			if (blockState.paraOpen) {
				blockState.paraOpen = false;
				builder.endBlock();
			}
		}
		builder.beginBlock(BlockType.QUOTE, attributes);
		quoteBlockState.push(new BlockState());
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		quoteBlockState = null;
		blockLineCount = 0;
		matcher = startPattern.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		return matcher.matches();
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			while (quoteBlockState != null && !quoteBlockState.isEmpty()) {
				closeOne();
			}
			quoteBlockState = null;
		}
		super.setClosed(closed);
	}

	private void closeOne() {
		BlockState blockState = quoteBlockState.pop();
		if (blockState.paraOpen) {
			builder.endBlock(); // para
		}
		builder.endBlock(); // quote
	}

	private static class BlockState {
		boolean paraOpen;
	}
}
