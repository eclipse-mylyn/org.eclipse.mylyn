/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

public class DefaultTasksState extends TasksState {

	public DefaultTasksState() {
		super(TasksUiPlugin.getTaskList(), TasksUiPlugin.getTaskDataManager(), TasksUiPlugin.getRepositoryManager(),
				TasksUiPlugin.getRepositoryModel(), TasksUiPlugin.getContextStore(),
				TasksUiPlugin.getTaskActivityManager(), TasksUiPlugin.getTaskJobFactory());
	}

}
