/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskListManager {

	public abstract void activateTask(AbstractTask task);

//	public abstract void addActivationListener(ITaskActivityListener activityListener);

	public abstract void deactivateAllTasks();

	public abstract void deactivateTask(AbstractTask task);

	public abstract AbstractTask getActiveTask();

	public abstract ITaskList getTaskList();

//	public abstract void removeActivationListener(ITaskActivityListener activityListener);

}