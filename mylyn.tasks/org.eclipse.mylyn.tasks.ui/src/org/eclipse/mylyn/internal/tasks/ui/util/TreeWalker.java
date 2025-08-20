/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Steffen Pingel
 */
public class TreeWalker {

	public enum Direction {
		UP, DOWN
	}

	public static abstract class TreeVisitor {

		public abstract boolean visit(Object object);

	}

	private Direction direction = Direction.DOWN;

	private final TreeViewer treeViewer;

	private final Tree tree;

	private boolean expandNodes;

	public TreeWalker(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
		tree = treeViewer.getTree();
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean getExpandNodes() {
		return expandNodes;
	}

	private TreePath getTreePath(TreeItem item) {
		List<Object> path = new ArrayList<>();
		do {
			// the tree is probably not fully refreshed at this point
			if (item.getData() == null) {
				return null;
			}
			path.add(0, item.getData());
			item = item.getParentItem();
		} while (item != null);
		return new TreePath(path.toArray());
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setExpandNodes(boolean expandNodes) {
		this.expandNodes = expandNodes;
	}

	private TreePath visitChildren(TreeViewer viewer, TreePath itemPath, TreeItem item, TreeVisitor visitor) {
		boolean restoreCollapsedState = false;
		// expand item
		try {
			if (getExpandNodes()) {
				boolean expandedState = item.getExpanded();
				if (!expandedState) {
					restoreCollapsedState = true;
					viewer.setExpandedState(itemPath, true);
				}
			}

			TreeItem[] children = item.getItems();
			if (children.length > 0 && children[0].getData() != null) {
				TreePath childPath = visitItems(viewer, itemPath, children, null, visitor);
				if (childPath != null) {
					return childPath;
				}
			}

		} finally {
			if (restoreCollapsedState) {
				// restore item state
				viewer.setExpandedState(itemPath, false);
			}
		}

		return null;
	}

	private TreePath visitItems(TreeViewer viewer, TreePath parentPath, TreeItem[] items, TreeItem visitedItem,
			TreeVisitor visitor) {
		if (direction == Direction.UP) {
			Collections.reverse(Arrays.asList(items));
		}

		boolean found = visitedItem == null;
		for (TreeItem item : items) {
			if (!found) {
				if (item == visitedItem) {
					found = true;
				}
			} else {
				TreePath itemPath = parentPath.createChildPath(item.getData());

				if (direction == Direction.DOWN) {
					if (visitor.visit(item.getData())) {
						return itemPath;
					}
				}

				TreePath childPath = visitChildren(viewer, itemPath, item, visitor);
				if (childPath != null) {
					return childPath;
				}

				if (direction == Direction.UP) {
					if (visitor.visit(item.getData())) {
						return itemPath;
					}
				}
			}
		}

		// visit parent siblings
		if (visitedItem != null) {
			TreeItem parent = visitedItem.getParentItem();
			if (parent != null) {
				if (direction == Direction.UP) {
					if (visitor.visit(parent.getData())) {
						return parentPath;
					}
				}

				return visitSiblings(viewer, parent, visitor);
			}
		}

		return null;
	}

	private TreePath visitSiblings(TreeViewer viewer, TreeItem item, TreeVisitor visitor) {
		TreeItem[] siblings;
		TreePath path;
		TreeItem parent = item.getParentItem();
		if (parent != null) {
			path = getTreePath(parent);
			if (path == null) {
				return null;
			}
			siblings = parent.getItems();
		} else {
			path = TreePath.EMPTY;
			siblings = viewer.getTree().getItems();
		}
		return visitItems(viewer, path, siblings, item, visitor);
	}

	public TreePath walk(TreeVisitor visitor, TreeItem startItem) {
		TreePath path = null;
		if (startItem != null) {
			if (direction == Direction.DOWN) {
				path = getTreePath(startItem);
				if (path != null) {
					path = visitChildren(treeViewer, path, startItem, visitor);
				}
			}
			if (path == null) {
				path = visitSiblings(treeViewer, startItem, visitor);
			}
		} else {
			path = visitItems(treeViewer, TreePath.EMPTY, tree.getItems(), null, visitor);
		}

		return path;
	}

}
