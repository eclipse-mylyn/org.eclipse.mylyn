/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.tasks.core.ITaskListRunnable;
import org.eclipse.mylyn.internal.tasks.core.TaskList;

/**
 * Use to perform atomic operations on the task list i.e. open/restore
 * 
 * @author Rob Elves
 * @since 3.0
 */
public abstract class TaskListModifyOperation implements IRunnableWithProgress {

	ISchedulingRule rule;

	public TaskListModifyOperation() {
		this(null);
	}

	public TaskListModifyOperation(ISchedulingRule rule) {
		this.rule = rule;
	}

	protected abstract void operations(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
			InterruptedException;

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
		return TasksUiPlugin.getTaskList();
	}
}
