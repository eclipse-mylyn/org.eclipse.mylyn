/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 26, 2004
 */
package org.eclipse.mylar.tasks.core;

import java.util.Set;


/**
 * @author Mik Kersten
 */
public class TaskCategory extends AbstractTaskContainer {

	public TaskCategory(String handleAndDescription) {
		super(handleAndDescription);
	}	

	public String getPriority() {
		String highestPriority = Task.PriorityLevel.P5.toString();
		Set<ITask> tasks = getChildren();
		if (tasks.isEmpty()) {
			return Task.PriorityLevel.P1.toString();
		}
		for (ITask task : tasks) {
			if (highestPriority.compareTo(task.getPriority()) > 0) {
				highestPriority = task.getPriority();
			}
		}
		return highestPriority;
	}

	@Override
	public boolean isLocal() {
		return true;
	}
}
