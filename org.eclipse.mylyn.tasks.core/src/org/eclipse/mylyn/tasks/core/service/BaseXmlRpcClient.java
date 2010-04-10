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

import org.apache.commons.httpclient.HttpClient;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class BaseXmlRpcClient {
	protected XmlRpcClientConfigImpl xmlConfig;

	protected XmlRpcClient xmlrpc;

	protected HttpClient httpClient;

	protected final TaskRepository repository;

	public BaseXmlRpcClient(TaskRepository repository) {
		this.repository = repository;
	}

}
