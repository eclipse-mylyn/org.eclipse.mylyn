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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListSynchronizationManager implements IPropertyChangeListener {

	private static final int DELAY_QUERY_REFRESH_ON_STARTUP = 5000;

	private ScheduledTaskListSynchJob refreshJob;

	private List<ScheduledTaskListSynchJob> jobs = new ArrayList<ScheduledTaskListSynchJob>();

	private List<ScheduledTaskListSynchJob> jobsQueue = Collections.synchronizedList(jobs);

	public TaskListSynchronizationManager(boolean refreshOnStartup) {		
		boolean enabled = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		if (refreshOnStartup && enabled) {
			addJobToQueue(new ScheduledTaskListSynchJob(DELAY_QUERY_REFRESH_ON_STARTUP, MylarTaskListPlugin
					.getTaskListManager()));
		}
	}

	public synchronized void startSynchJob() {
		if (jobsQueue.size() == 0) {
			scheduleRegularBackupJob();
		}
		if (jobsQueue.size() > 0) {
			refreshJob = jobsQueue.remove(0);
			refreshJob.schedule(refreshJob.getScheduleDelay());
		}
	}

	private void scheduleRegularBackupJob() {
		boolean enabled = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		if (enabled) {
			long miliseconds = MylarTaskListPlugin.getMylarCorePrefs().getLong(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
			refreshJob = new ScheduledTaskListSynchJob(miliseconds, MylarTaskListPlugin.getTaskListManager());
			addJobToQueue(refreshJob);
		}
	}

	private void addJobToQueue(final ScheduledTaskListSynchJob jobToAdd) {
		jobToAdd.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				synchronized (refreshJob) {
					if(refreshJob == jobToAdd) {
						startSynchJob();						
					}	
				}				
			}
		});
		jobsQueue.add(jobToAdd);
	}

	public void synchNow(long delay) {
		cancelAll();
		addJobToQueue(new ScheduledTaskListSynchJob(delay, MylarTaskListPlugin.getTaskListManager()));
		startSynchJob();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)
				|| event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)) {
			cancelAll();
			startSynchJob();
		}
	}

	public ScheduledTaskListSynchJob getRefreshJob() {
		return refreshJob;
	}

	public void cancelAll() {
		if (refreshJob != null) {
			refreshJob.cancel();
		}
		jobsQueue.clear();
	}
}
