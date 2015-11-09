/*******************************************************************************
 * Copyright (c) 2015, 2016 Patrik Suzzi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patrik Suzzi - Bug 481670 - [asciidoc] support for lists
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * List block, matches blocks that start with <code>*</code> or, <code>#</code>
 *
 * @author Patrik Suzzi
 */
public class ListBlock extends Block {

	private static final String ANY_CHAR = "\\s(.*+)"; //$NON-NLS-1$

	/** List item start */
	private static final Pattern startPattern = Pattern.compile("((?:(?:\\*)|(?:\\.))+)" + ANY_CHAR); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private Stack<ListState> listState;

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		listState = null;
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			boolean matchBlockStart = matcher.matches();
			return matchBlockStart;
		} else {
			matcher = null;
			// ignore
			return false;
		}
	}

	/** AsciiDoc line process with given offset. */
	@Override
	protected int processLineContent(String line, int offset) {
		// first line processed in current block
		if (blockLineCount == 0) {
			listState = new Stack<ListState>();
			Attributes attributes = new Attributes();
			String listSpec = matcher.group(1);
			int level = calculateLevel(listSpec);
			BlockType type = calculateType(listSpec);

			// first line of the block could be "** " or more
			offset = level; //matcher.start(LINE_REMAINDER_GROUP_OFFSET);

			listState.push(new ListState(1, type));
			builder.beginBlock(type, attributes);

			adjustLevel(matcher, level, type);
		} else {
			Matcher matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
			String listSpec = matcher.group(1);
			int level = calculateLevel(listSpec);
			BlockType type = calculateType(listSpec);

			offset = level;

			adjustLevel(matcher, level, type);
		}
		++blockLineCount;

		ListState listState = this.listState.peek();
		if (listState.openItem) {
			builder.endBlock();
		}
		listState.openItem = true;
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());

		markupLanguage.emitMarkupLine(getParser(), state, line, offset);
		// entire line processed
		return -1;
	}

	private int calculateLevel(String listSpec) {
		return listSpec.length();
	}

	private BlockType calculateType(String listSpec) {
		switch (listSpec.charAt(listSpec.length() - 1)) {
		case '.':
			return BlockType.NUMERIC_LIST;
		default:
			return BlockType.BULLETED_LIST;
		}
	}

	private void adjustLevel(Matcher matcher, int level, BlockType type) {
		for (ListState prevState = listState.peek(); level != prevState.level
				|| prevState.type != type; prevState = listState.peek()) {

			if (level > prevState.level) {
				if (!prevState.openItem) {
					builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
					prevState.openItem = true;
				}

				Attributes blockAttributes = new Attributes();

				listState.push(new ListState(prevState.level + 1, type));
				builder.beginBlock(type, blockAttributes);
			} else {
				closeOne();
				if (listState.isEmpty()) {
					Attributes blockAttributes = new Attributes();
					// TODO add attribute conf support if nedeed

					listState.push(new ListState(1, type));
					builder.beginBlock(type, blockAttributes);
				}
			}

		}
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			while (listState != null && !listState.isEmpty()) {
				closeOne();
			}
			listState = null;
		}
		// ignore
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
