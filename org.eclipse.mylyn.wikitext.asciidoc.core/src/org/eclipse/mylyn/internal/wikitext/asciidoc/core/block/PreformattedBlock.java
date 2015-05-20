/*******************************************************************************
 * Copyright (c) 2015 Max Rydahl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *      Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * AsciiDoc preformatted block.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class PreformattedBlock extends Block {

	private static final Pattern startPattern = Pattern.compile("(?: {4}|\\t)((?: {4}|\\t)*)(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			return startPattern.matcher(line).matches();
		}
		return false;
	}

	@Override
	protected int processLineContent(String line, int offset) {

		// start of block
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		}

		// extract the content
		Matcher matcher = startPattern.matcher(line);
		if (!matcher.matches()) {
			setClosed(true);
			return 0;
		}
		String intent = matcher.group(1);
		String content = matcher.group(2);

		//If there is no content, close the block:
		if (content.length() == 0) {
			setClosed(true);
			return 0;
		}

		// next line, does not convert to line break
		if (blockLineCount > 0) {
			builder.characters("\n"); //$NON-NLS-1$
		}

		// emit, handle intention, encode ampersands (&) and angle brackets (< and >)
		if (intent != null) {
			builder.characters(intent);
		}
		builder.characters(content);

		blockLineCount++;
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}
}
