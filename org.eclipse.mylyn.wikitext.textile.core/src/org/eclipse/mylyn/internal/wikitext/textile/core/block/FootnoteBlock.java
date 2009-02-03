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
package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.textile.core.Textile;
import org.eclipse.mylyn.internal.wikitext.textile.core.TextileContentState;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Footnote block, matching lines starting with <code>fn\d\d?. </code>.
 * 
 * @author David Green
 */
public class FootnoteBlock extends Block {

	private static final int LINE_REMAINDER_GROUP_OFFSET = Textile.ATTRIBUTES_BLOCK_GROUP_COUNT + 2;

	static final Pattern startPattern = Pattern.compile("fn([0-9]{1,2})" + Textile.REGEX_BLOCK_ATTRIBUTES //$NON-NLS-1$
			+ "\\.\\s+(.*)"); //$NON-NLS-1$

	private int blockLineCount = 0;

	private Matcher matcher;

	private String footnote;

	public FootnoteBlock() {
	}

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			Attributes attributes = new Attributes();
			attributes.setCssClass("footnote"); //$NON-NLS-1$

			// 0-offset matches may start with the "fnnn. " prefix.
			footnote = matcher.group(1);
			((TextileContentState) state).footnoteBlockDetected(footnote);
			attributes.setId(state.getFootnoteId(footnote));

			Textile.configureAttributes(attributes, matcher, 2, true);
			offset = matcher.start(LINE_REMAINDER_GROUP_OFFSET);

			builder.beginBlock(BlockType.PARAGRAPH, attributes);
			builder.beginSpan(SpanType.SUPERSCRIPT, new Attributes());
			builder.characters(footnote);
			builder.endSpan();
			builder.characters(" "); //$NON-NLS-1$
		}
		if (markupLanguage.isEmptyLine(line)) {
			setClosed(true);
			return 0;
		}
		if (blockLineCount != 0) {
			builder.lineBreak();
		}
		++blockLineCount;

		getMarkupLanguage().emitMarkupLine(getParser(), state, line, offset);

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
			builder.endBlock();
		}
		super.setClosed(closed);
	}

}
