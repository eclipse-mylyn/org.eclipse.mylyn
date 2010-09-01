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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public abstract class AbstractColumnViewerSorter<V extends ColumnViewer> extends ViewerSorter {
	protected <T> int compare(Comparable<T> key1, T key2) {
		if (key1 == null) {
			return (key2 != null) ? 1 : 0;
		} else if (key2 == null) {
			return -1;
		}
		return key1.compareTo(key2);
	}

	abstract Item getSortColumn(Viewer viewer);

	abstract int getSortDirection(Viewer viewer);

	abstract int getColumnIndex(Viewer viewer, Item column);

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Assert.isLegal(viewer instanceof ColumnViewer);
		Item column = getSortColumn(viewer);
		if (column != null) {
			int index = getColumnIndex(viewer, column);
			int result = compare((V) viewer, e1, e2, index);
			if (getSortDirection(viewer) == SWT.UP) {
				return -result;
			}
			return result;
		}
		return super.compare(viewer, e1, e2);
	}

	@SuppressWarnings("unchecked")
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
