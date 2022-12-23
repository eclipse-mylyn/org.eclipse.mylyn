/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.util;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.builds.internal.core.operations.BuildJob;

/**
 * @author Steffen Pingel
 * @author Lucas Panjer
 */
public class BuildScheduler {

	private static final long MAX_QUEUED_REFRESH_JOBS = 2;

	public BuildScheduler() {
	}

	public void schedule(Job job) {
		schedule(job, 0L);
	}

	public void schedule(Job job, long interval) {
		Job[] existingJobs = Job.getJobManager().find(job);
		if (existingJobs.length < MAX_QUEUED_REFRESH_JOBS) {
			job.schedule(interval);
		}
	}

	public void schedule(List<BuildJob> jobs) {
		for (BuildJob job : jobs) {
			schedule(job);
		}
	}

}
