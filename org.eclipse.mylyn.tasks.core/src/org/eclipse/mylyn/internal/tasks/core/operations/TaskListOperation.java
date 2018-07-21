/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.TaskList;

public abstract class TaskListOperation {

	private final ISchedulingRule rule;

	private final TaskList taskList;

	public TaskListOperation(TaskList taskList) {
		this(null, taskList);
	}

	public TaskListOperation(ISchedulingRule rule, TaskList taskList) {
		this.rule = rule;
		this.taskList = taskList;
	}

	protected abstract void operations(IProgressMonitor monitor)
			throws CoreException, InvocationTargetException, InterruptedException;

	final public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		final InvocationTargetException[] ite = new InvocationTargetException[1];
		try {
			ITaskListRunnable runnable = new ITaskListRunnable() {

				public void execute(IProgressMonitor monitor) throws CoreException {
					try {
						Job.getJobManager().beginRule(rule, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
						operations(monitor);
					} catch (InvocationTargetException e) {
						ite[0] = e;
					} catch (InterruptedException e) {
						throw new OperationCanceledException(e.getMessage());
					} finally {
						Job.getJobManager().endRule(rule);
					}
				}
			};
			getTaskList().run(runnable, monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		}

		if (ite[0] != null) {
			throw ite[0];
		}
	}

	protected TaskList getTaskList() {
		return taskList;
	}
}
