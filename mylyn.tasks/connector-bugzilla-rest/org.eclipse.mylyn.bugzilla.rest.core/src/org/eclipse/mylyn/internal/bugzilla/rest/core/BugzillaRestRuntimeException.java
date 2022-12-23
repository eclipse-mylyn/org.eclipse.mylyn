/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
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

public class BugzillaRestRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 5028038807086982720L;

	public BugzillaRestRuntimeException() {
	}

	public BugzillaRestRuntimeException(String message) {
		super(message);
	}

	public BugzillaRestRuntimeException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public BugzillaRestRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
