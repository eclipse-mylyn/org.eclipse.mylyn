/*******************************************************************************
 * Copyright (c) 2015, 2024 Patrik Suzzi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Patrik Suzzi - Bug 481670 - [asciidoc] support for lists
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.asciidoc.internal.util.ReadAheadDispatcher;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.ListAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * List block, matches blocks that start with <code>*</code> or, <code>#</code>
 *
 * @author Patrik Suzzi
 */
public class ListBlock extends Block {

	private static final String PARAM_NAME_START = "start"; //$NON-NLS-1$

	private static final String PARAM_NAME_STYLE = "style"; //$NON-NLS-1$

	private static final String ANY_CHAR = "\\s+(.*+)"; //$NON-NLS-1$

	private static final List<String> TYPE_ORDER = List.of("arabic", "loweralpha", "lowerroman", "upperalpha", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"upperroman"); //$NON-NLS-1$

	private static final List<String> TYPE_LISTSPEC = List.of("1.", "a.", "i)", "A.", "I)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private static final List<String> TYPE_CSS_STYLE = List.of("decimal", "lower-alpha", "lower-roman", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"upper-alpha", "upper-roman"); //$NON-NLS-1$//$NON-NLS-2$

	/** List item start */
	private static final Pattern startPattern = Pattern
			.compile("\\s*((?:(?:\\*)|(?:\\.)){1,5}+|-|[a-zA-Z0-9]+\\.|[IVXLCDM]+\\)|[ivxlcdm]+\\))" + ANY_CHAR); //$NON-NLS-1$

	private static final Pattern leadingBlankPattern = Pattern.compile("^\\s+"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private Stack<ListState> listState;

	private boolean blankSeparator;

	private boolean listContinuation;

	private boolean nestingBegin;

	private boolean nestedBlockInterruptible;

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
			ListAttributes attributes = new ListAttributes();
			String listSpec = normalizeListSpec(matcher.group(1));
			BlockType type = calculateType(listSpec);

			if (type == BlockType.NUMERIC_LIST) {

				List<String> positionalParameters = new ArrayList<>();
				positionalParameters.add(PARAM_NAME_STYLE);
				Map<String, String> lastProperties = getAsciiDocState().getLastProperties(positionalParameters);
				getAsciiDocState().setLastPropertiesText(""); //$NON-NLS-1$

				String startProperty = lastProperties.get(PARAM_NAME_START);
				if (startProperty != null) {
					attributes.setStart(startProperty);
				}
				String styleProperty = lastProperties.get(PARAM_NAME_STYLE);
				updateStyleAttribute(attributes, listSpec, styleProperty);
			}
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
				String listSpec = normalizeListSpec(matcher.group(1));
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

	private void updateStyleAttribute(ListAttributes attributes, String listSpec, String styleProperty) {
		int listTypeIndex = styleProperty == null ? -1 : TYPE_ORDER.indexOf(styleProperty);
		if (listTypeIndex < 0) {
			listTypeIndex = TYPE_LISTSPEC.indexOf(listSpec);
		}
		if (listTypeIndex < 0) {
			int level = 0;
			for (ListState ls : listState) {
				if (ls.type == BlockType.NUMERIC_LIST) {
					level++;
				}
			}
			listTypeIndex = level % TYPE_ORDER.size();
		}
		attributes.appendCssStyle("list-style-type:" + TYPE_CSS_STYLE.get(listTypeIndex) + ";"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String normalizeListSpec(String group) {
		if (group.matches("[a-z]+\\.")) { //$NON-NLS-1$
			group = "a."; //$NON-NLS-1$
		} else if (group.matches("[A-Z]+\\.")) { //$NON-NLS-1$
			group = "A."; //$NON-NLS-1$
		} else if (group.matches("[IVXLCDM]+\\)")) { //$NON-NLS-1$
			group = "I)"; //$NON-NLS-1$
		} else if (group.matches("[ivxlcdm]+\\)")) { //$NON-NLS-1$
			group = "i)"; //$NON-NLS-1$
		} else if (group.matches("[0-9]+\\.")) { //$NON-NLS-1$
			group = "1."; //$NON-NLS-1$
		}

		return group;
	}

	private AsciiDocContentState getAsciiDocState() {
		return (AsciiDocContentState) getState();
	}

	private boolean isListContinuation(String line) {
		return "+".equals(line); //$NON-NLS-1$
	}

	private BlockType calculateType(String listSpec) {
		return switch (listSpec.charAt(listSpec.length() - 1)) {
			case '.', ')' -> BlockType.NUMERIC_LIST;
			default -> BlockType.BULLETED_LIST;
		};
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

				ListAttributes attributes = new ListAttributes();

				if (type == BlockType.NUMERIC_LIST) {
					updateStyleAttribute(attributes, tag, null);
				}
				listState.push(new ListState(tag, prevState.level + 1, type));
				builder.beginBlock(type, attributes);
			} else {
				closeOne();
				if (listState.isEmpty()) {
					ListAttributes attributes = new ListAttributes();
					listState.push(new ListState(tag, 1, type));
					builder.beginBlock(type, attributes);
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
		nestingBegin = listContinuation;
		return nestingBegin; // will start nesting after this
	}

	@Override
	public int findCloseOffset(String line, int lineOffset) {
		if (listContinuation) {
			if (nestingBegin) {
				AbstractMarkupLanguage language = (AbstractMarkupLanguage) getParser().getMarkupLanguage();
				Block block = language.startBlock(line, lineOffset);
				nestedBlockInterruptible = isInterruptibleNestedBlock(block);
				nestingBegin = false;
			}
			if (nestedBlockInterruptible && (line.isEmpty() || isListContinuation(line))) {
				listContinuation = isListContinuation(line);
				return 0;
			}
		}
		return -1;
	}

	private boolean isInterruptibleNestedBlock(Block block) {
		return block == null || block instanceof ReadAheadDispatcher;
	}

	@Override
	public boolean canResume(String line, int lineOffset) {
		boolean resume = listContinuation
				&& (correspondsToListLine(line, lineOffset) || line.isEmpty() || isListContinuation(line));
		if (resume) {
			listContinuation = false;
			nestingBegin = false;
		}
		return resume;
	}

	private static class ListState {
		int level;

		String tag;

		BlockType type;

		boolean openItem;

		private ListState(String tag, int level, BlockType type) {
			this.tag = tag;
			this.level = level;
			this.type = type;
		}
	}

}
