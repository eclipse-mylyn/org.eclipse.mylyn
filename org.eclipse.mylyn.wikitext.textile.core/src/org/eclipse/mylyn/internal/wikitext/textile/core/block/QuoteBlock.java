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
import org.eclipse.mylyn.wikitext.core.parser.QuoteAttributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * quoted text block, matches blocks that start with <code>bc. </code>.
 * Creates an extended block type of {@link ParagraphBlock paragraph}.
 * 
 * @author David Green
 */
public class QuoteBlock extends Block {

	private static final int LINE_REMAINDER_GROUP = Textile.ATTRIBUTES_BLOCK_GROUP_COUNT+3;
	private static final int CITATION_GROUP = Textile.ATTRIBUTES_BLOCK_GROUP_COUNT+2;
	private static final int EXTENDED_GROUP = Textile.ATTRIBUTES_BLOCK_GROUP_COUNT+1;

	static final Pattern startPattern = Pattern.compile("bq"+Textile.REGEX_BLOCK_ATTRIBUTES+"\\.(\\.)?(?::(https?://[^\\s]*))?\\s+(.*)");

	private boolean extended;
	private boolean paraOpen;
	private int blockLineCount = 0;
	private Matcher matcher;

	public QuoteBlock() {
	}

	@Override
	public int processLineContent(String line,int offset) {
		if (blockLineCount == 0) {
			QuoteAttributes attributes = new QuoteAttributes();

			Textile.configureAttributes(attributes,matcher, 1,true);

			attributes.setCitation(matcher.group(CITATION_GROUP));

			offset = matcher.start(LINE_REMAINDER_GROUP);
			extended = matcher.group(EXTENDED_GROUP) != null;

			builder.beginBlock(BlockType.QUOTE, attributes);
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			paraOpen = true;
		}
		if (markupLanguage.isEmptyLine(line)) {
			if (!extended) {
				setClosed(true);
				return 0;
			} else {
				if (paraOpen) {
					builder.endBlock(); // para
					paraOpen = false;
				}
				return 0;
			}
		} else if (extended && Textile.explicitBlockBegins(line,offset)) {
			setClosed(true);
			return offset;
		}
		if (blockLineCount != 0) {
			if (paraOpen) {
				builder.lineBreak();
			} else {
				builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
				paraOpen = true;
			}
		}
		++blockLineCount;

		getMarkupLanguage().emitMarkupLine(getParser(),state,line, offset);

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
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
			if (paraOpen) {
				builder.endBlock(); // para
				paraOpen = false;
			}
			builder.endBlock(); // quote
		}
		super.setClosed(closed);
	}
}
