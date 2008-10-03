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

import java.util.Iterator;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;

/**
 * 
 * 
 * @author David Green
 */
public class Segment<ChildType extends Segment<?>> {
	private int offset;

	private int length;

	private Attributes attributes;

	private Segments<ChildType> children = new Segments<ChildType>();

	private Segment<?> parent;

	public Segment(int offset, int length) {
		if (offset < 0) {
			throw new IllegalArgumentException();
		}
		if (length < 0) {
			throw new IllegalArgumentException();
		}
		this.offset = offset;
		this.length = length;
	}

	public Segment(Attributes attributes, int offset, int length) {
		this(offset, length);
		this.attributes = attributes;
	}

	/**
	 * get the end offset of this segment, exclusive equivalent to <code>getOffset()+getLength()</code>
	 */
	public int getEndOffset() {
		return offset + length;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		if (length < 0) {
			throw new IllegalArgumentException();
		}
		this.length = length;

		// adjust child sizes
		if (!children.isEmpty()) {
			final int endOffset = getEndOffset();
			Iterator<ChildType> childIt = children.asList().iterator();
			while (childIt.hasNext()) {
				ChildType child = childIt.next();
				if (child.getOffset() >= endOffset) {
					// shouldn't ever happen, but we do this to maintain offset integrity
					childIt.remove();
				} else {
					if (child.getEndOffset() > endOffset) {
						int newChildLength = endOffset - child.getOffset();
						child.setLength(newChildLength);
					}
				}
			}
		}
	}

	public void add(ChildType child) {
		if (child.getOffset() < offset) {
			throw new IllegalArgumentException();
		}
		if (child.getEndOffset() > getEndOffset()) {
			throw new IllegalArgumentException();
		}
		children.add(child);
		child.parent = this;
	}

	public Segment<?> getParent() {
		return parent;
	}

	public Segments<ChildType> getChildren() {
		return children;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	@SuppressWarnings("unchecked")
	public void replaceChildren(Segment<?> s) {
		children = (Segments<ChildType>) s.children;
		if (children != null) {
			for (ChildType child : children.asList()) {
				child.parent = this;
			}
		}
		s.children = null;
	}
}
