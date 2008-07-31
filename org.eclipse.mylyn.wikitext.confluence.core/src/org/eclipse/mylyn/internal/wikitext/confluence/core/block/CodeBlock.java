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

/**
 *
 *
 * @author David Green
 */
public class CodeBlock extends ParameterizedBlock {


	private static final Pattern startPattern = Pattern.compile("\\{code(?::([^\\}]*))?\\}(.*)");
	private static final Pattern endPattern = Pattern.compile("\\{code\\}(.*)");


	private int blockLineCount = 0;
	private Matcher matcher;

	private String title;

	public CodeBlock() {
	}

	@Override
	public int processLineContent(String line,int offset) {
		if (blockLineCount == 0) {
			setOptions(matcher.group(1));

			offset = matcher.start(2);

			if (title != null) {
				Attributes attributes = new Attributes();
				attributes.setTitle(title);
				builder.beginBlock(BlockType.PANEL, attributes);
			}
			Attributes attributes = new Attributes();

			builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
			builder.beginBlock(BlockType.CODE, attributes);
		}
		++blockLineCount;

		if (blockLineCount > 1) {
			Matcher endMatcher = endPattern.matcher(line);
			if (endMatcher.matches()) {
				setClosed(true);
				return endMatcher.start(1);
			}
		} else if (offset >= line.length()) {
			builder.characters("\n");
			return -1;
		}

		builder.characters(offset > 0?line.substring(offset):line);
		builder.characters("\n");

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		title = null;
		blockLineCount = 0;
		matcher = startPattern.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		return matcher.matches();
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			if (title != null) {
				builder.endBlock(); // panel
			}
			builder.endBlock(); // code
			builder.endBlock(); // pre
		}
		super.setClosed(closed);
	}

	@Override
	protected void setOption(String key, String value) {
		if (key.equals("title")) {
			title = value;
		}
	}
}
