/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

/**
 * @author Steffen Pingel
 */
public class JenkinsException extends Exception {

	private static final long serialVersionUID = -4419540659554920327L;

	public JenkinsException() {
	}

	public JenkinsException(String message) {
		super(message);
	}

	public JenkinsException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public JenkinsException(String message, Throwable cause) {
		super(message, cause);
	}

}
