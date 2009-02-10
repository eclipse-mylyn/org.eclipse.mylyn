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
import org.eclipse.mylyn.wikitext.core.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * List block, matches blocks that follow trac list rules (whitespace, then '*' or 1.)
 * 
 * @author David Green
 */
public class ListBlock extends Block {

	private static final int LINE_REMAINDER_GROUP_OFFSET = 4;

	static final Pattern startPattern = Pattern.compile("(?:(\\s+)(?:(\\*)|(?:(\\d+)\\.)))\\s+(.*+)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private final Stack<ListState> listState = new Stack<ListState>();

	public ListBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			ListAttributes attributes = new ListAttributes();
			String spaces = matcher.group(1);
			String listSpec = matcher.group(2);
			String numericListSpec = matcher.group(3);

			if (numericListSpec != null && !"1".equals(numericListSpec)) { //$NON-NLS-1$
				attributes.setStart(numericListSpec);
			}

			int level = calculateLevel(spaces);

			BlockType type = listSpec == null ? BlockType.NUMERIC_LIST : BlockType.BULLETED_LIST;

			if (type == BlockType.BULLETED_LIST && "-".equals(listSpec)) { //$NON-NLS-1$
				attributes.setCssStyle("list-style: square"); //$NON-NLS-1$
			}

			offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);

			listState.push(new ListState(level, spaces.length(), type));
			builder.beginBlock(type, attributes);
		} else {
			ListAttributes attributes = new ListAttributes();
			Matcher matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
			String spaces = matcher.group(1);
			String listSpec = matcher.group(2);
			String numericListSpec = matcher.group(3);

			if (numericListSpec != null && !"1".equals(numericListSpec)) { //$NON-NLS-1$
				attributes.setStart(numericListSpec);
			}

			int level = calculateLevel(spaces);

			BlockType type = listSpec == null ? BlockType.NUMERIC_LIST : BlockType.BULLETED_LIST;

			offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);

			for (ListState listState = this.listState.peek(); listState.level != level || listState.type != type; listState = this.listState.peek()) {
				if (listState.level > level || (listState.level == level && listState.type != type)) {
					closeOne();
					if (this.listState.isEmpty()) {
						this.listState.push(new ListState(1, spaces.length(), type));
						builder.beginBlock(type, attributes);
					}
				} else {
					this.listState.push(new ListState(level, spaces.length(), type));
					builder.beginBlock(type, attributes);
				}
			}
		}
		++blockLineCount;

		ListState listState = this.listState.peek();
		if (listState.openItem) {
			builder.endBlock();
		}
		listState.openItem = true;
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());

		markupLanguage.emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	private int calculateLevel(String spaces) {
		int length = spaces.length();
		if (length == 0) {
			throw new IllegalStateException();
		}
		int level = 1;
		for (int x = 1; x < listState.size(); ++x) {
			ListState state = listState.get(x);
			if (state.numSpaces <= length) {
				level = state.level;
			} else {
				break;
			}
		}
		if (!listState.isEmpty()) {
			ListState outerState = listState.peek();
			if (level == outerState.level && length > outerState.numSpaces) {
				level = outerState.level + 1;
			}
		}
		return level;
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
			while (!listState.isEmpty()) {
				closeOne();
			}
		}
		super.setClosed(closed);
	}

	private void closeOne() {
		ListState e = listState.pop();
		if (e.openItem) {
			builder.endBlock();
		}
		builder.endBlock();
	}

	private static class ListState {
		int level;

		int numSpaces;

		BlockType type;

		boolean openItem;

		private ListState(int level, int numSpaces, BlockType type) {
			super();
			this.level = level;
			this.numSpaces = numSpaces;
			this.type = type;
		}

	}
}
