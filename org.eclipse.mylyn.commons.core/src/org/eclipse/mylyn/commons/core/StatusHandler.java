/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.internal.runtime.PlatformActivator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.commons.core.ErrorReporterManager;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 * @since 3.0
 */
public class StatusHandler {

	@Deprecated
	private static final String ID_PLUGIN = "org.eclipse.mylyn";

	@Deprecated
	static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();

	@Deprecated
	private static IStatusHandler defaultHandler;

	private static ErrorReporterManager errorReporterManager;

	/**
	 * @since 3.0
	 */
	@Deprecated
	public static IStatusHandler getDefaultStatusHandler() {
		return defaultHandler;
	}

	/**
	 * @since 2.2
	 */
	@Deprecated
	public static Set<IStatusHandler> getStatusHandlers() {
		return handlers;
	}

	/**
	 * @since 3.0
	 */
	@Deprecated
	public static void setDefaultStatusHandler(IStatusHandler handler) {
		defaultHandler = handler;
		handlers.add(handler);
	}

	/**
	 * @since 3.0
	 */
	@Deprecated
	public static void addStatusHandler(IStatusHandler handler) {
		if (handler == null) {
			return;
		}
		if (handler != defaultHandler) {
			internalRemoveStatusHandler(defaultHandler, false);
		}
		handlers.add(handler);
	}

	/**
	 * @since 3.0
	 */
	@Deprecated
	public static void removeStatusHandler(IStatusHandler handler) {
		internalRemoveStatusHandler(handler, true);
	}

	@Deprecated
	private static void internalRemoveStatusHandler(IStatusHandler handler, boolean restoreDefault) {
		if (handler == null) {
			return;
		}
		handlers.remove(handler);
		if (restoreDefault && handlers.size() == 0) {
			addStatusHandler(defaultHandler);
		}
	}

	/**
	 * Logs <code>status</code> to this bundle's log if a platform is running. Does nothing if no platform is running.
	 * Plug-ins that require running in Eclipse are encouraged to use their plug-in log.
	 * 
	 * @param status
	 *            status to log
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
	 * @deprecated use {@link #log(IStatus)} instead
	 */
	@Deprecated
	public static void log(String message, Object source) {
		message = "Mylyn: " + message;
		if (source != null) {
			message += ", source: " + source.getClass().getName();
		}

		log(new Status(IStatus.INFO, ID_PLUGIN, IStatus.OK, message, null));
	}

	/**
	 * @deprecated use {@link #log(IStatus)} instead
	 */
	@Deprecated
	public static void log(Throwable throwable, String message) {
		fail(throwable, message, false, IStatus.INFO);
	}

	/**
	 * @deprecated use {@link #fail(IStatus)} or {{@link #log(IStatus)} instead
	 */
	@Deprecated
	public static void fail(Throwable throwable, String message, boolean informUser) {
		fail(throwable, message, informUser, IStatus.ERROR);
	}

	/**
	 * @param throwable
	 *            can be null
	 * @param message
	 *            The message to include
	 * @param informUser
	 *            if true dialog box will be popped up
	 * @deprecated use {@link #fail(IStatus)} or {{@link #log(IStatus)} instead
	 */
	@Deprecated
	public static void fail(Throwable throwable, String message, boolean informUser, int severity) {
		if (message == null) {
			message = "no message";
		}
		message += "\n";

		final Status status = new Status(severity, ID_PLUGIN, IStatus.OK, message, throwable);

		for (IStatusHandler handler : handlers) {
			handler.fail(status, informUser);
		}
		log(status);
	}

	/**
	 * Logs <code>status</code> to this bundle's log if a platform is running. Forwards <code>status</code> to
	 * registered status handlers.
	 * <p>
	 * Listeners should not block.
	 * </p>
	 * 
	 * @see #log(IStatus)
	 * @since 2.3
	 */
	@SuppressWarnings("deprecation")
	public static void fail(IStatus status) {
		log(status);
		for (IStatusHandler handler : handlers) {
			handler.fail(status, true);
		}
		// TODO enable
		//getErrorReporterManager().fail(status);
	}

	@SuppressWarnings("unused")
	private static synchronized ErrorReporterManager getErrorReporterManager() {
		if (errorReporterManager == null) {
			errorReporterManager = new ErrorReporterManager();
		}
		return errorReporterManager;
	}

	private static void dumpErrorToConsole(IStatus status) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Calendar now = Calendar.getInstance();
		sb.append(DateUtil.getIsoFormattedDateTime(now));
		sb.append("] ");
		sb.append(status.toString() + ", ");

		if (status.getException() != null) {
			sb.append("exception: ");
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
