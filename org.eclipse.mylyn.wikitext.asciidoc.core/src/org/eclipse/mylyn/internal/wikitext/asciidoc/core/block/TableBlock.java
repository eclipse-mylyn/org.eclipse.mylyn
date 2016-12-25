/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.asciidoc.core.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.core.parser.TableRowAttributes;

import com.google.common.base.Splitter;

/**
 * Text block containing a table
 */
public class TableBlock extends AsciiDocBlock {
	private static final Splitter ROW_CELL_SPLITTER = Splitter.on(Pattern.compile("(?<!\\\\)\\|")); //$NON-NLS-1$

	private int cellsCount = 0;

	private List<TableCellAttributes> colsAttribute;

	private boolean hasHeader = false;

	public TableBlock() {
		super(Pattern.compile("^\\|===\\s*")); //$NON-NLS-1$
	}

	@Override
	protected void processBlockStart() {
		Map<String, String> lastProperties = getAsciiDocState().getLastProperties(Collections.emptyList());
		colsAttribute = LanguageSupport.computeColumnsAttributeList(lastProperties.get("cols")); //$NON-NLS-1$

		String options = lastProperties.get("options"); //$NON-NLS-1$
		if (options != null) {
			hasHeader = options.contains("header"); //$NON-NLS-1$
		}

		TableAttributes tableAttributes = new TableAttributes();
		tableAttributes.setWidth(lastProperties.get("width")); //$NON-NLS-1$
		builder.beginBlock(BlockType.TABLE, tableAttributes);
	}

	@Override
	protected void processBlockContent(String line) {
		if (!line.trim().isEmpty()) {
			//If the cols attribute is not set (processing of the first line), the row need to be opened:
			if (colsAttribute.isEmpty()) {
				TableRowAttributes tableRowAttributes = new TableRowAttributes();
				builder.beginBlock(BlockType.TABLE_ROW, tableRowAttributes);
			}

			boolean firstCellInLine = true;
			for (String cell : ROW_CELL_SPLITTER.split(line)) {
				String cellContent = cell.trim();
				if (!cellContent.isEmpty() || !firstCellInLine) {
					//Open row if necessary:
					if (!colsAttribute.isEmpty() && cellsCount % colsAttribute.size() == 0) {
						TableRowAttributes tableRowAttributes = new TableRowAttributes();
						builder.beginBlock(BlockType.TABLE_ROW, tableRowAttributes);
					}

					//Replace escaped pipe:
					String blockContent = cellContent.replaceAll("\\\\\\|", "|"); //$NON-NLS-1$ //$NON-NLS-2$

					//Prepare table cell attributes:
					TableCellAttributes attributes;
					if (colsAttribute.isEmpty()) {
						attributes = new TableCellAttributes();
					} else {
						attributes = colsAttribute.get(cellsCount % colsAttribute.size());
					}

					//Build the cell block:
					if (hasHeader && (colsAttribute.isEmpty() || cellsCount < colsAttribute.size())) {
						builder.beginBlock(BlockType.TABLE_CELL_HEADER, attributes);
					} else {
						builder.beginBlock(BlockType.TABLE_CELL_NORMAL, attributes);
					}
					markupLanguage.emitMarkupLine(parser, state, blockContent, 0);
					builder.endBlock();

					//Increment the cell counter:
					cellsCount = cellsCount + 1;

					//Close row if necessary:
					if (!colsAttribute.isEmpty() && cellsCount % colsAttribute.size() == 0) {
						builder.endBlock(); // table row
					}
				}
				firstCellInLine = false;
			}
			//If the cols attribute is not set (processing of the first line), the row need to be closed and colsAttribute can be determined:
			if (colsAttribute.isEmpty()) {
				builder.endBlock(); // table row
				// end of the first row, it defines the number of cells in the next rows when cols is missing:
				colsAttribute = LanguageSupport.createDefaultColumnsAttributeList(cellsCount);
			}
		}
	}

	@Override
	protected void processBlockEnd() {
		if (!colsAttribute.isEmpty() && cellsCount % colsAttribute.size() != 0) {
			builder.endBlock(); // table row
		}
		builder.endBlock(); // table
	}

}
