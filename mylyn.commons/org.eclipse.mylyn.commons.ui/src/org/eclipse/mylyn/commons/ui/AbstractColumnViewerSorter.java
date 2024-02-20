/*******************************************************************************
 * Copyright (c) 2010, 2024 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

/**
 * Provides sorting support for column-based viewers such as tables and tree-tables.
 * 
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class AbstractColumnViewerSorter<V extends ColumnViewer, I extends Item> extends ViewerComparator {

	/**
	 * Returns the current sort column.
	 * 
	 * @return the column that is being sorted; <code>null</code>, if no column is sorted
	 */
	abstract I getSortColumn(V viewer);

	/**
	 * Returns the current sort order.
	 * 
	 * @return {@link SWT#DOWN}, if sorting descending; 0, if no sort order is specified; {@link SWT#UP}, if sorting ascending
	 */
	abstract int getSortDirection(V viewer);

	/**
	 * Returns the index of <code>I</code>.
	 */
	abstract int getColumnIndex(V viewer, I column);

	/**
	 * Compares <code>e1</code> and <code>e2</code> according to the current sort column and order. Delegates to
	 * {@link #compareDefault(ColumnViewer, Object, Object)} if no sort column is selected.
	 * 
	 * @see #getSortColumn(ColumnViewer)
	 * @see #getSortDirection(ColumnViewer)
	 * @see #compareDefault(ColumnViewer, Object, Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Assert.isLegal(viewer instanceof ColumnViewer);
		@SuppressWarnings("unchecked")
		V columnViewer = (V) viewer;
		I column = getSortColumn(columnViewer);
		if (column != null) {
			int index = getColumnIndex(columnViewer, column);
			int result = compare(columnViewer, e1, e2, index);
			if (getSortDirection(columnViewer) == SWT.DOWN) {
				return -result;
			}
			return result;
		}
		return compareDefault(columnViewer, e1, e2);
	}

	/**
	 * Compares <code>e1</code> and <code>e2</code> following the contract of {@link ViewerComparator#compare(Viewer, Object, Object)}.
	 * Subclasses may override.
	 * 
	 * @see ViewerComparator#compare(Viewer, Object, Object)
	 */
	protected int compareDefault(V viewer, Object e1, Object e2) {
		return super.compare(viewer, e1, e2);
	}

	/**
	 * Compares <code>e1</code> and <code>e2</code> according based on their category and column specific label.
	 */
	public int compare(V viewer, Object e1, Object e2, int columnIndex) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		String name1;
		String name2;

		if (viewer == null || !(viewer instanceof ContentViewer)) {
			name1 = e1.toString();
			name2 = e2.toString();
		} else {
			CellLabelProvider prov = viewer.getLabelProvider(columnIndex);
			if (prov instanceof ILabelProvider lprov) {
				name1 = lprov.getText(e1);
				name2 = lprov.getText(e2);
			} else {
				name1 = e1.toString();
				name2 = e2.toString();
			}
		}
		if (name1 == null) {
			name1 = "";//$NON-NLS-1$
		}
		if (name2 == null) {
			name2 = "";//$NON-NLS-1$
		}

		// use the comparator to compare the strings
		return getComparator().compare(name1, name2);
	}

}
