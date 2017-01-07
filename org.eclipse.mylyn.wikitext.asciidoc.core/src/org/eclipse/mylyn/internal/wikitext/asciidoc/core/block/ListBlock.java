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

	private static final String ANY_CHAR = "\\s+(.*+)"; //$NON-NLS-1$

	/** List item start */
	private static final Pattern startPattern = Pattern.compile("\\s*((?:(?:\\*)|(?:\\.)){1,5}+|-)" + ANY_CHAR); //$NON-NLS-1$

	private static final Pattern leadingBlankPattern = Pattern.compile("^\\s+"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private Stack<ListState> listState;

	private boolean blankSeparator;

	private boolean listContinuation;

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		listState = null;
		return correspondsToListLine(line, lineOffset);
	}

	private boolean correspondsToListLine(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			// ignore
			return false;
		}
	}

	/** AsciiDoc line process with given offset. */
	@Override
	protected int processLineContent(String line, int offset) {
		boolean closeItem = true;
		// first line processed in current block
		if (blockLineCount == 0) {
			listState = new Stack<>();
			Attributes attributes = new Attributes();
			String listSpec = matcher.group(1);
			BlockType type = calculateType(listSpec);

			// first line of the block could be "** " or more
			offset = matcher.start(2);

			listState.push(new ListState(listSpec, 1, type));
			builder.beginBlock(type, attributes);

			adjustLevel(matcher, type, listSpec);
		} else if (line.isEmpty()) {
			if (!listContinuation) {
				blankSeparator = true;
			}
			return -1;
		} else if (isListContinuation(line)) {
			// list continuation
			blankSeparator = false;
			listContinuation = true;
			closeItem = false;
			return -1;
		} else {
			Matcher matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				if (blankSeparator) {
					setClosed(true);
					blankSeparator = false;
					return 0;
				}
				closeItem = false;
				Matcher leadingBlankMatcher = leadingBlankPattern.matcher(line);
				if (leadingBlankMatcher.find()) {
					offset = leadingBlankMatcher.end();
				}
				markupLanguage.emitMarkupText(getParser(), state, " "); //$NON-NLS-1$
			} else {
				String listSpec = matcher.group(1);
				BlockType type = calculateType(listSpec);

				offset = matcher.start(2);

				adjustLevel(matcher, type, listSpec);
			}
			blankSeparator = false;
			listContinuation = false;
		}
		++blockLineCount;

		ListState listState = this.listState.peek();
		if (closeItem) {
			if (listState.openItem) {
				builder.endBlock();
			}
			listState.openItem = true;
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		}

		markupLanguage.emitMarkupLine(getParser(), state, line, offset);
		// entire line processed
		return -1;
	}

	private boolean isListContinuation(String line) {
		return "+".equals(line); //$NON-NLS-1$
	}

	private BlockType calculateType(String listSpec) {
		switch (listSpec.charAt(listSpec.length() - 1)) {
		case '.':
			return BlockType.NUMERIC_LIST;
		default:
			return BlockType.BULLETED_LIST;
		}
	}

	private void adjustLevel(Matcher matcher, BlockType type, String tag) {
		// find correct level for next list item
		// default is next level
		int level = listState.size() + 1;
		for (ListState ls : listState) {
			if (ls.tag.equals(tag)) {
				level = ls.level; // tag already used: drop to that level
			}
		}

		for (ListState prevState = listState.peek(); level != prevState.level
				|| prevState.type != type; prevState = listState.peek()) {

			if (level > prevState.level) {
				if (!prevState.openItem) {
					builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
					prevState.openItem = true;
				}

				Attributes blockAttributes = new Attributes();

				listState.push(new ListState(tag, prevState.level + 1, type));
				builder.beginBlock(type, blockAttributes);
			} else {
				closeOne();
				if (listState.isEmpty()) {
					Attributes blockAttributes = new Attributes();
					// TODO add attribute conf support if nedeed

					listState.push(new ListState(tag, 1, type));
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

	@Override
	public boolean beginNesting() {
		return listContinuation;
	}

	@Override
	public int findCloseOffset(String line, int lineOffset) {
		if (line.isEmpty() || isListContinuation(line)) {
			return 0;
		}
		return -1;
	}

	@Override
	public boolean canResume(String line, int lineOffset) {
		return listContinuation && correspondsToListLine(line, lineOffset);
	}

	private static class ListState {
		int level;

		String tag;

		BlockType type;

		boolean openItem;

		private ListState(String tag, int level, BlockType type) {
			super();
			this.tag = tag;
			this.level = level;
			this.type = type;
		}
	}

}
