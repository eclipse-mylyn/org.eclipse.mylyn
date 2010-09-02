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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Shawn Minto
 * @author Steffen Pingel
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
