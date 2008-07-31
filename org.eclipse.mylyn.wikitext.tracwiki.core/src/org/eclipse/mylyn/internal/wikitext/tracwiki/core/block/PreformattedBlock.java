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
package org.eclipse.mylyn.internal.wikitext.tracwiki.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 *
 *
 * @author David Green
 */
public class PreformattedBlock extends Block {

	private static final Pattern startPattern = Pattern.compile("\\{\\{\\{(.*)");
	private static final Pattern endPattern = Pattern.compile("\\}\\}\\}(.*)");

	private int blockLineCount = 0;
	private Matcher matcher;


	@Override
	public int processLineContent(String line,int offset) {
		if (blockLineCount++ == 0) {
			offset = matcher.start(1);
			builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		} else {
			Matcher endMatcher = endPattern.matcher(line);
			if (endMatcher.matches()) {
				setClosed(true);
				return endMatcher.start(1);
			}
		}
		builder.characters(offset==0?line:line.substring(offset));
		builder.characters("\n");
		return -1;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endBlock(); // pre
		}
		super.setClosed(closed);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 ) {
			matcher = startPattern.matcher(line);
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
