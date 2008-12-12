/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.provisional.commons.soap;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.commons.net.UnsupportedRequestException;
import org.eclipse.mylyn.commons.net.WebRequest;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.internal.commons.soap.SoapRequest;

/**
 * Base class for clients that access SOAP servers.
 * 
 * @author Steffen Pingel
 */
public abstract class AbstractSoapClient {

	/**
	 * Executes <code>runnable</code>. If an authentication exception occurs <code>runnable</code>
	 * {@link #doLogin(IProgressMonitor)} is invoked and the <code>runnable</code> is executed again. If authentication
	 * fails on the second attempt
	 * {@link AbstractWebLocation#requestCredentials(AuthenticationType, String, IProgressMonitor)} is invoked and the
	 * cycle is restarted from the beginning.
	 * 
	 * @param <T>
	 *            type for the result of <code>runnable</code>
	 * @param monitor
	 *            the progress monitor for cancellation support
	 * @param runnable
	 *            the request to execute
	 * @return the result of <code>runnable</code>
	 * @throws Exception
	 *             if an error occurs during execution of <code>runnable</code>, aborts <code>runnable</code> and throws
	 *             {@link OperationCanceledException} if the progress monitor is canceled
	 * @see #callOnce(IProgressMonitor, Callable)
	 */
	protected <T> T call(IProgressMonitor monitor, Callable<T> runnable) throws Exception {
		while (true) {
			try {
				try {
					return callOnce(monitor, runnable);
				} catch (Exception e) {
					if (isAuthenticationException(e) && doLogin(monitor)) {
						return callOnce(monitor, runnable);
					} else {
						throw e;
					}
				}
			} catch (Exception e) {
				if (isAuthenticationException(e)) {
					try {
						getLocation().requestCredentials(AuthenticationType.REPOSITORY, null, monitor);
					} catch (UnsupportedRequestException ignored) {
						throw e;
					}
				} else {
					throw e;
				}
			}
		}
	}

	/**
	 * Executes <code>runnable</code>. Returns after runnable has completed.
	 * 
	 * @param <T>
	 *            type for the result of <code>runnable</code>
	 * @param monitor
	 *            the progress monitor for cancellation support
	 * @param runnable
	 *            the request to execute
	 * @return the result of <code>runnable</code>
	 * @throws Exception
	 *             if an error occurs during execution of <code>runnable</code>, aborts <code>runnable</code> and throws
	 *             {@link OperationCanceledException} if the progress monitor is canceled
	 */
	protected <T> T callOnce(IProgressMonitor monitor, final Callable<T> runnable) throws Exception {
		try {
			monitor = Policy.monitorFor(monitor);

			final SoapRequest request = new SoapRequest(monitor);
			return WebUtil.execute(monitor, new WebRequest<T>() {

				@Override
				public void abort() {
					request.cancel();
				}

				public T call() throws Exception {
					try {
						SoapRequest.setCurrentRequest(request);
						return runnable.call();
					} finally {
						request.done();
					}
				}

			});
		} catch (RemoteException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} catch (Error e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the server location.
	 */
	protected abstract AbstractWebLocation getLocation();

	/**
	 * Returns if <code>exception</code> indicates an authentication error. If true is returned the original request can
	 * be retried with different credentials.
	 * 
	 * @see #call(IProgressMonitor, Callable)
	 */
	protected abstract boolean isAuthenticationException(Exception exception);

	/**
	 * If a request fails due to an authentication error this method can be used to re-establish a session. If true is
	 * returned the original request is retried.
	 * 
	 * @param monitor
	 * @return if false is returned the
	 * @see #call(IProgressMonitor, Callable)
	 * @see #isAuthenticationException(Exception)
	 */
	protected abstract boolean doLogin(IProgressMonitor monitor);

}
