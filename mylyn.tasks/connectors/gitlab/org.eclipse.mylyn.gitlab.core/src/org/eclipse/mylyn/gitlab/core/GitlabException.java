/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
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

package org.eclipse.mylyn.gitlab.core;

public class GitlabException extends Exception {

	private static final long serialVersionUID = 4915767534271733762L;

	public GitlabException() {
	}

	public GitlabException(String message) {
		super(message);
	}

	public GitlabException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public GitlabException(String message, Throwable cause) {
		super(message, cause);
	}

}
