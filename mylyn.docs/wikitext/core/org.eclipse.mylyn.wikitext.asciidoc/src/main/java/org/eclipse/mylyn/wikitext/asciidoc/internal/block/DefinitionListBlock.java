/*******************************************************************************
 * Copyright (c) 2007, 2024 Fabrizio Iannetti and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabrizio Iannetti - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

public class DefinitionListBlock extends Block {

	private static final Pattern START_PATTERN = Pattern.compile("\\s*([^\\s:][^:]+)(:{2,})(?:(?:\\s+(.+))|(?:$))"); //$NON-NLS-1$

	private Matcher matcher;

	private boolean blockItemIsOpen;

	private boolean lastLineWasEmpty;

	private final Deque<Integer> levels = new ArrayDeque<>();

	@Override
	protected int processLineContent(String line, int offset) {
		if (matcher.reset(line).matches()) {
			handleItem();
			return -1;
		} else if (line.isEmpty()) {
			lastLineWasEmpty = true;
			return -1;
		} else if (lastLineWasEmpty && blockItemIsOpen) {
			lastLineWasEmpty = false;
			setClosed(true);
			return 0;
		} else {
			lastLineWasEmpty = false;
			if (!blockItemIsOpen) {
				openItemBlock(line, 0);
			} else {
				builder.characters(" "); //$NON-NLS-1$
				markupLanguage.emitMarkupLine(parser, state, 0, line, 0);
			}
			return -1;
		}
	}

	private void handleItem() {
		String key = matcher.group(1);
		String value = matcher.group(3);
		int keyOffset = matcher.start(1);
		int level = matcher.group(2).length();

		if (isCurrentLevel(level)) {
			closeItemBlockIfOpen();
		} else if (isNewLevel(level)) {
			openLevel(level);
		} else {
			dropToLevel(level);
		}

		state.setLineCharacterOffset(keyOffset);
		state.setLineSegmentEndOffset(keyOffset + key.length());
		builder.beginBlock(BlockType.DEFINITION_TERM, new Attributes(null, "hdlist1", null, null)); //$NON-NLS-1$
		markupLanguage.emitMarkupLine(parser, state, keyOffset, key, 0);
		builder.endBlock();

		if (value != null) {
			int valueOffset = matcher.start(3);
			openItemBlock(value, valueOffset);
		}
	}

	private void openLevel(int level) {
		if (!isCurrentLevel(0) && !blockItemIsOpen) {
			openItemBlock("", 0); //$NON-NLS-1$
		}
		builder.beginBlock(BlockType.DEFINITION_LIST, new ListAttributes());
		levels.push(level);
		blockItemIsOpen = false;
	}

	private boolean isCurrentLevel(int level) {
		return levels.isEmpty() ? level == 0 : levels.peek().equals(level);
	}

	private boolean isNewLevel(int level) {
		return !levels.contains(level);
	}

	private void dropToLevel(int level) {
		closeItemBlockIfOpen();
		while (!levels.isEmpty() && !levels.peek().equals(level)) {
			builder.endBlock();
			levels.pop();
			if (!isCurrentLevel(0)) {
				builder.endBlock();
			}
		}
	}

	private void openItemBlock(String value, int valueOffset) {
		state.setLineCharacterOffset(valueOffset);
		state.setLineSegmentEndOffset(valueOffset + value.length());
		builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
		markupLanguage.emitMarkupLine(parser, state, valueOffset, value, 0);
		blockItemIsOpen = true;
	}

	private void closeItemBlockIfOpen() {
		if (blockItemIsOpen) {
			builder.endBlock();
			blockItemIsOpen = false;
		}
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = START_PATTERN.matcher(line);
			if (matcher.matches()) {
				lastLineWasEmpty = false;
				return true;
			}
		}
		matcher = null;
		return false;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			dropToLevel(0);
		}
		super.setClosed(closed);
	}
}
