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
package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskTableLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider,
		ITableColorProvider, ITableFontProvider {

	private Color categoryBackgroundColor;

	/**
	 * @param view
	 *            can be null
	 */
	public TaskTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBackground) {
		super(provider, decorator);
		this.categoryBackgroundColor = parentBackground;
	}

	public String getColumnText(Object obj, int columnIndex) {
		if (obj instanceof ITaskListElement) {
			switch (columnIndex) {
			case 0:
				if (obj instanceof DateRangeContainer) {
					if (((DateRangeContainer) obj).isPresent()) {
						return super.getText(obj) + " - Today";
					}
				}
				return super.getText(obj);
			case 1:
				return null;
			}
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (columnIndex == 0) {
			return super.getImage(element);
		}
		return null;
	}

	public Font getFont(Object element, int columnIndex) {
		return super.getFont(element);
	}

	public Color getForeground(Object element, int columnIndex) {
		return super.getForeground(element);
	}

	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof AbstractTaskContainer && !(element instanceof ITask)) {
			AbstractTaskContainer category = (AbstractTaskContainer) element;
			if (category instanceof TaskArchive) {
				return TaskListColorsAndFonts.BACKGROUND_ARCHIVE;
			} else {
				return categoryBackgroundColor;
			}
		} else if (element instanceof AbstractRepositoryQuery) {
			return categoryBackgroundColor;
		}

		return super.getBackground(element);
	}

	public void setCategoryBackgroundColor(Color parentBackgroundColor) {
		this.categoryBackgroundColor = parentBackgroundColor;
	}
}
