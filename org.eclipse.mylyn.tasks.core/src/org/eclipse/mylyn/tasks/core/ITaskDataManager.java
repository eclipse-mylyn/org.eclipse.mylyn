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
public interface ITaskDataManager {

	/**
	 * Returns the most recent copy of the task data.
	 * 
	 * @return offline task data, null if no data found
	 */
	public abstract RepositoryTaskData getNewTaskData(String repositoryUrl, String id);

	@Deprecated
	public abstract void setNewTaskData(RepositoryTaskData taskData);

}