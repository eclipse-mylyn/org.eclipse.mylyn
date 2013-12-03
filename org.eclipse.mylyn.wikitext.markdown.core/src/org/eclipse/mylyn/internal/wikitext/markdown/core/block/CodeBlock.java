/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
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

/**
 * Markdown code block.
 * 
 * @author Stefan Seelmann
 */
public class CodeBlock extends NestableBlock {

	private static final Pattern startPattern = Pattern.compile("(?: {4}|\\t)((?: {4}|\\t)*)(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		return startPattern.matcher(line.substring(lineOffset)).matches();
	}

	@Override
	protected int processLineContent(String line, int offset) {

		// start of block
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.CODE, new Attributes());
		}

		// extract the content
		Matcher matcher = startPattern.matcher(line.substring(offset));
		if (!matcher.matches()) {
			setClosed(true);
			return offset;
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
		}
		super.setClosed(closed);
	}
}
