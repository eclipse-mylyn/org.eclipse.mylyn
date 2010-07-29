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

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Steffen Pingel
 */
public abstract class TableSorter extends ViewerSorter {

	protected <T> int compare(Comparable<T> key1, T key2) {
		if (key1 == null) {
			return (key2 != null) ? 1 : 0;
		} else if (key2 == null) {
			return -1;
		}
		return key1.compareTo(key2);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		TableViewer tableViewer = (TableViewer) viewer;
		TableColumn column = tableViewer.getTable().getSortColumn();
		if (column != null) {
			int index = tableViewer.getTable().indexOf(column);
			int result = compare(tableViewer, e1, e2, index);
			if (tableViewer.getTable().getSortDirection() == SWT.UP) {
				return -result;
			}
			return result;
		}
		return super.compare(viewer, e1, e2);
	}

	@SuppressWarnings("unchecked")
	public int compare(TableViewer viewer, Object e1, Object e2, int columnIndex) {
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
			if (prov instanceof ILabelProvider) {
				ILabelProvider lprov = (ILabelProvider) prov;
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
