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

	void merge(@NonNull ITaskMapping source);

	List<String> getCc();

	Date getCompletionDate();

	String getComponent();

	Date getCreationDate();

	String getDescription();

	Date getDueDate();

	List<String> getKeywords();

	Date getModificationDate();

	String getOwner();

	/**
	 * @since 3.15
	 */
	String getOwnerId();

	String getPriority();

	PriorityLevel getPriorityLevel();

	String getProduct();

	String getReporter();

	String getResolution();

	/**
	 * @since 3.2
	 */
	String getSeverity();

	String getSummary();

	String getStatus();

	TaskData getTaskData();

	String getTaskKey();

	String getTaskKind();

	String getTaskStatus();

	String getTaskUrl();

	/**
	 * @since 3.2
	 */
	String getVersion();

}