/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 18, 2005
 */
package org.eclipse.mylar.internal.tasklist.ui.views;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITaskCategory;
import org.eclipse.mylar.internal.tasklist.ITaskListElement;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskListLabelProvider extends TaskElementLabelProvider implements ITableLabelProvider,
		ITableColorProvider, ITableFontProvider {

	public String getColumnText(Object obj, int columnIndex) {
		if (obj instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) obj;
			switch (columnIndex) {
			case 0:
				return null;
			case 1:
				return null;
			case 2:
				if (element instanceof ITaskCategory || element instanceof AbstractRepositoryQuery) {
					return null;
				} else {
					return element.getPriority();
				}
			case 3:
				return super.getText(obj);
			}
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (columnIndex == 0) {
			if (element instanceof ITaskCategory || element instanceof AbstractRepositoryQuery) {
				return ((ITaskListElement) element).getIcon();
			} else {
				return ((ITaskListElement) element).getStatusIcon();
			}
		} else if (columnIndex == 1) {
			if (element instanceof ITaskCategory || element instanceof AbstractRepositoryQuery) {
				return null;
			}
			return ((ITaskListElement) element).getIcon();
		} else {
			return null;
		}
	}
	
	public Font getFont(Object element, int columnIndex) {
		return super.getFont(element);
	}

	public Color getForeground(Object element, int columnIndex) {
		return getForeground(element);
	}

	public Color getBackground(Object element, int columnIndex) {
		return getBackground(element);
	}
}
