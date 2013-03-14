/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Markdown blockquote.
 * 
 * @author Stefan Seelmann
 */
public class QuoteBlock extends Block {

	private static final Pattern startPattern = Pattern.compile(">\\s*(.*)"); //$NON-NLS-1$

	private static final Pattern linePattern = Pattern.compile("(?:>\\s*)?(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Block nestedBlock = null;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			return startPattern.matcher(line).matches();
		} else {
			return false;
		}
	}

	@Override
	protected int processLineContent(String line, int offset) {

		// start of block
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.QUOTE, new Attributes());
		}

		// empty line: end of block
		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		// extract the content
		Matcher matcher = linePattern.matcher(line);
		if (!matcher.matches()) {
			setClosed(true);
			return 0;
		}
		String content = matcher.group(1);

		// determine nested block, at least the paragraph block must match
		for (Block block : getMarkupLanguage().getBlocks()) {
			if (block.canStart(content, 0)) {
				if (nestedBlock != null && nestedBlock.getClass() != block.getClass()) {
					nestedBlock.setClosed(true);
					nestedBlock = null;
				}
				if (nestedBlock == null) {
					nestedBlock = block.clone();
					nestedBlock.setParser(getParser());
					nestedBlock.setState(getState());
				}
				break;
			}
		}

		// delegate content processing to nested block
		int lineOffset = nestedBlock.processLine(content, 0);
		if (nestedBlock.isClosed()) {
			nestedBlock = null;
		}

		blockLineCount++;
		return -1;
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
