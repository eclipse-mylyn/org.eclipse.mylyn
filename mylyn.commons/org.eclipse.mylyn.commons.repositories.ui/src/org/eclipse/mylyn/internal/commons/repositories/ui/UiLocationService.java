/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui;

import java.net.Proxy;

import javax.net.ssl.X509TrustManager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.net.NetUtil;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.ILocationService;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationRequest;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.ICredentialsStore;
import org.eclipse.mylyn.internal.commons.repositories.ui.auth.RepositoryAuthenticator;

/**
 * @author Steffen Pingel
 */
public class UiLocationService implements ILocationService {

	public UiLocationService() {
	}

	public ICredentialsStore getCredentialsStore(String id) {
		Assert.isNotNull(id);
		return new UiSecureCredentialsStore(id);
	}

	public Proxy getProxyForHost(String host, String proxyType) {
		return NetUtil.getProxy(host, proxyType);
	}

	public X509TrustManager getTrustManager() {
		throw new UnsupportedOperationException();
	}

	public <T extends AuthenticationCredentials> T requestCredentials(
			AuthenticationRequest<AuthenticationType<T>> request, IProgressMonitor monitor) {
		if (CoreUtil.TEST_MODE) {
			throw new UnsupportedOperationException();
		}

		if (OperationUtil.isBackgroundMonitor(monitor)) {
			throw new UnsupportedOperationException();
		}

		RepositoryAuthenticator<T> requester = new RepositoryAuthenticator<T>(request);
		return requester.open(monitor);
	}

}
