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
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.block;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * List block, matches blocks that start with <code>*</code>, <code>#</code> or <code>-</code>
 * 
 * @author David Green
 */
public class ListBlock extends Block {

	private static final int LINE_REMAINDER_GROUP_OFFSET = 2;

	static final Pattern startPattern = Pattern.compile("((?:(?:\\*)|(?:#)|(?:-)|(?:\\;)|(?:\\:))+)\\s?(.+)");

	static final Pattern definitionPattern = Pattern.compile("(\\s+\\:\\s+)(.*)");

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
			Attributes attributes = new Attributes();
			String listSpec = matcher.group(1);
			char lastChar = listSpec.charAt(listSpec.length() - 1);
			int level = calculateLevel(listSpec);
			BlockType type = calculateType(lastChar);
			BlockType itemType = calculateItemType(lastChar);

			if (type == BlockType.BULLETED_LIST && '-' == lastChar) {
				attributes.setCssStyle("list-style: square");
			}

			offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);

			listState.push(new ListState(1, type, itemType));
			builder.beginBlock(type, attributes);

			adjustLevel(lastChar, level, type, itemType);
		} else {
			Matcher matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
			String listSpec = matcher.group(1);
			char lastChar = listSpec.charAt(listSpec.length() - 1);
			int lineLevel = calculateLevel(listSpec);
			BlockType type = calculateType(lastChar);
			BlockType itemType = calculateItemType(lastChar);

			offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);

			continuation = adjustLevel(lastChar, lineLevel, type, itemType);
		}
		++blockLineCount;

		ListState listState = this.listState.peek();
		if (!continuation && listState.openItem) {
			listState.openItem = false;
			builder.endBlock();
		}
		if (!listState.openItem) {
			listState.openItem = true;
			builder.beginBlock(listState.itemType, new Attributes());
		}

		String definition = null;
		int definitionOffset = -1;
		if (listState.itemType == BlockType.DEFINITION_TERM) {
			// detect definition on same line as term
			Matcher definitionMatcher = definitionPattern.matcher(line);
			if (offset > 0) {
				definitionMatcher.region(offset, line.length());
			}
			if (definitionMatcher.find()) {
				line = line.substring(offset, definitionMatcher.start(1));
				offset = 0;
				definition = definitionMatcher.group(2);
				definitionOffset = definitionMatcher.start(2);
			}
		}
		if (definition == null) {
			markupLanguage.emitMarkupLine(getParser(), state, line, offset);
		} else {
			markupLanguage.emitMarkupLine(getParser(), state, offset, line, 0);
		}

		if (definition != null) {
			listState.openItem = false;
			builder.endBlock();

			adjustLevel(' ', listState.level, BlockType.DEFINITION_LIST, BlockType.DEFINITION_ITEM);

			listState = this.listState.peek();
			if (listState.openItem) {
				builder.endBlock();
			}
			listState.openItem = true;
			builder.beginBlock(listState.itemType, new Attributes());

			markupLanguage.emitMarkupLine(parser, state, definitionOffset, definition, 0);
		}

		return -1;
	}

	/**
	 * 
	 * @param lastChar
	 *            the last character of the list specification, or ' ' if unknown
	 * 
	 * @return true if the item is a continuation
	 */
	private boolean adjustLevel(char lastChar, int lineLevel, BlockType type, BlockType itemType) {
		boolean continuation = false;

		for (ListState previousState = listState.peek(); lineLevel != previousState.level || previousState.type != type
				|| previousState.itemType != itemType; previousState = listState.peek()) {

			if (lineLevel > previousState.level) {
				if (!previousState.openItem) {
					builder.beginBlock(previousState.itemType, new Attributes());
					previousState.openItem = true;
				}

				Attributes blockAttributes = new Attributes();
				if (type == BlockType.BULLETED_LIST && '-' == lastChar) {
					blockAttributes.setCssStyle("list-style: square");
				}
				listState.push(new ListState(previousState.level + 1, type, itemType));
				builder.beginBlock(type, blockAttributes);
			} else if (lineLevel == previousState.level && previousState.type == type
					&& previousState.itemType != itemType) {
				if (previousState.openItem) {
					builder.endBlock();
					previousState.openItem = false;
				}
				previousState.itemType = itemType;
			} else {
				if (lineLevel == previousState.level && lastChar == ':') {
					// this is possibly a continuation of the previous item.
					if (previousState.itemType != BlockType.DEFINITION_ITEM
							&& previousState.itemType != BlockType.DEFINITION_TERM) {
						// we found a continuation
						continuation = true;
						break;
					}
				}
				closeOne();
				if (listState.isEmpty()) {
					Attributes blockAttributes = new Attributes();
					if (type == BlockType.BULLETED_LIST && '-' == lastChar) {
						blockAttributes.setCssStyle("list-style: square");
					}
					listState.push(new ListState(1, type, itemType));
					builder.beginBlock(type, blockAttributes);
				}
			}
		}

		return continuation;
	}

	private int calculateLevel(String listSpec) {
		return listSpec.length();
	}

	private BlockType calculateType(char lastChar) {
		switch (lastChar) {
		case '#':
			return BlockType.NUMERIC_LIST;
		case ':':
		case ';':
			return BlockType.DEFINITION_LIST;
		default:
			return BlockType.BULLETED_LIST;
		}
	}

	private BlockType calculateItemType(char lastChar) {
		switch (lastChar) {
		case ';':
			return BlockType.DEFINITION_TERM;
		case ':':
			return BlockType.DEFINITION_ITEM;
		default:
			return BlockType.LIST_ITEM;
		}
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		listState = null;
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

		BlockType itemType;

		boolean openItem;

		private ListState(int level, BlockType type, BlockType itemType) {
			super();
			this.level = level;
			this.type = type;
			this.itemType = itemType;
		}

	}
}
