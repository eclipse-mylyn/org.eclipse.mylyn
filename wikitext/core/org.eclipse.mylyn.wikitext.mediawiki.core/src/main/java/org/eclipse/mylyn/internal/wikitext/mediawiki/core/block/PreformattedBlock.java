/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Jeremie Bresson - Bug 381506
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * @author David Green
 */
public class PreformattedBlock extends Block {

	private static final Pattern PRE_OPEN_PATTERN = Pattern.compile(
			"(<pre((?:\\s+[a-zA-Z][a-zA-Z0-9_:-]*=\"[^\"]*\")*)\\s*>).*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private static final Pattern PRE_CLOSE_PATTERN = Pattern.compile("(</pre*\\s*>)", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	private int blockLineCount = 0;

	private boolean usesTag = false;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && line.length() > 0 && line.charAt(0) == ' ') {
			usesTag = false;
			return true;
		}
		final Matcher matcher = PRE_OPEN_PATTERN.matcher(line);
		if (lineOffset > 0) {
			matcher.region(lineOffset, line.length());
		}
		if (matcher.matches()) {
			usesTag = true;
			return true;
		}
		return false;
	}

	@Override
	public int processLineContent(String line, int offset) {

		int lineStart = usesTag ? 0 : 1;
		if (blockLineCount++ == 0) {
			Attributes attributes = new Attributes();
			if (usesTag) {
				Matcher matcher = PRE_OPEN_PATTERN.matcher(line);
				if (offset > 0) {
					matcher.region(offset, line.length());
				}
				if (matcher.matches()) {
					String htmlAttributes = matcher.group(2);
					processHtmlAttributes(attributes, htmlAttributes);

					lineStart = matcher.end(1);
					offset = lineStart;
				} else {
					throw new IllegalStateException();
				}
			}
			builder.beginBlock(BlockType.PREFORMATTED, attributes);

		}
		if (usesTag) {
			if (blockLineCount > 0) {
				Matcher closeMatcher = PRE_CLOSE_PATTERN.matcher(line);
				if (offset > 0) {
					closeMatcher.region(offset, line.length());
				}
				if (closeMatcher.find()) {
					int contentEnd = closeMatcher.start(1);
					int newContentStart = closeMatcher.end(1);
					if (contentEnd > 0) {
						addContent(line.substring(0, contentEnd), offset);
					}
					setClosed(true);
					if (newContentStart < line.length()) {
						return newContentStart;
					}
					return -1;
				}
			}
		} else {
			if (markupLanguage.isEmptyLine(line) || (offset == 0 && line.charAt(0) != ' ')) {
				setClosed(true);
				return 0;
			}
		}
		if (line.length() >= lineStart) {
			addContent(line, lineStart);
		}
		return -1;
	}

	private void addContent(String line, int offset) {
		if (usesTag) {
			builder.characters(line.substring(offset));
		} else {
			getMarkupLanguage().emitMarkupLine(parser, state, line, offset);
		}
		builder.characters("\n"); //$NON-NLS-1$
	}

	private void processHtmlAttributes(Attributes attributes, String htmlAttributes) {
		if (htmlAttributes != null) {
			Pattern pattern = Pattern.compile("\\s+([a-zA-Z][a-zA-Z0-9_:-]*)=\"([^\"]*)\""); //$NON-NLS-1$
			Matcher matcher = pattern.matcher(htmlAttributes);
			while (matcher.find()) {
				String attrName = matcher.group(1);
				String attrValue = matcher.group(2);
				if ("id".equals(attrName)) { //$NON-NLS-1$
					attributes.setId(attrValue);
				} else if ("style".equals(attrName)) { //$NON-NLS-1$
					attributes.setCssStyle(attrValue);
				} else if ("class".equals(attrName)) { //$NON-NLS-1$
					attributes.setCssClass(attrValue);
				}
			}
		}
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock(); // pre
			usesTag = false;
		}
		super.setClosed(closed);
	}

}
