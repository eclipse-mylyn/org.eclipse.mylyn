/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.web.core.WebCorePlugin;

/**
 * @author Mik Kersten
 */
public class MylarStatusHandler {

	// private static boolean testingMode = false;

	private static final String ID_PLUGIN = "org.eclipse.mylar";
	
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
		if (WebCorePlugin.getDefault() != null) {
			WebCorePlugin.getDefault().getLog().log(status);
		}
	}

	public static void log(String message, Object source) {
		message = "Mylar: " + message;
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

	// public static void setDumpErrorsForTesting(boolean dumpErrors) {
	// MylarStatusHandler.testingMode = dumpErrors;
	// }
}
