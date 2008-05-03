/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITaskListManager;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TasksUi {

	public static final String ID_PLANNING_PAGE = "org.eclipse.mylyn.tasks.ui.pageFactory.Planning";

	public static ITaskListManager getTaskListManager() {
		return TasksUiPlugin.getTaskListManager();
	}

	/**
	 * TODO: in progress, will return interface once extraction complete
	 */
	public static TaskActivityManager getTaskActivityManager() {
		return TasksUiPlugin.getTaskActivityManager();
	}

	public static ITaskDataManager getTaskDataManager() {
		return TasksUiPlugin.getTaskDataManager();
	}

	public static ITaskRepositoryManager getRepositoryManager() {
		return TasksUiPlugin.getRepositoryManager();
	}

}
