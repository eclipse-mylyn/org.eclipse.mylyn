/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.core.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * @author Mik Kersten
 */
public class MylarStatusHandler {

	private static boolean dumpErrors = false;
	
	private static final String ERROR_MESSAGE = "Please report the following error by following the bugs link at:\n" + "https://eclipse.org/mylar\n\n"
		+ "For details on this error please open the PDE Runtime -> Error Log view";

	public static PrintStream logStream = null;
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(IStatus status) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(DateUtil.getFormattedDate());
		buffer.append(", ");
		buffer.append(DateUtil.getFormattedTime());
		buffer.append("] ");
	
		if (WorkbenchPlugin.getDefault() != null) {
			buffer.append("version: " + WorkbenchPlugin.getDefault().getBundle().getLocation() + ", ");
		}
	
		buffer.append(status.toString() + ", ");
	
		if (status.getException() != null) {
			buffer.append("exception: ");
			buffer.append(printStrackTrace(status.getException()));
		}
	
		if (MylarPlugin.getDefault() != null) {
			MylarPlugin.getDefault().getLog().log(status);
			if (logStream != null)
				logStream.println(buffer.toString());
		}
		if (dumpErrors) System.err.println(buffer.toString());
	}

	public void setLogStream(PrintStream logStream) {
		MylarStatusHandler.logStream = logStream;
	}

	public PrintStream getLogStream() {
		return logStream;
	}
	
	private static String printStrackTrace(Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	public static void log(String message, Object source) {
		message = "Mylar: " + message;
		if (source != null) 
			message += ", source: " + source.getClass().getName();
	
		log(new Status(IStatus.INFO, MylarPlugin.PLUGIN_ID, IStatus.OK, message, null));
	}

	public static void log(Throwable throwable, String message) {
		fail(throwable, message, false);
	}

	/**
	 * @param throwable
	 *            can be null
	 * @param message
	 *            The message to include
	 * @param informUser
	 *            if true dialog box will be popped up
	 */
	public static void fail(Throwable throwable, String message, boolean informUser) {
		if (message == null)
			message = "no message";
		message += "\n";
	
		final Status status = new Status(Status.ERROR, MylarPlugin.PLUGIN_ID, IStatus.OK, message, throwable);
		log(status);
	
		if (informUser && Workbench.getInstance() != null) {
			try {
				Workbench.getInstance().getDisplay().asyncExec(new Runnable() {
					public void run() {
						Shell shell = null;
						if (Workbench.getInstance() != null && Workbench.getInstance().getActiveWorkbenchWindow() != null) {
							shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
						}
						ErrorDialog.openError(shell, "Mylar error", MylarStatusHandler.ERROR_MESSAGE, status);
					}
				});
			} catch (Throwable t) {
				throwable.printStackTrace();
			}
		}
	}

	public static void setDumpErrors(boolean dumpErrors) {
		MylarStatusHandler.dumpErrors = dumpErrors;
	}
}
