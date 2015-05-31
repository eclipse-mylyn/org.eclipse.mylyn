/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.blocks;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;
import org.eclipse.mylyn.internal.wikitext.commonmark.TextSegment;
import org.eclipse.mylyn.internal.wikitext.commonmark.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HeadingAttributes;

public class AtxHeaderBlock extends SourceBlock {

	private final Pattern pattern = Pattern.compile("\\s{0,3}(#{1,6})(?:\\s+([^#\\s].*?))?(\\s+#*)?\\s*");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line currentLine = lineSequence.getCurrentLine();
		Matcher matcher = pattern.matcher(currentLine.getText());
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
		return line != null && pattern.matcher(line.getText()).matches();
	}

}
