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

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.eclipse.mylyn.wikitext.commonmark.internal.inlines.InlineParser;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.HeadingAttributes;

public class SetextHeaderBlock extends SourceBlock {

	private final Pattern indentPattern = Pattern.compile("\\s{0,3}\\S.*"); //$NON-NLS-1$

	private final Pattern setextUnderlinePattern = Pattern.compile("\\s{0,3}((-|=)+)\\s*"); //$NON-NLS-1$

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line currentLine = lineSequence.getCurrentLine();
		Line nextLine = lineSequence.getNextLine();
		Matcher matcher = setextUnderlinePattern.matcher(nextLine.getText());
		Validate.isTrue(matcher.matches());

		lineSequence.advance();

		builder.setLocator(currentLine.toLocator());
		int headingLevel = headingLevel(matcher);

		TextSegment textSegment = new TextSegment(Collections.singletonList(currentLine));

		HeadingAttributes attributes = new HeadingAttributes();

		InlineParser inlineParser = context.getInlineParser();
		String headingText = inlineParser.toStringContent(context, textSegment);
		attributes.setId(context.generateHeadingId(headingLevel, headingText));

		builder.beginHeading(headingLevel, attributes);

		inlineParser.emit(context, builder, textSegment);

		builder.endHeading();

		lineSequence.advance();
	}

	private int headingLevel(Matcher matcher) {
		return matcher.group(1).charAt(0) == '=' ? 1 : 2;
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		Line nextLine = lineSequence.getNextLine();
		return line != null && nextLine != null && indentPattern.matcher(line.getText()).matches()
				&& setextUnderlinePattern.matcher(nextLine.getText()).matches();
	}

}
