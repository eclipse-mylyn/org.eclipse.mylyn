/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.tasks.ui.util.bugzilla;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.QuoteAttributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * A quote block that detects lines that start with '>' (email-style quoting)
 * 
 * @author David Green
 * 
 */
public class BugzillaQuoteBlock extends Block {
	private static Pattern pattern = Pattern.compile("((\\s*>)|\\(In reply to comment #\\d{1,}\\)).*"); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && line.length() > 0 && pattern.matcher(line).matches()) {
			return true;
		}
		return false;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (!canStart(line, offset)) {
			setClosed(true);
			return 0;
		}
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.QUOTE, new QuoteAttributes());
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		} else {
			builder.lineBreak();
		}
		++blockLineCount;

		getMarkupLanguage().emitMarkupLine(getParser(), state, line, offset);

		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			blockLineCount = 0;
			builder.endBlock(); // para
			builder.endBlock(); // quote
		}
		super.setClosed(closed);
	}
}
