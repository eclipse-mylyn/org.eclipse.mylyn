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

import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.LineSequence;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.SourceBlock;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

public class HtmlType1Block extends SourceBlock {

	private final Pattern startPattern = Pattern.compile("\\s{0,3}<(?:pre|script|style)(\\s|>|$).*",
			Pattern.CASE_INSENSITIVE);

	private final Pattern closePattern = Pattern.compile("\\s{0,3}</(?:pre|script|style)\\s*>",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		while (line != null && !line.isEmpty()) {
			builder.charactersUnescaped(line.getText());
			builder.charactersUnescaped("\n");

			lineSequence.advance();

			if (closePattern.matcher(line.getText()).find()) {
				break;
			}

			line = lineSequence.getCurrentLine();
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		if (line != null) {
			return startPattern.matcher(line.getText()).matches();
		}
		return false;
	}
}
