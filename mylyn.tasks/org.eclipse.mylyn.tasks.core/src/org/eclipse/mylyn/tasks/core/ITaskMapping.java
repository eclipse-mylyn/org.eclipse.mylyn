/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients. Extend {@link TaskMapping} instead.
 */
public interface ITaskMapping {

	public void merge(@NonNull ITaskMapping source);

	public abstract List<String> getCc();

	public abstract Date getCompletionDate();

	public abstract String getComponent();

	public abstract Date getCreationDate();

	public abstract String getDescription();

	public abstract Date getDueDate();

	public abstract List<String> getKeywords();

	public abstract Date getModificationDate();

	public abstract String getOwner();

	/**
	 * @since 3.15
	 */
	public abstract String getOwnerId();

	public abstract String getPriority();

	public abstract PriorityLevel getPriorityLevel();

	public abstract String getProduct();

	public abstract String getReporter();

	public abstract String getResolution();

	/**
	 * @since 3.2
	 */
	public abstract String getSeverity();

	public abstract String getSummary();

	public abstract String getStatus();

	public abstract TaskData getTaskData();

	public abstract String getTaskKey();

	public abstract String getTaskKind();

	public abstract String getTaskStatus();

	public abstract String getTaskUrl();

	/**
	 * @since 3.2
	 */
	public abstract String getVersion();

}