/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
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

package org.eclipse.mylyn.internal.bugzilla.core.service;

import org.apache.xmlrpc.XmlRpcException;

public class XmlRpcClassCastException extends XmlRpcException {

	private static final long serialVersionUID = -4092811363954657375L;

	public XmlRpcClassCastException(String pMessage) {
		super(pMessage);
	}

	public XmlRpcClassCastException(int pCode, String pMessage) {
		super(pCode, pMessage);
	}

	public XmlRpcClassCastException(String pMessage, Throwable pLinkedException) {
		super(pMessage, pLinkedException);
	}

	public XmlRpcClassCastException(int pCode, String pMessage, Throwable pLinkedException) {
		super(pCode, pMessage, pLinkedException);
	}

	public XmlRpcClassCastException(Throwable pLinkedException) {
		super("XMLRPC Cast error: " + pLinkedException.getMessage(), pLinkedException); //$NON-NLS-1$
	}
}
