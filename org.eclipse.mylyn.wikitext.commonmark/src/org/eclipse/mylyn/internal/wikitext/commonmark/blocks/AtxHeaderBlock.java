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
		builder.beginHeading(headingLevel(matcher), new HeadingAttributes());

		int contentOffset = matcher.start(2);
		int contentEnd = matcher.end(2);
		if (contentEnd > contentOffset) {
			Line headerContent = currentLine.segment(contentOffset, contentEnd - contentOffset);
			new InlineContent().emit(context, builder, new TextSegment(Collections.singletonList(headerContent)));
		}

		builder.endHeading();
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
