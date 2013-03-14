/*******************************************************************************
 * Copyright (c) 2012 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Markdown code block.
 * 
 * @author Stefan Seelmann
 */
public class CodeBlock extends Block {

	private static final Pattern startPattern = Pattern.compile("(?: {4}|\\t)((?: {4}|\\t)*)(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			return startPattern.matcher(line).matches();
		} else {
			return false;
		}
	}

	@Override
	protected int processLineContent(String line, int offset) {

		// start of block
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
			builder.beginBlock(BlockType.CODE, new Attributes());
		}

		// extract the content
		Matcher matcher = startPattern.matcher(line);
		if (!matcher.matches()) {
			setClosed(true);
			return 0;
		}
		String intent = matcher.group(1);
		String content = matcher.group(2);

		// next line, does not convert to line break
		if (blockLineCount > 0) {
			builder.characters("\n"); //$NON-NLS-1$
		}

		// emit, replace intention tabs by 4 spaces, encode ampersands (&) and angle brackets (< and >)
		if (intent != null) {
			builder.characters(intent.replace("\t", "    ")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		builder.characters(content);

		blockLineCount++;
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
			builder.endBlock();
		}
		super.setClosed(closed);
	}
}
