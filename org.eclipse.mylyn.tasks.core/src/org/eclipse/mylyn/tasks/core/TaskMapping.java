/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.List;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class TaskMapping implements ITaskMapping {

	public void merge(ITaskMapping source) {
		// ignore
	}

	public Date getCompletionDate() {
		// ignore
		return null;
	}

	public String getComponent() {
		// ignore
		return null;
	}

	public Date getCreationDate() {
		// ignore
		return null;
	}

	public String getDescription() {
		// ignore
		return null;
	}

	public Date getDueDate() {
		// ignore
		return null;
	}

	public Date getModificationDate() {
		// ignore
		return null;
	}

	public String getOwner() {
		// ignore
		return null;
	}

	public PriorityLevel getPriorityLevel() {
		// ignore
		return null;
	}

	public String getProduct() {
		// ignore
		return null;
	}

	public String getSummary() {
		// ignore
		return null;
	}

	public TaskData getTaskData() {
		// ignore
		return null;
	}

	public String getTaskKey() {
		// ignore
		return null;
	}

	public String getTaskKind() {
		// ignore
		return null;
	}

	public String getTaskUrl() {
		// ignore
		return null;
	}

	public List<String> getCc() {
		// ignore
		return null;
	}

	public List<String> getKeywords() {
		// ignore
		return null;
	}

	public String getReporter() {
		// ignore
		return null;
	}

	public String getResolution() {
		// ignore
		return null;
	}

	public String getTaskStatus() {
		// ignore
		return null;
	}

	public String getStatus() {
		// ignore
		return null;
	}

	public String getPriority() {
		// ignore
		return null;
	}

}
