/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.block;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocLanguage;
import org.eclipse.mylyn.wikitext.asciidoc.internal.AsciiDocContentState;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.parser.markup.block.AbstractTableOfContentsBlock;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;

public class TableOfContentsBlock extends AbstractTableOfContentsBlock {

	private static final int tocLevelDefault = 2;

	private static final int tocLevelMax = 5;

	private static final Pattern startPattern = Pattern.compile("toc::\\[\\]\\s*"); //$NON-NLS-1$

	private int blockLineNumber;

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}
		String tocAttribute = getAsciiDocState().getAttribute("toc"); //$NON-NLS-1$
		if ("macro".equals(tocAttribute)) { //$NON-NLS-1$
			emitFullToc();
		}

		return -1;
	}

	public void emitFullToc() {
		if (!getMarkupLanguage().isFilterGenerativeContents()) {
			setMaxLevel(1 + getTocLevelsAttribute());
			String tocTitle = getAsciiDocState().getAttribute("toc-title"); //$NON-NLS-1$
			OutlineParser outlineParser = new OutlineParser(new AsciiDocLanguage());
			OutlineItem rootItem = outlineParser.parse(state.getMarkupContent());
			List<OutlineItem> zeroLevelItems = rootItem.getChildren();
			if (zeroLevelItems.size() > 0) {
				emitTocTitle(tocTitle);
				emitToc(zeroLevelItems.get(0));
			}
		}
	}

	private void emitTocTitle(String tocTitle) {
		if (tocTitle != null) {
			Attributes attributes = new Attributes();
			attributes.setCssClass("title"); //$NON-NLS-1$
			builder.beginBlock(BlockType.DIV, attributes);
			builder.characters(tocTitle);
			builder.endBlock();
		}
	}

	private int getTocLevelsAttribute() {
		int tocLevel = tocLevelDefault;
		try {
			tocLevel = Integer
					.parseInt(getAsciiDocState().getAttributeOrValue("toclevels", Integer.toString(tocLevelDefault))); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			// leave default in case of parsing error
		}
		return Math.min(tocLevel, tocLevelMax);
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		if (lineOffset == 0 && startPattern.matcher(line).matches()
				&& !getMarkupLanguage().isFilterGenerativeContents()) {
			blockLineNumber = 0;
			return true;
		}
		return false;
	}

	public TableOfContentsBlock cloneAndStart(AbstractMarkupLanguage markup, MarkupParser parser, ContentState state) {
		TableOfContentsBlock tocBlock = (TableOfContentsBlock) clone();
		tocBlock.blockLineNumber = 0;
		tocBlock.markupLanguage = markup;
		tocBlock.setParser(parser);
		tocBlock.setState(state);
		return tocBlock;
	}

	private AsciiDocContentState getAsciiDocState() {
		return (AsciiDocContentState) state;
	}
}
