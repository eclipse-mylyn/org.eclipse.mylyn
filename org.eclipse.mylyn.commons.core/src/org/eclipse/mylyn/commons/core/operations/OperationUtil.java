/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.core.operations;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.internal.commons.core.CommonsCorePlugin;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.commons.core.operations.OperationMonitor;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public class OperationUtil {

	private static final int POLL_INTERVAL = 500;

	public static IOperationMonitor convert(IProgressMonitor monitor) {
		return convert(monitor, "", 0); //$NON-NLS-1$
	}

	public static IOperationMonitor convert(IProgressMonitor monitor, int work) {
		return convert(monitor, "", work); //$NON-NLS-1$
	}

	public static IOperationMonitor convert(IProgressMonitor monitor, String taskName, int work) {
		if (monitor instanceof IOperationMonitor) {
			return (IOperationMonitor) monitor;
		}
		if (monitor == null) {
			return new NullOperationMonitor();
		}
		return new OperationMonitor(null, monitor, taskName, work);
	}

	public static boolean isBackgroundMonitor(IProgressMonitor monitor) {
		if (monitor == null) {
			return false;
		}
		if (monitor instanceof IOperationMonitor) {
			return ((IOperationMonitor) monitor).hasFlag(OperationFlag.BACKGROUND);
		}
		if (monitor.getClass().getSimpleName().equals("BackgroundProgressMonitor")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	/**
	 * @since 3.7
	 */
	public static synchronized ExecutorService getExecutorService() {
		return CommonsCorePlugin.getExecutorService();
	}

	/**
	 * @since 3.7
	 */
	@Deprecated
	public static <T> T execute(IProgressMonitor monitor, Operation<T> request) throws Throwable {
		// check for legacy reasons
		SubMonitor subMonitor = (monitor instanceof SubMonitor) ? (SubMonitor) monitor : SubMonitor.convert(null);

		Future<T> future = getExecutorService().submit(request);
		while (true) {
			if (monitor.isCanceled()) {
				request.abort();

				// wait for executor to finish
				future.cancel(false);
				try {
					if (!future.isCancelled()) {
						future.get();
					}
				} catch (CancellationException e) {
					// ignore
				} catch (InterruptedException e) {
					// ignore
				} catch (ExecutionException e) {
					// ignore
				}
				throw new OperationCanceledException();
			}

			try {
				return future.get(POLL_INTERVAL, TimeUnit.MILLISECONDS);
			} catch (CancellationException e) {
				throw new OperationCanceledException();
			} catch (ExecutionException e) {
				// XXX this hides the original stack trace from the caller invoking execute() 
				throw e.getCause();
			} catch (TimeoutException ignored) {
			}

			subMonitor.setWorkRemaining(20);
			subMonitor.worked(1);
		}
	}

}
