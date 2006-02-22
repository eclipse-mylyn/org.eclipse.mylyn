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

package org.eclipse.mylar.internal.tasklist;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskList;
import org.eclipse.mylar.provisional.tasklist.TaskListManager;

/**
 * @author Robert Elves
 */
public class ScheduledTaskListRefreshJob extends Job {

	private static final String JOB_NAME = "Scheduled Tasklist Refresh Job";

	private long scheduleDelay = 1000 * 60 * 20;// 20 minutes default

	private TaskList taskList = null;

	private long count = 0;

	private TaskListManager taskManager;

	public ScheduledTaskListRefreshJob(long schedule, TaskListManager manager) {
		super(JOB_NAME);
		this.scheduleDelay = schedule;
		this.taskManager = manager;

	}

	public IStatus run(IProgressMonitor monitor) {
		if (TaskListView.getDefault() != null) {
			try {

				taskList = taskManager.getTaskList();
				List<AbstractRepositoryQuery> queries = Collections.unmodifiableList(taskList.getQueries());

				for (AbstractRepositoryQuery query : queries) {
					AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryClient(
							query.getRepositoryKind());
					connector.synchronize(query);
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
				}

			} finally {
				count++;
				if (count == Long.MAX_VALUE)
					count = 0;
				schedule(scheduleDelay);
			}
		}
		return Status.OK_STATUS;
	}

	public void setSchedule(long schedule) {
		this.scheduleDelay = schedule;
	}

	/**
	 * for testing purposes
	 * 
	 * @return
	 */
	public long getCount() {
		return count;
	}

}
