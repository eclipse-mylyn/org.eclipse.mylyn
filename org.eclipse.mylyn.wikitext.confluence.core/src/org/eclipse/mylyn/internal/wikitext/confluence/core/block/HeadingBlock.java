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
package org.eclipse.mylyn.internal.wikitext.confluence.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Matches any textile text, including lines starting with <code>p. </code>.
 * 
 * @author David Green
 */
public class HeadingBlock extends Block {

	static final Pattern startPattern = Pattern.compile("h([1-6])\\.\\s+(.*)");

	private int blockLineCount = 0;
	private int level = -1;
	private Matcher matcher;

	public HeadingBlock() {
	}

	@Override
	public int processLineContent(String line,int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();
			// 0-offset matches may start with the "hn. " prefix.
			level = Integer.parseInt(matcher.group(1));
			offset = matcher.start(2);

			if (attributes.getId() == null) {
				attributes.setId(state.getIdGenerator().newId("h"+level,line.substring(offset)));
			}
			builder.beginHeading(level, attributes);
		}
		if (blockLineCount != 0 || markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}
		++blockLineCount;

		getMarkupLanguage().emitMarkupLine(getParser(),state,line, offset);

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

	@Override
	public void setClosed(boolean closed) {
		if (closed && !isClosed()) {
			builder.endHeading();
		}
		super.setClosed(closed);
	}


}
