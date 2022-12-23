/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TableDecoratingLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider {

	public TableDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskContainer)) {
			return null;
		}
		if (columnIndex == 0) {
			if (element instanceof ITaskContainer && !(element instanceof ITask)) {
				return super.getImage(element);
			} else {
				if (element instanceof AbstractTask) {
					AbstractTask task = (AbstractTask) element;
					if (task.isActive()) {
						return CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE);
					} else {
						if (TasksUiPlugin.getContextStore().hasContext(task)) {
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
			if (element instanceof ITaskContainer || element instanceof IRepositoryQuery) {
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
