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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * A general class for managing interaction between a remote API call and resulting local applications of the result.
 * The consumer life-cycle is managed by an {@link AbstractRemoteService}. A given service can have any number of
 * consumers.
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteConsumer {

	/**
	 * Override to perform the request against remote API, storing the results of that request as state (e.g. as
	 * member(s) field of an implementing class). May be long-running and should be able to safely fail.
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	protected abstract void retrieve(IProgressMonitor monitor) throws CoreException;

	/**
	 * Override to apply the remotely obtained state to a local model object. This method is expected to execute
	 * <em>very</em> quickly, as the typical implementation will occur on the UI thread.
	 * 
	 * @throws CoreException
	 */
	protected abstract void apply();

	/**
	 * Provides notification of failure. See {@link AbstractRemoteService#execute(AbstractRemoteProcess)} for details.
	 */
	public abstract void notifyDone(IStatus status);

	/**
	 * Disposes any consumer controlled resources and listeners.
	 */
	public void dispose() {
	}

	/**
	 * Returns true if the request method should run asynchronously and false if and only if the request is known to
	 * have very short execution time and <em>cannot</em> block.
	 */
	public abstract boolean isAsynchronous();

	/**
	 * Returns short description for this consumer. Must not return null.
	 * 
	 * @return
	 */
	public abstract String getDescription();
}
