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
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.twiki.TWikiLanguage;

/**
 * implements the %TOC% variable of TWiki syntax
 * 
 * @author David Green
 */
public class TableOfContentsBlock extends Block {

	static final Pattern startPattern = Pattern.compile("\\s*\\%TOC\\%\\s*"); //$NON-NLS-1$

	private int blockLineNumber = 0;

	private Matcher matcher;

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}

		if (!getMarkupLanguage().isFilterGenerativeContents()) {

			OutlineParser outlineParser = new OutlineParser(new TWikiLanguage());
			OutlineItem rootItem = outlineParser.parse(state.getMarkupContent());

			emitToc(rootItem);
		}
		return -1;
	}

	private void emitToc(OutlineItem item) {
		if (item.getChildren().isEmpty()) {
			return;
		}
		Attributes nullAttributes = new Attributes();

		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		for (OutlineItem child : item.getChildren()) {
			builder.beginBlock(BlockType.LIST_ITEM, nullAttributes);
			builder.link('#' + child.getId(), child.getLabel());
			emitToc(child);
			builder.endBlock();
		}
		builder.endBlock();
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && !getMarkupLanguage().isFilterGenerativeContents()) {
			matcher = startPattern.matcher(line);
			blockLineNumber = 0;
			return matcher.matches();
		} else {
			matcher = null;
			return false;
		}
	}

}
