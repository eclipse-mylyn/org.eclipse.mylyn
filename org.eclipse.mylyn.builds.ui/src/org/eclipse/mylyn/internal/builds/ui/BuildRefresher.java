/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.BuildJob;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.commons.core.IOperationMonitor;

/**
 * @author Steffen Pingel
 */
public class BuildRefresher implements IPropertyChangeListener {

	private class RefreshJob extends BuildJob {

		public RefreshJob() {
			super("Background Builds Refresh");
		}

		@Override
		protected IStatus doExecute(IOperationMonitor progress) {
			RefreshOperation refreshOperation = new RefreshOperation(BuildsUiInternal.getModel());
			return refreshOperation.syncExec(progress);
		}
	};

	private RefreshJob refreshJob;

	private long getInterval() {
		return BuildsUiPlugin.getDefault().getPreferenceStore().getLong(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL);
	}

	public boolean isEnabled() {
		return BuildsUiPlugin.getDefault().getPreferenceStore().getBoolean(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED)
				|| event.getProperty().equals(BuildsUiInternal.PREF_AUTO_REFRESH_INTERVAL)) {
			reschedule();
		}
	}

	private synchronized void reschedule() {
		if (isEnabled()) {
			if (refreshJob == null) {
				refreshJob = new RefreshJob();
				refreshJob.setSystem(true);
				refreshJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						reschedule();
					}
				});
			}
			BuildsUiInternal.getModel().getScheduler().schedule(refreshJob, getInterval());
		} else {
			if (refreshJob != null) {
				refreshJob.cancel();
			}
		}
	}

	public synchronized void stop() {
		if (refreshJob != null) {
			refreshJob.cancel();
		}
	}

}
