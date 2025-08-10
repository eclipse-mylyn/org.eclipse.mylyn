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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class TreeSorter extends AbstractColumnViewerSorter<TreeViewer, TreeColumn> {

	@Override
	int getColumnIndex(TreeViewer viewer, TreeColumn column) {
		return viewer.getTree().indexOf(column);
	}

	@Override
	TreeColumn getSortColumn(TreeViewer viewer) {
		return viewer.getTree().getSortColumn();
	}

	@Override
	protected int getSortDirection(TreeViewer viewer) {
		return viewer.getTree().getSortDirection();
	}

}
