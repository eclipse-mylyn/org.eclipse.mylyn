/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.creole.core.block;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * List block, matches blocks that start with <code>*</code> or <code>#</code>
 * 
 * @author Igor Malinin
 */
public class ListBlock extends Block {

	static final Pattern pattern = Pattern.compile("\\s*((?:[*]+)|(?:#+))\\s*(.+)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private Stack<ListState> listState;

	public ListBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		boolean continuation = false;
		if (blockLineCount == 0) {
			listState = new Stack<ListState>();
			String listSpec = matcher.group(1);
			int level = calculateLevel(listSpec);
			BlockType type = calculateType(listSpec.charAt(0));

			offset = matcher.start(2);

			listState.push(new ListState(1, type));
			builder.beginBlock(type, new Attributes());

			adjustLevel(level, type);
		} else {
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
			String listSpec = matcher.group(1);
			int lineLevel = calculateLevel(listSpec);
			BlockType type = calculateType(listSpec.charAt(0));

			offset = matcher.start(2);

			continuation = adjustLevel(lineLevel, type);
		}
		++blockLineCount;

		ListState listState = this.listState.peek();
		if (!continuation && listState.openItem) {
			listState.openItem = false;
			builder.endBlock();
		}
		if (!listState.openItem) {
			listState.openItem = true;
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		}

		markupLanguage.emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	/**
	 * @return true if the item is a continuation
	 */
	private boolean adjustLevel(int lineLevel, BlockType type) {
		boolean continuation = false;

		for (ListState previousState = listState.peek(); lineLevel != previousState.level || previousState.type != type; previousState = listState.peek()) {
			if (lineLevel > previousState.level) {
				if (!previousState.openItem) {
					builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
					previousState.openItem = true;
				}

				listState.push(new ListState(previousState.level + 1, type));
				builder.beginBlock(type, new Attributes());
			} else if (lineLevel == previousState.level && previousState.type == type) {
				if (previousState.openItem) {
					builder.endBlock();
					previousState.openItem = false;
				}
			} else {
				closeOne();
				if (listState.isEmpty()) {
					listState.push(new ListState(1, type));
					builder.beginBlock(type, new Attributes());
				}
			}
		}

		return continuation;
	}

	private int calculateLevel(String listSpec) {
		return listSpec.length();
	}

	private BlockType calculateType(char ch) {
		return (ch == '#') ? BlockType.NUMERIC_LIST : BlockType.BULLETED_LIST;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		listState = null;
		if (lineOffset == 0) {
			matcher = pattern.matcher(line);
			if (matcher.matches()) {
				String listSpec = matcher.group(1);
				if (calculateLevel(listSpec) == 1) {
					return true;
				}
			}
		}
		matcher = null;
		return false;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			while (listState != null && !listState.isEmpty()) {
				closeOne();
			}
			listState = null;
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

		BlockType type;

		boolean openItem;

		private ListState(int level, BlockType type) {
			super();
			this.level = level;
			this.type = type;
		}

	}
}
