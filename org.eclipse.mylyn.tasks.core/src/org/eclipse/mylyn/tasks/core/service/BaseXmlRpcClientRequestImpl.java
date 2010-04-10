/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.service;

import org.apache.xmlrpc.XmlRpcRequestConfig;
import org.apache.xmlrpc.client.XmlRpcClientRequestImpl;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class BaseXmlRpcClientRequestImpl extends XmlRpcClientRequestImpl {
	private final IProgressMonitor progressMonitor;

	public BaseXmlRpcClientRequestImpl(XmlRpcRequestConfig config, String methodName, Object[] params,
			IProgressMonitor monitor) {
		super(config, methodName, params);
		this.progressMonitor = monitor;
	}

	public IProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}
}
