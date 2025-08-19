/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.sync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;

/**
 * @author Steffen Pingel
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class SubmitJob extends TaskJob {

	private final List<SubmitJobListener> submitJobListeners = Collections
			.synchronizedList(new ArrayList<SubmitJobListener>());

	/**
	 * @since 3.0
	 */
	public SubmitJob(String name) {
		super(name);
	}

	/**
	 * @since 3.0
	 */
	public void addSubmitJobListener(SubmitJobListener listener) {
		submitJobListeners.add(listener);
	}

	/**
	 * @since 3.0
	 */
	public void removeSubmitJobListener(SubmitJobListener listener) {
		submitJobListeners.remove(listener);
	}

	/**
	 * @since 3.0
	 */
	protected SubmitJobListener[] getSubmitJobListeners() {
		return submitJobListeners.toArray(new SubmitJobListener[0]);
	}

	/**
	 * @since 3.0
	 */
	protected void fireTaskSubmitted(final IProgressMonitor monitor) throws CoreException {
		SubmitJobListener[] listeners = submitJobListeners.toArray(new SubmitJobListener[0]);
		if (listeners.length > 0) {
			final SubmitJobEvent event = new SubmitJobEvent(this);
			for (final SubmitJobListener listener : listeners) {
				listener.taskSubmitted(event, Policy.subMonitorFor(monitor, 100));
			}
		}
	}

	/**
	 * @since 3.0
	 */
	protected void fireTaskSynchronized(final IProgressMonitor monitor) throws CoreException {
		SubmitJobListener[] listeners = submitJobListeners.toArray(new SubmitJobListener[0]);
		if (listeners.length > 0) {
			final SubmitJobEvent event = new SubmitJobEvent(this);
			for (final SubmitJobListener listener : listeners) {
				listener.taskSynchronized(event, Policy.subMonitorFor(monitor, 100));
			}
		}
	}

	/**
	 * @since 3.0
	 */
	protected void fireDone() {
		SubmitJobListener[] listeners = submitJobListeners.toArray(new SubmitJobListener[0]);
		if (listeners.length > 0) {
			final SubmitJobEvent event = new SubmitJobEvent(this);
			for (final SubmitJobListener listener : listeners) {
				SafeRunner.run(new ISafeRunnable() {
					@Override
					public void handleException(Throwable e) {
						StatusHandler
								.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Listener failed", e)); //$NON-NLS-1$
					}

					@Override
					public void run() throws Exception {
						listener.done(event);
					}
				});
			}
		}
	}

	/**
	 * @since 3.0
	 */
	public abstract ITask getTask();

	/**
	 * Returns the connector specific result of the submission.
	 *
	 * @return the response from the repository, null if no response was received or the submission failed
	 * @since 3.2
	 */
	public abstract RepositoryResponse getResponse();

}
