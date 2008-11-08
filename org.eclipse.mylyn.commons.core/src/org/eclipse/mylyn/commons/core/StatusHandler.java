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

package org.eclipse.mylyn.commons.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.internal.runtime.PlatformActivator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.commons.core.ErrorReporterManager;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 3.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class StatusHandler {

	private static ErrorReporterManager errorReporterManager;

	/**
	 * Logs <code>status</code> to this bundle's log if a platform is running. Does nothing if no platform is running.
	 * Plug-ins that require running in Eclipse are encouraged to use their plug-in log.
	 * 
	 * @param status
	 *            status to log
	 * @since 3.0
	 */
	public static void log(IStatus status) {
		if (InternalPlatform.getDefault() != null && PlatformActivator.getContext() != null) {
			ILog log = InternalPlatform.getDefault().getLog(PlatformActivator.getContext().getBundle());
			if (log != null) {
				log.log(status);
			}
		}
		if (CoreUtil.TEST_MODE) {
			dumpErrorToConsole(status);
		}
	}

	/**
	 * Logs <code>status</code> to this bundle's log if a platform is running. Forwards <code>status</code> to
	 * registered status handlers.
	 * <p>
	 * Listeners should not block.
	 * </p>
	 * 
	 * @see #log(IStatus)
	 * @since 3.0
	 */
	public static void fail(IStatus status) {
		log(status);
		getErrorReporterManager().fail(status);
	}

	private static synchronized ErrorReporterManager getErrorReporterManager() {
		if (errorReporterManager == null) {
			errorReporterManager = new ErrorReporterManager();
		}
		return errorReporterManager;
	}

	private static void dumpErrorToConsole(IStatus status) {
		StringBuilder sb = new StringBuilder();
		sb.append("["); //$NON-NLS-1$
		Calendar now = Calendar.getInstance();
		sb.append(DateUtil.getIsoFormattedDateTime(now));
		sb.append("] "); //$NON-NLS-1$
		sb.append(status.toString() + ", "); //$NON-NLS-1$
		if (status.getException() != null) {
			sb.append("Exception:\n"); //$NON-NLS-1$
			sb.append(printStrackTrace(status.getException()));
		}
		System.err.println(sb.toString());
	}

	private static String printStrackTrace(Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
}
