/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
