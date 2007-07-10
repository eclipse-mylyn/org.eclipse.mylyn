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
 * NOTE: very likely to be removed for 3.0 due to the new workbench StatusManager API
 * 
 * @author Mik Kersten
 */
public class StatusHandler {

	private static final String ID_PLUGIN = "org.eclipse.mylyn";

	private static Set<IStatusHandler> handlers = new HashSet<IStatusHandler>();

	public static void addStatusHandler(IStatusHandler handler) {
		handlers.add(handler);
	}

	public static void removeStatusHandler(IStatusHandler handler) {
		handlers.remove(handler);
	}

	/**
	 * Logs the specified status with this plug-in's log.
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

	public static void log(String message, Object source) {
		message = "Mylyn: " + message;
		if (source != null)
			message += ", source: " + source.getClass().getName();

		log(new Status(IStatus.INFO, ID_PLUGIN, IStatus.OK, message, null));
	}

	public static void log(Throwable throwable, String message) {
		fail(throwable, message, false, Status.INFO);
	}

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
	 */
	public static void fail(Throwable throwable, String message, boolean informUser, int severity) {
		if (message == null)
			message = "no message";
		message += "\n";

		final Status status = new Status(severity, ID_PLUGIN, IStatus.OK, message, throwable);

		for (IStatusHandler handler : handlers) {
			handler.fail(status, informUser);
		}
		log(status);
	}

	/**
	 * Display error to user
	 * 
	 * @param title
	 *            dialog title
	 * @param status
	 *            IStatus to reveal in dialog
	 */
	public static void displayStatus(String title, IStatus status) {
		for (IStatusHandler handler : handlers) {
			handler.displayStatus(title, status);
		}
	}
}
