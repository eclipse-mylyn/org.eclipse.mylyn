/*******************************************************************************
 * Copyright (c) 2013 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

public class BugzillaRestStatus {
	int httpStatusCode;

	int code;

	boolean error;

	String message;

	String documentation;

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public int getCode() {
		return code;
	}

	public boolean isError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public String getDocumentation() {
		return documentation;
	}

}
