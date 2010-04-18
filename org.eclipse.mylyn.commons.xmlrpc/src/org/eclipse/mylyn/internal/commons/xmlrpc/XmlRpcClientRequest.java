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

import java.net.URL;

import org.apache.xmlrpc.XmlRpcRequestConfig;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Steffen Pingel
 */
class XmlRpcClientRequest extends XmlRpcClientRequestImpl {

	private final IProgressMonitor progressMonitor;

	private final URL url;

	public XmlRpcClientRequest(XmlRpcRequestConfig config, URL url, String methodName, Object[] params,
			IProgressMonitor monitor) {
		super(config, methodName, params);
		this.url = url;
		this.progressMonitor = monitor;
	}

	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public URL getUrl() {
		return url;
	}

}
