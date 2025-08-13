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
 * Indicates an authentication error during login.
 *
 * @author Steffen Pingel
 */
public class XmlRpcLoginException extends XmlRpcException {

	private static final long serialVersionUID = -6128773690643367414L;

	private boolean ntlmAuthRequested;

	public XmlRpcLoginException(String message) {
		super(message);
	}

	public XmlRpcLoginException() {
		super(null);
	}

	public boolean isNtlmAuthRequested() {
		return ntlmAuthRequested;
	}

	void setNtlmAuthRequested(boolean ntlmAuthRequested) {
		this.ntlmAuthRequested = ntlmAuthRequested;
	}

}
