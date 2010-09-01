/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.io.File;

import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Shawn Minto
 */
public class TreeViewerSupport extends AbstractColumnViewerSupport {

	private final Tree tree;

	public TreeViewerSupport(TreeViewer viewer, File stateFile) {
		super(viewer, stateFile);
		this.tree = viewer.getTree();
		initializeViewerSupport();
	}

	public TreeViewerSupport(TreeViewer viewer, File stateFile, boolean[] defaultVisibilities) {
		super(viewer, stateFile, defaultVisibilities);
		this.tree = viewer.getTree();
		initializeViewerSupport();
	}

	@Override
	Item[] getColumns() {
		return tree.getColumns();
	}

	@Override
	AbstractColumnLayout getColumnLayout() {
		if (tree.getLayout() instanceof AbstractColumnLayout) {
			return (AbstractColumnLayout) tree.getLayout();
		} else if (tree.getParent().getLayout() instanceof AbstractColumnLayout) {
			return (AbstractColumnLayout) tree.getParent().getLayout();
		} else {
			return null;
		}
	}

	@Override
	Rectangle getClientArea() {
		return tree.getClientArea();
	}

	@Override
	int getHeaderHeight() {
		return tree.getHeaderHeight();
	}

	@Override
	int[] getColumnOrder() {
		return tree.getColumnOrder();
	}

	@Override
	void setColumnOrder(int[] order) {
		tree.setColumnOrder(order);
	}

	@Override
	int getSortDirection() {
		return tree.getSortDirection();
	}

	@Override
	void setSortDirection(int direction) {
		tree.setSortDirection(direction);
	}

	@Override
	Item getSortColumn() {
		return tree.getSortColumn();
	}

	@Override
	void setSortColumn(Item column) {
		if (column instanceof TreeColumn) {
			tree.setSortColumn(((TreeColumn) column));
		}
	}

	@Override
	void addColumnSelectionListener(Item column, SelectionListener selectionListener) {
		if (column instanceof TreeColumn) {
			((TreeColumn) column).addSelectionListener(selectionListener);
		}
	}

	@Override
	int getColumnWidth(Item column) {
		if (column instanceof TreeColumn) {
			return ((TreeColumn) column).getWidth();
		}
		return 0;
	}

	@Override
	void setColumnResizable(Item column, boolean resizable) {
		if (column instanceof TreeColumn) {
			((TreeColumn) column).setResizable(resizable);
		}
	}

	@Override
	void setColumnWidth(Item column, int width) {
		if (column instanceof TreeColumn) {
			((TreeColumn) column).setWidth(width);
		}
	}

	@Override
	Item getColumn(int index) {
		return tree.getColumn(index);
	}

	@Override
	int getColumnIndexOf(Item column) {
		if (column instanceof TreeColumn) {
			return tree.indexOf(((TreeColumn) column));
		}
		return 0;
	}
}
