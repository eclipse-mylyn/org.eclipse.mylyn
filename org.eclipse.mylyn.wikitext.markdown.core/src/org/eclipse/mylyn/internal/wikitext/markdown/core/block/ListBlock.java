/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
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

/**
 * Markdown lists.
 * 
 * @author Stefan Seelmann
 */
public class ListBlock extends NestableBlock {

	private static final Pattern itemStartPattern = Pattern.compile(" {0,3}(?:([\\*\\+\\-])|([0-9]+\\.))\\s+(.+?)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		Matcher matcher = itemStartPattern.matcher(line.substring(lineOffset));
		return matcher.matches();
	}

	@Override
	protected int processLineContent(String line, int offset) {

		String text = line.substring(offset);

		// check start of block/item
		String content;
		Matcher itemStartMatcher = itemStartPattern.matcher(text);
		if (itemStartMatcher.matches()) {
			if (blockLineCount == 0) {
				// start list block
				BlockType blockType = itemStartMatcher.group(1) != null
						? BlockType.BULLETED_LIST
						: BlockType.NUMERIC_LIST;
				builder.beginBlock(blockType, new Attributes());
			} else {
				// end previous item
				builder.endBlock();
			}

			// start item
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());

			// extract content
			content = itemStartMatcher.group(3);
		} else if (!text.trim().isEmpty()) {
			// TODO: improve handling of wrapped lines, e.g. trim left
			builder.characters("\n"); //$NON-NLS-1$
			content = text;
		} else {
			// TODO: check for multiple paragraphs and nested blocks, for now just close the list block 
			setClosed(true);
			return offset;
		}

		int textStart = 0;
		markupLanguage.emitMarkupLine(getParser(), state, content, textStart);

		blockLineCount++;
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			// end list item
			builder.endBlock();
			// end list block
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
