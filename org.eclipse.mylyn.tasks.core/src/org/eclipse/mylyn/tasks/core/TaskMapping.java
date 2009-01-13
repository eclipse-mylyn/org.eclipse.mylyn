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
 * Clients may subclass.
 * 
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskMapping implements ITaskMapping {

	/**
	 * @since 3.0
	 */
	public void merge(ITaskMapping source) {
		// ignore
	}

	/**
	 * @since 3.0
	 */
	public Date getCompletionDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getComponent() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public Date getCreationDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getDescription() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public Date getDueDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public Date getModificationDate() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getOwner() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public PriorityLevel getPriorityLevel() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getProduct() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getSummary() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public TaskData getTaskData() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getTaskKey() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getTaskKind() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getTaskUrl() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public List<String> getCc() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public List<String> getKeywords() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getReporter() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getResolution() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getTaskStatus() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getStatus() {
		// ignore
		return null;
	}

	/**
	 * @since 3.0
	 */
	public String getPriority() {
		// ignore
		return null;
	}

}
