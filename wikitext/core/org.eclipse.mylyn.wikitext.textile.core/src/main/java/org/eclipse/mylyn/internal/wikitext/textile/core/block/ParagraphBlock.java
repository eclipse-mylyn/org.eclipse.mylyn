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
package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.textile.core.Textile;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * Matches any textile text, including lines starting with <code>p. </code>.
 * 
 * @author David Green
 */
public class ParagraphBlock extends Block {

	private static final int LINE_REMAINDER_GROUP_OFFSET = Textile.ATTRIBUTES_BLOCK_GROUP_COUNT + 1;

	static final Pattern startPattern = Pattern.compile("p" + Textile.REGEX_BLOCK_ATTRIBUTES + "\\.\\s+(.*)"); //$NON-NLS-1$ //$NON-NLS-2$

	private int blockLineCount = 0;

	private boolean unwrapped = false;

	private boolean enableUnwrapped = true;

	public ParagraphBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();
			if (offset == 0) {
				// 0-offset matches may start with the "p. " prefix.
				Matcher matcher = startPattern.matcher(line);
				if (matcher.matches()) {
					Textile.configureAttributes(attributes, matcher, 1, true);
					offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);
				} else {
					if (line.charAt(0) == ' ') {
						offset = 1;
						if (enableUnwrapped) {
							unwrapped = true;
						}
					}
				}
			}
			if (!unwrapped) {
				builder.beginBlock(BlockType.PARAGRAPH, attributes);
			}
		}

		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}

		TextileLanguage textileLanguage = (TextileLanguage) getMarkupLanguage();

		// NOTE: in Textile paragraphs can have nested lists and other things, however
		//       the resulting XHTML is invalid -- so here we allow for similar constructs
		//       however we cause them to end the paragraph rather than being nested.
		for (Block block : textileLanguage.getParagraphBreakingBlocks()) {
			if (block.canStart(line, offset)) {
				setClosed(true);
				return 0;
			}
		}

		if (blockLineCount != 0) {
			if (unwrapped) {
				builder.characters("\n"); //$NON-NLS-1$
			} else {
				builder.lineBreak();
			}
		}
		++blockLineCount;

		textileLanguage.emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		unwrapped = false;
		blockLineCount = 0;
		return true;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			if (!unwrapped) {
				builder.endBlock();
			}
		}
		super.setClosed(closed);
	}

	public void setEnableUnwrapped(boolean enableUnwrapped) {
		this.enableUnwrapped = enableUnwrapped;
	}

	public boolean isEnableUnwrapped() {
		return enableUnwrapped;
	}
}
