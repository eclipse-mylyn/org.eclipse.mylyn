/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.internal.commons.net.InfiniteSubProgressMonitor;

/**
 * @since 2.3
 */
public class Policy {

	/**
	 * @since 2.3
	 */
	public static boolean DEBUG_STREAMS = false;

	static {
		if (CommonsNetPlugin.getDefault() != null && CommonsNetPlugin.getDefault().isDebugging()) {
			DEBUG_STREAMS = "true".equalsIgnoreCase(Platform.getDebugOption(CommonsNetPlugin.ID_PLUGIN + "/streams"));//$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @since 3.0
	 */
	public static void advance(IProgressMonitor monitor, int worked) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		monitor.worked(worked);
	}

	public static void checkCanceled(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	/**
	 * @since 3.0
	 */
	public static boolean isBackgroundMonitor(IProgressMonitor monitor) {
		return monitor instanceof BackgroundProgressMonitor;

	}

	/**
	 * @since 3.0
	 */
	public static IProgressMonitor backgroundMonitorFor(IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		return new BackgroundProgressMonitor(monitor);
	}

	/**
	 * @since 2.3
	 */
	public static IProgressMonitor monitorFor(IProgressMonitor monitor) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		return monitor;
	}

	/**
	 * @since 3.0
	 */
	public static IProgressMonitor monitorFor(IProgressMonitor monitor, boolean backgroundOperation) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		if (backgroundOperation) {
			return backgroundMonitorFor(monitor);
		}
		return monitor;
	}

	/**
	 * @since 2.3
	 */
	public static IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		if (monitor instanceof NullProgressMonitor) {
			return monitor;
		}
		if (monitor instanceof BackgroundProgressMonitor) {
			return new BackgroundProgressMonitor(SubMonitor.convert(monitor, ticks));
		}
		return SubMonitor.convert(monitor, ticks);
	}

	/**
	 * @since 2.3
	 */
	public static IProgressMonitor infiniteSubMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		if (monitor instanceof NullProgressMonitor) {
			return monitor;
		}
		if (monitor instanceof BackgroundProgressMonitor) {
			return new BackgroundProgressMonitor(new InfiniteSubProgressMonitor(monitor, ticks));
		}
		return new InfiniteSubProgressMonitor(monitor, ticks);
	}

	/**
	 * Wrapped progress monitor for background operations.
	 */
	private static class BackgroundProgressMonitor extends ProgressMonitorWrapper {

		protected BackgroundProgressMonitor(IProgressMonitor monitor) {
			super(monitor);
		}

	}

}
