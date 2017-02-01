/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.textile.core.block;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;

/**
 * @author David Green
 */
public abstract class AbstractTableOfContentsBlock extends Block {

	// TODO: move to core and make API in 1.4

	private String style = "none"; //$NON-NLS-1$

	private String cssClass = "toc"; //$NON-NLS-1$

	protected int maxLevel = Integer.MAX_VALUE;

	public AbstractTableOfContentsBlock() {
		super();
	}

	protected void emitToc(OutlineItem item) {

		Attributes nullAttributes = new Attributes();

		emitToc(item, 0, nullAttributes);
	}

	private void emitToc(OutlineItem item, int level, Attributes nullAttributes) {
		if (item.getChildren().isEmpty()) {
			return;
		}
		if ((item.getLevel() + 1) > maxLevel) {
			return;
		}

		Attributes listAttributes = new Attributes(null, level == 0 ? cssClass : null,
				"list-style: " + style + ";", null);//$NON-NLS-1$ //$NON-NLS-2$ 
		builder.beginBlock(BlockType.NUMERIC_LIST, listAttributes);
		for (OutlineItem child : item.getChildren()) {
			builder.beginBlock(BlockType.LIST_ITEM, nullAttributes);
			builder.link('#' + child.getId(), child.getLabel());
			emitToc(child, level + 1, nullAttributes);
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

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

}