/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.activity.core;

import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Defines the scope for a single task.
 * 
 * @author Steffen Pingel
 */
public class TaskActivityScope extends ActivityScope {

	private final ITask task;

	public TaskActivityScope(ITask task) {
		this.task = task;
	}

	public ITask getTask() {
		return task;
	}

}
