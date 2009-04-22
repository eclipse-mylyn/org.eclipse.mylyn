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
public class ColorBlock extends ParameterizedBlock {

	private final Pattern startPattern;

	private final Pattern endPattern;

	private final BlockType blockType;

	private int blockLineCount = 0;

	private Matcher matcher;

	private boolean nesting = false;

	private String color;

	public ColorBlock() {
		this.blockType = BlockType.DIV;
		startPattern = Pattern.compile("\\{color(?::([^\\}]*))?\\}(.*)"); //$NON-NLS-1$
		endPattern = Pattern.compile("(\\{color\\})(.*)"); //$NON-NLS-1$
	}

	@Override
	public int processLineContent(String line, int offset) {
		int end = line.length();
		if (blockLineCount == 0) {
			setOptions(matcher.group(1));

			Attributes attributes = new Attributes();
			if (color != null) {
				attributes.setCssStyle(String.format("color: %s;", color)); //$NON-NLS-1$
			}

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
		color = null;
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
	protected void setOption(String option) {
		color = option;
	}

	@Override
	protected void setOption(String key, String value) {
		// no options
	}
}
