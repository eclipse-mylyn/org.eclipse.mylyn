/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.net;

/**
 * Indicates that the request is not supported.
 * 
 * @author Steffen Pingel
 * @since 3.0
 */
public class UnsupportedRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedRequestException() {
	}

	public UnsupportedRequestException(String message) {
		super(message);
	}

	public UnsupportedRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedRequestException(Throwable cause) {
		super(cause);
	}

}
