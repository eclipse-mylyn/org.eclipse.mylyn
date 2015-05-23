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

public class SetextHeaderBlock extends SourceBlock {

	private final Pattern indentPattern = Pattern.compile("\\s{0,3}\\S.*");

	private final Pattern setextUnderlinePattern = Pattern.compile("\\s{0,3}((-|=)+)\\s*");

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line currentLine = lineSequence.getCurrentLine();
		Line nextLine = lineSequence.getNextLine();
		Matcher matcher = setextUnderlinePattern.matcher(nextLine.getText());
		checkState(matcher.matches());

		lineSequence.advance();

		builder.setLocator(currentLine.toLocator());
		int headingLevel = headingLevel(matcher);
		builder.beginHeading(headingLevel, new HeadingAttributes());

		new InlineContent().emit(context, builder, new TextSegment(Collections.singletonList(currentLine)));

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
