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

package org.eclipse.mylyn.wikitext.core.parser.markup.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * An abstract block that is delimited by HTML-style open and close tags.
 * 
 * @author dgreen
 * @since 1.2
 */
public abstract class AbstractHtmlBlock extends Block {

	private final Pattern startPattern;

	private final Pattern endPattern;

	protected int blockLineCount = 0;

	private Matcher matcher;

	public AbstractHtmlBlock(String tagName) {
		startPattern = Pattern.compile("\\s*(<" + tagName + "((?:\\s*[a-zA-Z][a-zA-Z0-9_:-]*=\"[^\"]*\")*)\\s*>)(.*)"); //$NON-NLS-1$ //$NON-NLS-2$
		endPattern = Pattern.compile("\\s*(</" + tagName + "\\s*>)(.*)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			setAttributes(matcher.group(2));

			offset = matcher.start(3);

			beginBlock();
		}

		int end = line.length();
		int segmentEnd = end;
		boolean terminating = false;

		if (offset < end) {
			Matcher endMatcher = endPattern.matcher(line);
			if (blockLineCount == 0) {
				endMatcher.region(offset, end);
			}
			if (endMatcher.find()) {
				terminating = true;
				end = endMatcher.start(2);
				segmentEnd = endMatcher.start(1);
			}
		}

		if (end < line.length()) {
			state.setLineSegmentEndOffset(end);
		}

		++blockLineCount;

		final String content = line.substring(offset, segmentEnd);
		handleBlockContent(content);

		if (terminating) {
			setClosed(true);
		}
		return end == line.length() ? -1 : end;
	}

	/**
	 * handle attributes of the opening tag. The default implementation processes XML-style attributes however
	 * subclasses may override this method to provide special attribute processing.
	 * 
	 * @param attributes
	 *            the attributes, or null if there are none
	 * @see #handleAttribute(String, String)
	 */
	protected void setAttributes(String attributes) {
		if (attributes != null) {
			Pattern pattern = Pattern.compile("\\s+([a-zA-Z][a-zA-Z0-9_:-]*)=\"([^\"]*)\""); //$NON-NLS-1$
			Matcher matcher = pattern.matcher(attributes);
			while (matcher.find()) {
				String attrName = matcher.group(1);
				String attrValue = matcher.group(2);
				handleAttribute(attrName, attrValue);
			}
		}
	}

	/**
	 * Handle a named attribute. The default implementation does nothing
	 * 
	 * @param attrName
	 *            the attribute name
	 * @param attrValue
	 *            the attribute value
	 * @see #setAttributes(String)
	 * @since 1.6
	 */
	protected void handleAttribute(String attrName, String attrValue) {
		// nothing to do
	}

	protected abstract void handleBlockContent(String content);

	protected abstract void beginBlock();

	protected abstract void endBlock();

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			endBlock();
		}
		super.setClosed(closed);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		resetState();
		matcher = startPattern.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		return matcher.matches();
	}

	protected void resetState() {
		blockLineCount = 0;
	}

}
