/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskDataWorkingCopy {

	/**
	 * @since 3.0
	 */
	public abstract TaskData getEditsData();

	/**
	 * @since 3.0
	 */
	public abstract TaskData getLastReadData();

	/**
	 * @since 3.0
	 */
	public abstract TaskData getLocalData();

	/**
	 * @since 3.0
	 */
	public abstract TaskData getRepositoryData();

	/**
	 * @since 3.0
	 */
	public abstract boolean isSaved();

	/**
	 * @since 3.0
	 */
	public abstract void revert();

	/**
	 * @since 3.0
	 */
	public abstract void refresh(IProgressMonitor monitor) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract void save(Set<TaskAttribute> edits, IProgressMonitor monitor) throws CoreException;

	/**
	 * @since 3.0
	 */
	public abstract String getConnectorKind();

	/**
	 * @since 3.0
	 */
	public abstract String getRepositoryUrl();

	/**
	 * @since 3.0
	 */
	public abstract String getTaskId();

}