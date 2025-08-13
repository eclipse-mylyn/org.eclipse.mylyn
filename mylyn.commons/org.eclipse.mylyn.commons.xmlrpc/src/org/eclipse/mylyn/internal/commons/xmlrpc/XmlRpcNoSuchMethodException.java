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

public class XmlRpcNoSuchMethodException extends XmlRpcException {

	private static final long serialVersionUID = 9075003728286406705L;

	public XmlRpcNoSuchMethodException(XmlRpcException e) {
		super("No such method", e); //$NON-NLS-1$
	}

}
