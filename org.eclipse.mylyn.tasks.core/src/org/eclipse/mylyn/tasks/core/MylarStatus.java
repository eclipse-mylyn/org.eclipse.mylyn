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

package org.eclipse.mylar.tasks.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class MylarStatus extends Status implements IMylarStatusConstants {

	private String htmlMessage;

	/**
	 * Constructs a status object with a message.
	 */
	public MylarStatus(int severity, String pluginId, int code, String message) {
		super(severity, pluginId, code, message, null);
	}

	/**
	 * Constructs a status object with a message and an exception. that caused
	 * the error.
	 */
	public MylarStatus(int severity, String pluginId, int code, String message, Throwable e) {
		super(severity, pluginId, code, message, e);
	}

	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	public String getMessage() {
		String message = super.getMessage();
		if (message != null && !"".equals(message)) {
			return message;
		}

		Throwable exception = getException();
		if (exception != null) {
			if (exception.getMessage() != null) {
				return exception.getMessage();
			}
			return exception.toString();
		}

		return "";
	}

	protected void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}

	public String getHtmlMessage() {
		return htmlMessage;
	}

	public boolean isHtmlMessage() {
		return htmlMessage != null;
	}

	public static MylarStatus createInternalError(String pluginId, String message, Throwable t) {
		return new MylarStatus(IStatus.ERROR, pluginId, IMylarStatusConstants.INTERNAL_ERROR, message, t);
	}

	public static MylarStatus createHtmlStatus(int severity, String pluginId, int code, String message,
			String htmlMessage) {
		if (htmlMessage == null) {
			throw new IllegalArgumentException("htmlMessage must not be null");
		}

		MylarStatus status = new MylarStatus(severity, pluginId, code, message);
		status.setHtmlMessage(htmlMessage);
		return status;
	}

}
