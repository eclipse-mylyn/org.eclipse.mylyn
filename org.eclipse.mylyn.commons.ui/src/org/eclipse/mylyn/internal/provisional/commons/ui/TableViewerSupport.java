/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.io.File;

import org.eclipse.jface.layout.AbstractColumnLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 * @author Shawn Minto
 */
public class TableViewerSupport extends AbstractColumnViewerSupport<TableColumn> {

	private final Table table;

	public TableViewerSupport(TableViewer viewer, File stateFile) {
		super(viewer, stateFile);
		this.table = viewer.getTable();
		initializeViewerSupport();
	}

	public TableViewerSupport(TableViewer viewer, File stateFile, boolean[] defaultVisibilities) {
		super(viewer, stateFile, defaultVisibilities);
		this.table = viewer.getTable();
		initializeViewerSupport();
	}

	@Override
	void addColumnSelectionListener(TableColumn column, SelectionListener selectionListener) {
		column.addSelectionListener(selectionListener);
	}

	@Override
	Rectangle getClientArea() {
		return table.getClientArea();
	}

	@Override
	TableColumn getColumn(int index) {
		return table.getColumn(index);
	}

	@Override
	int getColumnIndexOf(TableColumn column) {
		return table.indexOf(column);
	}

	@Override
	AbstractColumnLayout getColumnLayout() {
		if (table.getLayout() instanceof AbstractColumnLayout) {
			return (AbstractColumnLayout) table.getLayout();
		} else if (table.getParent().getLayout() instanceof AbstractColumnLayout) {
			return (AbstractColumnLayout) table.getParent().getLayout();
		} else {
			return null;
		}
	}

	@Override
	int[] getColumnOrder() {
		return table.getColumnOrder();
	}

	@Override
	TableColumn[] getColumns() {
		return table.getColumns();
	}

	@Override
	int getColumnWidth(TableColumn column) {
		return column.getWidth();
	}

	@Override
	int getHeaderHeight() {
		return table.getHeaderHeight();
	}

	@Override
	TableColumn getSortColumn() {
		return table.getSortColumn();
	}

	@Override
	int getSortDirection() {
		return table.getSortDirection();
	}

	@Override
	void setColumnOrder(int[] order) {
		table.setColumnOrder(order);
	}

	@Override
	void setColumnResizable(TableColumn column, boolean resizable) {
		column.setResizable(resizable);
	}

	@Override
	void setColumnWidth(TableColumn column, int width) {
		column.setWidth(width);
	}

	@Override
	void setSortColumn(TableColumn column) {
		table.setSortColumn(column);
	}

	@Override
	void setSortDirection(int direction) {
		table.setSortDirection(direction);
	}

}
