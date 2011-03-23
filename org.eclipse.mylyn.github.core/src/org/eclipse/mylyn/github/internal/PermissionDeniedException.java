/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

import org.apache.commons.httpclient.StatusLine;

public class PermissionDeniedException extends GitHubServiceException {

	private static final long serialVersionUID = -4635370712942848361L;

	protected PermissionDeniedException(Exception exception) {
		super(exception);
	}

	protected PermissionDeniedException(StatusLine statusLine) {
		super(statusLine);
	}

	protected PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	protected PermissionDeniedException(String message) {
		super(message);
	}

}
