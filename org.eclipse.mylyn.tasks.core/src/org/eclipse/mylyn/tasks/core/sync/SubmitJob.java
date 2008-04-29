/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.sync;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.web.core.Policy;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public abstract class SubmitJob extends Job {

	private final List<SubmitJobListener> submitJobListeners = new ArrayList<SubmitJobListener>();

	public SubmitJob(String name) {
		super(name);
	}

	public void addSubmitJobListener(SubmitJobListener listener) {
		submitJobListeners.add(listener);
	}

	public void removeSubmitJobListener(SubmitJobListener listener) {
		submitJobListeners.remove(listener);
	}

	protected SubmitJobListener[] getSubmitJobListeners() {
		return submitJobListeners.toArray(new SubmitJobListener[0]);
	}

	public abstract IStatus getError();

	protected void fireTaskDataPosted(final IProgressMonitor monitor) throws CoreException {
		SubmitJobListener[] listeners = submitJobListeners.toArray(new SubmitJobListener[0]);
		if (listeners.length > 0) {
			final SubmitJobEvent event = new SubmitJobEvent(this);
			for (final SubmitJobListener listener : listeners) {
				listener.taskDataPosted(event, Policy.subMonitorFor(monitor, 100));
			}
		}
	}

	protected void fireTaskSynchronized(final IProgressMonitor monitor) {
		SubmitJobListener[] listeners = submitJobListeners.toArray(new SubmitJobListener[0]);
		if (listeners.length > 0) {
			final SubmitJobEvent event = new SubmitJobEvent(this);
			for (final SubmitJobListener listener : listeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Listener failed", e));
					}

					public void run() throws Exception {
						listener.taskSynchronized(event, Policy.subMonitorFor(monitor, 100));
					}
				});
			}
		}
	}

	protected void fireDone() {
		SubmitJobListener[] listeners = submitJobListeners.toArray(new SubmitJobListener[0]);
		if (listeners.length > 0) {
			final SubmitJobEvent event = new SubmitJobEvent(this);
			for (final SubmitJobListener listener : listeners) {
				SafeRunner.run(new ISafeRunnable() {
					public void handleException(Throwable e) {
						StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Listener failed", e));
					}

					public void run() throws Exception {
						listener.done(event);
					}
				});
			}
		}
	}

	public abstract AbstractTask getTask();

}
