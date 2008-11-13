/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITaskActivityManager;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author Steffen Pingel
 * @author Mik Kersten
 * @since 3.0
 */
public class TasksUi {

	/**
	 * @since 3.0
	 */
	public static AbstractRepositoryConnector getRepositoryConnector(String kind) {
		return getRepositoryManager().getRepositoryConnector(kind);
	}

	/**
	 * @since 3.0
	 */
	public static AbstractRepositoryConnectorUi getRepositoryConnectorUi(String kind) {
		return TasksUiPlugin.getConnectorUi(kind);
	}

	/**
	 * @since 3.0
	 */
	public static IRepositoryManager getRepositoryManager() {
		return TasksUiPlugin.getRepositoryManager();
	}

	/**
	 * @since 3.0
	 */
	public static ITaskActivityManager getTaskActivityManager() {
		return TasksUiPlugin.getTaskActivityManager();
	}

	/**
	 * @since 3.0
	 */
	public static ITaskDataManager getTaskDataManager() {
		return TasksUiPlugin.getTaskDataManager();
	}

	/**
	 * @since 3.0
	 */
	public static IRepositoryModel getRepositoryModel() {
		return TasksUiPlugin.getRepositoryModel();
	}

	/**
	 * @since 3.1
	 */
	public static ITasksUiFactory getUiFactory() {
		return TasksUiPlugin.getUiFactory();
	}

}
