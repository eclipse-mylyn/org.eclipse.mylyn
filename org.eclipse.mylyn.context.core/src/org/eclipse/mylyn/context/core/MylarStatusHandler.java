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

package org.eclipse.mylar.context.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Mik Kersten
 */
public class MylarStatusHandler {

//	private static boolean testingMode = false;

	private static IStatusNotifier statusNotifier = null;
	
	public static void setStatusNotifier(IStatusNotifier notifier) {
		statusNotifier = notifier;
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(IStatus status) {
		if (ContextCorePlugin.getDefault() != null) {
			ContextCorePlugin.getDefault().getLog().log(status);
		}
	}

	public static void log(String message, Object source) {
		message = "Mylar: " + message;
		if (source != null)
			message += ", source: " + source.getClass().getName();

		log(new Status(IStatus.INFO, ContextCorePlugin.PLUGIN_ID, IStatus.OK, message, null));
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

		final Status status = new Status(severity, ContextCorePlugin.PLUGIN_ID, IStatus.OK, message, throwable);
		
		if (statusNotifier != null) {
			statusNotifier.notify(status, informUser);
		}
	}

//	public static void setDumpErrorsForTesting(boolean dumpErrors) {
//		MylarStatusHandler.testingMode = dumpErrors;
//	}
}
