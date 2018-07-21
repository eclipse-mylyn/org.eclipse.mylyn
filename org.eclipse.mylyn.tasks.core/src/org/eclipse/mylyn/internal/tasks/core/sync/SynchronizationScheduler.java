/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.sync;

import java.util.HashMap;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author Steffen Pingel
 */
public class SynchronizationScheduler {

	public static abstract class Synchronizer<T extends Job> {

		private boolean pendingRequest;

		public abstract T createJob();

	}

	public HashMap<Object, Synchronizer<?>> synchronizerByObject;

	public SynchronizationScheduler() {
		synchronizerByObject = new HashMap<Object, Synchronizer<?>>();
	}

	public void schedule(final Object object, final Synchronizer<?> synchronizer) {
		synchronized (synchronizerByObject) {
			Synchronizer<?> running = synchronizerByObject.get(object);
			if (running != null) {
				running.pendingRequest = true;
				return;
			} else {
				synchronizerByObject.put(object, synchronizer);
			}
		}

		scheduleJob(object, synchronizer);
	}

	private void scheduleJob(final Object object, final Synchronizer<?> synchronizer) {
		final Job job = synchronizer.createJob();
		final JobChangeAdapter listener = new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				job.removeJobChangeListener(this);
				boolean reschedule;
				synchronized (synchronizerByObject) {
					reschedule = synchronizer.pendingRequest;
					if (synchronizer.pendingRequest) {
						synchronizer.pendingRequest = false;
					} else {
						synchronizerByObject.remove(object);
					}
				}
				if (reschedule) {
					scheduleJob(object, synchronizer);
				}
			}
		};
		job.addJobChangeListener(listener);
		job.schedule();
	}

	public void cancel(Object object) {
		synchronized (synchronizerByObject) {
			Synchronizer<?> running = synchronizerByObject.get(object);
			if (running != null) {
				running.pendingRequest = false;
			}
		}
	}

}
