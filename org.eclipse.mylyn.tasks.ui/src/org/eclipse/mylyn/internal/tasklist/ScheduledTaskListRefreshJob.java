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
		this.setSystem(true);
		this.setPriority(Job.BUILD);

	}

	public IStatus run(IProgressMonitor monitor) {
		if (TaskListView.getDefault() != null) {
			try {

				taskList = taskManager.getTaskList();
				List<TaskRepository>repositories  = MylarTaskListPlugin.getRepositoryManager().getAllRepositories();
				
				for (TaskRepository repository : repositories) {
					Set<AbstractRepositoryQuery> queries = Collections.unmodifiableSet(taskList.getRepositoryQueries(repository.getUrl()));
					if(queries.size() > 0) {
						AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
						if(connector != null) {
							connector.synchronize(queries, null);							
						}
					}
				}
				
//				Set<AbstractRepositoryQuery> queries = Collections.unmodifiableSet(taskList.getQueries());
				
				// HERE lies the problem.  We may have multiple different query types (connectors) so we need to
				// aggregate the diferent types into their respective sets... this is not very smooth. what do we do?
				
				
//				connector.synchronize(queries, null);
//				for (AbstractRepositoryQuery query : queries) {
//					AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
//							query.getRepositoryKind());
//					connector.synchronize(query, null);
//					if (monitor.isCanceled())
//						return Status.CANCEL_STATUS;
//				}

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
