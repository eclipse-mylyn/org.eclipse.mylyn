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

package org.eclipse.mylyn.internal.tasks.activity.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.ActivityScope;
import org.eclipse.mylyn.tasks.activity.core.TaskActivityScope;
import org.eclipse.mylyn.tasks.activity.core.spi.ActivityProvider;
import org.eclipse.mylyn.tasks.activity.core.spi.IActivitySession;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Steffen Pingel
 */
public class TaskActivityProvider extends ActivityProvider {

	private static final String ID_PROVIDER = "org.eclipse.mylyn.tasks.activity.core.providers.TaskActivityProvider"; //$NON-NLS-1$

	private IActivitySession session;

	private final IRepositoryManager repositoryManager;

	private final TaskList taskList;

	public TaskActivityProvider(IRepositoryManager repositoryManager, TaskList taskList) {
		Assert.isNotNull(repositoryManager);
		Assert.isNotNull(taskList);
		this.repositoryManager = repositoryManager;
		this.taskList = taskList;
	}

	@Override
	public void open(IActivitySession session) {
		this.session = session;
	}

	@Override
	public void query(ActivityScope scope, IProgressMonitor monitor) throws CoreException {
		if (scope instanceof TaskActivityScope) {
			ITask scopeTask = ((TaskActivityScope) scope).getTask();
			for (ITask task : taskList.getAllTasks()) {
				if (task.getSummary().contains(scopeTask.getTaskId())) {
					ActivityEvent event = new ActivityEvent(task.getHandleIdentifier(), ID_PROVIDER, task.getSummary(),
							task.getCreationDate(), null);
					session.fireActivityEvent(event);
				}
			}
		}
	}

	@Override
	public void close() {
	}

}
