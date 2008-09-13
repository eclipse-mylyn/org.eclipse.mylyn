/*******************************************************************************
* Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
