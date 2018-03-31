/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
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

package org.eclipse.mylyn.wikitext.mediawiki.internal.block;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;

/**
 * @author David Green
 */
public abstract class AbstractTableOfContentsBlock extends Block {

	// TODO: move to core and make API in 1.4

	private String style = "none"; //$NON-NLS-1$

	protected int maxLevel = Integer.MAX_VALUE;

	public AbstractTableOfContentsBlock() {
		super();
	}

	protected void emitToc(OutlineItem item) {
		if (item.getChildren().isEmpty()) {
			return;
		}
		if ((item.getLevel() + 1) > maxLevel) {
			return;
		}
		Attributes nullAttributes = new Attributes();

		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes(null, null, "list-style: " + style + ";", null)); //$NON-NLS-1$ //$NON-NLS-2$
		for (OutlineItem child : item.getChildren()) {
			builder.beginBlock(BlockType.LIST_ITEM, nullAttributes);
			builder.link('#' + child.getId(), child.getLabel());
			emitToc(child);
			builder.endBlock();
		}
		builder.endBlock();
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

}
