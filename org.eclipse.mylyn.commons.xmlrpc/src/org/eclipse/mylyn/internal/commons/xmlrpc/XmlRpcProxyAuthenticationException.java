/*******************************************************************************
 * Copyright (c) 2006, 2010 Steffen Pingel and others.
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

public class XmlRpcProxyAuthenticationException extends XmlRpcException {

	private static final long serialVersionUID = 305145749259511429L;

	public XmlRpcProxyAuthenticationException(String message) {
		super(message);
	}

	public XmlRpcProxyAuthenticationException() {
		super(null);
	}

}
