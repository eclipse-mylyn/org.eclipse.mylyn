/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.util;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.internal.core.BuildsCorePlugin;

/**
 * @author Steffen Pingel
 */
public class BuildRunner {

	private static void handleException(BuildRunnable code, Throwable exception)
			throws CoreException, OperationCanceledException {
		if (exception instanceof OperationCanceledException) {
			throw (OperationCanceledException) exception;
		}
		if (code.handleException(exception)) {
			return;
		}
		if (exception instanceof CoreException) {
			throw (CoreException) exception;
		}
		throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
				Messages.BuildRunner_unexpectedError, exception));
	}

	private static void handleException(BuildRunnableWithResult<?> code, Throwable exception)
			throws CoreException, OperationCanceledException {
		if (exception instanceof OperationCanceledException) {
			throw (OperationCanceledException) exception;
		}
		if (code.handleException(exception)) {
			return;
		}
		if (exception instanceof CoreException) {
			throw (CoreException) exception;
		}
		throw new CoreException(new Status(IStatus.ERROR, BuildsCorePlugin.ID_PLUGIN,
				Messages.BuildRunner_unexpectedError, exception));
	}

	public static void run(BuildRunnable code) throws CoreException, OperationCanceledException {
		Assert.isNotNull(code);
		try {
			code.run();
		} catch (Exception | LinkageError | AssertionError e) {
			handleException(code, e);
		}
	}

	public static <T> T run(BuildRunnableWithResult<T> code) throws CoreException, OperationCanceledException {
		Assert.isNotNull(code);
		try {
			return code.run();
		} catch (Exception | LinkageError | AssertionError e) {
			handleException(code, e);
		}
		throw new IllegalStateException(Messages.BuildRunner_unreachableCode);
	}

}
