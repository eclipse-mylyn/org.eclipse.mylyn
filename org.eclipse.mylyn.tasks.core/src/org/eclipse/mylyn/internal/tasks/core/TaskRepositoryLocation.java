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

import java.net.Proxy;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.WebUtil;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
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
			if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null) {
				try {
					int proxyPortNum = Integer.parseInt(proxyPort);
					AuthenticationCredentials credentials = taskRepository.getCredentials(AuthenticationType.PROXY);
					return WebUtil.createProxy(proxyHost, proxyPortNum, credentials);
				} catch (NumberFormatException e) {
					StatusHandler.log(new RepositoryStatus(taskRepository, IStatus.ERROR,
							ITasksCoreConstants.ID_PLUGIN, 0, "Error occured while configuring proxy. Invalid port \"" //$NON-NLS-1$
									+ proxyPort + "\" specified.", e)); //$NON-NLS-1$
				}
			}
		}
		return WebUtil.getProxy(host, proxyType);
	}

	@Override
	public AuthenticationCredentials getCredentials(AuthenticationType type) {
		return taskRepository.getCredentials(type);
	}

}