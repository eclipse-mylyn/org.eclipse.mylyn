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
 * Indicates that an exception on the repository side has been encountered while processing the request.
 * 
 * @author Steffen Pingel
 */
public class TracRemoteException extends TracException {

	private static final long serialVersionUID = -6761365344287289624L;

	public TracRemoteException() {
	}

	public TracRemoteException(String message) {
		super(message);
	}

	public TracRemoteException(Throwable cause) {
		super(cause);
	}

	public TracRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
