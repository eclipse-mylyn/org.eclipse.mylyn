/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 */
public interface ITaskDataState {

	public abstract TaskData getEditsData();

	public abstract TaskData getLastReadData();

	public abstract TaskData getLocalData();

	public abstract TaskData getRepositoryData();

	public abstract void refresh(IProgressMonitor monitor) throws CoreException;

	public abstract void save(IProgressMonitor monitor) throws CoreException;

	public abstract String getConnectorKind();

	public abstract String getRepositoryUrl();

	public abstract String getTaskId();

}