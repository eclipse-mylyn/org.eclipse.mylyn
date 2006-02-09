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

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author Rob Elves
 */
public class TaskListRefreshManager implements IPropertyChangeListener {

	private ScheduledTaskListRefreshJob refreshJob;

	
	private final long SECONDS = 1000;
	private final long MINUTES = 60 * SECONDS;
	private final long HOURS = 60*MINUTES;

	private long miliseconds = 5 * MINUTES;

	public void startRefreshJob() {
		if (refreshJob != null) {
			refreshJob.cancel();
		}

		boolean enabled = MylarTaskListPlugin.getPrefs().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);

		if (enabled) {
			String interval = MylarTaskListPlugin.getPrefs().getString(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_VALUE);
			String units = MylarTaskListPlugin.getPrefs().getString(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_UNITS);

			if (units.equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_UNITS_MINUTES)) {
				miliseconds = MINUTES * Long.parseLong(interval);
			} else {
				miliseconds = HOURS * Long.parseLong(interval);
			}

			refreshJob = new ScheduledTaskListRefreshJob(miliseconds, MylarTaskListPlugin.getTaskListManager());
			refreshJob.schedule(miliseconds);
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)
				|| event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_UNITS)
				|| event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_VALUE)) {
			startRefreshJob();
		}
	}

}
