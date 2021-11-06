/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.block;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.markdown.internal.util.LookAheadReader;
import org.eclipse.mylyn.wikitext.markdown.internal.util.ReadAheadBlock;
import org.eclipse.mylyn.wikitext.parser.Attributes;

/**
 * Markdown underlined headings.
 *
 * @author Stefan Seelmann
 */
public class UnderlinedHeadingBlock extends NestableBlock implements ReadAheadBlock {

	private static final Pattern h1pattern = Pattern.compile("=+\\s*"); //$NON-NLS-1$

	private static final Pattern h2pattern = Pattern.compile("-+\\s*"); //$NON-NLS-1$

	private int blockLineCount;

	private int level;

	@Override
	public boolean canStart(String line, int lineOffset, LookAheadReader lookAheadReader) {
		blockLineCount = 0;
		level = 0;
		String nextLine = lookAheadReader.lookAhead();
		if (nextLine == null || nextLine.length() < lineOffset) {
			return false;
		}
		String text = nextLine.substring(lineOffset);
		if (h1pattern.matcher(text).matches()) {
			level = 1;
			return true;
		} else if (h2pattern.matcher(text).matches()) {
			level = 2;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		String message = "Read-ahead required, call canStart(String, int, LookAheadReader) instead."; //$NON-NLS-1$
		throw new UnsupportedOperationException(message);
	}

	@Override
	protected int processLineContent(String line, int offset) {

		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();
			attributes.setId(state.getIdGenerator().newId("h" + level, line)); //$NON-NLS-1$

			builder.beginHeading(level, attributes);
			markupLanguage.emitMarkupLine(getParser(), state, line, offset);
		} else {
			builder.endHeading();
			setClosed(true);
		}

		blockLineCount++;
		return -1;
	}
}
