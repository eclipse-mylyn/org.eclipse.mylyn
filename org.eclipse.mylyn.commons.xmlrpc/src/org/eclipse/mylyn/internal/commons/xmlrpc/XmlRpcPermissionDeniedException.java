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
