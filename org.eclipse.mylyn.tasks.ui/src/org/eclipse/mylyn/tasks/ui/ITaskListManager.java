/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskList;

public interface ITaskListManager {

	public abstract TaskList getTaskList();

	public abstract void activateTask(AbstractTask task);

	public abstract void deactivateAllTasks();

	public abstract void deactivateTask(AbstractTask task);

	public abstract void addActivityListener(ITaskActivityListener activityListener);

	public abstract void removeActivityListener(ITaskActivityListener activityListener);

}