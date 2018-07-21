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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;

import com.google.common.collect.ImmutableList;

class TaskListScheduledSynchronizationManager {

	private static final int DELAY_QUERY_REFRESH_ON_STARTUP = 20 * 1000;

	private static final List<String> FULL_REFRESH_ENABLEMENT_KEYS = ImmutableList
			.of(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);

	private static final List<String> RELEVANT_TASKS_REFRESH_ENABLEMENT_KEYS = ImmutableList.of(
			ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED,
			ITasksUiPreferenceConstants.RELEVANT_SYNCH_SCHEDULE_ENABLED);

	private final TaskListSynchronizationScheduler fullRefreshScheduler;

	private final TaskListSynchronizationScheduler relevantTaskScheduler;

	public TaskListScheduledSynchronizationManager(ITaskJobFactory taskJobFactory,
			TaskActivityManager taskActivityManager, IRepositoryManager repositoryManager) {
		SynchronizationJob refreshJob = taskJobFactory.createSynchronizeRepositoriesJob(null);
		refreshJob.setFullSynchronization(true);

		fullRefreshScheduler = new TaskListSynchronizationScheduler(refreshJob);
		updateSynchronizationScheduler(fullRefreshScheduler, true,
				ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, FULL_REFRESH_ENABLEMENT_KEYS);

		Job relevantJob = new SynchronizeRelevantTasksJob(taskActivityManager, repositoryManager, taskJobFactory);
		relevantTaskScheduler = new TaskListSynchronizationScheduler(relevantJob);
		updateSynchronizationScheduler(relevantTaskScheduler, true,
				ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS,
				RELEVANT_TASKS_REFRESH_ENABLEMENT_KEYS);

		MonitorUiPlugin.getDefault().getActivityContextManager().addListener(fullRefreshScheduler);
		MonitorUiPlugin.getDefault().getActivityContextManager().addListener(relevantTaskScheduler);
	}

	public void processPreferenceChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)
				|| event.getProperty().equals(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)) {
			updateSynchronizationScheduler(fullRefreshScheduler, false,
					ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS, FULL_REFRESH_ENABLEMENT_KEYS);
		}

		if (event.getProperty().equals(ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS)
				|| event.getProperty().equals(ITasksUiPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)
				|| event.getProperty().equals(ITasksUiPreferenceConstants.RELEVANT_SYNCH_SCHEDULE_ENABLED)) {
			updateSynchronizationScheduler(relevantTaskScheduler, false,
					ITasksUiPreferenceConstants.RELEVANT_TASKS_SCHEDULE_MILISECONDS,
					RELEVANT_TASKS_REFRESH_ENABLEMENT_KEYS);
		}
	}

	/**
	 * Updates the scheduler with the latest user-set preferences.
	 *
	 * @param scheduler
	 *            The scheduler to schedule refreshes.
	 * @param isInitialInvocation
	 *            {@code true} for the initial invocation; {@code false} for later invocations. When {@code true}, the
	 *            scheduler interval is set to a fixed startup delay (typically 20 seconds).
	 * @param intervalKey
	 *            The key in the preferences which is used to retrieve the latest schedule interval time.
	 * @param enabledKeys
	 *            The keys of the preferences which must all be true to enable the given scheduler
	 */
	private void updateSynchronizationScheduler(TaskListSynchronizationScheduler scheduler, boolean isInitialInvocation,
			String intervalKey, List<String> enabledKeys) {
		if (scheduler == null) {
			return;
		}

		boolean enabled = true;
		for (String enabledKey : enabledKeys) {
			enabled &= TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(enabledKey);
		}

		if (enabled) {
			long interval = TasksUiPlugin.getDefault().getPreferenceStore().getLong(intervalKey);
			if (isInitialInvocation) {
				scheduler.setInterval(DELAY_QUERY_REFRESH_ON_STARTUP, interval);
			} else {
				scheduler.setInterval(interval);
			}
		} else {
			scheduler.setInterval(0);
		}
	}
}
