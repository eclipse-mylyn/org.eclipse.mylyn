/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * @author Rob Elves
 */
public interface ITaskFactory {

	/**
	 * @param synchData -
	 *            synchronize task with the provided taskData
	 * @param forced -
	 *            user requested synchronization
	 */
	public abstract AbstractRepositoryTask createTask(RepositoryTaskData taskData, boolean synchData, boolean forced, IProgressMonitor monitor) throws CoreException;

}