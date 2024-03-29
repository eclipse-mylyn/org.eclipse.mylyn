/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.textile.internal.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.textile.internal.Textile;

/**
 * An implementation of a Textile 2.2 comment block.
 * 
 * @author David Green
 * @see <a href="http://txstyle.org/doc/40/textile-comments">textile reference documentation</a>
 */
public class CommentBlock extends Block {
	private static final Pattern startPattern = Pattern.compile("###\\.(\\.)?\\s+(.*)"); //$NON-NLS-1$

	private boolean extended;

	private int blockLineCount = 0;

	private Matcher matcher;

	public CommentBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			offset = matcher.start(2);
			extended = matcher.group(1) != null;
		}
		if (markupLanguage.isEmptyLine(line) && !extended) {
			setClosed(true);
			return 0;
		} else if (extended && Textile.explicitBlockBegins(line, offset)) {
			setClosed(true);
			return offset;
		}
		++blockLineCount;

		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
