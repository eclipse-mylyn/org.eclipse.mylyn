/*******************************************************************************
 * Copyright (c) 2012 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Definition list block, matches blocks that follow trac list rules (item line: whitespace, item, '::' and definition
 * possibility to continue the definition on the next lines)
 * 
 * @author Jeremie Bresson
 */
public class DefinitionListBlock extends Block {

	/**
	 * Pattern for a line containing an item: " item:: foo" or " item::"
	 */
	static final Pattern itemLinePattern = Pattern.compile("(\\s+)([^\\:\\:]*+)(\\:\\:\\s*)(.*+)"); //$NON-NLS-1$

	/**
	 * pattern for definition
	 */
	static final Pattern definitionLinePattern = Pattern.compile("(\\s+)(.*+)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private boolean listBlockOpened;

	private boolean definitionBlockOpened;

	public DefinitionListBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.DEFINITION_LIST, new Attributes());
			listBlockOpened = true;

			return processItemLine(line, matcher);
		} else {
			Matcher itemLineMatcher = itemLinePattern.matcher(line);
			if (!itemLineMatcher.matches()) {
				Matcher definitionLineMatcher = definitionLinePattern.matcher(line);
				if (definitionLineMatcher.matches()) {
					return processDefinitionLine(line, definitionLineMatcher);
				} else {
					setClosed(true);
					return 0;
				}
			} else {
				//matches an itemLine
				return processItemLine(line, itemLineMatcher);
			}
		}
	}

	private int processItemLine(String line, Matcher itemLineMatcher) {
		closeDefinitionBlock();

		int offset;
		++blockLineCount;

		//term block:
		builder.beginBlock(BlockType.DEFINITION_TERM, new Attributes());
		offset = itemLineMatcher.start(2);
		markupLanguage.emitMarkupLine(getParser(), state, line.substring(0, itemLineMatcher.end(2)), offset);
		builder.endBlock(); // close  BlockType.DEFINITION_TERM

		//definition block:
		if (itemLineMatcher.group(4).length() > 0) {
			builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
			definitionBlockOpened = true;
			offset = itemLineMatcher.start(4);
			markupLanguage.emitMarkupLine(getParser(), state, line, offset);
		}

		return -1;
	}

	private int processDefinitionLine(String line, Matcher definitionLineMatcher) {
		int offset;
		++blockLineCount;

		offset = definitionLineMatcher.start(2);
		if (definitionBlockOpened) {
			offset = offset - 1;
		} else {
			builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
			definitionBlockOpened = true;
		}
		markupLanguage.emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			matcher = itemLinePattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			closeDefinitionBlock();
			closeListBlock();
		}
		super.setClosed(closed);
	}

	private void closeDefinitionBlock() {
		if (definitionBlockOpened) {
			builder.endBlock();
			definitionBlockOpened = false;
		}
	}

	private void closeListBlock() {
		if (listBlockOpened) {
			builder.endBlock();
			listBlockOpened = false;
		}
	}
}
