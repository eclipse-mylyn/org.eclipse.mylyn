/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * A general class for managing interaction between a remote API call and resulting local applications of the result. The consumer
 * life-cycle is managed by an {@link AbstractRemoteService}. A given service can have any number of consumers.
 *
 * @author Miles Parker
 */
public abstract class AbstractRemoteConsumer {

	/**
	 * Override to perform the request against remote API, storing the results of that request as state (e.g. as member(s) field of an
	 * implementing class). May be long-running and should be able to safely fail.
	 *
	 * @param force
	 *            pull from remote even when API doesn't require
	 * @param monitor
	 * @throws CoreException
	 */
	public abstract void pull(boolean force, IProgressMonitor monitor) throws CoreException;

	/**
	 * Override to apply the remotely obtained state to a local model object. This method is expected to execute <em>very</em> quickly, as
	 * the typical implementation will occur on the UI thread.
	 *
	 * @param force
	 *            apply the changes even when API doesn't require
	 * @throws CoreException
	 */
	public abstract void applyModel(boolean force);

	/**
	 * Provides notification of failure. See {@link AbstractRemoteService#retrieve(AbstractRemoteProcess, boolean)} for details.
	 */
	public abstract void notifyDone(IStatus status);

	/**
	 * Disposes any consumer controlled resources and listeners.
	 */
	public void dispose() {
	}

	/**
	 * Returns true if the request method should run asynchronously and false if and only if the request is known to have very short
	 * execution time and <em>cannot</em> block.
	 */
	public abstract boolean isAsynchronous();

	public abstract boolean isUserJob();

	public abstract boolean isSystemJob();

	/**
	 * Returns short description for this consumer. Must not return null.
	 *
	 * @return
	 */
	public abstract String getDescription();

	/**
	 * Returns {@link #getDescription()}.
	 */
	@Override
	public String toString() {
		return getDescription();
	}
}
