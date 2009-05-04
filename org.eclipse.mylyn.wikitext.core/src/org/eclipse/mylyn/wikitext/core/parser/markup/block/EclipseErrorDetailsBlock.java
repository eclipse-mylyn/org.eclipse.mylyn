/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup.block;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * A block that starts a preformatted section that begins with <tt>-- Error Details --</tt>, which is the prefix for
 * error information inserted into a bug description by Mylyn when creating bugs from the Eclipse Error Log view. The
 * block terminates with the first {@link MarkupLanguage#isEmptyLine(String) empty line}.
 * 
 * @author David Green
 */
public class EclipseErrorDetailsBlock extends Block {

	private static final Pattern START_PATTERN = Pattern.compile("\\s*-- Error Details --.*"); //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && START_PATTERN.matcher(line).matches()) {
			blockLineCount = 0;
			return true;
		}
		return false;
	}

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount++ == 0) {
			Attributes attributes = new Attributes();
			attributes.setCssClass("eclipseErrorDetails"); //$NON-NLS-1$
			builder.beginBlock(BlockType.PREFORMATTED, attributes);
		} else {
			if (markupLanguage.isEmptyLine(line)) {
				setClosed(true);
				return 0;
			}
		}

		builder.characters(offset > 0 ? line.substring(offset) : line);
		builder.characters("\n"); //$NON-NLS-1$

		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}
}
