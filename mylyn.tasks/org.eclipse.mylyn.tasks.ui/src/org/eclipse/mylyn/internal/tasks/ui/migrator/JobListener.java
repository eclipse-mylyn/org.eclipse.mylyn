/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public class JobListener {

	private final Set<Job> jobs = Collections.synchronizedSet(new HashSet<Job>());

	private final Runnable allJobsDone;

	private boolean started;

	private boolean complete;

	public JobListener(Runnable allJobsDone) {
		this.allJobsDone = allJobsDone;
	}

	/**
	 * Must be called once when all jobs have been added.
	 */
	public void start() {
		synchronized (jobs) {
			started = true;
			if (jobs.isEmpty()) {
				allJobsDone.run();
				complete = true;
			}
		}
	}

	public boolean isComplete() {
		return complete;
	}

	/**
	 * This method should only be called from a single thread.
	 */
	public void add(final Job job, final Runnable jobDone) {
		jobs.add(job);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				jobDone.run();
				synchronized (jobs) {
					jobs.remove(job);
					if (jobs.isEmpty() && started) {
						allJobsDone.run();
						complete = true;
					}
				}
			}
		});
	}

}
