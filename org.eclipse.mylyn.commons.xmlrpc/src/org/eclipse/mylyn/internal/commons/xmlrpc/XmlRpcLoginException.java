/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
