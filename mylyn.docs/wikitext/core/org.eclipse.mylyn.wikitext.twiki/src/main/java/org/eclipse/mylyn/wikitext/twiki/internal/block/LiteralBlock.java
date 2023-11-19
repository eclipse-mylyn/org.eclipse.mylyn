/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;

/**
 * A literal block disables wiki text but allows for nested HTML tags.
 * 
 * @see VerbatimBlock
 * @author David Green
 */
public class LiteralBlock extends Block {

	private static final Pattern startPattern = Pattern.compile("\\s*<literal>(.*)"); //$NON-NLS-1$

	private static final Pattern endPattern = Pattern.compile("\\s*</literal>(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount++ == 0) {
			offset = matcher.start(1);
			((TWikiLanguage) markupLanguage).setLiteralMode(true);
			builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		} else {
			Matcher endMatcher = endPattern.matcher(line);
			if (endMatcher.matches()) {
				setClosed(true);
				return endMatcher.start(1);
			}
		}
		markupLanguage.emitMarkupLine(parser, state, line, offset);
		builder.characters("\n"); //$NON-NLS-1$
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
			((TWikiLanguage) markupLanguage).setLiteralMode(false);
		}
		super.setClosed(closed);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			blockLineCount = 0;
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
