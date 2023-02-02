/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;

public class DefaultTasksState extends TasksState {

	public DefaultTasksState() {
		super(TasksUiPlugin.getTaskList(), TasksUiPlugin.getTaskDataManager(), TasksUiPlugin.getRepositoryManager(),
				TasksUiPlugin.getRepositoryModel(), TasksUiPlugin.getContextStore(),
				TasksUiPlugin.getTaskActivityManager(), TasksUiPlugin.getTaskJobFactory());
	}

}