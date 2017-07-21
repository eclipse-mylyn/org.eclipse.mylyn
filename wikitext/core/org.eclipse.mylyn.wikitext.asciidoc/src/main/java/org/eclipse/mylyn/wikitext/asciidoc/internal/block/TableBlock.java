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

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.TableAttributes;
import org.eclipse.mylyn.wikitext.parser.TableCellAttributes;
import org.eclipse.mylyn.wikitext.parser.TableRowAttributes;

import com.google.common.base.Splitter;

/**
 * Text block containing a table
 */
public class TableBlock extends AsciiDocBlock {

	private TableFormat format;

	private String separator;

	private enum TableFormat {
		PREFIX_SEPARATED_VALUES, //PSV
		DELIMITER_SEPARATED_VALUES, //DSV
		COMMA_SEPARATED_VALUES //CSV
	}

	private int cellsCount = 0;

	private List<TableCellAttributes> colsAttribute;

	private boolean hasHeader = false;

	private boolean cellBlockIsOpen = false;

	public TableBlock() {
		super(Pattern.compile("^(\\||,|:)===\\s*")); //$NON-NLS-1$
	}

	@Override
	protected void processBlockStart() {

		if (startDelimiter.startsWith(",")) { //$NON-NLS-1$
			// ",===" is the shorthand notation for [format="csv", options="header"]
			format = TableFormat.COMMA_SEPARATED_VALUES;
			hasHeader = true;
		} else if (startDelimiter.startsWith(":")) { //$NON-NLS-1$
			// ":===" is the shorthand notation for [format="dsv", options="header"]
			format = TableFormat.DELIMITER_SEPARATED_VALUES;
			hasHeader = true;
		} else {
			// default table format is PSV with separator "|"
			format = TableFormat.PREFIX_SEPARATED_VALUES;
			separator = "|"; //$NON-NLS-1$
		}

		Map<String, String> lastProperties = getAsciiDocState().getLastProperties(Collections.emptyList());
		colsAttribute = LanguageSupport.computeColumnsAttributeList(lastProperties.get("cols")); //$NON-NLS-1$

		String formatProperty = lastProperties.get("format"); //$NON-NLS-1$
		if (formatProperty != null) {
			switch (formatProperty) {
			case "dsv": //$NON-NLS-1$
				format = TableFormat.DELIMITER_SEPARATED_VALUES;
				break;
			case "csv": //$NON-NLS-1$
				format = TableFormat.COMMA_SEPARATED_VALUES;
				break;
			}
		}

		String separator = lastProperties.get("separator"); //$NON-NLS-1$
		if (separator != null) {
			this.separator = separator;
		}

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
			if (colsAttribute.isEmpty() && !cellBlockIsOpen) {
				TableRowAttributes tableRowAttributes = new TableRowAttributes();
				builder.beginBlock(BlockType.TABLE_ROW, tableRowAttributes);
			}

			boolean firstCellInLine = true;
			for (String cell : createRowCellSplitter(line)) {
				String cellContent = cell.trim();
				if (format == TableFormat.PREFIX_SEPARATED_VALUES && cellBlockIsOpen && !cellContent.isEmpty()
						&& firstCellInLine) {
					markupLanguage.emitMarkupLine(parser, state, " " + cellContent, 0); //$NON-NLS-1$
				} else {
					if (colsAttribute.isEmpty() && cellBlockIsOpen && firstCellInLine) {
						closeCellBlockIfNeeded();
						builder.endBlock(); // close table row
						colsAttribute = LanguageSupport.createDefaultColumnsAttributeList(cellsCount);
					}

					if (!cellContent.isEmpty() || !firstCellInLine) {
						handleCellContent(cellContent);
					}
				}
				firstCellInLine = false;
			}
		}
	}

	private Iterable<String> createRowCellSplitter(String line) {
		if (format == TableFormat.COMMA_SEPARATED_VALUES) {
			return Splitter.on(Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")).split(line); //$NON-NLS-1$
		}
		String delimiter = getCellSeparator();
		return Splitter.on(Pattern.compile("(?<!\\\\)" + Pattern.quote(delimiter))).split(line); //$NON-NLS-1$
	}

	private String getCellSeparator() {
		switch (format) {
		case COMMA_SEPARATED_VALUES:
			return ","; //$NON-NLS-1$
		case DELIMITER_SEPARATED_VALUES:
			return ":"; //$NON-NLS-1$
		case PREFIX_SEPARATED_VALUES:
		default:
			return separator;
		}
	}

	private void handleCellContent(String cellContent) {
		closeCellBlockIfNeeded();

		String blockContent;
		if (format == TableFormat.COMMA_SEPARATED_VALUES) {
			if (cellContent.startsWith("\"") && cellContent.endsWith("\"")) {
				blockContent = cellContent.substring(1, cellContent.length() - 1).replaceAll("\"\"", "\"");
			} else {
				blockContent = cellContent;
			}
		} else {
			String delimiter = getCellSeparator();
			blockContent = cellContent.replaceAll("\\\\" + Pattern.quote(delimiter), delimiter);//$NON-NLS-1$
		}

		if (!colsAttribute.isEmpty() && cellsCount % colsAttribute.size() == 0) {
			TableRowAttributes tableRowAttributes = new TableRowAttributes();
			builder.beginBlock(BlockType.TABLE_ROW, tableRowAttributes);
		}

		TableCellAttributes attributes;
		if (colsAttribute.isEmpty()) {
			attributes = new TableCellAttributes();
		} else {
			attributes = colsAttribute.get(cellsCount % colsAttribute.size());
		}

		if (hasHeader && (colsAttribute.isEmpty() || cellsCount < colsAttribute.size())) {
			builder.beginBlock(BlockType.TABLE_CELL_HEADER, attributes);
		} else {
			builder.beginBlock(BlockType.TABLE_CELL_NORMAL, attributes);
		}
		cellBlockIsOpen = true;

		markupLanguage.emitMarkupLine(parser, state, blockContent, 0);
	}

	@Override
	protected void processBlockEnd() {
		closeCellBlockIfNeeded();
		if ((colsAttribute.isEmpty() && cellsCount > 0)
				|| (!colsAttribute.isEmpty() && cellsCount % colsAttribute.size() != 0)) {
			builder.endBlock(); // close table row
		}
		builder.endBlock(); // close table
	}

	private void closeCellBlockIfNeeded() {
		if (cellBlockIsOpen) {
			builder.endBlock(); // close table cell
			cellBlockIsOpen = false;

			cellsCount = cellsCount + 1;

			if (!colsAttribute.isEmpty() && cellsCount % colsAttribute.size() == 0) {
				builder.endBlock(); // close table row
			}
		}
	}
}
