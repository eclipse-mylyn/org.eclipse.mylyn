/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.mylyn.commons.net.SslCertificateException;

/**
 * @author Steffen Pingel
 */
public class XmlRpcSslCertificateException extends XmlRpcException {

	private static final long serialVersionUID = 6981133252991248441L;

	public XmlRpcSslCertificateException(String message) {
		super(message);
	}

	public XmlRpcSslCertificateException() {
		super(null);
	}

	public XmlRpcSslCertificateException(SslCertificateException e) {
		super(null);
		initCause(e);
	}

}
