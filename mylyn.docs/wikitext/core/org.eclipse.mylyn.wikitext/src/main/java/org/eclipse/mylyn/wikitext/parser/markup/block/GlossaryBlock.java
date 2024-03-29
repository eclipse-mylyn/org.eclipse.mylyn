/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup.block;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;

/**
 * An abstract implementation of a glossary. Emits a definition list containing all terms {@link ContentState#getGlossaryTerms() defined} in
 * the document. Subclasses need only define {Block {@link #canStart(String, int)}.
 *
 * @author David Green
 * @since 3.0
 */
public abstract class GlossaryBlock extends Block {

	protected int blockLineNumber = 0;

	private String style;

	@Override
	public int processLineContent(String line, int offset) {
		if (blockLineNumber++ > 0) {
			setClosed(true);
			return 0;
		}
		if (!getMarkupLanguage().isFilterGenerativeContents()) {
			SortedMap<String, String> glossary = new TreeMap<>(state.getGlossaryTerms());

			builder.beginBlock(BlockType.DEFINITION_LIST,
					new Attributes(null, null, style == null ? null : "list-style: " + style, null)); //$NON-NLS-1$
			Attributes nullAttributes = new Attributes();
			for (Map.Entry<String, String> ent : glossary.entrySet()) {
				builder.beginBlock(BlockType.DEFINITION_TERM, nullAttributes);
				builder.characters(ent.getKey());
				builder.endBlock();

				builder.beginBlock(BlockType.DEFINITION_ITEM, nullAttributes);
				builder.characters(ent.getValue());
				builder.endBlock();
			}

			builder.endBlock();
		}
		return -1;
	}

	/**
	 * the CSS style of the glossary block
	 *
	 * @return the style, or null if the style is not defined
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * the CSS style of the glossary block
	 *
	 * @param style
	 *            the style, or null if the style is not defined
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public void setClosed(boolean closed) {
		if (closed) {
			style = null;
		}
		super.setClosed(closed);
	}
}
