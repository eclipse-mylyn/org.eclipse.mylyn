/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskDataManager {

	public ITaskDataWorkingCopy createWorkingCopy(ITask task, String kind, TaskData taskData);

	public abstract ITaskDataWorkingCopy getWorkingCopy(ITask task, String kind) throws CoreException;

	public abstract void discardEdits(ITask task, String kind) throws CoreException;

	public abstract TaskData getTaskData(ITask task, String kind) throws CoreException;

	public abstract boolean hasTaskData(ITask task, String connectorKind);

	public abstract void putUpdatedTaskData(ITask task, TaskData taskData, boolean user) throws CoreException;

	public abstract void putSubmittedTaskData(ITask task, TaskData taskData) throws CoreException;

}