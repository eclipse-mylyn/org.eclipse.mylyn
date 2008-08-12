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
package org.eclipse.mylyn.internal.wikitext.confluence.core.block;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

/**
 * quoted text block, matches blocks that start with <code>{quote}</code>. Creates an extended block type of
 * {@link ParagraphBlock paragraph}.
 * 
 * @author David Green
 */
public class ExtendedQuoteBlock extends AbstractConfluenceDelimitedBlock {

	private int paraLine = 0;

	private boolean paraOpen = false;

	public ExtendedQuoteBlock() {
		super("quote");
	}

	@Override
	protected void resetState() {
		super.resetState();
		paraOpen = false;
		paraLine = 0;
	}

	@Override
	protected void beginBlock() {
		Attributes attributes = new Attributes();
		builder.beginBlock(BlockType.QUOTE, attributes);
	}

	@Override
	protected void endBlock() {
		if (paraOpen) {
			builder.endBlock(); // para
			paraLine = 0;
			paraOpen = false;
		}
		builder.endBlock(); // quote
	}

	@Override
	protected void handleBlockContent(String content) {
		if (blockLineCount == 1 && content.length() == 0) {
			return;
		}
		if (getMarkupLanguage().isEmptyLine(content) && blockLineCount > 1 && paraOpen) {
			builder.endBlock(); // para
			paraOpen = false;
			paraLine = 0;
			return;
		}
		if (!paraOpen) {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			paraOpen = true;
		}
		if (paraLine != 0) {
			builder.lineBreak();
		}
		++paraLine;
		getMarkupLanguage().emitMarkupLine(getParser(), state, content, 0);

	}

	@Override
	protected void setOption(String key, String value) {
		// no options
	}
}
