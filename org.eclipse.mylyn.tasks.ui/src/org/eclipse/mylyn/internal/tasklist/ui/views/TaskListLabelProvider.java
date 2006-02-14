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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskContainer;
import org.eclipse.mylar.internal.tasklist.ITaskListElement;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskListLabelProvider implements ITableLabelProvider,
		ITableColorProvider, ITableFontProvider {

	private TaskElementLabelProvider taskElementLabelProvider = new TaskElementLabelProvider();
	
	private TaskElementLabelProvider labelProvider;
	
	private Color parentBackgroundColor;
	
	public TaskListLabelProvider(Color color) {
		this.parentBackgroundColor = color;
		labelProvider = taskElementLabelProvider;
//		labelProvider = new DecoratingLabelProvider(taskElementLabelProvider,
//				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
//				new TaskDecorator());
	}
	
	public TaskListLabelProvider() {
		this.parentBackgroundColor = null;
	}

	public String getColumnText(Object obj, int columnIndex) {
		if (obj instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) obj;
			switch (columnIndex) {
			case 0:
				return null;
			case 1:
				return null;
			case 2:
				if (element instanceof ITaskContainer || element instanceof AbstractRepositoryQuery) {
					return null;
				} else {
					return element.getPriority();
				}
			case 3:
				return labelProvider.getText(obj);
			}
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (columnIndex == 0) {
			if (element instanceof ITaskContainer) {
				return labelProvider.getImage(element);
			} else {
				ITask task = taskElementLabelProvider.getCorrespondingTask((ITaskListElement)element);
				if (task != null) {
					if (task.isActive()) {
						return TaskListImages.getImage(TaskListImages.TASK_ACTIVE);
					} else {
						if (MylarPlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
							return TaskListImages.getImage(TaskListImages.TASK_INACTIVE_CONTEXT);
						} else {
							return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
						}
					}
				} else {
					return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
				}
			}
		} else if (columnIndex == 1) {
			if (element instanceof ITaskContainer || element instanceof AbstractRepositoryQuery) {
				return null;
			}
			return labelProvider.getImage(element);
		} 
		return null;
	}
	
	public Font getFont(Object element, int columnIndex) {
		return labelProvider.getFont(element);
	}

	public Color getForeground(Object element, int columnIndex) {
		return labelProvider.getForeground(element);
	}

	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof ITaskContainer) {
			ITaskContainer category = (ITaskContainer) element;
			if (category.isArchive()) {
				return TaskListImages.BACKGROUND_ARCHIVE;
			} else {
				return parentBackgroundColor;
			}
		} else if (element instanceof AbstractRepositoryQuery) {
			return parentBackgroundColor;
		}
		
		return labelProvider.getBackground(element);
	}

	public void addListener(ILabelProviderListener listener) {
		taskElementLabelProvider.addListener(listener);
	}

	public void dispose() {
		taskElementLabelProvider.dispose();
	}

	public boolean isLabelProperty(Object element, String property) {
		return taskElementLabelProvider.isLabelProperty(element, property);
	}

	public void removeListener(ILabelProviderListener listener) {
		taskElementLabelProvider.removeListener(listener);
	}
}
