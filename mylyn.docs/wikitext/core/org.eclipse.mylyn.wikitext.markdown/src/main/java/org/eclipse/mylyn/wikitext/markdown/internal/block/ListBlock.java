/*******************************************************************************
 * Copyright (c) 2013, 2014 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.markdown.internal.util.LookAheadReader;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;

/**
 * Markdown lists.
 *
 * @author Stefan Seelmann
 */
public class ListBlock extends NestableBlock {

	private static final Pattern itemStartPattern = Pattern.compile(" {0,3}(?:([\\*\\+\\-])|([0-9]+\\.))\\s+(.+?)"); //$NON-NLS-1$

	private static final Pattern nestedItemStartPattern = Pattern
			.compile("(( +)|(\t+))(?:([\\*\\+\\-])|([0-9]+\\.))\\s+(.+?)"); //$NON-NLS-1$

	private static final Pattern indentedParagraphPattern = Pattern.compile("( +).*"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private ListBlock nestedBlock = null;

	private ParagraphBlock nestedParagraph = null;

	private int thisIndentation = 0;

	private boolean nextLineStartsNewParagraph = false;

	@Override
	public boolean canStart(String line, int lineOffset) {
		Matcher matcher = itemStartPattern.matcher(line.substring(lineOffset));
		return matcher.matches();
	}

	@Override
	protected int processLineContent(String line, int offset) {
		String text = line.substring(offset);

		// check start of block/item
		Matcher itemStartMatcher = itemStartPattern.matcher(text);
		Matcher nestedItemStartMatcher = nestedItemStartPattern.matcher(text);

		boolean thisLineStartsNewParagraph = nextLineStartsNewParagraph;
		nextLineStartsNewParagraph = false;
		if (itemStartMatcher.matches()) {
			handleItem(text, itemStartMatcher);
		} else if (nestedItemStartMatcher.matches()) {
			handleNestedItem(text, nestedItemStartMatcher);
		} else if (getMarkupLanguage().isEmptyLine(text) && canContinueWithNextLine()) {
			closeNestedParagraph();
			// next line will start a new paragraph
			nextLineStartsNewParagraph = true;
		} else if (!getMarkupLanguage().isEmptyLine(text)) {
			handleText(text, thisLineStartsNewParagraph);
		} else {
			setClosed(true);
			return offset;
		}

		blockLineCount++;
		return -1;
	}

	private void handleItem(String text, Matcher itemStartMatcher) {
		if (blockLineCount == 0) {
			// start list block
			BlockType blockType = itemStartMatcher.group(1) != null ? BlockType.BULLETED_LIST : BlockType.NUMERIC_LIST;
			builder.beginBlock(blockType, new Attributes());
			// start item
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		} else {
			if (nestedBlock != null) {
				nestedBlock.setClosed(true);
				nestedBlock = null;
			} else {
				// end list item
				builder.endBlock();
			}
			// start item
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		}

		int contentStart = itemStartMatcher.start(3);
		thisIndentation = contentStart;
		getMarkupLanguage().emitMarkupLine(getParser(), getState(), text, contentStart);
	}

	private void handleNestedItem(String text, Matcher nestedItemStartMatcher) {
		if (nestedBlock == null) {
			// end list item
			builder.endBlock();
			int nestedOffset = nestedItemStartMatcher.end(1);
			nestedBlock = new ListBlock();
			nestedBlock.setParser(getParser());
			nestedBlock.setState(getState());
			nestedBlock.processLine(text, nestedOffset);
		} else {
			nestedBlock.processLine(text, thisIndentation);
		}
	}

	private boolean canContinueWithNextLine() {
		String nextLine = getNextLine();
		if (nextLine == null) {
			return false;
		}

		Matcher indentedParagraph = indentedParagraphPattern.matcher(nextLine);
		Matcher nextItem = itemStartPattern.matcher(nextLine);
		Matcher nestedItem = nestedItemStartPattern.matcher(nextLine);

		return ((indentedParagraph.matches() && indentedParagraph.end(1) == thisIndentation) // paragraph starts
				|| nextItem.matches() || nestedItem.matches()); // or (nested) list continues
	}

	private void handleText(String text, boolean startsNewParagraph) {
		if (nestedParagraph == null) {
			builder.characters("\n"); //$NON-NLS-1$
			if (startsNewParagraph) {
				nestedParagraph = new ParagraphBlock();
				nestedParagraph.setParser(getParser());
				nestedParagraph.setState(getState());
				nestedParagraph.processLine(text, thisIndentation);
				startsNewParagraph = false;
			} else {
				Matcher matcher = indentedParagraphPattern.matcher(text);
				if (matcher.matches() && matcher.end(1) == thisIndentation) {
					getMarkupLanguage().emitMarkupLine(getParser(), getState(), text, thisIndentation);
				} else {
					getMarkupLanguage().emitMarkupLine(getParser(), getState(), text, 0);
				}
			}
		} else {
			Matcher matcher = indentedParagraphPattern.matcher(text);
			if (matcher.matches() && matcher.end(1) == thisIndentation) {
				nestedParagraph.processLine(text, thisIndentation);
			} else {
				nestedParagraph.processLine(text, 0);
			}
		}
	}

	private void closeNestedParagraph() {
		if (nestedParagraph != null) {
			nestedParagraph.setClosed(true);
			nestedParagraph = null;
		}
	}

	private String getNextLine() {
		LookAheadReader lookAhead = new LookAheadReader();
		lookAhead.setContentState(getState());
		return lookAhead.lookAhead();
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			closeNestedParagraph();
			if (nestedBlock != null && !nestedBlock.isClosed()) {
				nestedBlock.setClosed(closed);
				nestedBlock = null;
			} else {
				// end list item
				builder.endBlock();
			}
			// end list block
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
