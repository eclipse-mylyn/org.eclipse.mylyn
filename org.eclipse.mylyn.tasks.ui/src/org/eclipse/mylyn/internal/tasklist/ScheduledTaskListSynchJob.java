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
import java.util.Set;

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
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Rob Elves
 */
public class ScheduledTaskListSynchJob extends Job {

	private static final String JOB_NAME = "Scheduled Tasklist Refresh Job";

	private long scheduleDelay = 1000 * 60 * 20;// 20 minutes default

	private TaskList taskList = null;

	private long count = 0;

	private TaskListManager taskListManager;
	
	public ScheduledTaskListSynchJob(long schedule, TaskListManager taskListManager) {
		super(JOB_NAME);
		this.scheduleDelay = schedule;
		this.taskListManager = taskListManager;
		this.setSystem(true);
		this.setPriority(Job.BUILD);
	}

	public ScheduledTaskListSynchJob(TaskListManager taskListManager) {
		super(JOB_NAME);
		this.taskListManager = taskListManager;
		this.setPriority(Job.BUILD);
		this.scheduleDelay = -1;
	}

	public IStatus run(IProgressMonitor monitor) {
		if (TaskListView.getDefault() != null) {
			try {
				taskList = taskListManager.getTaskList();
				List<TaskRepository>repositories  = MylarTaskListPlugin.getRepositoryManager().getAllRepositories();
				
				for (TaskRepository repository : repositories) {
					Set<AbstractRepositoryQuery> queries = Collections.unmodifiableSet(taskList.getRepositoryQueries(repository.getUrl()));
					if(queries.size() > 0) {
						AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
						if(connector != null) {
							connector.synchronize(queries, null, Job.DECORATE, 0);							
						}
					}
				} 
			} finally {
				count++;
				if (count == Long.MAX_VALUE)
					count = 0;
				if (scheduleDelay != -1) {
					schedule(scheduleDelay);
				}
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
