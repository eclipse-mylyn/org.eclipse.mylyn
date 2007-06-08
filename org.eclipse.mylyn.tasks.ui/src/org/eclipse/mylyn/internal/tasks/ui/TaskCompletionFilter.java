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
package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskCompletionFilter extends AbstractTaskListFilter {

	@Override
	public boolean select(Object parent, Object element) {
		boolean exposeSubTasks = !TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.FILTER_SUBTASKS);
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			if (shouldAlwaysShow(parent, task, exposeSubTasks)) {
				return true;
			}
			return !task.isCompleted();
		}
		return true;
	}
}
