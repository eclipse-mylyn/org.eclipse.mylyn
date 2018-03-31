/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.block;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;

/**
 * Matches any text
 * 
 * @author David Green
 */
public class ParagraphBlock extends Block {

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

		TWikiLanguage textileLanguage = (TWikiLanguage) getMarkupLanguage();

		for (Block block : textileLanguage.getParagraphBreakingBlocks()) {
			if (block.canStart(line, offset)) {
				setClosed(true);
				return 0;
			}
		}

		if (blockLineCount != 0) {
			builder.lineBreak();
		}
		++blockLineCount;

		textileLanguage.emitMarkupLine(getParser(), state, line, offset);

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

}
