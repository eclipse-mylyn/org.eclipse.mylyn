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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.internal.builds.core.operations.BuildJob;
import org.eclipse.mylyn.internal.builds.core.util.BuildScheduler;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Steffen Pingel
 */
public class BuildSchedulerUi extends BuildScheduler {

	public class JobStatusHandler extends JobChangeAdapter {
		@Override
		public void done(IJobChangeEvent event) {
			if (event.getJob() instanceof BuildJob) {
				IStatus status = ((BuildJob) event.getJob()).getStatus();
				if (status != null && !status.isOK() && status.getSeverity() != IStatus.CANCEL) {
					StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
				}
			}
			event.getJob().removeJobChangeListener(this);
		}
	}

	private final JobStatusHandler listener = new JobStatusHandler();

	@Override
	public void schedule(Job job) {
		job.addJobChangeListener(listener);
		super.schedule(job);
	}

}
