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

/**
 * Specifies a contract for a service that supports managed execution against remote (or other asynchronous or
 * unpredictable) APIs and/or resources.
 * 
 * @author Miles Parker
 */
public abstract class AbstractRemoteService {

	/**
	 * Implementors should invoke the the
	 * {@link AbstractRemoteConsumer#retrieve(org.eclipse.core.runtime.IProgressMonitor)} and
	 * {@link AbstractRemoteConsumer#apply()} methods for the supplied process in the following well defined way. This
	 * method is expected to return very quickly and <em>must not block</em> under any circumstances, as it must be
	 * safely callable from the UI thread. Implementors should support one full cycle of invocation:
	 * <ol>
	 * <li>The retrieve phase of the process is invoked. This invocation must be asynchronous if
	 * {@link AbstractRemoteConsumer#isAsynchronous()} is true.</li>
	 * <li>If a failure occurs or a core exception is thrown during the request phase,
	 * {@link AbstractRemoteConsumer#notifyDone(org.eclipse.core.runtime.IStatus)} is invoked with the exception.</li>
	 * <li>Otherwise, when the request process returns, the {@link AbstractRemoteConsumer#apply()} phase of the process
	 * is invoked. (In the case of the UI implementations, this might occur on the UI thread.)</li>
	 * <li>If a failure occurs during the create phase, notifyDone may optionally be invoked to report the failure.</li>
	 * <li>If both phases complete successfully,
	 * {@link AbstractRemoteConsumer#notifyDone(org.eclipse.core.runtime.IStatus)} is invoked on the process with an OK
	 * status.</li>
	 * </ol>
	 * 
	 * @param process
	 * @throws CoreException
	 */
	public abstract void execute(final AbstractRemoteConsumer process) throws CoreException;

	/**
	 * Supports apply and notification services executed against a specific thread. (For example, the Remote Ui Service
	 * overrides this to force all model update events to occur on the UI thread, as best EMF practices require.)
	 * 
	 * @param runnable
	 */
	public abstract void modelExec(Runnable runnable);

	/**
	 * Returns true if any consumers are currently being managed.
	 * 
	 * @return
	 */
	public abstract boolean isActive();

	/**
	 * Dispose of all resources and listeners used by the service.
	 */
	public abstract void dispose();
}
