/*******************************************************************************
 * Copyright (c) 2012, 2024 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.HeadingAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * AsciiDoc atx style headings.
 *
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 * @author Jeremie Bresson
 */
public class HeadingBlock extends Block {

	private static final Pattern pattern = Pattern.compile("(={1,6})\\s+(.+?)(\\s*)((?:=*\\s*))?"); //$NON-NLS-1$

	private Matcher matcher;

	int lineCount;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			Matcher m = pattern.matcher(line);
			if (m.matches()) {
				matcher = m;
				return true;
			}
		}
		matcher = null;
		return false;
	}

	@Override
	public int processLineContent(String line, int offset) {

		if (lineCount == 0) {
			int declaredLevel = matcher.group(1).length();
			int level = LanguageSupport.computeHeadingLevel(declaredLevel, getAsciiDocState());
			// heading 1 can only appear once
			if (level == 1) {
				AsciiDocContentState asciiDocState = getAsciiDocState();
				if (asciiDocState.isHeading1Present() || declaredLevel != 1) {
					getMarkupLanguage().emitMarkupLine(getParser(), state, line, offset);
					setClosed(true);
					return -1;
				}
				asciiDocState.setHeading1Present(true);
			}
			emitHeading(level);
			if (level != 1) {
				setClosed(true);
			} else {
				lineCount++;
				String tocAttribute = getAsciiDocState().getAttribute("toc"); //$NON-NLS-1$
				if (tocAttribute != null && !"macro".equals(tocAttribute)) { //$NON-NLS-1$
					emitTableOfContent();
				}
			}
		} else {
			if (line.trim().isEmpty()) {
				setClosed(true);
			} else {
				processHeaderLine(line);
			}
			lineCount++;
		}

		return -1;
	}

	private void emitTableOfContent() {
		List<Block> blocks = getMarkupLanguage().getBlocks();
		for (Block block : blocks) {
			if (block instanceof TableOfContentsBlock) {
				TableOfContentsBlock tocBlock = ((TableOfContentsBlock) block).cloneAndStart(getMarkupLanguage(),
						getParser(), getState());
				tocBlock.emitFullToc();
			}
		}
	}

	private void processHeaderLine(String line) {
		// fist line may contain author(s)
		if (lineCount == 1 && processAuthors(line)) {
			return;
		}
		// line may contain an attribute definition
		AttributeDefinitionBlock attributeBlock = new AttributeDefinitionBlock();
		if (attributeBlock.canStart(line, 0)) {
			attributeBlock.setState(getState());
			attributeBlock.processLineContent(line, 0);
			return;
		}
		getMarkupLanguage().emitMarkupLine(getParser(), state, line, 0);
	}

	private void emitHeading(int level) {
		String text = matcher.group(2);
		String closingGroup = matcher.group(4);

		HeadingAttributes attributes = new HeadingAttributes();
		if (level == 1) {
			attributes.setId("header"); //$NON-NLS-1$
		} else {
			attributes.setId(state.getIdGenerator().newId(null, text));
		}

		builder.beginHeading(level, attributes);
		getMarkupLanguage().emitMarkupLine(getParser(), state, matcher.start(2), text, 0);
		if (closingGroup.length() > 0 && closingGroup.length() != level) {
			builder.characters(matcher.group(3));
			builder.characters(closingGroup);
		}
		builder.endHeading();
	}

	private boolean processAuthors(String line) {
		Matcher authorMatcher = Pattern.compile("(?:^|;)\\s*((?:\\w+\\s+)+)\\s*<([^>]+)>\\s*").matcher(line); //$NON-NLS-1$
		int authorCount = 0;
		while (authorMatcher.find()) {
			if (authorCount == 0) {
				Attributes attributes = new Attributes(null, "details", null, null); //$NON-NLS-1$
				builder.beginBlock(BlockType.DIV, attributes);
			}
			authorCount++;
			String idCount = authorCount > 1 ? Integer.toString(authorCount) : ""; //$NON-NLS-1$
			Attributes attributes = new Attributes("author" + idCount, "author", null, null); //$NON-NLS-1$//$NON-NLS-2$
			builder.beginSpan(SpanType.SPAN, attributes);
			getMarkupLanguage().emitMarkupLine(getParser(), state, authorMatcher.group(1).trim(), 0);
			builder.endSpan();
			attributes = new Attributes("email" + idCount, "email", null, null); //$NON-NLS-1$ //$NON-NLS-2$
			builder.beginSpan(SpanType.SPAN, attributes);
			getMarkupLanguage().emitMarkupLine(getParser(), state, authorMatcher.group(2).trim(), 0);
			builder.endSpan();
		}
		if (authorCount > 0) {
			builder.endBlock();
		}
		return authorCount > 0;
	}

	protected AsciiDocContentState getAsciiDocState() {
		return (AsciiDocContentState) state;
	}
}
