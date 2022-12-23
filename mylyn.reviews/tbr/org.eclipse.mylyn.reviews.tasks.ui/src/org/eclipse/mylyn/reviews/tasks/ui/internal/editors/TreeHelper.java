/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
/**
 * @author mattk
 *
 */
public class TreeHelper {

	public static TreeViewer createColumns(TreeViewer tree,
			IColumnSpec<?>[] columns) {
		for (IColumnSpec<?> column : columns) {
			createColumn(tree, column);
		}
		return tree;
	}

	public static TreeViewerColumn createColumn(TreeViewer tree,
			IColumnSpec<?> columnSpec) {
		TreeViewerColumn column = new TreeViewerColumn(tree, SWT.LEFT);
		column.getColumn().setText(columnSpec.getTitle());
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		return column;
	}
}
