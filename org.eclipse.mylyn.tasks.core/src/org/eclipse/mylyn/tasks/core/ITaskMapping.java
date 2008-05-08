/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
// API 3.0 find a better name?
public interface ITaskMapping {

	public abstract Date getCompletionDate();

	public abstract String getComponent();

	public abstract Date getCreationDate();

	public abstract String getDescription();

	public abstract Date getDueDate();

	public abstract Date getModificationDate();

	public abstract String getOwner();

	public abstract PriorityLevel getPriority();

	public abstract String getProduct();

	public abstract String getSummary();

	public TaskData getTaskData();

	public abstract String getTaskKey();

	public abstract String getTaskKind();

	public abstract String getTaskUrl();

	public abstract void setCompletionDate(Date dateCompleted);

	public abstract void setComponent(String component);

	public abstract void setCreationDate(Date dateCreated);

	public abstract void setDescription(String description);

	public abstract void setDueDate(Date value);

	public abstract void setModificationDate(Date dateModified);

	// TODO use Person class?
	public abstract void setOwner(String owner);

	public abstract void setPriority(PriorityLevel priority);

	public abstract void setProduct(String product);

	// TODO use Person class?
	public abstract void setReporter(String reporter);

	public abstract void setSummary(String summary);

	public abstract void setTaskKind(String taskKind);

	public abstract void setTaskUrl(String taskUrl);

}