/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tests.integration;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.monitor.core.DateUtil;
import org.eclipse.mylyn.monitor.core.IStatusHandler;

/**
 * @author Mik Kersten
 */
public class TestingStatusNotifier implements IStatusHandler {

	public void fail(IStatus status, boolean informUser) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(DateUtil.getFormattedDate());
		buffer.append(", ");
		buffer.append(DateUtil.getFormattedTime());
		buffer.append("] ");
		buffer.append(status.toString() + ", ");

		if (status.getException() != null) {
			buffer.append("exception: ");
			buffer.append(printStrackTrace(status.getException()));
		}
	}

	private static String printStrackTrace(Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}

	public void displayStatus(String title, IStatus status) {
		// ignore

	}

}
