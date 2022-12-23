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

import org.eclipse.swt.graphics.Image;
/**
 * @author mattk
 *
 * @param <T>
 */
public class ColumnLabelProvider<T> extends TableLabelProvider {

	private IColumnSpec<T>[] specs;

	public ColumnLabelProvider(IColumnSpec<T>[] specs) {
		this.specs = specs.clone();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return specs[columnIndex].getImage((T) element);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnText(Object element, int columnIndex) {
		return specs[columnIndex].getText((T) element);
	}

}
