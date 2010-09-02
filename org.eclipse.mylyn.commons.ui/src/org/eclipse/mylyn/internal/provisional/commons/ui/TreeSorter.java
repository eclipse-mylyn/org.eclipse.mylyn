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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Shawn Minto
 */
public abstract class TreeSorter extends AbstractColumnViewerSorter<TreeViewer> {

	@Override
	int getColumnIndex(Viewer viewer, Item column) {
		if (viewer instanceof TreeViewer && column instanceof TreeColumn) {
			((TreeViewer) viewer).getTree().indexOf((TreeColumn) column);
		}
		return 0;
	}

	@Override
	Item getSortColumn(Viewer viewer) {
		if (viewer instanceof TreeViewer) {
			return ((TreeViewer) viewer).getTree().getSortColumn();
		}
		return null;
	}

	@Override
	protected int getSortDirection(Viewer viewer) {
		if (viewer instanceof TreeViewer) {
			return ((TreeViewer) viewer).getTree().getSortDirection();
		}
		return 0;
	}

}
