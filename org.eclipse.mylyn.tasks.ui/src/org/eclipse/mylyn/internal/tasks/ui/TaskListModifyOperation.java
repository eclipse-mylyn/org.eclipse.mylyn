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

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.tasks.core.operations.TaskListOperation;

/**
 * Use to perform atomic operations on the task list i.e. open/restore
 *
 * @author Rob Elves
 * @since 3.0
 */
public abstract class TaskListModifyOperation extends TaskListOperation implements IRunnableWithProgress {

	public TaskListModifyOperation() {
		this(null);
	}

	public TaskListModifyOperation(ISchedulingRule rule) {
		super(rule, TasksUiPlugin.getTaskList());
	}

}
