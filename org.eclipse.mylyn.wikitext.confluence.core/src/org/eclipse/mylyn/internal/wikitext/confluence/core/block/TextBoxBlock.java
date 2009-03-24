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
public class TextBoxBlock extends ParameterizedBlock {

	private final Pattern startPattern;

	private final Pattern endPattern;

	private final BlockType blockType;

	private int blockLineCount = 0;

	private Matcher matcher;

	private String title;

	private boolean nesting = false;

	public TextBoxBlock(BlockType blockType, String name) {
		this.blockType = blockType;
		startPattern = Pattern.compile("\\{" + name + "(?::([^\\}]*))?\\}(.*)"); //$NON-NLS-1$ //$NON-NLS-2$
		endPattern = Pattern.compile("(\\{" + name + "\\})(.*)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public int processLineContent(String line, int offset) {
		int end = line.length();
		if (blockLineCount == 0) {
			setOptions(matcher.group(1));

			Attributes attributes = new Attributes();
			attributes.setTitle(title);

			offset = matcher.start(2);

			builder.beginBlock(blockType, attributes);

			nesting = true;
			end = offset;
		} else {

			boolean terminating = false;

			Matcher endMatcher = endPattern.matcher(line);
			if (offset < end) {
				if (offset > 0) {
					endMatcher.region(offset, end);
				}
				if (endMatcher.find()) {
					terminating = true;
					end = endMatcher.start(2);
				} else {
					end = offset;
				}
			}
			if (terminating) {
				setClosed(true);
			}
		}
		++blockLineCount;

		return end == line.length() ? -1 : end;
	}

	@Override
	public boolean beginNesting() {
		return nesting;
	}

	@Override
	public int findCloseOffset(String line, int lineOffset) {
		Matcher endMatcher = endPattern.matcher(line);
		if (lineOffset != 0) {
			endMatcher.region(lineOffset, line.length());
		}
		if (endMatcher.find()) {
			return endMatcher.start();
		}
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		title = null;
		nesting = false;
		matcher = startPattern.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		return matcher.matches();
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock(); // the block	
		}
		super.setClosed(closed);
	}

	@Override
	protected void setOption(String key, String value) {
		if (key.equals("title")) { //$NON-NLS-1$
			title = value;
		}
	}
}
