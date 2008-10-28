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
package org.eclipse.mylyn.internal.wikitext.twiki.core.block;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * List block, matches blocks that start with <code>   *</code>, <code>   1</code>, <code>   a</code>,
 * <code>   A</code> <code>   i</code> or  <code>   I</code>.  Note that preceding spaces must be
 * in multiples of 3.
 * 
 * @author David Green
 */
public class ListBlock extends Block {

	static final Pattern startPattern = Pattern.compile("((?: {3})+)(\\*|(?:1|a|A|i|I)\\.)\\s(.*+)"); //$NON-NLS-1$
	static final Pattern continuationPattern = Pattern.compile(" {3}\\s*(.*+)"); //$NON-NLS-1$

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
			int level = calculateLevel(listSpec);
			String typeSpec = matcher.group(2);
			BlockType type = calculateType(typeSpec);

			computeAttributes(attributes, type, typeSpec);

			// 0-offset matches may start with the "*** " prefix.
			offset = matcher.start(3);

			listState.push(new ListState(1, type));
			builder.beginBlock(type, attributes);

			adjustLevel(type, listSpec, level);
		} else {
			Matcher matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				// FIXME: continuations not yet implemented
				matcher = continuationPattern.matcher(line);
				if (listState.isEmpty() || !matcher.matches()) {
					setClosed(true);
					return 0;
				} else {
					continuation = true;
					offset = matcher.start(1)-1; // use -1 to get one whitespace character
				}
			} else {
				String listSpec = matcher.group(1);
				int level = calculateLevel(listSpec);
				String typeSpec = matcher.group(2);
				
				BlockType type = calculateType(typeSpec);
				offset = matcher.start(3);

				adjustLevel(type, typeSpec, level);
			}
		}
		++blockLineCount;

		ListState listState = this.listState.peek();
		if (!continuation && listState.openItem) {
			builder.endBlock();
			listState.openItem = false;
		}
		if (!listState.openItem) {
			listState.openItem = true;
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		}

		markupLanguage.emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	private void computeAttributes(Attributes attributes, BlockType type,
			String typeSpec) {
		if (type == BlockType.NUMERIC_LIST) {
			switch (typeSpec.charAt(0)) {
			case 'a':
				attributes.setCssStyle("list-style: lower-alpha;"); //$NON-NLS-1$
				break;
			case 'A':
				attributes.setCssStyle("list-style: upper-alpha;"); //$NON-NLS-1$
				break;
			case 'i':
				attributes.setCssStyle("list-style: lower-roman;"); //$NON-NLS-1$
				break;
			case 'I':
				attributes.setCssStyle("list-style: upper-roman;"); //$NON-NLS-1$
				break;
			}
		}
	}

	private void adjustLevel(BlockType type,String typeSpec, int level) {
		for (ListState previousState = listState.peek(); level != previousState.level || previousState.type != type; previousState = listState.peek()) {

			if (level > previousState.level) {
				if (!previousState.openItem) {
					builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
					previousState.openItem = true;
				}

				Attributes blockAttributes = new Attributes();
				computeAttributes(blockAttributes, type, typeSpec);

				listState.push(new ListState(previousState.level + 1, type));
				builder.beginBlock(type, blockAttributes);
			} else {
				closeOne();
				if (listState.isEmpty()) {
					Attributes blockAttributes = new Attributes();
					computeAttributes(blockAttributes, type, typeSpec);

					listState.push(new ListState(1, type));
					builder.beginBlock(type, blockAttributes);
				}
			}
		}
	}

	private int calculateLevel(String listSpec) {
		return listSpec.length() / 3;
	}

	private BlockType calculateType(String listSpec) {
		return listSpec.charAt(listSpec.length() - 1) == '*' ? BlockType.BULLETED_LIST: BlockType.NUMERIC_LIST;
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

		boolean openItem;

		private ListState(int level, BlockType type) {
			super();
			this.level = level;
			this.type = type;
		}

	}
}
