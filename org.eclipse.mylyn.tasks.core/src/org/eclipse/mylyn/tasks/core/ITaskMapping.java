/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients. Extend {@link TaskMapping} instead.
 */
public interface ITaskMapping {

	public void merge(ITaskMapping source);

	public abstract List<String> getCc();

	public abstract Date getCompletionDate();

	public abstract String getComponent();

	public abstract Date getCreationDate();

	public abstract String getDescription();

	public abstract Date getDueDate();

	public abstract List<String> getKeywords();

	public abstract Date getModificationDate();

	public abstract String getOwner();

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