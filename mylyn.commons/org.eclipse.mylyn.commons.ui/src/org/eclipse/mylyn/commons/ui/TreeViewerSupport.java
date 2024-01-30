/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import java.io.File;

import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 3.7
 */
public class TreeViewerSupport extends AbstractColumnViewerSupport<TreeColumn> {

	private final Tree tree;

	public TreeViewerSupport(TreeViewer viewer, File stateFile) {
		super(viewer, stateFile);
		tree = viewer.getTree();
		initializeViewerSupport();
	}

	public TreeViewerSupport(TreeViewer viewer, File stateFile, boolean[] defaultVisibilities) {
		super(viewer, stateFile, defaultVisibilities);
		tree = viewer.getTree();
		initializeViewerSupport();
	}

	@Override
	void addColumnSelectionListener(TreeColumn column, SelectionListener selectionListener) {
		column.addSelectionListener(selectionListener);
	}

	@Override
	Rectangle getClientArea() {
		return tree.getClientArea();
	}

	@Override
	TreeColumn getColumn(int index) {
		return tree.getColumn(index);
	}

	@Override
	int getColumnIndexOf(TreeColumn column) {
		return tree.indexOf(column);
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
	int[] getColumnOrder() {
		return tree.getColumnOrder();
	}

	@Override
	TreeColumn[] getColumns() {
		return tree.getColumns();
	}

	@Override
	int getColumnWidth(TreeColumn column) {
		return column.getWidth();
	}

	@Override
	int getHeaderHeight() {
		return tree.getHeaderHeight();
	}

	@Override
	TreeColumn getSortColumn() {
		return tree.getSortColumn();
	}

	@Override
	int getSortDirection() {
		return tree.getSortDirection();
	}

	@Override
	void setColumnOrder(int[] order) {
		tree.setColumnOrder(order);
	}

	@Override
	void setColumnResizable(TreeColumn column, boolean resizable) {
		column.setResizable(resizable);
	}

	@Override
	void setColumnWidth(TreeColumn column, int width) {
		column.setWidth(width);
	}

	@Override
	void setSortColumn(TreeColumn column) {
		tree.setSortColumn(column);
	}

	@Override
	void setSortDirection(int direction) {
		tree.setSortDirection(direction);
	}

}
