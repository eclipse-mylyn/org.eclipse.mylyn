/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.commons.net.AuthenticatedProxy;
import org.eclipse.mylyn.internal.commons.net.CommonsNetPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;

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
			String proxyHost = taskRepository.getProperty(TaskRepository.PROXY_HOSTNAME);
			String proxyPort = taskRepository.getProperty(TaskRepository.PROXY_PORT);
			String proxyUsername = "";
			String proxyPassword = "";
			AuthenticationCredentials credentials = taskRepository.getCredentials(AuthenticationType.PROXY);
			if (proxyHost != null && proxyHost.length() > 0 && credentials != null) {
				proxyUsername = credentials.getUserName();
				proxyPassword = credentials.getPassword();
			}
			return getProxy(proxyHost, proxyPort, proxyUsername, proxyPassword);
		}

		IProxyService service = CommonsNetPlugin.getProxyService();
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

	private static Proxy getProxy(String proxyHost, String proxyPort, String proxyUsername, String proxyPassword) {
		boolean authenticated = (proxyUsername != null && proxyPassword != null && proxyUsername.length() > 0 && proxyPassword.length() > 0);
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort.length() > 0) {
			int proxyPortNum = Integer.parseInt(proxyPort);
			InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPortNum);
			if (authenticated) {
				return new AuthenticatedProxy(Type.HTTP, sockAddr, proxyUsername, proxyPassword);
			} else {
				return new Proxy(Type.HTTP, sockAddr);
			}
		}
		return Proxy.NO_PROXY;
	}

	private Type getJavaProxyType(String type) {
		return (IProxyData.SOCKS_PROXY_TYPE.equals(type)) ? Proxy.Type.SOCKS : Proxy.Type.HTTP;
	}

	@Override
	public AuthenticationCredentials getCredentials(AuthenticationType type) {
		return taskRepository.getCredentials(type);
	}

}