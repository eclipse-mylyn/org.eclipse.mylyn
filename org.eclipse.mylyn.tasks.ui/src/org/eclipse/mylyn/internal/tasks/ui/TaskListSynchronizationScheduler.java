/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListSynchronizationScheduler implements IPropertyChangeListener {

	private static final int DELAY_QUERY_REFRESH_ON_STARTUP = 10000;

	private ScheduledTaskListSynchJob refreshJob;

	private List<ScheduledTaskListSynchJob> jobs = new ArrayList<ScheduledTaskListSynchJob>();

	private List<ScheduledTaskListSynchJob> jobsQueue = Collections.synchronizedList(jobs);

	private final MutexRule rule = new MutexRule();

	public TaskListSynchronizationScheduler(boolean refreshOnStartup) {
		boolean enabled = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		if (refreshOnStartup && enabled) {
			addJobToQueue(new ScheduledTaskListSynchJob(DELAY_QUERY_REFRESH_ON_STARTUP,
					TasksUiPlugin.getTaskListManager()));
		}
	}

	public synchronized void startSynchJob() {
		if (jobsQueue.size() == 0) {
			scheduleRegularSynchronizationJob();
		}
		if (jobsQueue.size() > 0) {
			refreshJob = jobsQueue.remove(0);
			if (!TasksUiPlugin.getSynchronizationManager().isForcedSyncExec()) {
				refreshJob.schedule(refreshJob.getScheduleDelay());
			} else {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						refreshJob.run(new NullProgressMonitor());
					}
				});
			}
		}
	}

	private void scheduleRegularSynchronizationJob() {
		if (TasksUiPlugin.getDefault() == null) {
			return;
		}
		boolean enabled = TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(
				TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);
		if (enabled) {
			long miliseconds = TasksUiPlugin.getDefault().getPreferenceStore().getLong(
					TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);
			refreshJob = new ScheduledTaskListSynchJob(miliseconds, TasksUiPlugin.getTaskListManager());
			refreshJob.setRule(rule);
			addJobToQueue(refreshJob);
		}
	}

	private void addJobToQueue(final ScheduledTaskListSynchJob jobToAdd) {
		jobToAdd.addJobChangeListener(new JobChangeAdapter() {

			@Override
			public void done(IJobChangeEvent event) {
				synchronized (refreshJob) {
					if (refreshJob == jobToAdd && event.getResult() != Status.CANCEL_STATUS) {
						startSynchJob();
					}
				}
			}
		});
		jobsQueue.add(jobToAdd);
	}

	/**
	 * @param delay
	 *            sync delay (ms)
	 * @param repositories
	 *            used to scope sync to queries associated with given repositories, can be null (sync all repositories)
	 */
	public void synchNow(long delay, List<TaskRepository> repositories) {
		cancelAll();
		ScheduledTaskListSynchJob job = new ScheduledTaskListSynchJob(delay, TasksUiPlugin.getTaskListManager());
		job.setRepositories(repositories);
		addJobToQueue(job);
		startSynchJob();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)
				|| event.getProperty().equals(TasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)) {
			cancelAll();
			startSynchJob();
		}
	}

	/**
	 * for testing
	 */
	public ScheduledTaskListSynchJob getRefreshJob() {
		return refreshJob;
	}

	public void cancelAll() {
		jobsQueue.clear();
		if (refreshJob != null) {
			if (!refreshJob.cancel()) {
//				try {
//					// Potential deadlock if synch job never ends bug#178745
//					// refreshJob.join();
//				} catch (InterruptedException e) {
//					// ignore
//				}
			}
		}
	}

	static class MutexRule implements ISchedulingRule {
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == this;
		}

		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}
	}
}
