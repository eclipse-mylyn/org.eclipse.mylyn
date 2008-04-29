/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.provisional.workbench.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TableDecoratingLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider {

	public TableDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof AbstractTaskContainer)) {
			return null;
		}
		if (columnIndex == 0) {
			if (element instanceof AbstractTaskContainer && !(element instanceof AbstractTask)) {
				return super.getImage(element);
			} else {
				AbstractTask task = TaskElementLabelProvider.getCorrespondingTask((AbstractTaskContainer) element);
				if (task != null) {
					if (task.isActive()) {
						return CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE);
					} else {
						if (ContextCore.getContextManager().hasContext(task.getHandleIdentifier())) {
							return CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE);
						} else {
							return CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE_EMPTY);
						}
					}
				} else {
					return CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE_EMPTY);
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
		return null;
	}

}
