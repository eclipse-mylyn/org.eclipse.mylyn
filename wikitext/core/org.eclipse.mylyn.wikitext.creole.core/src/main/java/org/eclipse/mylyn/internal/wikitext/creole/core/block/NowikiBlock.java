/*******************************************************************************
 * Copyright (c) 2011 Igor Malinin and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Igor Malinin - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.creole.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * @author Igor Malinin
 */
public class NowikiBlock extends Block {

	private static final Pattern startPattern = Pattern.compile("^\\{\\{\\{\\s*$"); //$NON-NLS-1$

	private static final Pattern endPattern = Pattern.compile("^\\}\\}\\}\\s*$"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount++ == 0) {
			builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		} else {
			Matcher endMatcher = endPattern.matcher(line);
			if (endMatcher.matches()) {
				setClosed(true);
				return -1;
			}
			builder.characters(line);
			builder.characters("\n"); //$NON-NLS-1$
		}
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
