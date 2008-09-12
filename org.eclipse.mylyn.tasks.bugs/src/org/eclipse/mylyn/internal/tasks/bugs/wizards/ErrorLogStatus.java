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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import java.util.Date;

import org.eclipse.core.runtime.MultiStatus;

/**
 * @author Steffen Pingel
 */
public class ErrorLogStatus extends MultiStatus {

	private String logSessionData;

	private String stack;

	private Date date;

	public ErrorLogStatus(int severity, String pluginId, int code, String message) {
		super(pluginId, code, message, null);

		setSeverity(severity);
	}

	public String getLogSessionData() {
		return logSessionData;
	}

	public void setLogSessionData(String logSessionData) {
		this.logSessionData = logSessionData;
	}

	public String getStack() {
		return stack;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
