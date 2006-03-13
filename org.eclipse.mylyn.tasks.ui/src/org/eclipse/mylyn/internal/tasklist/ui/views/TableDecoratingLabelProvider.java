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

package org.eclipse.mylar.internal.tasklist.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TableDecoratingLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider {

	public TableDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (columnIndex == 0) {
			if (element instanceof AbstractTaskContainer) {
				return super.getImage(element);
			} else {
				ITask task = TaskElementLabelProvider.getCorrespondingTask((ITaskListElement)element);
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
			if (element instanceof AbstractTaskContainer || element instanceof AbstractRepositoryQuery) {
				return null;
			}
			return super.getImage(element);
		} 
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		// ignore
		return null;
	}

}
