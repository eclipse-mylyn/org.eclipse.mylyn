/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskDataManager {

	public abstract ITaskDataWorkingCopy createWorkingCopy(AbstractTask task, String kind) throws CoreException;

	public abstract void discardEdits(AbstractTask task, String kind) throws CoreException;

	@Deprecated
	public abstract RepositoryTaskData getNewTaskData(String repositoryUrl, String taskId);

	public abstract TaskData getTaskData(AbstractTask task, String kind) throws CoreException;

	public abstract boolean hasTaskData(AbstractTask task, String connectorKind);

	public abstract void putUpdatedTaskData(AbstractTask task, TaskData taskData, boolean user) throws CoreException;

	public abstract void putSubmittedTaskData(AbstractTask task, TaskData taskData) throws CoreException;

	/**
	 * Saves incoming data and updates task sync state appropriately
	 * 
	 * @return true if call results in change of sync state
	 */
	public abstract boolean saveIncoming(final AbstractTask repositoryTask, final RepositoryTaskData newTaskData,
			boolean forceSync);

	@Deprecated
	public abstract void setNewTaskData(RepositoryTaskData taskData);

}