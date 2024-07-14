/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static org.eclipse.mylyn.wikitext.util.Preconditions.checkState;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.HeadingAttributes;

public class AtxHeaderBlock extends SourceBlock {

	private static final Pattern PATTERN = Pattern.compile(" {0,3}(#{1,6})(?:[ \t]+?(.+?))??(?:[ \t]+#+)?[ \t]*"); //$NON-NLS-1$

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line currentLine = lineSequence.getCurrentLine();
		Matcher matcher = PATTERN.matcher(currentLine.getText());
		checkState(matcher.matches());

		lineSequence.advance();

		builder.setLocator(currentLine.toLocator());

		int contentOffset = matcher.start(2);
		int contentEnd = matcher.end(2);
		int headingLevel = headingLevel(matcher);
		if (contentEnd > contentOffset) {
			Line headerContent = currentLine.segment(contentOffset, contentEnd - contentOffset);
			TextSegment textSegment = new TextSegment(Collections.singletonList(headerContent));

			HeadingAttributes attributes = new HeadingAttributes();

			InlineParser inlineParser = context.getInlineParser();
			String headingText = inlineParser.toStringContent(context, textSegment);
			attributes.setId(context.generateHeadingId(headingLevel, headingText));

			builder.beginHeading(headingLevel, attributes);

			inlineParser.emit(context, builder, textSegment);

			builder.endHeading();
		} else {
			builder.beginHeading(headingLevel, new HeadingAttributes());
			builder.endHeading();
		}

	}

	private int headingLevel(Matcher matcher) {
		return matcher.group(1).length();
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		return line != null && PATTERN.matcher(line.getText()).matches();
	}

}
