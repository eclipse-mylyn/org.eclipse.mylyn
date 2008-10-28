/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.twiki.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * implements definition lists: <code><pre>
 *    $ word : definition
 * </pre></code>
 * 
 * @author David Green
 */
public class DefinitionListBlock extends Block {

	private static final Pattern startPattern = Pattern
			.compile(" {3}\\$\\s+([^:]+):\\s+(.+)"); //$NON-NLS-1$

	private int blockLineCount;

	private Matcher matcher;

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			builder.beginBlock(BlockType.DEFINITION_LIST, new Attributes());
		} else {
			matcher = startPattern.matcher(line);
			if (!matcher.matches()) {
				setClosed(true);
				return 0;
			}
		}
		++blockLineCount;

		builder.beginBlock(BlockType.DEFINITION_TERM, new Attributes());
		markupLanguage.emitMarkupLine(parser, state, matcher.start(1), matcher
				.group(1), 0);
		builder.endBlock();

		builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
		markupLanguage.emitMarkupLine(parser, state, matcher.start(2), matcher
				.group(2), 0);
		builder.endBlock();

		return -1;
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

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
