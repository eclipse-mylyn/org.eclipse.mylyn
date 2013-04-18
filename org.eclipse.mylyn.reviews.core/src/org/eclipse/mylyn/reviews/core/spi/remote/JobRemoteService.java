/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * An implementation of a remote service using jobs to fulfill the remote service contract. (Future implementations will
 * support a more scalable approach to supporting multiple executions.)
 * 
 * @author Miles Parker
 */
public class JobRemoteService extends AbstractRemoteService {

	private final List<Job> jobs;

	public JobRemoteService() {
		jobs = new ArrayList<Job>();
	}

	/**
	 * Fully implements the {@link AbstractRemoteService#retrieve(AbstractRemoteConsumer, boolean)} contract:
	 * <ol>
	 * <li>If {@link AbstractRemoteConsumer#isAsynchronous()}, creates and runs a job to
	 * {@link AbstractRemoteConsumer#pull(boolean, org.eclipse.core.runtime.IProgressMonitor)} the remote API data.
	 * Otherwise, simply calls retrieve.</li>
	 * <li>If a failure occurs, calls {@link AbstractRemoteConsumer#notifyDone(org.eclipse.core.runtime.IStatus)}.</li>
	 * <li>Invokes {@link AbstractRemoteConsumer#applyModel(boolean)} inside of a modelExec call, so that extending
	 * classes can manage thread context.</li>
	 * <li>(No notification occurs in the case of an error while applying.)</li>
	 * </ol>
	 */
	@Override
	public void retrieve(final AbstractRemoteConsumer process, final boolean force) {
		if (process.isAsynchronous()) {
			new Thread() { //We create a new temporary thread here just to ensure that retrieve returns as quickly as possible
				@Override
				public void run() {
					final Job job = new Job(process.getDescription()) {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								process.pull(force, monitor);
							} catch (CoreException e) {
								return new Status(IStatus.WARNING, "org.eclipse.mylyn.reviews.core",
										"Couldn't update model.", e);
							} catch (OperationCanceledException e) {
								return Status.CANCEL_STATUS;
							}
							return Status.OK_STATUS;
						}
					};
					job.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(final IJobChangeEvent event) {
							modelExec(new Runnable() {
								public void run() {
									final IStatus result = event.getResult();
									if (result.isOK()) {
										process.applyModel(force);
									}
									process.notifyDone(event.getResult());
								}
							});
						}
					});
					addJob(job);
					job.schedule();
				}
			}.start();
		} else {
			try {
				process.pull(force, new NullProgressMonitor());
			} catch (CoreException e) {
				process.notifyDone(e.getStatus());
				return;
			}
			modelExec(new Runnable() {
				public void run() {
					process.applyModel(force);
					process.notifyDone(Status.OK_STATUS);
				}
			});
		}
	}

	private void addJob(final Job job) {
		synchronized (jobs) {
			jobs.add(job);
		}
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				removeJob(job);
			}
		});
	}

	private void removeJob(final Job job) {
		synchronized (jobs) {
			jobs.remove(job);
		}
	}

	/**
	 * Returns true if any jobs are currently running.
	 * 
	 * @return
	 */
	@Override
	public boolean isActive() {
		synchronized (jobs) {
			return jobs.size() > 0;
		}
	}

	/**
	 * Cancels all running jobs.
	 */
	@Override
	public synchronized void dispose() {
		synchronized (jobs) {
			for (Job job : jobs) {
				job.cancel();
			}
			jobs.clear();
		}
	}

	@Override
	public void modelExec(Runnable runnable, boolean block) {
		runnable.run();
	}
}
