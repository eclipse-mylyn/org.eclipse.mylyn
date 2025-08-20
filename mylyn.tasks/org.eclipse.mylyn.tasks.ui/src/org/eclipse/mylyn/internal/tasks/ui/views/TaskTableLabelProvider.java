/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskTableLabelProvider extends DecoratingLabelProvider
		implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	private Color categoryBackgroundColor;

	/**
	 * @param view
	 *            can be null
	 */
	public TaskTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBackground) {
		super(provider, decorator);
		categoryBackgroundColor = parentBackground;
	}

	@Override
	public String getColumnText(Object obj, int columnIndex) {
		if (obj instanceof ITaskContainer) {
			switch (columnIndex) {
				case 0:
//				if (obj instanceof ScheduledTaskContainer) {
//					if (((ScheduledTaskContainer) obj).isToday()) {
//						return super.getText(obj) + " - Today";
//					}
//				}
					return super.getText(obj);
				case 1:
					return null;
			}
		}
		return null;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskContainer)) {
			return null;
		}
		if (columnIndex == 0) {
			return super.getImage(element);
		}
		return null;
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		return super.getFont(element);
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		return super.getForeground(element);
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof ITaskContainer && !(element instanceof ITask)) {
			return categoryBackgroundColor;
		} else if (element instanceof IRepositoryQuery) {
			return categoryBackgroundColor;
		}

		return super.getBackground(element);
	}

	public void setCategoryBackgroundColor(Color parentBackgroundColor) {
		categoryBackgroundColor = parentBackgroundColor;
	}
}
