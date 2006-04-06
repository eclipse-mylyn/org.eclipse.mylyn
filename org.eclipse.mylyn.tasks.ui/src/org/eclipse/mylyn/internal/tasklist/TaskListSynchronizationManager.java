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
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskListSynchronizationManager implements IPropertyChangeListener {

	private ScheduledTaskListSynchJob refreshJob;

	public void startSynchJob() {
		if (refreshJob != null) {
			refreshJob.cancel();
		}

		boolean enabled = MylarTaskListPlugin.getMylarCorePrefs().getBoolean(
				TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED);

		if (enabled) {
			long miliseconds = MylarTaskListPlugin.getMylarCorePrefs().getLong(
					TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS);

			refreshJob = new ScheduledTaskListSynchJob(miliseconds, MylarTaskListPlugin.getTaskListManager());
			refreshJob.schedule(miliseconds);
		}
	}
	
	public void synchNow(long delay) {
		if (refreshJob != null) {
			refreshJob.cancel();
		}
		
		refreshJob = new ScheduledTaskListSynchJob(MylarTaskListPlugin.getTaskListManager());
		refreshJob.schedule(delay);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_ENABLED)				
				|| event.getProperty().equals(TaskListPreferenceConstants.REPOSITORY_SYNCH_SCHEDULE_MILISECONDS)) {
			startSynchJob();
		}
	}

	public ScheduledTaskListSynchJob getRefreshJob() {
		return refreshJob;
	}

}
