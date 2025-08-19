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

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * Clients may subclass.
 *
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskMapping implements ITaskMapping {

	/**
	 * @since 3.0
	 */
	@Override
	public void merge(ITaskMapping source) {
		// ignore
	}

	/**
	 * @since 3.0
	 */
	@Override
	public Date getCompletionDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getComponent() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public Date getCreationDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getDescription() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public Date getDueDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public Date getModificationDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getOwner() {
		// ignore
		return null;
	}

	/**
	 * @since 3.15
	 */
	@Override
	public String getOwnerId() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public PriorityLevel getPriorityLevel() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getProduct() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getSummary() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public TaskData getTaskData() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getTaskKey() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getTaskKind() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getTaskUrl() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public List<String> getCc() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public List<String> getKeywords() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getReporter() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getResolution() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getTaskStatus() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getStatus() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	@Override
	public String getPriority() {
		// ignore
		return null;
	}

	/**
	 * @since 3.2
	 */
	@Override
	public String getSeverity() {
		// ignore
		return null;
	}

	/**
	 * @since 3.2
	 */
	@Override
	public String getVersion() {
		// ignore
		return null;
	}

}
