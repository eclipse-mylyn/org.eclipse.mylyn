/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;

/**
 * Used for creating tasks from repository task data.
 * 
 * NOTE: likely to change for 3.0.
 * 
 * @author Rob Elves
 * @since 2.0
 */
@Deprecated
public interface ITaskFactory {

	/**
	 * @param synchData
	 * 		- synchronize task with the provided taskData
	 * @param forced
	 * 		- user requested synchronization
	 */
	public abstract AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException;

}