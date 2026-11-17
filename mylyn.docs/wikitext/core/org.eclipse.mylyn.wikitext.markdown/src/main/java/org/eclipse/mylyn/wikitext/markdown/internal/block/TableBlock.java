/*******************************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.markdown.internal.block;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * A block that parses GitHub Flavored Markdown (GFM) pipe tables.
 * <p>
 * The block buffers the first line (header) and only commits to table parsing if the immediately following line is a valid delimiter row
 * (e.g., <code>|---|---|</code>). If not, the buffered line is emitted as a regular paragraph.
 * </p>
 */
public class TableBlock extends Block {

	private static final Pattern DELIMITER_ROW_PATTERN = Pattern.compile(
			"^\\|?\\s*:?-{3,}:?\\s*(\\|\\s*:?-{3,}:?\\s*)*\\|?$"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private int headerCellCount = 0;

	private String headerLine;

	private boolean tableStarted = false;

	public TableBlock() {
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			// A table must contain at least one pipe.
			return line.indexOf('|') != -1;
		}
		return false;
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			// Buffer the header line; we don't know yet if this is a table.
			headerLine = line;
			blockLineCount++;
			return -1;
		} else if (blockLineCount == 1) {
			// Check if the second line is a valid delimiter row.
			// Require at least one pipe to distinguish from a thematic break (---).
			if (line.indexOf('|') != -1 && DELIMITER_ROW_PATTERN.matcher(line.trim()).matches()) {
				// Valid table: emit the table, header row, and header cells.
				emitTableStart();
				blockLineCount++;
				return -1;
			} else {
				// Not a table: emit the buffered header line as a paragraph.
				emitBufferedHeaderAsParagraph();
				setClosed(true);
				// Return 0 to allow the parser to reprocess the current line.
				return 0;
			}
		} else {
			// Process data rows.
			if (markupLanguage.isEmptyLine(line)) {
				setClosed(true);
				return 0;
			}
			// Per GFM: a table row is any line containing a pipe. A line without a pipe ends the table.
			if (line.indexOf('|') == -1) {
				setClosed(true);
				return 0;
			}
			emitDataRow(line);
			blockLineCount++;
			return -1;
		}
	}

	private void emitTableStart() {
		builder.beginBlock(BlockType.TABLE, new Attributes());
		// Emit header row
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		String[] headerCells = parseCells(headerLine);
		headerCellCount = headerCells.length;
		for (String cell : headerCells) {
			builder.beginBlock(BlockType.TABLE_CELL_HEADER, new Attributes());
			markupLanguage.emitMarkupLine(getParser(), state, 0, cell.trim(), 0);
			builder.endBlock(); // table cell
		}
		builder.endBlock(); // table row
		tableStarted = true;
	}

	private void emitDataRow(String line) {
		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
		String[] cells = parseCells(line);
		for (int i = 0; i < headerCellCount; i++) {
			String cell = i < cells.length ? cells[i] : ""; //$NON-NLS-1$
			builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
			markupLanguage.emitMarkupLine(getParser(), state, 0, cell.trim(), 0);
			builder.endBlock(); // table cell
		}
		builder.endBlock(); // table row
	}

	private void emitBufferedHeaderAsParagraph() {
		if (headerLine == null) {
			return;
		}
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		markupLanguage.emitMarkupLine(getParser(), state, 0, headerLine, 0);
		builder.endBlock();
		headerLine = null;
	}

	/**
	 * Splits a table line into cells, respecting backslash-escaped pipes (<code>\|</code>). Escaped pipes are treated as literal pipe
	 * characters within the cell content.
	 */
	private String[] parseCells(String line) {
		// Remove leading and trailing pipes if present.
		String trimmed = line.trim();
		if (trimmed.startsWith("|")) { //$NON-NLS-1$
			trimmed = trimmed.substring(1);
		}
		if (trimmed.endsWith("|")) { //$NON-NLS-1$
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}

		List<String> cells = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean escaped = false;
		for (int i = 0; i < trimmed.length(); i++) {
			char c = trimmed.charAt(i);
			if (escaped) {
				if (c != '|') {
					// Preserve non-pipe escapes so downstream inline parsing can handle them.
					current.append('\\');
				}
				current.append(c);
				escaped = false;
			} else if (c == '\\') {
				escaped = true;
			} else if (c == '|') {
				cells.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		if (escaped) {
			current.append('\\');
		}
		cells.add(current.toString());
		return cells.toArray(new String[0]);
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			if (tableStarted) {
				builder.endBlock(); // table
				tableStarted = false;
			} else {
				// The table never started (either the delimiter check failed, or the document ended).
				// Emit the buffered header so its content is not lost.
				emitBufferedHeaderAsParagraph();
			}
		}
		super.setClosed(closed);
	}
}
