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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.Validate;
import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.SourceBlock;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

abstract class AbstractHtmlBlock extends SourceBlock {

	@Override
	public void process(ProcessingContext context, DocumentBuilder builder, LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		final Line firstLine = line;
		while (line != null) {
			String lineText = line.getText();
			builder.charactersUnescaped(lineText);
			builder.charactersUnescaped("\n"); //$NON-NLS-1$

			lineSequence.advance();

			if (firstLine.equals(line)) {
				Matcher matcher = startPattern().matcher(lineText);
				Validate.isTrue(matcher.matches());
				int offset = matcher.end(1);
				if (offset < lineText.length() - 1) {
					Matcher closeMatcher = closePattern().matcher(lineText);
					closeMatcher.region(offset, lineText.length());
					if (closeMatcher.find()) {
						break;
					}
				}
			} else if (closePattern().matcher(lineText).find()) {
				break;
			}

			line = lineSequence.getCurrentLine();
		}
	}

	@Override
	public boolean canStart(LineSequence lineSequence) {
		Line line = lineSequence.getCurrentLine();
		if (line != null) {
			return startPattern().matcher(line.getText()).matches();
		}
		return false;
	}

	protected abstract Pattern closePattern();

	/**
	 * Provides a pattern that must be matched for the block to start. The pattern must provide a first group which cannot match the close
	 * pattern.
	 * 
	 * @return the pattern
	 */
	protected abstract Pattern startPattern();
}
