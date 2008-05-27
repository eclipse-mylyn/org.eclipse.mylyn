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

	public void copyFrom(ITaskMapping source);

}