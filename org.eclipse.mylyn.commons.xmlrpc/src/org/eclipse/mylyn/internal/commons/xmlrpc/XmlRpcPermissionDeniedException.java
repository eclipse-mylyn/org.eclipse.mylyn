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
 * Indicates insufficient permissions to execute an operation.
 * 
 * @author Steffen Pingel
 */
public class XmlRpcPermissionDeniedException extends XmlRpcException {

	private static final long serialVersionUID = -6128773690643367414L;

	public XmlRpcPermissionDeniedException() {
		super(null);
	}

	public XmlRpcPermissionDeniedException(String message) {
		super(message);
	}

}
