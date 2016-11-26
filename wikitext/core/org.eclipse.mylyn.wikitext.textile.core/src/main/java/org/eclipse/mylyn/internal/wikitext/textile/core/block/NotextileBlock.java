/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.textile.core.Textile;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * No Textile block for allowing HTML in the markup, matches blocks that start with <code>notextile. </code>
 * 
 * @author David Green
 */
public class NotextileBlock extends Block {

	private static final int LINE_REMAINDER_GROUP_OFFSET = 2;

	private static final int EXTENDED_GROUP = 1;

	static final Pattern startPattern = Pattern.compile("notextile\\.(\\.)?\\s+(.*)"); //$NON-NLS-1$ 

	private boolean extended;

	private int blockLineCount = 0;

	private Matcher matcher;

	public NotextileBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);
			extended = matcher.group(EXTENDED_GROUP) != null;

			// we don't start a block with the document builder
		}
		if (markupLanguage.isEmptyLine(line) && !extended) {
			setClosed(true);
			return 0;
		} else if (extended && Textile.explicitBlockBegins(line, offset)) {
			setClosed(true);
			return offset;
		}
		++blockLineCount;

		final String lineText = offset > 0 ? line.substring(offset) : line;
		if (blockLineCount > 1 || lineText.trim().length() > 0) {
			builder.charactersUnescaped(lineText);
			builder.characters("\n"); //$NON-NLS-1$
		}

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
