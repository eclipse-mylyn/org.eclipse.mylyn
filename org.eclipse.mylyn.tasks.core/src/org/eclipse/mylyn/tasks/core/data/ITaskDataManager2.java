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

/**
 * @author Steffen Pingel
 */
public interface ITaskDataManager2 {

	public abstract ITaskDataState createWorkingCopy(AbstractTask task, String kind) throws CoreException;

	public abstract void discardEdits(AbstractTask task, String kind) throws CoreException;

	public abstract TaskData getTaskData(AbstractTask task, String kind) throws CoreException;

	public abstract void putTaskData(AbstractTask task, String kind, TaskData taskData) throws CoreException;

	public abstract boolean hasTaskData(AbstractTask task, String connectorKind);

}