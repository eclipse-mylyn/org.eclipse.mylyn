/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.util;

import org.apache.xmlrpc.XmlRpcRequestConfig;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 */
public class TracXmlRpcClientRequest extends XmlRpcClientRequestImpl {

	private final IProgressMonitor progressMonitor;

	public TracXmlRpcClientRequest(XmlRpcRequestConfig config, String methodName, Object[] params,
			IProgressMonitor monitor) {
		super(config, methodName, params);
		this.progressMonitor = monitor;
	}

	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

}
