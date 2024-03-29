/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

/**
 * Matches any textile text, including lines starting with <code>p. </code>.
 * 
 * @author David Green
 */
public class ParagraphBlock extends Block {

	private static final Pattern confluenceBlockStart = Pattern
			.compile("\\{(code|info|tip|warning|panel|note|toc|noformat)(?:(:[^\\}]*))?\\}"); //$NON-NLS-1$

	private int blockLineCount = 0;

	public ParagraphBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();

			builder.beginBlock(BlockType.PARAGRAPH, attributes);
		}

		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		++blockLineCount;

		ConfluenceLanguage markupLanguage = (ConfluenceLanguage) getMarkupLanguage();

		// NOTE: in Confluence paragraphs can have nested lists and other things, however
		//       the resulting XHTML is invalid -- so here we allow for similar constructs
		//       however we cause them to end the paragraph rather than being nested.
		if (paragraphBreakingBlockMatches(markupLanguage, line, offset)) {
			setClosed(true);
			return 0;
		}

		Matcher blockStartMatcher = confluenceBlockStart.matcher(line);
		if (offset > 0) {
			blockStartMatcher.region(offset, line.length());
		}
		if (blockStartMatcher.find()) {
			int end = blockStartMatcher.start();
			if (end > offset) {
				markupLanguage.emitMarkupLine(getParser(), state, offset, line.substring(offset, end), 0);
			}
			setClosed(true);
			return end;
		}
		if (blockLineCount > 1) {
			builder.lineBreak();
		}
		markupLanguage.emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		return true;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

	static boolean paragraphBreakingBlockMatches(MarkupLanguage language, String line, int offset) {
		ConfluenceLanguage markupLanguage = (ConfluenceLanguage) language;
		for (Block block : markupLanguage.getParagraphBreakingBlocks()) {
			if (block.canStart(line, offset)) {
				return true;
			}
		}
		return false;
	}
}
