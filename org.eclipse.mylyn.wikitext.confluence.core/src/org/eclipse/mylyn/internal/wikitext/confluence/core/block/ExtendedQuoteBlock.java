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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * quoted text block, matches blocks that start with <code>bc. </code>.
 * Creates an extended block type of {@link ParagraphBlock paragraph}.
 * 
 * @author David Green
 */
public class ExtendedQuoteBlock extends Block {


	static final Pattern startPattern = Pattern.compile("\\{quote\\}(.*)");
	static final Pattern endPattern = Pattern.compile("\\{quote\\}(.*)");

	private int paraLine = 0;
	private int blockLineCount = 0;
	private Matcher matcher;
	private boolean paraOpen = false;

	public ExtendedQuoteBlock() {
	}

	@Override
	public int processLineContent(String line,int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();

			offset = matcher.start(1);

			builder.beginBlock(BlockType.QUOTE, attributes);
		}
		if (markupLanguage.isEmptyLine(line) && blockLineCount > 0 && paraOpen) {
			builder.endBlock(); // para
			paraOpen = false;
			paraLine = 0;
			return -1;
		}
		++blockLineCount;

		if (!paraOpen) {
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
			paraOpen = true;
		}

		if (blockLineCount > 1) {
			Matcher endMatcher = endPattern.matcher(line);
			if (endMatcher.matches()) {
				setClosed(true);
				return endMatcher.start(1);
			}
		} else if (offset >= line.length()) {
			return -1;
		}

		if (paraLine != 0) {
			builder.lineBreak();
		}
		++paraLine;
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
				paraLine = 0;
				paraOpen = false;
			}
			builder.endBlock(); // quote
		}
		super.setClosed(closed);
	}
}
