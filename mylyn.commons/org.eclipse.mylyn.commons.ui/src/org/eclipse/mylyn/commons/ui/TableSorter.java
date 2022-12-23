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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Steffen Pingel
 * @since 3.7
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
