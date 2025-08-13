/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
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

import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.xmlrpc.XmlRpcException;

/**
 * @author Steffen Pingel
 */
public class XmlRpcHttpException extends XmlRpcException {

	private static final long serialVersionUID = 9032521978140685830L;

	private AuthScheme authScheme;

	public XmlRpcHttpException(int responseCode) {
		super(responseCode, "HTTP Error " + responseCode); //$NON-NLS-1$
	}

	public AuthScheme getAuthScheme() {
		return authScheme;
	}

	public void setAuthScheme(AuthScheme authScheme) {
		this.authScheme = authScheme;
	}

}