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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

// FIXME: move to internal

/**
 * 
 * 
 * @author David Green
 */
public class Block extends Segment<Segment<?>> {

	private final BlockType type;

	private final int headingLevel;

	private boolean spansComputed = false;

	public Block(BlockType type, int offset, int length) {
		super(offset, length);
		this.type = type;
		headingLevel = 0;
	}

	public Block(int headingLevel, int offset, int length) {
		super(offset, length);
		if (headingLevel <= 0) {
			throw new IllegalArgumentException();
		}
		this.headingLevel = headingLevel;
		type = null;
	}

	public Block(BlockType type, Attributes attributes, int offset, int length) {
		super(attributes, offset, length);
		this.type = type;
		headingLevel = 0;
	}

	public Block(int headingLevel, Attributes attributes, int offset, int length) {
		super(attributes, offset, length);
		this.headingLevel = headingLevel;
		type = null;
	}

	@Override
	public Block getParent() {
		return (Block) super.getParent();
	}

	/**
	 * the type of block
	 * 
	 * @return the block type, or null if this block is a heading
	 */
	public BlockType getType() {
		return type;
	}

	/**
	 * the heading level, or 0 if this is not a heading.
	 */
	public int getHeadingLevel() {
		return headingLevel;
	}

	public boolean isSpansComputed() {
		return spansComputed;
	}

	public void setSpansComputed(boolean spansComputed) {
		this.spansComputed = spansComputed;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("<");
		String elementName;
		if (type != null) {
			elementName = type.name();
		} else {
			elementName = "h" + headingLevel;
		}
		buf.append(elementName);
		buf.append(" offset=\"");
		buf.append(getOffset());
		buf.append("\" length=\"");
		buf.append(getLength());
		buf.append('"');
		if (getChildren().isEmpty()) {
			buf.append("/>\n");
		} else {
			buf.append(">\n");
			StringBuilder buf2 = new StringBuilder();
			buf2.append("\t");
			for (Segment<?> child : getChildren().asList()) {
				buf2.append(child);
			}
			String children = buf2.toString();
			children = children.replace("\n", "\n\t");
			if (children.endsWith("\t")) {
				children = children.substring(0, children.length() - 1);
			} else {
				children = children + "\n";
			}
			buf.append(children);
			buf.append("</");
			buf.append(elementName);
			buf.append(">\n");
		}
		return buf.toString();
	}
}
