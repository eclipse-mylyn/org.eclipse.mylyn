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

public class XmlRpcNoSuchMethodException extends XmlRpcException {

	private static final long serialVersionUID = 9075003728286406705L;

	public XmlRpcNoSuchMethodException(XmlRpcException e) {
		super("No such method", e); //$NON-NLS-1$
	}

}
