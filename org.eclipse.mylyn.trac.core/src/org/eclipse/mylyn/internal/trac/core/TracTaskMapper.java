/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector.TaskKind;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracPriority;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

/**
 * @author Steffen Pingel
 */
public class TracTaskMapper extends TaskMapper {

	private final ITracClient client;

	public TracTaskMapper(TaskData taskData, ITracClient client) {
		super(taskData);
		this.client = client;
	}

	@Override
	public PriorityLevel getPriorityLevel() {
		if (client != null) {
			String priority = getPriority();
			TracPriority[] tracPriorities = client.getPriorities();
			return TracRepositoryConnector.getTaskPriority(priority, tracPriorities);
		}
		return null;
	}

	@Override
	public String getTaskKind() {
		String tracTaskKind = super.getTaskKind();
		TaskKind taskKind = TaskKind.fromType(tracTaskKind);
		return (taskKind != null) ? taskKind.toString() : tracTaskKind;
	}

}
