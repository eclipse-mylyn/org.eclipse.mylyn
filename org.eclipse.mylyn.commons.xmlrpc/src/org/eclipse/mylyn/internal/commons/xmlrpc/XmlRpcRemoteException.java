/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Indicates that an exception on the repository side has been encountered while processing the request.
 * 
 * @author Steffen Pingel
 */
public class XmlRpcRemoteException extends XmlRpcException {

	private static final long serialVersionUID = -6761365344287289624L;

	public XmlRpcRemoteException() {
		super(null);
	}

	public XmlRpcRemoteException(String message) {
		super(message);
	}

	public XmlRpcRemoteException(Throwable cause) {
		super("Remote exception", cause); //$NON-NLS-1$
	}

	public XmlRpcRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
