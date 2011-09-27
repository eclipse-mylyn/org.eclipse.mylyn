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

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.creole.core.CreoleLanguage;

/**
 * Matches any text
 * 
 * @author Igor Malinin
 */
public class ParagraphBlock extends Block {

	private int blockLineCount = 0;

	public ParagraphBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		}

		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		CreoleLanguage markupLanguage = (CreoleLanguage) getMarkupLanguage();

		// paragraphs can have nested lists and other things
		for (Block block : markupLanguage.getParagraphBreakingBlocks()) {
			if (block.canStart(line, offset)) {
				setClosed(true);
				return 0;
			}
		}

		if (blockLineCount > 0) {
			builder.characters("\n"); //$NON-NLS-1$
		}
		++blockLineCount;

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

}
