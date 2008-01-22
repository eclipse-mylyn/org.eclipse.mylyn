/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;

/**
 * @author Mik Kersten
 * 
 * deprecated
 */
public class TaskArchive extends AbstractTaskCategory {

	public static final String HANDLE = "archive";

	public static final String LABEL_ARCHIVE = "Archive (all tasks)";

	public TaskList taskList;

	public TaskArchive(TaskList taskList) {
		super(HANDLE);
		this.taskList = taskList;
	}

	@Override
	public String getPriority() {
		return PriorityLevel.P5.toString();
	}

	@Override
	public String getHandleIdentifier() {
		return HANDLE;
	}

	@Override
	public String getSummary() {
		return LABEL_ARCHIVE;
	}

	@Override
	public boolean isUserDefined() {
		return false;
	}

	@Override
	public Set<AbstractTask> getChildren() {
		Set<AbstractTask> children = new HashSet<AbstractTask>();
		for (UnmatchedTaskContainer container : taskList.getOrphanContainers()) {
			children.addAll(container.getChildren());
		}
		return children;
	}

	@Override
	public Set<AbstractTask> getChildrenInternal() {
		return getChildren();
	}

	@Override
	public void internalAddChild(AbstractTask task) {
		// ignore
	}

	@Override
	public void internalRemoveChild(AbstractTask task) {
		// ignore
	}
}
