/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.web.core.AbstractWebLocation;
import org.eclipse.mylyn.web.core.AuthenticatedProxy;
import org.eclipse.mylyn.web.core.AuthenticationCredentials;
import org.eclipse.mylyn.web.core.AuthenticationType;
import org.eclipse.mylyn.web.core.WebCorePlugin;

/**
 * @author Steffen Pingel
 */
public class TaskRepositoryLocation extends AbstractWebLocation {

	protected final TaskRepository taskRepository;

	public TaskRepositoryLocation(TaskRepository taskRepository) {
		super(taskRepository.getRepositoryUrl());

		this.taskRepository = taskRepository;
	}

	@Override
	public Proxy getProxyForHost(String host, String proxyType) {
		if (!taskRepository.isDefaultProxyEnabled()) {
			return taskRepository.getProxy();
		}

		IProxyService service = WebCorePlugin.getProxyService();
		if (service != null && service.isProxiesEnabled()) {
			IProxyData data = service.getProxyDataForHost(host, proxyType);
			if (data != null && data.getHost() != null) {
				String proxyHost = data.getHost();
				int proxyPort = data.getPort();
				// change the IProxyData default port to the Java default port
				if (proxyPort == -1) {
					proxyPort = 0;
				}

				InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPort);
				if (data.isRequiresAuthentication()) {
					return new AuthenticatedProxy(getJavaProxyType(proxyType), sockAddr, data.getUserId(),
							data.getPassword());
				} else {
					return new Proxy(getJavaProxyType(proxyType), sockAddr);
				}
			}
		}
		return null;
	}

	private Type getJavaProxyType(String type) {
		return (IProxyData.SOCKS_PROXY_TYPE.equals(type)) ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
	}

	@Override
	public ResultType requestCredentials(AuthenticationType type, String message) {
		return ResultType.NOT_SUPPORTED;
	}

	@Override
	public AuthenticationCredentials getCredentials(AuthenticationType type) {
		return taskRepository.getCredentials(type);
	}

}