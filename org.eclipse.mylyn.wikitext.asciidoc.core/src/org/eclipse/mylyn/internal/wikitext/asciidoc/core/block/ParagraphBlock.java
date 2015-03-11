/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Asciidoc default paragraph.
 * 
 * @author Stefan Seelman
 * @author Max Rydahl Andersen - based/copied from markdown to adopt for asciidoc
 */
public class ParagraphBlock extends Block {

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		return true;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		// start of block
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		}

		// empty line: start new block
		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		// next line, does not convert to line break
		if (blockLineCount > 0) {
			builder.characters("\n"); //$NON-NLS-1$
		}

		getMarkupLanguage().emitMarkupLine(getParser(), state, line, offset);

		blockLineCount++;
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
			builder.characters("\n"); //$NON-NLS-1$
		}
		super.setClosed(closed);
	}
}
