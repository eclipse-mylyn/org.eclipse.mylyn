/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.creole.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * Table block, matches any line that looks like a table row.
 * 
 * @author Igor Malinin
 */
public class TableBlock extends Block {

	static final Pattern tableRowPattern = Pattern.compile("^\\s*(\\|(.*)(\\|\\s*$))"); //$NON-NLS-1$

	static final Pattern TABLE_ROW_PATTERN = Pattern.compile("\\|(=?)\\s*([^\\|]*)\\s*(\\|\\s*$)?"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	public TableBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();
			builder.beginBlock(BlockType.TABLE, attributes);
		} else {
			matcher = tableRowPattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
		}
		++blockLineCount;

		String creoleLine = offset == 0 ? line : line.substring(offset);
		Matcher rowMatcher = TABLE_ROW_PATTERN.matcher(creoleLine);
		if (!rowMatcher.find()) {
			setClosed(true);
			return 0;
		}

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		do {
			int start = rowMatcher.start();
			if (start == creoleLine.length() - 1) {
				break;
			}

			String head = rowMatcher.group(1);
			String text = rowMatcher.group(2);
			int textOffset = rowMatcher.start(2);

			Attributes attributes = new Attributes();
			state.setLineCharacterOffset(start);
			if (head.length() > 0) {
				builder.beginBlock(BlockType.TABLE_CELL_HEADER, attributes);
				markupLanguage.emitMarkupLine(getParser(), state, textOffset, text, 0);
			} else {
				builder.beginBlock(BlockType.TABLE_CELL_NORMAL, attributes);
				markupLanguage.emitMarkupLine(getParser(), state, textOffset, text, 0);
			}

			builder.endBlock(); // table cell
		} while (rowMatcher.find());

		builder.endBlock(); // table row

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			matcher = tableRowPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
