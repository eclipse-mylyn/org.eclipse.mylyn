/*******************************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder.Event;
import org.junit.jupiter.api.Test;

/**
 * Tests for GFM pipe-table support in {@link org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage}.
 * <p>
 * Uses {@link org.eclipse.mylyn.wikitext.toolkit.RecordingDocumentBuilder} so assertions are made against the structural events emitted by
 * the parser rather than against raw HTML.
 * </p>
 */
@SuppressWarnings({ "nls", "restriction" })
public class MarkdownTableTest extends MarkdownLanguageTestBase {

	@Test
	public void testBasicTable() {
		String markup = """
				| Name | Value |
				|------|-------|
				| A    | 1     |
				| B    | 2     |""";

		List<Event> events = parseToEvents(markup);

		assertCell(events, BlockType.TABLE_CELL_HEADER, "Name");
		assertCell(events, BlockType.TABLE_CELL_HEADER, "Value");
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "A");
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "1");
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "B");
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "2");
	}

	@Test
	public void testTableCornerCases() {
		// 1. Content before the table (a normal paragraph).
		// 2. No leading/trailing pipes on any row.
		// 3. Alignment colons in the delimiter row.
		// 4. Escaped pipe (\|) inside a cell becomes a literal pipe.
		// 5. A non-pipe line after the table becomes a paragraph and is NOT swallowed as a row.
		String markup = """
				Before the table

				Name | Value
				:-----|------:
				A    | 1
				B    | a \\| b
				After the table""";

		List<Event> events = parseToEvents(markup);

		// Paragraph before the table.
		assertBlockContains(events, BlockType.PARAGRAPH, "Before the table");

		// Headers (no leading/trailing pipes).
		assertCell(events, BlockType.TABLE_CELL_HEADER, "Name");
		assertCell(events, BlockType.TABLE_CELL_HEADER, "Value");

		// Data cells.
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "A");
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "1");
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "B");
		// escaped pipe
		assertCell(events, BlockType.TABLE_CELL_NORMAL, "a | b");

		// The trailing line is a paragraph...
		assertBlockContains(events, BlockType.PARAGRAPH, "After the table");
	}

	private void assertCell(List<Event> events, BlockType cellType, String expectedText) {
		assertBlockContent(events, cellType, expectedText, false);
	}

	private void assertBlockContains(List<Event> events, BlockType blockType, String expectedText) {
		assertBlockContent(events, blockType, expectedText, true);
	}

	private void assertBlockContent(List<Event> events, BlockType blockType, String expectedText, boolean contains) {
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			if (event.blockType != blockType) {
				continue;
			}
			// We're at a block event for the target type. Collect the character events that follow
			// it until the next block boundary, then compare.
			StringBuilder content = new StringBuilder();
			for (int j = i + 1; j < events.size(); j++) {
				Event next = events.get(j);
				if (next.blockType != null) {
					break; // Next block begins (or current one ends); stop collecting.
				}
				if (next.text != null) {
					content.append(next.text);
				}
			}
			String actual = content.toString().trim();
			boolean matches = contains ? actual.contains(expectedText) : actual.equals(expectedText);
			if (matches) {
				return;
			}
		}

		throw new AssertionError(
				"Expected " + blockType + " " + (contains ? "containing" : "equal to") + " '" + expectedText
				+ "'; events: " + events);
	}
}
