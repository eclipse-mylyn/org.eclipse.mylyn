/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An item in a document outline. A document outline reflects the heading structure of the document. Generally there is
 * always a root item that represents the document itself. Every level-1 heading becomes a child item of the root.
 * 
 * @author David Green
 * @since 1.0
 */
public class OutlineItem {

	private OutlineItem parent;

	private final int level;

	private List<OutlineItem> children = new ArrayList<OutlineItem>();

	private final int offset;

	private int length;

	private final String id;

	private String label;

	private String kind;

	private int childOffset;

	private String tooltip;

	private Map<String, OutlineItem> itemsById;

	private String resourcePath;

	public OutlineItem(OutlineItem parent, int level, String id, int offset, int length, String label) {
		super();
		this.parent = parent;
		this.level = (parent == null) ? 0 : level;
		if (parent != null && level < parent.getLevel()) {
			throw new IllegalArgumentException();
		}
		this.id = id;
		this.offset = offset;
		this.length = length;
		this.label = label;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	/**
	 * get the length of the outline item, which corresponds to the length of the heading text. The length does not
	 * include content following the heading text itself.
	 * 
	 * @see #getSectionLength()
	 */
	public int getLength() {
		return length;
	}

	/**
	 * get the length of the section, which is the length of the heading text plus the length of any following content
	 * up to the next peer-leveled heading or the parent's following sibling.
	 * 
	 * @see #getLength()
	 */
	public int getSectionLength() {
		if (parent == null) {
			return length;
		}
		List<OutlineItem> siblings = getParent().getChildren();
		int index = siblings.indexOf(this);
		if (index < (siblings.size() - 1)) {
			return siblings.get(index + 1).getOffset() - getOffset();
		}
		int parentRelativeOffset = getOffset() - parent.getOffset();
		return parent.getSectionLength() - parentRelativeOffset;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * the text of the heading which could be truncated
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * the id of the heading, which is typically (though not guaranteed to be) unique within a document. Heading ids may
	 * be used as the target of document-relative anchors
	 */
	public String getId() {
		return id;
	}

	/**
	 * the level of the document which is positive and usually <= 6 except for the root item where the value is
	 * undefined.
	 */
	public int getLevel() {
		if (parent == null) {
			return 0;
		}
		return level;
	}

	void setLength(int length) {
		this.length = length;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public OutlineItem getParent() {
		return parent;
	}

	/**
	 * indicate if this is the root item (that is, the item representing the whole document)
	 */
	public boolean isRootItem() {
		return parent == null;
	}

	/**
	 * Get the previous item. The order of the items is determined via document order traversal of all nodes in the
	 * outline.
	 * 
	 * @return the previous item or null if there is no previous (ie: the root item).
	 */
	public OutlineItem getPrevious() {
		if (parent == null) {
			return null;
		}
		List<OutlineItem> siblings = parent.getChildren();
		int index = siblings.indexOf(this);
		if (index > 0) {
			return siblings.get(index - 1);
		}
		return parent;
	}

	public List<OutlineItem> getChildren() {
		return children;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int hashCode() {
		return calculatePositionKey().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OutlineItem other = (OutlineItem) obj;
		return other.calculatePositionKey().equals(calculatePositionKey());
	}

	public void clear() {
		children.clear();
	}

	private String calculatePositionKey() {
		if (parent == null) {
			return ""; //$NON-NLS-1$
		}
		return getParent().calculatePositionKey() + "/" + kind + childOffset; //$NON-NLS-1$
	}

	private void addChild(OutlineItem outlineItem) {
		outlineItem.childOffset = children.size();
		children.add(outlineItem);
	}

	public OutlineItem findNearestMatchingOffset(int offset) {
		NearestItemVisitor visitor = new NearestItemVisitor(offset);
		accept(visitor);

		return visitor.nearest;
	}

	public OutlineItem findItemById(String id) {
		if (itemsById == null) {
			itemsById = new HashMap<String, OutlineItem>();
			accept(new Visitor() {
				public boolean visit(OutlineItem item) {
					if (item.getId() != null) {
						itemsById.put(item.getId(), item);
					}
					return true;
				}
			});
		}
		return itemsById.get(id);
	}

	private static class NearestItemVisitor implements Visitor {

		private OutlineItem nearest = null;

		private final int offset;

		public NearestItemVisitor(int offset) {
			this.offset = offset;
		}

		public boolean visit(OutlineItem item) {
			if (item.getOffset() == -1) {
				return true;
			}
			if (nearest == null) {
				nearest = item;
				return true;
			}
			int itemDistance = item.distance(offset);
			if (itemDistance > 0) {
				return true;
			}
			int nearestDistance = nearest.distance(offset);
			nearestDistance = Math.abs(nearestDistance);
			itemDistance = Math.abs(itemDistance);
			if (itemDistance < nearestDistance) {
				nearest = item;
			} else if (itemDistance > nearestDistance) {
				return false;
			}
			return true;
		}

	}

	public int distance(int offset) {
		int startDistance = this.offset - offset;

		return startDistance;
	}

	public interface Visitor {
		/**
		 * @param item
		 *            the item to visit
		 * 
		 * @return true if the items children should be visited
		 */
		public boolean visit(OutlineItem item);
	}

	public void accept(Visitor visitor) {
		if (visitor.visit(this)) {
			for (OutlineItem item : getChildren()) {
				item.accept(visitor);
			}
		}
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getTooltip() {
		return tooltip;
	}

	/**
	 * the resource path to the resource of this outline item
	 * 
	 * @return the resource path, or null if it's unknown.
	 */
	public String getResourcePath() {
		if (getParent() != null) {
			return getParent().getResourcePath();
		}
		return resourcePath;
	}

	/**
	 * the resource path to the resource of this outline item
	 * 
	 * @param resourcePath
	 *            the resource path, or null if it's unknown.
	 */
	public void setResourcePath(String resourcePath) {
		if (getParent() != null) {
			getParent().setResourcePath(resourcePath);
		} else {
			this.resourcePath = resourcePath;
		}
	}

	/**
	 * move children from the given outline item to this
	 */
	public void moveChildren(OutlineItem otherParent) {
		if (!otherParent.children.isEmpty()) {
			if (children.isEmpty()) {
				List<OutlineItem> temp = children;
				children = otherParent.children;
				otherParent.children = temp;
				for (OutlineItem child : children) {
					child.parent = this;
				}
			} else {
				children.addAll(otherParent.children);
				for (OutlineItem child : otherParent.children) {
					child.parent = this;
				}
				otherParent.children.clear();
			}
		}
		itemsById = null;
		setLength(otherParent.getLength());
	}

	/**
	 * Indicate if this outline item contains the given outline item. The computation uses outline item offsets (the
	 * {@link #getOffset() offset} and {@link #getSectionLength() section length}.
	 * 
	 * @return true if and only if the offsets of the provided item lie within the offsets of this outline item.
	 */
	public boolean contains(OutlineItem item) {
		if (item == this || isRootItem()) {
			return true;
		}
		if (getOffset() <= item.getOffset()) {
			int end = getOffset() + getSectionLength();
			int itemEnd = item.getOffset() + item.getSectionLength();
			if (end >= itemEnd) {
				return true;
			}
		}
		return false;
	}
}
