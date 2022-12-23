/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

/**
 * Indicates an error during repository access.
 * 
 * @author Steffen Pingel
 */
public class TracException extends Exception {

	private static final long serialVersionUID = 1929614326467463462L;

	public TracException() {
	}

	public TracException(String message) {
		super(message);
	}

	public TracException(Throwable cause) {
		super(cause.getMessage(), cause);
	}

	public TracException(String message, Throwable cause) {
		super(message, cause);
	}

}
