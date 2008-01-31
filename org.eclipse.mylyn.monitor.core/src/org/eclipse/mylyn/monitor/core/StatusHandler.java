/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.internal.runtime.PlatformActivator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.monitor.core.util.IStatusHandler;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Steffen Pingel
 */
// API 3.0 review for concurrency bugs
public class StatusHandler {

	// API 3.0 remove
	private static final String ID_PLUGIN = "org.eclipse.mylyn";

	// API 3.0 remove
	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();

	private static IStatusHandler defaultHandler;

	/**
	 * @since 2.2
	 */
	// API 3.0 remove
	public static IStatusHandler getDefaultStatusHandler() {
		return defaultHandler;
	}

	/**
	 * @since 2.2
	 */
	// API 3.0 remove
	public static Set<IStatusHandler> getStatusHandlers() {
		return handlers;
	}

	/**
	 * @since 2.2
	 */
	// API 3.0 remove
	public static void setDefaultStatusHandler(IStatusHandler handler) {
		defaultHandler = handler;
		handlers.add(handler);
	}

	// API 3.0 remove
	public static void addStatusHandler(IStatusHandler handler) {
		if (handler == null) {
			return;
		}
		if (handler != defaultHandler) {
			internalRemoveStatusHandler(defaultHandler, false);
		}
		handlers.add(handler);
	}

	// API 3.0 remove
	public static void removeStatusHandler(IStatusHandler handler) {
		internalRemoveStatusHandler(handler, true);
	}

	// API 3.0 remove
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
//			InternalPlatform.getDefault().log(status);
			ILog log = InternalPlatform.getDefault().getLog(PlatformActivator.getContext().getBundle());
			if (log != null) {
				log.log(status);
			}
		}
	}

	/**
	 * @deprecated use {@link #log(IStatus)} instead
	 */
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
	public static void log(Throwable throwable, String message) {
		fail(throwable, message, false, Status.INFO);
	}

	/**
	 * @deprecated use {@link #fail(IStatus)} or {{@link #log(IStatus)} instead
	 */
	public static void fail(Throwable throwable, String message, boolean informUser) {
		fail(throwable, message, informUser, Status.ERROR);
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
	 * 
	 * API 3.0 add comment that method does not block
	 * 
	 * @see #log(IStatus)
	 * @since 2.3
	 */
	public static void fail(IStatus status) {
		log(status);
		for (IStatusHandler handler : handlers) {
			handler.fail(status, true);
		}
	}
	
	/**
	 * Display error to user
	 * 
	 * @param title
	 *            dialog title
	 * @param status
	 *            IStatus to reveal in dialog
	 * @deprecated use <code>org.eclipse.ui.statushandlers.StatusMananger#getManager().handle()</code> instead.
	 */
	public static void displayStatus(String title, IStatus status) {
		for (IStatusHandler handler : handlers) {
			handler.displayStatus(title, status);
		}
	}
}
