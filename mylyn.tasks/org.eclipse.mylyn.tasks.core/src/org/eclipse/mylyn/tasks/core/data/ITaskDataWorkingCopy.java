/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.data;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITaskDataWorkingCopy {

	/**
	 * Get the unsubmitted edits made locally by the user.
	 *
	 * @return TaskData containing only those attributes that have been edited.
	 * @since 3.0
	 */
	public abstract TaskData getEditsData();

	/**
	 * Get the "old" data that was retrieved from the repository some time in the past. When the
	 * {@link #getRepositoryData() repository data} contains unread incoming changes, the last read data is the data
	 * that the user last (marked) read. If the task has never been read, this will be the same as the
	 * {@link #getRepositoryData() repository data}.
	 * <p>
	 * When new TaskData is retrieved, if the previous incoming changes in the repository data have been
	 * {@link AbstractTask#isMarkReadPending() marked read}, the last read data is replaced with the repository data,
	 * and the repository data is replaced with the newly retrieved data. If the data has not been marked read, the
	 * repository data is replaced with the newly retrieved data and the last read data is unchanged.
	 *
	 * @return TaskData that has been read by the user, or the latest data retrieved from the repository if the task has
	 *         never been read
	 * @since 3.0
	 */
	public abstract TaskData getLastReadData();

	/**
	 * Get the TaskData that results from applying the user's {@link #getEditsData() edits} to the new
	 * {@link #getRepositoryData() repository data}. This is the only TaskData used to populate the task editor.
	 *
	 * @return TaskData used to populate the task editor
	 * @since 3.0
	 */
	public abstract TaskData getLocalData();

	/**
	 * Get the "new" data that was recently retrieved from the repository and may have changed since the
	 * {@link #getLastReadData() last read} data was read.
	 *
	 * @return TaskData that may contain incoming changes; the most current copy of the repository's data
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