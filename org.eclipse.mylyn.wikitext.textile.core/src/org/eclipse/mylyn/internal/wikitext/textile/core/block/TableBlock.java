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
package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.textile.core.Textile;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableRowAttributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Table block, matches blocks that start with <code>table. </code> or those that start with a table row.
 * 
 * @author David Green
 */
public class TableBlock extends Block {

	// NOTE: no need for whitespace after the dot on 'table.'
	static final Pattern startPattern = Pattern.compile("(table" + Textile.REGEX_BLOCK_ATTRIBUTES
			+ "\\.)|(\\|(.*)?(\\|\\s*$))");

	static final Pattern rowAttributesPattern = Pattern.compile(Textile.REGEX_BLOCK_ATTRIBUTES + "\\.\\s*.*");

	static final Pattern TABLE_ROW_PATTERN = Pattern.compile("\\|(?:\\\\(\\d+))?(?:/(\\d+))?((?:\\<\\>)|\\<|\\>|\\^)?"
			+ Textile.REGEX_ATTRIBUTES + "(_|\\|)?\\.?\\s?([^\\|]*)(\\|\\|?\\s*$)?");

	private int blockLineCount = 0;

	private Matcher matcher;

	public TableBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();
			if (matcher.group(1) != null) {
				// 0-offset matches may start with the "table. " prefix.
				Textile.configureAttributes(attributes, matcher, 2, true);
				offset = line.length();
			}
			builder.beginBlock(BlockType.TABLE, attributes);
		} else if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}
		++blockLineCount;

		if (offset == line.length()) {
			return -1;
		}

		String textileLine = offset == 0 ? line : line.substring(offset);
		Matcher rowMatcher = TABLE_ROW_PATTERN.matcher(textileLine);
		if (!rowMatcher.find()) {
			setClosed(true);
			return 0;
		}

		{
			TableRowAttributes rowAttributes = new TableRowAttributes();
			int rowStart = rowMatcher.start();
			if (rowStart > 0) {
				// if the row content starts somewhere in the line then it's likely
				// that we have some row-level attributes
				Matcher rowAttributesMatcher = rowAttributesPattern.matcher(textileLine);
				if (rowAttributesMatcher.matches()) {
					Textile.configureAttributes(rowAttributes, rowAttributesMatcher, 1, true);
				}
			}
			builder.beginBlock(BlockType.TABLE_ROW, rowAttributes);
		}

		do {
			int start = rowMatcher.start();
			if (start == textileLine.length() - 1) {
				break;
			}

			String colSpan = rowMatcher.group(1);
			String rowSpan = rowMatcher.group(2);
			String alignment = rowMatcher.group(3);
			String headerIndicator = rowMatcher.group(8);
			String text = rowMatcher.group(9);
			int textLineOffset = rowMatcher.start(9);

			boolean header = headerIndicator != null && ("_".equals(headerIndicator) || "|".equals(headerIndicator));

			String textAlign = null;
			if (alignment != null) {
				if (alignment.equals("<>")) {
					textAlign = "text-align: center;";
				} else if (alignment.equals(">")) {
					textAlign = "text-align: right;";
				} else if (alignment.equals("<")) {
					textAlign = "text-align: left;";
				} else if (alignment.equals("^")) {
					textAlign = "text-align: top;";
				}
			}
			TableCellAttributes attributes = new TableCellAttributes();
			attributes.setCssStyle(textAlign);
			attributes.setRowspan(rowSpan);
			attributes.setColspan(colSpan);
			Textile.configureAttributes(attributes, rowMatcher, 4, false);

			state.setLineCharacterOffset(start);
			builder.beginBlock(header ? BlockType.TABLE_CELL_HEADER : BlockType.TABLE_CELL_NORMAL, attributes);

			markupLanguage.emitMarkupLine(getParser(), state, textLineOffset, text, 0);

			builder.endBlock(); // table cell
		} while (rowMatcher.find());

		builder.endBlock(); // table row

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
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
