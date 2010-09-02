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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Steffen Pingel
 */
public abstract class TableSorter extends AbstractColumnViewerSorter<TableViewer, TableColumn> {

	@Override
	int getColumnIndex(TableViewer viewer, TableColumn column) {
		return viewer.getTable().indexOf(column);
	}

	@Override
	TableColumn getSortColumn(TableViewer viewer) {
		return viewer.getTable().getSortColumn();
	}

	@Override
	int getSortDirection(TableViewer viewer) {
		return viewer.getTable().getSortDirection();
	}

}
